package test.maps.app;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import test.maps.app.fragment.MapsFragment;

public class MyActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Home", "Android", "Sitemap", "About", "Contact Me"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        nitView();
        if (toolbar != null) {
            toolbar.setTitle("Navigation Drawer");
            setSupportActionBar(toolbar);
        }
        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                displayView(position);
            }
        });
        initDrawer();
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                if (fragment == null) {
                    fragment = new MapsFragment();
                }
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title
            leftDrawerList.setItemChecked(position, true);
            leftDrawerList.setSelection(position);
            toolbar.setTitle(leftSliderData[position]);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
        //close the drawer
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter=new ArrayAdapter<String>( MyActivity.this, android.R.layout.simple_list_item_1, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
    }
    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}