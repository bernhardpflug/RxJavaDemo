package at.bernhardpflug.rxjavademo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import at.bernhardpflug.rxjavademo.four.ExampleFourActivity;
import bernhardpflug.at.rxjavademo.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.exampleOne)
    void onExampleOneClick() {
        startActivity(new Intent(this, ExampleOneActivity.class));
    }

    @OnClick(R.id.exampleTwo)
    void onExampleTwoClick() {
        startActivity(new Intent(this, ExampleTwoActivity.class));
    }

    @OnClick(R.id.exampleThree)
    void onExampleThreeClick() {
        startActivity(new Intent(this, ExampleThreeActivity.class));
    }

    @OnClick(R.id.exampleFour)
    void onExampleFourClick() {
        startActivity(new Intent(this, ExampleFourActivity.class));
    }
}
