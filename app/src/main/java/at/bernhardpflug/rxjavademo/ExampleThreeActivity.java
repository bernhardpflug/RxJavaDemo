package at.bernhardpflug.rxjavademo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.regex.Pattern;

import bernhardpflug.at.rxjavademo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * RxBindings for Android Widgets
 * From https://www.ykode.com/2015/02/20/android-frp-rxjava-retrolambda.html and
 * https://www.ykode.com/2015/10/22/android-rxjava-rxandroid-memory-leak.html
 */
public class ExampleThreeActivity extends AppCompatActivity {

    @Bind(R.id.email)
    EditText email;

    @Bind(R.id.password)
    EditText password;

    @Bind(R.id.email_sign_in_button)
    Button signIn;

    private CompositeSubscription compositeSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_three);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //we have to take care of all subscriptions to un-subscribe them when the activity gets destroyed, otherwise we gonna create memory leaks
        //as subscriptions hold references to views
        compositeSubs = new CompositeSubscription();

        final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        //RxBindings provides bindings for most Android Widgets
        //We use the map operator to convert the given text to a boolean whether it matches the pattern and set the email text color accordingly
        Observable<Boolean> emailValid = RxTextView.textChanges(email).map(s -> emailPattern.matcher(s).matches());
        //we use the distinctUntilChanged method to avoid firing events for every letter but only if validity changes
        Subscription emailSub = emailValid.distinctUntilChanged().subscribe(b -> email.setTextColor(b ? Color.BLACK : Color.RED));

        compositeSubs.add(emailSub);

        /* We do the same for the password depending whether it has more than six characters
           For comparison here is usual android code for it

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setTextColor(charSequence.length() > 6 ? Color.BLACK : Color.RED);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        */
        Observable<Boolean> passwordValid = RxTextView.textChanges(password).map(s -> s.length() > 6);
        Subscription passwordSub = passwordValid.distinctUntilChanged().subscribe(b -> password.setTextColor(b ? Color.BLACK : Color.RED));

        compositeSubs.add(passwordSub);

        //finally we want to enable the register button only if both fields are valid
        //Therefore we use the combineLatest method which merges an observable sequence into one observable and omits whenever any of them produces an element
        Subscription registerSub = Observable.combineLatest(emailValid, passwordValid, (a, b) -> a && b).distinctUntilChanged().subscribe(b -> signIn.setEnabled(b));

        compositeSubs.add(registerSub);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //we un-subscribe all subscriptions to release all references to views etc.
        compositeSubs.unsubscribe();
    }
}
