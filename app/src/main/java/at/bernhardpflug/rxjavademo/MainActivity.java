package at.bernhardpflug.rxjavademo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
        startActivity(new Intent(this,ExampleTwoActivity.class));
    }
}