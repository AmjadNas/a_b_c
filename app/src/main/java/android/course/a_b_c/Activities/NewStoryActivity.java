package android.course.a_b_c.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Objects.Story;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.LoadImageAsync;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class NewStoryActivity extends AppCompatActivity implements LoadImageAsync.OnImageResized {
    private static final int PICK_IMAGE = 111;

    private Spinner spinner, rateSpinner;
    private String[] tags;
    private TextInputLayout title, synopsis;
    private TextView categories, genres, tViewTags;
    private ImageView cover;
    private Bitmap image;
    private String[] chars;
    private Story story;
    private TextView tvCharacters;
    private boolean isfinished;
    private Switch finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);

        String title = getIntent().getStringExtra(Constants.TITLE);
        if (title != null)
            story = DataHandler.getInstance().getStoryByTitle(title);

        initViews();
        initSpinner();
    }

    private void initViews() {
        title = (TextInputLayout) findViewById(R.id.edt_st_title);
        synopsis = (TextInputLayout) findViewById(R.id.edt_st_synopsis);
        cover = (ImageView) findViewById(R.id.edt_st_cover);
        categories = (TextView) findViewById(R.id.edt_st_categories);
        genres = (TextView) findViewById(R.id.edt_st_genres);
        tViewTags = (TextView) findViewById(R.id.edt_st_tags);
        tvCharacters = (TextView) findViewById(R.id.edt_st_characters);
        finished = (Switch) findViewById(R.id.finished_st_sw);

        if (story != null){
            title.getEditText().setEnabled(false);
            title.getEditText().setText(story.getTitle());
            synopsis.getEditText().setText(story.getSynopsis());
            genres.setText(story.getGenreString());
            categories.setText(story.getCategoryString());
            tViewTags.setText(story.getTagsString());
            tags = new String[story.getTags().size()];
            int i = 0;
            for (String s : story.getTags()) {
                tags[i] = s;
                i++;
            }
            tvCharacters.setText(story.getCharactersString());
            chars = new String[story.getCharacters().size()];
            i = 0;
            for (String s : story.getCharacters()) {
                chars[i] = s;
                i++;
            }
            image = DataHandler.getInstance().getStoryCover(story.getTitle());
            cover.setImageBitmap(image);
            isfinished = story.isStatus();

        }

    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner_st_lang);
        rateSpinner = (Spinner) findViewById(R.id.spinner_st_rate);

        String[] list = getResources().getStringArray(R.array.languages);
        String[] rateList = getResources().getStringArray(R.array.ratings);

        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list));
        rateSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, rateList));

        if (story != null){
            int i = 0;
            for (String s : list){
                if (s.equals(story.getLanguage())){
                    spinner.setSelection(i);
                    break;
                }
                i++;
            }

            i = 0;
            for (String s : rateList){
                if (s.equals(story.getRated())){
                    rateSpinner.setSelection(i);
                    break;
                }
                i++;
            }
        }
    }

    public void getImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectpick)), PICK_IMAGE);
    }

    public void submit(View view) {

        String txtLanguage = (String) spinner.getSelectedItem();
        String txtSynopsis = synopsis.getEditText().getText().toString();
        String txtTitles = title.getEditText().getText().toString();
        String txtcateGories = categories.getText().toString();
        String txtGenre = genres.getText().toString();
        String txtRate = (String)  rateSpinner.getSelectedItem();
        boolean a = txtGenre.equals(getString(R.string.click_here_to_add));
        boolean b = txtcateGories.equals(getString(R.string.click_here_to_add));
        isfinished = finished.isChecked();

        if (story != null && image != null && tags != null && !txtSynopsis.isEmpty() && !a && !b
                && chars != null){
            if (DataHandler.getInstance().updateStory(story.getTitle(), txtSynopsis, txtGenre, txtcateGories,
                    txtLanguage,txtRate, image, tags, chars, isfinished)) {
                Toast.makeText(this, getString(R.string.itemupdated), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra(Constants.STORY_TITLE, txtTitles);
                setResult(RESULT_OK, i);
                finish();
            }else {
                Toast.makeText(this, getString(R.string.itemnotupdated), Toast.LENGTH_SHORT).show();
            }
        }else if (image != null && tags != null && !txtTitles.isEmpty() && !txtSynopsis.isEmpty() && !a && !b
                && chars != null){
            if (DataHandler.getInstance().addStory(txtTitles, txtSynopsis, txtGenre, txtcateGories,
                        txtLanguage,txtRate, image, tags, chars, isfinished)) {
                Toast.makeText(this, getString(R.string.itemupdated), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra(Constants.STORY_TITLE, txtTitles);
                setResult(RESULT_OK, i);
                finish();
            }else
                Toast.makeText(this, getString(R.string.itemnotupdated), Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(this, getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();

    }

    public void openTagsDialog(View view) {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.tags_dialog, null);
        final EditText editText = (EditText)v.findViewById(R.id.diag_tags_add);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String str = editText.getEditableText().toString();
                        tags = str.split(", ");
                        tViewTags.setText(str);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
        .setMessage(R.string.addtags);
         builder.create().show();
    }

    public void openCharssDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.tags_dialog, null);
        final EditText editText = (EditText)v.findViewById(R.id.diag_tags_add);
        builder.setView(v)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String str = editText.getEditableText().toString();
                        chars = str.split(", ");
                        tvCharacters.setText(str);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setMessage(R.string.addcharacters);
        builder.create().show();
    }

    public void getGenres(View view) {
        launchActivity(Constants.CHECKBOX_VIEW_TYPE, R.array.storyGenre, Constants.GENRE_LIST);
    }

    public void addCategories(View view) {
        launchActivity(Constants.CHECKBOX_VIEW_TYPE, R.array.storyCategory, Constants.CATEGORY_LIST);
    }

    private void launchActivity(int viewType, int resource, int request){
        Intent intent = new Intent(this, ViewListActivity.class);
        intent.putExtra(Constants.VIEW_TYPE, viewType);
        intent.putExtra(Constants.RESOURCE, resource);
        startActivityForResult(intent, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (data != null) {
                if (requestCode == Constants.CATEGORY_LIST && !data.getStringExtra(Constants.RESULT).isEmpty())
                    categories.setText(data.getStringExtra(Constants.RESULT));
                else if (requestCode == Constants.GENRE_LIST && !data.getStringExtra(Constants.RESULT).isEmpty())
                    genres.setText(data.getStringExtra(Constants.RESULT));
                else if(requestCode == PICK_IMAGE){
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
    }


    @Override
    public void ImageResized(Bitmap o) {
        image = o;
        cover.setImageBitmap(image);
    }

    @Override
    public void ErrorResizingImage() {
        Toast.makeText(this, getString(R.string.failedImage), Toast.LENGTH_SHORT).show();
    }
}
