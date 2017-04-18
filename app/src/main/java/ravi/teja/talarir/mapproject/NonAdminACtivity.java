package ravi.teja.talarir.mapproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import ravi.teja.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import ravi.teja.talarir.mapproject.NonAdminClasses.Fragments.MainListFragmentNonAdmin;
import ravi.teja.talarir.mapproject.NonAdminClasses.Interfaces.MainListInterface;
import ravi.teja.talarir.mapproject.NonAdminClasses.NonAdminSubActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class NonAdminACtivity extends AppCompatActivity implements MainListInterface
{
    private ProgressDialog progressDialog;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_name);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // initializing navigation menu
        setUpNavigationView();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarFunctions();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void setUpNavigationView()
    {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbaractionbarToggle to drawer layout
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void collapsingToolbarFunctions()
    {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener
                (
                    new NavigationView.OnNavigationItemSelectedListener()
                    {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem)
                        {
                            switch (menuItem.getItemId()) {
                                //Replacing the main content with ContentFragment Which is our Inbox View;
                                case R.id.nav_home:
                                    Toast.makeText(getApplicationContext(),"home",Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.nav_profile:
                                    Toast.makeText(getApplicationContext(),"profile",Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.nav_logout:
                                    Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(),"home",Toast.LENGTH_SHORT).show();
                            }

                            //Checking if the item is in checked state or not, if not make it in checked state
                            if (menuItem.isChecked()) {
                                menuItem.setChecked(false);
                            } else {
                                menuItem.setChecked(true);
                            }
                            menuItem.setChecked(true);


                            return true;
                        }
                    }
                );
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        MainListFragmentNonAdmin mainList= new MainListFragmentNonAdmin();
        ft.replace(R.id.fragmentContainer, mainList);
        ft.commit();
    }

    public void showProgressBar()
    {
        progressDialog = new ProgressDialog(NonAdminACtivity.this,
                R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Please wait :) ");
        progressDialog.show();

    }


    public void cancelProgressBar()
    {
        progressDialog.dismiss();

    }

//    @Override
//    public void onBackPressed()
//    {
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
//        {
//            mDrawerLayout.closeDrawers();
//            return;
//        }
//    }

}


