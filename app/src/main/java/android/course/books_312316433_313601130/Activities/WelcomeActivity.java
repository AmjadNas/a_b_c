package android.course.books_312316433_313601130.Activities;

import android.content.Intent;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }


    public void LaunchJohn(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "jSmith");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();
    }

    public void LaunchShelly(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "aNas");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();
    }

    public void LaunchArthur(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "aNas");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();
    }

    public void LaunchJenna(View view) {
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.USERNAME, "aNas");
        intent.putExtra(Constants.PASSWORD, "123456");
        startActivity(intent);
        finish();
    }
}
