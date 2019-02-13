package android.course.books.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.course.books.DatabaseHandkers.DataHandler;
import android.course.books.Objects.User;
import android.course.books.R;
import android.course.books.Utils.LoadImageAsync;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity implements LoadImageAsync.OnImageResized {

    private final static int IMAGE_CAPTUE_REQ = 1;
    private static final int PICK_IMAGE = 111;
    private Bitmap img;
    private ImageView imageView;
    private EditText fname,lname,email;
    private User user;
    private DataHandler data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        data = DataHandler.getInstance();
        user = data.getUser();
        bindViews();
        initViews();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindViews() {
        imageView = (ImageView)findViewById(R.id.prof_img);
        fname = (EditText) findViewById(R.id.edit_prof_fname);
        lname = (EditText) findViewById(R.id.edit_prof_lname);
        email = (EditText) findViewById(R.id.edit_prof_email);
    }

    /**
     * launch cam or get photo from gallery
     * @param view
     */
    public void onClickCam(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pickone)
                .setItems(R.array.choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.selectpick)), PICK_IMAGE);
                        }else if (which == 1){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,IMAGE_CAPTUE_REQ);
                        }
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     *  initialize the views
     */
    private void initViews(){
        fname.setText(user.getFirstName());
        lname.setText(user.getLastName());
        email.setText(user.getEmail());
        if (user.getThumb() != null)
            imageView.setImageBitmap(user.getThumb());
    }

    /**
     * save the
     * @param view
     */
    public void saveProf(View view) {
        String uFname = fname.getText().toString(),
                uLname = lname.getText().toString(),
                uEmail = email.getText().toString();

        if(!uFname.isEmpty() && !uLname.isEmpty() && !uEmail.isEmpty() ){
            if (data.saveProfChanges(uFname, uLname, uEmail, img)){
                Toast.makeText(this, R.string.itemupdated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }else {
                Toast.makeText(this, R.string.itemnotupdated, Toast.LENGTH_SHORT).show();
            }
            finish();
        }else {
            Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * handle photo capture
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_CAPTUE_REQ ) { // if image was captured from camera
                // restore photo from intent
                new LoadImageAsync(this).execute((Bitmap) data.getExtras().get("data"));

            }else if (requestCode == PICK_IMAGE){  // if image was chosen from gallery

                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    // get image using content resolver
                    imageStream = getContentResolver().openInputStream(selectedImage);
                    new LoadImageAsync(this).execute(BitmapFactory.decodeStream(imageStream));

                } catch (FileNotFoundException e) {
                    Log.e("ERROR:","Loading file failed");
                }
            }


        }
    }

    @Override
    public void ImageResized(Bitmap o) {
        img = o;
        imageView.setImageBitmap(img);
    }

    @Override
    public void ErrorResizingImage() {
        Toast.makeText(this, getString(R.string.failedImage), Toast.LENGTH_SHORT).show();
    }
}
