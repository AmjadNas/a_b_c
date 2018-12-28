package android.course.a_b_c.Activities;

import android.content.Intent;
import android.course.a_b_c.Adapters.ProfileFragmentsAdapter;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.Network.NetworkResListener;
import android.course.a_b_c.Network.ResStatus;
import android.course.a_b_c.Objects.User;
import android.course.a_b_c.R;
import android.course.a_b_c.Utils.Constants;
import android.graphics.Bitmap;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements NetworkResListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TextView name, follow;
    private ImageView imageView;
    private TabLayout tabLayout;
    private DataHandler dataHandler;
    private User user;
    private boolean isUser, isFollowing;
    private ProfileFragmentsAdapter mSectionsPagerAdapter;
    private String username;

    private final int REQUESTEDITPROF = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bindViews();

        dataHandler = DataHandler.getInstance();
        username = getIntent().getStringExtra(Constants.USERNAME);
        initUser();
        initViews();
        initCollapsingToolbar();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ProfileFragmentsAdapter(this, getSupportFragmentManager(), username);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // set up tab layout according to viewpager
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void initUser() {
        User u = dataHandler.getUser();

        if (u.equals(new User(username))) {
            user = u;
            isUser = true;
        }else {
            user = dataHandler.getUser(username);
            if (dataHandler.isFollowing(username)) {
                follow.setText(getString(R.string.unfollow));
                isFollowing = true;
            }
        }

        NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_USER_FOLLOWERS, user, this);
    }

    private void bindViews() {
        name = (TextView) findViewById(R.id.txt_profileName);
        follow = (TextView) findViewById(R.id.followBtn);
        imageView = (ImageView) findViewById(R.id.backdrop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isUser)
            getMenuInflater().inflate(R.menu.menu_user_items, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent prof = new Intent(this, EditProfileActivity.class);
            startActivityForResult(prof, REQUESTEDITPROF);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (isUser)
            follow.setVisibility(View.GONE);

        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(user.getFullName());
                    name.setVisibility(View.INVISIBLE);
                    if (!isUser)
                        follow.setVisibility(View.INVISIBLE);
                    isShow = true;

                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    name.setVisibility(View.VISIBLE);
                    if (!isUser)
                        follow.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    /**
     * bind data to the views
     */
    private void initViews(){
        name.setText(user.getFullName());
        if(user.getThumb() != null){
            imageView.setImageBitmap(user.getThumb());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUESTEDITPROF ) {
                initViews();
            }
        }
    }

    public void followUser(View view) {
        if (!isFollowing) {
            if (dataHandler.followUser(username)) {
                follow.setText(getString(R.string.unfollow));
                isFollowing = true;
            }
        }else {
            if (dataHandler.unFollowUser(username)){
                follow.setText(getString(R.string.follow));
                isFollowing = false;
            }
        }
    }

    public void viewFollowers(View view) {
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra(Constants.USERNAME, username);
        startActivity(intent);
    }

    @Override
    public void onPreUpdate() {

    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        TextView fs = (TextView)findViewById(R.id.followers_txt_prof);
        int i = 0;
        if (status == ResStatus.SUCCESS){
            i =  DataHandler.getInstance().parseStringList(res, Constants.FOLLOWERS).size();
        }else {
             i = dataHandler.getUserFollowers(username).size();
        }
        String s = getString(R.string.followers) + i;
        fs.setText(s);
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }
}
