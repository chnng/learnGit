/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.internal.platform;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.internal.tls.BasicTrustRootIndex;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.TrustRootIndex;

import static okhttp3.internal.Util.assertionError;

/** Android 2.3 or better. */
class AndroidPlatform extends Platform {
  private static final int MAX_LOG_LENGTH = 4000;

  private final Class<?> sslParametersClass;
  private final OptionalMethod<Socket> setUseSessionTickets;
  private final OptionalMethod<Socket> setHostname;

  // Non-null on Android 5.0+.
  private final OptionalMethod<Socket> getAlpnSelectedProtocol;
  private final OptionalMethod<Socket> setAlpnProtocols;

  private final CloseGuard closeGuard = CloseGuard.get();

  AndroidPlatform(Class<?> sslParametersClass, OptionalMethod<Socket> setUseSessionTickets,
      OptionalMethod<Socket> setHostname, OptionalMethod<Socket> getAlpnSelectedProtocol,
      OptionalMethod<Socket> setAlpnProtocols) {
    this.sslParametersClass = sslParametersClass;
    this.setUseSessionTickets = setUseSessionTickets;
    this.setHostname = setHostname;
    this.getAlpnSelectedProtocol = getAlpnSelectedProtocol;
    this.setAlpnProtocols = setAlpnProtocols;
  }

  @Override public void connectSocket(Socket socket, InetSocketAddress address,
      int connectTimeout) throws IOException {
    try {
      socket.connect(address, connectTimeout);
    } catch (AssertionError e) {
      if (Util.isAndroidGetsocknameError(e)) throw new IOException(e);
      throw e;
    } catch (SecurityException e) {
      // Before android 4.3, socket.connect could throw a SecurityException
      // if opening a socket resulted in an EACCES error.
      IOException ioException = new IOException("Exception in connect");
      ioException.initCause(e);
      throw ioException;
    } catch (ClassCastException e) {
      // On android 8.0, socket.connect throws a ClassCastException due to a bug
      // see https://issuetracker.google.com/issues/63649622
//      if (Build.VERSION.SDK_INT == 26) {
//        IOException ioException = new IOException("Exception in connect");
//        ioException.initCause(e);
//        throw ioException;
//      } else {
        throw e;
//      }
    }
  }

  @Override protected X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
    Object context = readFieldOrNull(sslSocketFactory, sslParametersClass, "sslParameters");
    if (context == null) {
      // If that didn't work, try the Google Play Services SSL provider before giving up. This
      // must be loaded by the SSLSocketFactory's class loader.
      try {
        Class<?> gmsSslParametersClass = Class.forName(
            "com.google.android.gms.org.conscrypt.SSLParametersImpl", false,
            sslSocketFactory.getClass().getClassLoader());
        context = readFieldOrNull(sslSocketFactory, gmsSslParametersClass, "sslParameters");
      } catch (ClassNotFoundException e) {
        return super.trustManager(sslSocketFactory);
      }
    }

    X509TrustManager x509TrustManager = readFieldOrNull(
        context, X509TrustManager.class, "x509TrustManager");
    if (x509TrustManager != null) return x509TrustManager;

    return readFieldOrNull(context, X509TrustManager.class, "trustManager");
  }

  @Override public void configureTlsExtensions(
      SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
    // Enable SNI and session tickets.
    if (hostname != null) {
      setUseSessionTickets.invokeOptionalWithoutCheckedException(sslSocket, true);
      setHostname.invokeOptionalWithoutCheckedException(sslSocket, hostname);
    }

    // Enable ALPN.
    if (setAlpnProtocols != null && setAlpnProtocols.isSupported(sslSocket)) {
      Object[] parameters = {concatLengthPrefixed(protocols)};
      setAlpnProtocols.invokeWithoutCheckedException(sslSocket, parameters);
    }
  }

  @Override public @Nullable String getSelectedProtocol(SSLSocket socket) {
    if (getAlpnSelectedProtocol == null) return null;
    if (!getAlpnSelectedProtocol.isSupported(socket)) return null;

    byte[] alpnResult = (byte[]) getAlpnSelectedProtocol.invokeWithoutCheckedException(socket);
    return alpnResult != null ? new String(alpnResult, Util.UTF_8) : null;
  }

  @Override public void log(int level, String message, Throwable t) {
//    int logLevel = level == WARN ? Log.WARN : Log.DEBUG;
//    if (t != null) message = message + '\n' + Log.getStackTraceString(t);
//
//    // Split by line, then ensure each line can fit into Log's maximum length.
//    for (int i = 0, length = message.length(); i < length; i++) {
//      int newline = message.indexOf('\n', i);
//      newline = newline != -1 ? newline : length;
//      do {
//        int end = Math.min(newline, i + MAX_LOG_LENGTH);
//        Log.println(logLevel, "OkHttp", message.substring(i, end));
//        i = end;
//      } while (i < newline);
//    }
  }

  @Override public Object getStackTraceForCloseable(String closer) {
    return closeGuard.createAndOpen(closer);
  }

  @Override public void logCloseableLeak(String message, Object stackTrace) {
    boolean reported = closeGuard.warnIfOpen(stackTrace);
    if (!reported) {
      // Unable to report via CloseGuard. As a last-ditch effort, send it to the logger.
      log(WARN, message, null);
    }
  }

  @Override public boolean isCleartextTrafficPermitted(String hostname) {
    try {
      Class<?> networkPolicyClass = Class.forName("android.security.NetworkSecurityPolicy");
      Method getInstanceMethod = networkPolicyClass.getMethod("getInstance");
      Object networkSecurityPolicy = getInstanceMethod.invoke(null);
      return api24IsCleartextTrafficPermitted(hostname, networkPolicyClass, networkSecurityPolicy);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      return super.isCleartextTrafficPermitted(hostname);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw assertionError("unable to determine cleartext support", e);
    }
  }

  private boolean api24IsCleartextTrafficPermitted(String hostname, Class<?> networkPolicyClass,
      Object networkSecurityPolicy) throws InvocationTargetException, IllegalAccessException {
    try {
      Method isCleartextTrafficPermittedMethod = networkPolicyClass
          .getMethod("isCleartextTrafficPermitted", String.class);
      return (boolean) isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy, hostname);
    } catch (NoSuchMethodException e) {
      return api23IsCleartextTrafficPermitted(hostname, networkPolicyClass, networkSecurityPolicy);
    }
  }

  private boolean api23IsCleartextTrafficPermitted(String hostname, Class<?> networkPolicyClass,
      Object networkSecurityPolicy) throws InvocationTargetException, IllegalAccessException {
    try {
      Method isCleartextTrafficPermittedMethod = networkPolicyClass
          .getMethod("isCleartextTrafficPermitted");
      return (boolean) isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy);
    } catch (NoSuchMethodException e) {
      return super.isCleartextTrafficPermitted(hostname);
    }
  }

  /**
   * Checks to see if Google Play Services Dynamic Security Provider is present which provides ALPN
   * support. If it isn't checks to see if device is Android 5.0+ since 4.x device have broken
   * ALPN support.
   */
  private static boolean supportsAlpn() {
    if (Security.getProvider("GMSCore_OpenSSL") != null) {
      return true;
    } else {
      try {
        Class.forName("android.net.Network"); // Arbitrary class added in Android 5.0.
        return true;
      } catch (ClassNotFoundException ignored) { }
    }
    return false;
  }

  public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager trustManager) {
    try {
      Class<?> extensionsClass = Class.forName("android.net.http.X509TrustManagerExtensions");
      Constructor<?> constructor = extensionsClass.getConstructor(X509TrustManager.class);
      Object extensions = constructor.newInstance(trustManager);
      Method checkServerTrusted = extensionsClass.getMethod(
          "checkServerTrusted", X509Certificate[].class, String.class, String.class);
      return new AndroidCertificateChainCleaner(extensions, checkServerTrusted);
    } catch (Exception e) {
      return super.buildCertificateChainCleaner(trustManager);
    }
  }

  public static Platform buildIfSupported() {
    // Attempt to find Android 2.3+ APIs.
    try {
      Class<?> sslParametersClass;
      try {
        sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
      } catch (ClassNotFoundException e) {
        // Older platform before being unbundled.
        sslParametersClass = Class.forName(
            "org.apache.harmony.xnet.provider.jsse.SSLParametersImpl");
      }

      OptionalMethod<Socket> setUseSessionTickets = new OptionalMethod<>(
          null, "setUseSessionTickets", boolean.class);
      OptionalMethod<Socket> setHostname = new OptionalMethod<>(
          null, "setHostname", String.class);
      OptionalMethod<Socket> getAlpnSelectedProtocol = null;
      OptionalMethod<Socket> setAlpnProtocols = null;

      if (supportsAlpn()) {
        getAlpnSelectedProtocol
            = new OptionalMethod<>(byte[].class, "getAlpnSelectedProtocol");
        setAlpnProtocols
            = new OptionalMethod<>(null, "setAlpnProtocols", byte[].class);
      }

      return new AndroidPlatform(sslParametersClass, setUseSessionTickets, setHostname,
          getAlpnSelectedProtocol, setAlpnProtocols);
    } catch (ClassNotFoundException ignored) {
      // This isn't an Android runtime.
    }

    return null;
  }

  @Override
  public TrustRootIndex buildTrustRootIndex(X509TrustManager trustManager) {

    try {
      // From org.conscrypt.TrustManagerImpl, we want the method with this signature:
      // private TrustAnchor findTrustAnchorByIssuerAndSignature(X509Certificate lastCert);
      Method method = trustManager.getClass().getDeclaredMethod(
              "findTrustAnchorByIssuerAndSignature", X509Certificate.class);
      method.setAccessible(true);
      return new AndroidTrustRootIndex(trustManager, method);
    } catch (NoSuchMethodException e) {
      return super.buildTrustRootIndex(trustManager);
    }
  }

  /**
   * X509TrustManagerExtensions was added to Android in API 17 (Android 4.2, released in late 2012).
   * This is the best way to get a clean chain on Android because it uses the same code as the TLS
   * handshake.
   */
  static final class AndroidCertificateChainCleaner extends CertificateChainCleaner {
    private final Object x509TrustManagerExtensions;
    private final Method checkServerTrusted;

    AndroidCertificateChainCleaner(Object x509TrustManagerExtensions, Method checkServerTrusted) {
      this.x509TrustManagerExtensions = x509TrustManagerExtensions;
      this.checkServerTrusted = checkServerTrusted;
    }

    @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"}) // Reflection on List<Certificate>.
    @Override public List<Certificate> clean(List<Certificate> chain, String hostname)
        throws SSLPeerUnverifiedException {
      try {
        X509Certificate[] certificates = chain.toArray(new X509Certificate[chain.size()]);
        return (List<Certificate>) checkServerTrusted.invoke(
            x509TrustManagerExtensions, certificates, "RSA", hostname);
      } catch (InvocationTargetException e) {
        SSLPeerUnverifiedException exception = new SSLPeerUnverifiedException(e.getMessage());
        exception.initCause(e);
        throw exception;
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    @Override public boolean equals(Object other) {
      return other instanceof AndroidCertificateChainCleaner; // All instances are equivalent.
    }

    @Override public int hashCode() {
      return 0;
    }
  }

  /**
   * Provides access to the internal dalvik.system.CloseGuard class. Android uses this in
   * combination with android.os.StrictMode to report on leaked java.io.Closeable's. Available since
   * Android API 11.
   */
  static final class CloseGuard {
    private final Method getMethod;
    private final Method openMethod;
    private final Method warnIfOpenMethod;

    CloseGuard(Method getMethod, Method openMethod, Method warnIfOpenMethod) {
      this.getMethod = getMethod;
      this.openMethod = openMethod;
      this.warnIfOpenMethod = warnIfOpenMethod;
    }

    Object createAndOpen(String closer) {
      if (getMethod != null) {
        try {
          Object closeGuardInstance = getMethod.invoke(null);
          openMethod.invoke(closeGuardInstance, closer);
          return closeGuardInstance;
        } catch (Exception ignored) {
        }
      }
      return null;
    }

    boolean warnIfOpen(Object closeGuardInstance) {
      boolean reported = false;
      if (closeGuardInstance != null) {
        try {
          warnIfOpenMethod.invoke(closeGuardInstance);
          reported = true;
        } catch (Exception ignored) {
        }
      }
      return reported;
    }

    static CloseGuard get() {
      Method getMethod;
      Method openMethod;
      Method warnIfOpenMethod;

      try {
        Class<?> closeGuardClass = Class.forName("dalvik.system.CloseGuard");
        getMethod = closeGuardClass.getMethod("get");
        openMethod = closeGuardClass.getMethod("open", String.class);
        warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen");
      } catch (Exception ignored) {
        getMethod = null;
        openMethod = null;
        warnIfOpenMethod = null;
      }
      return new CloseGuard(getMethod, openMethod, warnIfOpenMethod);
    }
  }

  /**
   * An index of trusted root certificates that exploits knowledge of Android implementation
   * details. This class is potentially much faster to initialize than {@link BasicTrustRootIndex}
   * because it doesn't need to load and index trusted CA certificates.
   *
   * <p>This class uses APIs added to Android in API 14 (Android 4.0, released October 2011). This
   * class shouldn't be used in Android API 17 or better because those releases are better served by
   * {@link AndroidPlatform.AndroidCertificateChainCleaner}.
   */
  static final class AndroidTrustRootIndex implements TrustRootIndex {
    private final X509TrustManager trustManager;
    private final Method findByIssuerAndSignatureMethod;

    AndroidTrustRootIndex(X509TrustManager trustManager, Method findByIssuerAndSignatureMethod) {
      this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
      this.trustManager = trustManager;
    }

    @Override public X509Certificate findByIssuerAndSignature(X509Certificate cert) {
      try {
        TrustAnchor trustAnchor = (TrustAnchor) findByIssuerAndSignatureMethod.invoke(
                trustManager, cert);
        return trustAnchor != null
                ? trustAnchor.getTrustedCert()
                : null;
      } catch (IllegalAccessException e) {
        throw assertionError("unable to get issues and signature", e);
      } catch (InvocationTargetException e) {
        return null;
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof AndroidTrustRootIndex)) {
        return false;
      }
      AndroidTrustRootIndex that = (AndroidTrustRootIndex) obj;
      return trustManager.equals(that.trustManager)
              && findByIssuerAndSignatureMethod.equals(that.findByIssuerAndSignatureMethod);
    }

    @Override
    public int hashCode() {
      return trustManager.hashCode() + 31 * findByIssuerAndSignatureMethod.hashCode();
    }
  }
}
