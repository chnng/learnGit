package com.learn.git.ui.fragment;

import com.learn.git.ui.base.BaseFragment;

public class NotificationFragment extends BaseFragment {
    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public void onCreate() {

    }


    public void sendNotification() {
        //    NotificationManager notificationManager =
        //        (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //    if (notificationManager == null) {
        //      return;
        //    }
        //    Intent intent = new Intent();
        //    intent.setClassName(this, "com.qinhe.ispeak.AppStartActivity");
        //    intent.addCategory(Intent.CATEGORY_LAUNCHER);
        //    intent.setAction(Intent.ACTION_MAIN);
        //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        //                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //    PendingIntent pendingIntent = PendingIntent.getActivity(
        //        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //    NotificationCompat.Builder builder =
        //        new NotificationCompat.Builder(this, null)
        //            .setLargeIcon(BitmapFactory.decodeResource(getResources(),
        //                                                       R.mipmap.ic_launcher))
        //            .setSmallIcon(R.mipmap.ic_launcher)
        //            .setContentTitle("中文")
        //            .setContentText("中文")
        //            .setAutoCancel(true)
        //            .setDefaults(Notification.DEFAULT_ALL)
        //            .setContentIntent(pendingIntent)
        //            .setFullScreenIntent(pendingIntent, true);
        //    notificationManager.notify(4, builder.build());
        //    new MyFragment().show(getSupportFragmentManager(), "");
        //    startActivity(new Intent(this, ImageViewerActivity.class));
        //    System.setProperty("http.keepAlive", "false");
//        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        GlideUrl url =
//                new GlideUrl("http://admin.zpjuxingyuan"
//                        + ".com/Public/live_img/2018-05-11/5af50b4de0fc0.jpg",
//                        () -> {
//                            HashMap<String, String> map = new HashMap<>();
//                            //      map.put("Connection", "close");
//                            //      map.put("Content-type", "image/jpeg");
//                            //      map.put("Accept-Encoding", "");
//                            return map;
//                        });
        //    Glide.with(this).load("http://admin.zpjuxingyuan" +
        //            ".com/Public/live_img/2018-05-11/5af50b4de0fc0.jpg").crossFade().skipMemoryCache
        //            (true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.ic_launcher)
        //            //            .listener(new RequestListener<GlideUrl,
        //            GlideDrawable>() {
        //            //              @Override
        //            //              public boolean onException(Exception e,
        //            GlideUrl model,
        //            // Target<GlideDrawable>
        //            //                      target, boolean isFirstResource) {
        //            //                return false;
        //            //              }
        //            //
        //            //              @Override
        //            //              public boolean onResourceReady(GlideDrawable
        //            resource, GlideUrl
        //            // model, Target<GlideDrawable> target, boolean
        //            isFromMemoryCache, boolean
        //            // isFirstResource) {
        //            //                return false;
        //            //              }
        //            //            })
        //            .into(imageView);
    }

//    public static class MyFragment extends BottomSheetDialogFragment {
//
//        private BottomSheetBehavior mBehavior;
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            BottomSheetDialog dialog =
//                    (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//            View view = View.inflate(getContext(), R.layout.activity_main, null);
//            dialog.setContentView(view);
//            mBehavior = BottomSheetBehavior.from((View) view.getParent());
//            return dialog;
//        }
//    }
//
//    private void uri() {
//        Log.d("url", "url");
//        Log.d("url", getUri("https://www.baidu.com").toString());
//        Log.d("url", getUri("//www.baidu.com/").toString());
//        Log.d("url", getUri("/123.js").toString());
//    }
//
//    private Uri getUri(String url) {
//        Uri srcUri = Uri.parse("https://www.baidu.com/db.js");
//        if (srcUri != null) {
//            if (url.startsWith("//")) {
//                url = srcUri.getScheme() + ':' + url;
//            } else if (url.startsWith("/")) {
//                String srcUrl = srcUri.toString();
//                url = srcUrl.substring(0, srcUrl.lastIndexOf("/")) + url;
//            }
//        }
//        return Uri.parse(url);
//    }
}
