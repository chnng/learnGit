package ispeak.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ispeak.myapplication.R;
import ispeak.myapplication.entity.Subject;
import ispeak.myapplication.http.HttpMethods;
import ispeak.myapplication.subscribers.ProgressSubscriber;
import ispeak.myapplication.subscribers.SubscriberOnNextListener;

public class MainActivity extends AppCompatActivity
{

    @BindView(R.id.click_me_BN)
    Button clickMeBN;
    @BindView(R.id.result_TV)
    TextView resultTV;

    private SubscriberOnNextListener getTopMovieOnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        System.currentTimeMillis();
        getTopMovieOnNext = new SubscriberOnNextListener<List<Subject>>()
        {
            @Override
            public void onNext(List<Subject> subjects)
            {
                resultTV.setText(subjects.toString());
            }
        };
    }

    @Override
    protected void onStart()
    {
        super.onStart();


    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @OnClick(R.id.click_me_BN)
    public void onClick()
    {
        getMovie();
    }

    //进行网络请求
    private void getMovie()
    {
        HttpMethods.getInstance().getTopMovie(new ProgressSubscriber(getTopMovieOnNext, MainActivity.this), 0, 10);
    }
}
