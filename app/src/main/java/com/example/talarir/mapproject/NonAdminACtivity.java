package com.example.talarir.mapproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.NonAdminClasses.CreationOfUserClass;
import com.example.talarir.mapproject.NonAdminClasses.RecyclerDataGetSetCLass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NonAdminACtivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public Button saveLocationBtn,getLocationBtn;
    public RecyclerView nonAdminRecyclerView;

    private Boolean flag=false;
    public static int i;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private DatabaseReference mFirebaseDatabase1;
    private FirebaseDatabase mFirebaseInstance1;

    private DatabaseReference mFirebaseDatabaseTest;
    private FirebaseDatabase mFirebaseInstanceTest;
    private ArrayList<RecyclerDataGetSetCLass> arrayListTest;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_activity);


        arrayListTest=new ArrayList<RecyclerDataGetSetCLass>();

        testing();

        mFirebaseInstance1 = FirebaseDatabase.getInstance();
        mFirebaseDatabase1 = mFirebaseInstance1.getReference("RecyclerTest");

        nonAdminRecyclerView= (RecyclerView) findViewById(R.id.recycler_view_fragment_one);
        nonAdminRecyclerView.setHasFixedSize(true);
        nonAdminRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    flag=true;
                    Log.d("CooActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // User is signed out
                    Log.d("CooActivity", "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();

                }

            }
        };
    }

    private void testing()
    {
        mFirebaseInstanceTest = FirebaseDatabase.getInstance();
        mFirebaseDatabaseTest = mFirebaseInstanceTest.getReference("MainGroup");
        mFirebaseDatabaseTest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot da : dataSnapshot.getChildren())
                {
                    arrayListTest.add(new RecyclerDataGetSetCLass(da.getKey()));
                }
                testingCreateMethod(arrayListTest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void testingCreateMethod(ArrayList<RecyclerDataGetSetCLass> arrayListTest)
    {
        mFirebaseInstanceTest = FirebaseDatabase.getInstance();
        mFirebaseDatabaseTest = mFirebaseInstanceTest.getReference("RecyclerTest");
        for (int i=0;i<arrayListTest.size();i++)
        {
            mFirebaseDatabaseTest.child(String.valueOf(i)).setValue(arrayListTest.get(i));
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


        saveLocationBtn= (Button) findViewById(R.id.saveLocationOfUser);
        saveLocationBtn.setOnClickListener(this);
        getLocationBtn= (Button) findViewById(R.id.getLocationOfUser);
        getLocationBtn.setOnClickListener(this);

        FirebaseRecyclerAdapter<RecyclerDataGetSetCLass,RecyclerDataGetSetClassViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<RecyclerDataGetSetCLass, RecyclerDataGetSetClassViewHolder>(
                RecyclerDataGetSetCLass.class,
                R.layout.each_list_non_admin,
                RecyclerDataGetSetClassViewHolder.class,
                mFirebaseDatabase1
        ) {
            @Override
            protected void populateViewHolder(RecyclerDataGetSetClassViewHolder viewHolder, RecyclerDataGetSetCLass model, int position)
            {

                    viewHolder.setMainGroupName(model.getMainGroupName());

            }
        };

        nonAdminRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class RecyclerDataGetSetClassViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerDataGetSetClassViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String mainGroupName)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewRecyclerViewNonAdmin);
            textView.setText(mainGroupName);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId()==R.id.saveLocationOfUser)
        {
            if (flag)
            {
                saveUserLocation_NonAdminActivity();
            }
            else
            {
                Toast.makeText(this,"firebase not ready",Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId()==R.id.getLocationOfUser)
        {
            retrieveUserLocation_NonAdminActivity();
        }
    }

    private void retrieveUserLocation_NonAdminActivity()
    {
        CreationOfUserClass creationOfUserClass=new CreationOfUserClass(this);
        creationOfUserClass.retrieveUserDataFromFireBase(mFirebaseDatabase,mFirebaseInstance);
    }

    private void saveUserLocation_NonAdminActivity()
    {

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        final CreationOfUserClass creationOfUserClass=new CreationOfUserClass(this);
        creationOfUserClass.createUsers(user, mFirebaseDatabase,mFirebaseInstance);
    }
}
