package android.course.a_b_c.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.Network.NetworkResListener;
import android.course.a_b_c.Network.ResStatus;
import android.course.a_b_c.Objects.ActivityEvent;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

public class NewEventActivity extends AppCompatActivity {
    private TextInputLayout post;
    private ActivityEvent ae;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        post = (TextInputLayout) findViewById(R.id.edt_act);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void postActivity(View view) {
        String txtPost = post.getEditText().getText().toString();

        if (!txtPost.isEmpty()){
            long id = DataHandler.getInstance().postActivity(txtPost);
            if (id > -1){
                Toast.makeText(this, getString(R.string.activityPosted), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(Constants.ID, id);
                setResult(RESULT_OK, intent);
                finish();
            }else
                Toast.makeText(this, getString(R.string.activityNotPosted), Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
    }
}
