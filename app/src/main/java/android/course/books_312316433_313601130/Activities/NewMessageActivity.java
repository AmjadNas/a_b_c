package android.course.books_312316433_313601130.Activities;


import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Utils.Constants;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class NewMessageActivity extends AppCompatActivity {
    private TextInputLayout username, sjubject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String prof = getIntent().getStringExtra(Constants.USERNAME);

        username = (TextInputLayout) findViewById(R.id.edt_user_message);
        if (prof != null)
            username.getEditText().setText(prof);

        sjubject = (TextInputLayout) findViewById(R.id.edt_subject_message);
        message = (TextInputLayout) findViewById(R.id.edt_message);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void postMessage(View view) {
        String txtUsername = username.getEditText().getText().toString();
        String txtSubject = sjubject.getEditText().getText().toString();
        String txtMessage = message.getEditText().getText().toString();

        if (!txtUsername.isEmpty() && !txtSubject.isEmpty() && !txtMessage.isEmpty() ){

            if (DataHandler.getInstance().sendMessage(txtUsername, txtSubject, txtMessage)){
                Toast.makeText(this, getString(R.string.messageSent), Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }else
                Toast.makeText(this, getString(R.string.failedToSaveMessage) ,Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(this, getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();

    }
}
