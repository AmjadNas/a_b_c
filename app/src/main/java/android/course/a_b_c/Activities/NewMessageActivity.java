package android.course.a_b_c.Activities;


import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class NewMessageActivity extends AppCompatActivity {
    private TextInputLayout username, sjubject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        String prof = getIntent().getStringExtra(Constants.USERNAME);

        username = (TextInputLayout) findViewById(R.id.edt_user_message);
        if (prof != null)
            username.getEditText().setText(prof);

        sjubject = (TextInputLayout) findViewById(R.id.edt_subject_message);
        message = (TextInputLayout) findViewById(R.id.edt_message);

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
