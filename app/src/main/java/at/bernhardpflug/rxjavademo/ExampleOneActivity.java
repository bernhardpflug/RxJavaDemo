package at.bernhardpflug.rxjavademo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;

import bernhardpflug.at.rxjavademo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Create and subscribe to an observer
 */
public class ExampleOneActivity extends AppCompatActivity {

    @Bind(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_one);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //create simple observer that emits strings
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                String text = "Welcome to Rx Java";
                try {
                    for (int i = 0; i <= text.length(); i++) {
                        Thread.sleep(300);
                        subscriber.onNext(text.substring(0, i));
                    }
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
        });

        //by default all code runs synchronously on the main thread, but we want to run the logic
        //in the background but emit the results to the UI thread
        observable = observable
                .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                .observeOn(AndroidSchedulers.mainThread());

        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onCompleted() {
                textView.setText(textView.getText() + "!");
            }

            @Override
            public void onError(Throwable e) {
                //print stacktrace to textview
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                textView.setText(sw.toString());
            }

            @Override
            public void onNext(String s) {
                textView.setText(s);
            }
        };

        observable.subscribe(subscriber);
    }
}
