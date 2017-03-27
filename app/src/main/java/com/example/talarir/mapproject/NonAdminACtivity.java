package com.example.talarir.mapproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import com.example.talarir.mapproject.NonAdminClasses.NonAdminSubActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class NonAdminACtivity extends AppCompatActivity
{

    RecyclerView nonAdminMainRecyclerView;

    private DatabaseReference mFirebaseDatabaseNonAdmin;
    private FirebaseDatabase mFirebaseInstanceNonAdmin;
    private StorageReference mStorageRefMainAdmin;

    private DrawerLayout mDrawerLayout;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_activity);

        mFirebaseInstanceNonAdmin = FirebaseDatabase.getInstance();
        mFirebaseDatabaseNonAdmin = mFirebaseInstanceNonAdmin.getReference("MainGroup");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_name);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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

        initializeView();
        //calls to recycler list
    }

    private void collapsingToolbarFunctions()
    {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void initializeView()
    {
        nonAdminMainRecyclerView= (RecyclerView) findViewById(R.id.recyclerViewNonAdminMain);
        nonAdminMainRecyclerView.setHasFixedSize(true);
        nonAdminMainRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Upload,RecyclerMainGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Upload, RecyclerMainGroupViewHolder>(
                Upload.class,
                R.layout.each_main_element_admin,
                RecyclerMainGroupViewHolder.class,
                mFirebaseDatabaseNonAdmin
        ) {
            @Override
            protected void populateViewHolder(RecyclerMainGroupViewHolder viewHolder, Upload model, int position)
            {

                viewHolder.setMainGroupName(model.getName(),model.getUrl());

            }
        };

        nonAdminMainRecyclerView.setAdapter(firebaseRecyclerAdapter);

        nonAdminMainRecyclerView.addOnItemTouchListener
                (
                        new RecyclerItemClickListener(getApplicationContext(), nonAdminMainRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position)
                            {
                                // do whatever
                                TextView tv = (TextView) view.findViewById(R.id.textViewMainGroupAdminRecyclerList);
                                Toast.makeText(getApplicationContext(),"simple "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                                Intent subCategoryIntent= new Intent(getApplicationContext(),NonAdminSubActivity.class);
                                subCategoryIntent.putExtra("MainSelectedStringNonActivity",tv.getText().toString());
                                startActivity(subCategoryIntent);
                            }

                            @Override public void onLongItemClick(View view, int position)
                            {
                                // do whatever
                                Toast.makeText(getApplicationContext(),"long",Toast.LENGTH_SHORT).show();
                            }
                        })
                );

    }

    public static class RecyclerMainGroupViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerMainGroupViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String mainGroupName,String mainGroupImageUri)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewMainGroupAdminRecyclerList);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageViewMainGroupAdminRecyclerList);
            textView.setText(mainGroupName);
            Glide.with(mView.getContext()).load(mainGroupImageUri).into(imageView);
        }
    }

}
