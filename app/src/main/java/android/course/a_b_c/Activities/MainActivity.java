package android.course.a_b_c.Activities;

import android.content.Intent;
import android.course.a_b_c.DatabaseHandkers.DataHandler;
import android.course.a_b_c.Fragments.TabbedFragment;
import android.course.a_b_c.Network.NetworkConnector;
import android.course.a_b_c.R;
import android.course.a_b_c.Services.NotifyierService;
import android.course.a_b_c.Utils.Constants;
import android.course.a_b_c.Utils.DataLoadingListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, DataLoadingListener {

   // private FrameLayout frame;
    private DataHandler dataHandler;
    private Intent intent;
    private int precID;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataHandler = DataHandler.getInstance();
        dataHandler.initDataBase(this);
        NetworkConnector.getInstance().initialize(getApplicationContext());
        intent = getIntent();
        if (dataHandler.initUser() == null) {
            String username = intent.getStringExtra(Constants.USERNAME);

            if(username != null )
                dataHandler.initUser(username);
        }

        if (dataHandler.getUser() != null) {
            //frame = (FrameLayout) findViewById(R.id.frag_container);
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, TabbedFragment.newInstance(R.id.navigation_home))
                    .commit();
           // navigation.setSelectedItemId(R.id.navigation_home);
            navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            intent = new Intent(this, NotifyierService.class);
            startService(intent);
        }else {
            launchActivity(WelcomeActivity.class, null);
            finish();
        }
    }

    private void launchActivity(Class<?> cls, String username) {
        if (username != null) {
            intent = new Intent(this, cls);
            intent.putExtra(Constants.USERNAME, username);
        }else
            intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.prof_main_mn:
                launchActivity(ProfileActivity.class, dataHandler.getUser().getUsername());
                return true;
            case R.id.search_main_mn:
                launchActivity(SearchActivity.class, null);
                return true;
            case R.id.logOut:
                DataHandler.getInstance().logOut();
                DataHandler.getInstance().closeDataBase();
                launchActivity(WelcomeActivity.class, null);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item){
        if (precID != item.getItemId()) {
            getSupportActionBar().setTitle(item.getTitle());
            replaceFragment(TabbedFragment.newInstance(item.getItemId()));
            precID = item.getItemId();
            return true;
        }
        return false;

    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<Fragment> fs = getSupportFragmentManager().getFragments();
            Fragment f = fs.get(fs.size()-1);

            f.onActivityResult(requestCode, resultCode, data);

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHandler.closeDataBase();
    }

    @Override
    public void onLoadingStart() {
        ProgressBar progressBar = toolbar.findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

    }

    @Override
    public void onnLoadingDone() {
        ProgressBar progressBar = toolbar.findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.GONE);


    }
}
