package at.bernhardpflug.rxjavademo.four;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bernhardpflug.at.rxjavademo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Create and subscribe to an observer
 */
public class ExampleFourActivity extends AppCompatActivity {

    @Bind(R.id.country)
    AutoCompleteTextView countryTextView;

    private CompositeSubscription compositeSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_four);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        compositeSubs = new CompositeSubscription();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restcountries.eu")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        RestCountriesService api = retrofit.create(RestCountriesService.class);

//        Subscription subscription = approachOne(api);
        Subscription subscription = approachTwo(api);

        compositeSubs.add(subscription);
    }

    private Subscription approachOne(RestCountriesService api) {

        return RxTextView.textChanges(countryTextView).debounce(2, TimeUnit.SECONDS).subscribe(s -> {

            Log.d("test", "firing request for " + s + "...");

            api.getCountries(s.toString())
                    .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(countries -> {

                        ArrayAdapter<Country> countryAdapter = new ArrayAdapter<Country>(ExampleFourActivity.this, android.R.layout.simple_list_item_1, countries);
                        countryTextView.setAdapter(countryAdapter);

                    }, error -> {

                        //print stacktrace to textview
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        error.printStackTrace(pw);
                        pw.flush();
                        Log.e("test", sw.toString());

                        countryTextView.setAdapter(null);

                    }, () -> Log.i("test", "onCompleted"));
        });
    }

    /**
     * We use flat map to chain observables. The result string of textChanges is used as input for the subsequent api call
     * As nested observables stop emitting if an error occurs we have to handle that separately
     *
     * @param api
     * @return
     */
    private Subscription approachTwo(RestCountriesService api) {

        return RxTextView.textChanges(countryTextView)
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(s -> api.getCountries(s.toString())
                                .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                                .observeOn(AndroidSchedulers.mainThread())
                                        //as errors are not propagated we want to handle them here
                                .doOnError(t -> {
                                    log(t);

                                    countryTextView.setAdapter(null);
                                })
                                        //If nested observable fails it stops emitting events; As we don't want that we catch the error and swallow it
                                .onErrorResumeNext(throwable -> Observable.<List<Country>>empty())
                )
                .subscribe(countries -> {

                    Log.d("test", "Got countries");
                    ArrayAdapter<Country> countryAdapter = new ArrayAdapter<Country>(ExampleFourActivity.this, android.R.layout.simple_list_item_1, countries);
                    countryTextView.setAdapter(countryAdapter);

                }, error -> {

                    //as errors are swallowed we never get in here
                }, () -> Log.i("test", "onCompleted"));
    }

    private void log(Throwable throwable) {
        //print stacktrace to textview
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        Log.e("test", sw.toString());
    }

    @Override
    protected void onStop() {
        compositeSubs.unsubscribe();
        super.onStop();
    }
}