package at.bernhardpflug.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bernhardpflug.at.rxjavademo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Using lambda expressions to reduce boilerplate code
 */
public class ExampleTwoActivity extends AppCompatActivity {

    @Bind(R.id.textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_two);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<String> names = Arrays.asList("Didiet", "Doni", "Asep", "Reza", "Sari", "Rendi", "Akbar");

        //we can create observables from iterables, arrays and futures
        Observable<String> listObservable = Observable.from(names)
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());

        /*we can use a huge set of methods to manipulate the input stream before passing it to the subscriber
        
        instead of needing this:
        listObservable.map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.toUpperCase();
            }
        });
        we can use lambda expressions and reduce it to...
        */
        listObservable = listObservable.map(s -> s.toUpperCase());

        //using lambda expressions reduces the boilerplate code
        listObservable.subscribe((s -> textView.setText(textView.getText() + "on " + Thread.currentThread().getName() + ": " + s + "\n")));
    }
}
