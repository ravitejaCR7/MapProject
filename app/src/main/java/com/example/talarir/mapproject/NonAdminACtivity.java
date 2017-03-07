package com.example.talarir.mapproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NonAdminACtivity extends AppCompatActivity
{

    RecyclerView nonAdminMainRecyclerView;
    FrameLayout frameLayout;

    private DatabaseReference mFirebaseDatabaseNonAdmin;
    private FirebaseDatabase mFirebaseInstanceNonAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_activity);


        initializeView();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // User is signed in
            checkThePresentUsers(user.getUid());

        }
        else
        {
            // No user is signed in
            Toast.makeText(this,"Sorry. Try logging in",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
        }

    }

    private void initializeView()
    {
        nonAdminMainRecyclerView = (RecyclerView) findViewById(R.id.nonAdminRecyclerViewMainGroup);
        frameLayout= (FrameLayout) findViewById(R.id.frameLayoutNonAdmin);
    }

    private void checkThePresentUsers(final String uid)
    {

        mFirebaseInstanceNonAdmin = FirebaseDatabase.getInstance();
        mFirebaseDatabaseNonAdmin = mFirebaseInstanceNonAdmin.getReference("UsersList");
        mFirebaseDatabaseNonAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getChildrenCount()<=0)
                {
                    addUserToFirebase(uid);
                }
                if (dataSnapshot.getChildrenCount()>=0)
                {
                    int counter=0;
                    for (DataSnapshot datasnap1 : dataSnapshot.getChildren())
                    {
                        if (uid.equals(datasnap1.getKey()))
                        {
                            counter++;
                        }
                    }
                    if (counter>0)
                    {
                        showTheNonAdminRecyclerMainList();
                    }
                    else if (counter==0)
                    {
                        addUserToFirebase(uid);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showTheNonAdminRecyclerMainList()
    {

    }

    private void addUserToFirebase(String userId)
    {
        showFrameLayout(frameLayout);
        takeTheLocationInputs();
        hideFrameLayout(frameLayout);
    }

    private void hideFrameLayout(FrameLayout frameLayout)
    {
        frameLayout.setVisibility(View.GONE);
    }

    private void takeTheLocationInputs()
    {

    }

    private void showFrameLayout(FrameLayout frameLayout)
    {
        frameLayout.setVisibility(View.VISIBLE);
    }
}
