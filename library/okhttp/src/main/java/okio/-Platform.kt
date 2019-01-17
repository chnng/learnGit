/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("-Platform")
package okio

typealias JvmOverloads = kotlin.jvm.JvmOverloads
typealias JvmField = kotlin.jvm.JvmField
typealias JvmStatic = kotlin.jvm.JvmStatic
typealias JvmName = kotlin.jvm.JvmName

internal fun arraycopy(
  src: ByteArray,
  srcPos: Int,
  dest: ByteArray,
  destPos: Int,
  length: Int
) {
  System.arraycopy(src, srcPos, dest, destPos, length)
}

internal fun ByteArray.toUtf8String(): String = String(this, Charsets.UTF_8)

internal fun String.asUtf8ToByteArray(): ByteArray = toByteArray(Charsets.UTF_8)

// TODO remove if https://youtrack.jetbrains.com/issue/KT-20641 provides a better solution
typealias ArrayIndexOutOfBoundsException = java.lang.ArrayIndexOutOfBoundsException
