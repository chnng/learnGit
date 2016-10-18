//package ispeak.myapplication;
//
//
//import android.os.Bundle;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Action;
//import io.reactivex.functions.Consumer;
//
//import java.util.concurrent.Callable;
//import java.util.concurrent.TimeUnit;
//
//import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
//
//
//public class MainActivity extends AppCompatActivity
//{
//    private static final String TAG = "RxJava";
//    private Looper backgroundLooper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        BackgroundThread backgroundThread = new BackgroundThread();
//        backgroundThread.start();
//        backgroundLooper = backgroundThread.getLooper();
//
//        findViewById(R.id.button_run_scheduler).setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                onRunSchedulerExampleButtonClicked();
//            }
//        });
//    }
//
//    void onRunSchedulerExampleButtonClicked()
//    {
//        sampleObservable()
//                // Run on a background thread
//                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
//                // Be notified on the main thread
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>()
//                {
//                    @Override
//                    public void accept(String t) throws Exception
//                    {
//                        Log.d(TAG, "onNext(" + t + ")");
//                    }
//
//                }, new Consumer<Throwable>()
//                {
//
//                    @Override
//                    public void accept(Throwable t) throws Exception
//                    {
//                        Log.e(TAG, "onError()", t);
//                    }
//                }, new Action()
//                {
//                    @Override
//                    public void run() throws Exception
//                    {
//                        Log.e(TAG, "onComplete()");
//                    }
//                });
//    }
//
//    static Observable<String> sampleObservable()
//    {
//        return Observable.defer(new Callable<Observable<String>>()
//        {
//            @Override
//            public Observable<String> call()
//            {
//                // Do some long running operation
//                try
//                {
//                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
//                } catch (InterruptedException e)
//                {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                return Observable.just("one", "two", "three", "four", "five");
//            }
//        });
//    }
//
//    static class BackgroundThread extends HandlerThread
//    {
//        BackgroundThread()
//        {
//            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
//        }
//    }
//
//
//}
