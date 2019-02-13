package android.course.books.Activities;

import android.content.Intent;
import android.course.books.DatabaseHandkers.DataHandler;
import android.course.books.Objects.Chapter;
import android.course.books.R;
import android.course.books.Utils.Constants;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class NewChapterActivity extends AppCompatActivity {

    private TextInputLayout title, content;
    private String str;
    private String currentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        str = getIntent().getStringExtra(Constants.STORY_TITLE);
        currentTitle = getIntent().getStringExtra(Constants.TITLE);

        getSupportActionBar().setTitle(str + " New Chapter");
        initView();
    }

    private void initView() {
        title = (TextInputLayout) findViewById(R.id.edt_ch_title);
        content = (TextInputLayout) findViewById(R.id.edt_ch_synopsis);
        if (currentTitle != null) {
            Chapter c = DataHandler.getInstance().getChapter(str, currentTitle);
            title.getEditText().setText(currentTitle);
            title.getEditText().setEnabled(false);
            content.getEditText().setText(c.getLinesString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveChapter();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveChapter() {
        String txtTitle = title.getEditText().getText().toString();
        String txtContent = content.getEditText().getText().toString();
        // update chapter
        if (currentTitle != null && !txtContent.isEmpty()) {
            if (DataHandler.getInstance().updateChapter(str, currentTitle, txtContent)) {
                Toast.makeText(this, getString(R.string.itemupdated), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(Constants.TITLE, currentTitle);
                intent.putExtra(Constants.STORY_TITLE, str);
                setResult(RESULT_OK, intent);
                finish();
            } else
                Toast.makeText(this, getString(R.string.itemnotupdated), Toast.LENGTH_SHORT).show();
        } else if (!txtTitle.isEmpty() && !txtContent.isEmpty()) { // add new chapter

            if (DataHandler.getInstance().addChapterToStory(str, txtTitle, txtContent)) {
                Toast.makeText(this, getString(R.string.itemupdated), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(Constants.TITLE, txtTitle);
                intent.putExtra(Constants.STORY_TITLE, str);
                setResult(RESULT_OK, intent);
                finish();
            } else
                Toast.makeText(this, getString(R.string.itemnotupdated), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getString(R.string.emptyFields), Toast.LENGTH_SHORT).show();

    }
}
