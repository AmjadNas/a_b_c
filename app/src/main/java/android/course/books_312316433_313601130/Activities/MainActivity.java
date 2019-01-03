package android.course.books_312316433_313601130.Activities;

import android.content.Intent;
import android.course.books_312316433_313601130.DatabaseHandkers.DataHandler;
import android.course.books_312316433_313601130.Fragments.Refreshable;
import android.course.books_312316433_313601130.Fragments.TabbedFragment;
import android.course.books_312316433_313601130.Network.NetworkConnector;
import android.course.books_312316433_313601130.R;
import android.course.books_312316433_313601130.Services.NotifyierService;
import android.course.books_312316433_313601130.Utils.Constants;
import android.course.books_312316433_313601130.Utils.DataLoadingListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Stack;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, DataLoadingListener {

   // private FrameLayout frame;
    private DataHandler dataHandler;
    private Intent intent;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Stack<Integer> ids;


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
            ids = new Stack<>();
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, TabbedFragment.newInstance(R.id.navigation_home))
                    .commit();

            ids.push(R.id.navigation_home);

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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ids.pop();
            int id = ids.peek();
            navigation.getMenu().findItem(id).setChecked(true);
            getSupportActionBar().setTitle(navigation.getMenu().findItem(id).getTitle());
        }
        super.onBackPressed();
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
                intent = new Intent(this, NotifyierService.class);
                stopService(intent);
                DataHandler.getInstance().logOut();
                DataHandler.getInstance().closeDataBase();
                launchActivity(WelcomeActivity.class, null);
                finish();
                return true;

                case R.id.action_regresh:
                    ((Refreshable)getSupportFragmentManager().getFragments().get(0)).refresh();
                    return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item){

        if (ids.peek() != item.getItemId()) {
            replaceFragment(TabbedFragment.newInstance(item.getItemId()));
            getSupportActionBar().setTitle(item.getTitle());
            ids.push(item.getItemId());
            return true;
       }
        return false;

    }

    private void replaceFragment(Fragment fragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frag_container, fragment);
        transaction.addToBackStack("Frag");
        transaction.commit();
        final int count = fragmentManager.getBackStackEntryCount();


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
