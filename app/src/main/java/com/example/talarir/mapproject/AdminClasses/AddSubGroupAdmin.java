package com.example.talarir.mapproject.AdminClasses;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.talarir.mapproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSubGroupAdmin extends AppCompatActivity
{
    String mainGroupSelected;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_group_admin);

        mainGroupSelected=getIntent().getStringExtra("MainSelectedString");
        Toast.makeText(this,"main : "+mainGroupSelected,Toast.LENGTH_SHORT).show();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");
        ValueEventListener x=mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if (mainGroupSelected.equals(dataSnapshot1.getValue()))
                    {
                        Toast.makeText(getApplicationContext(),"key : "+dataSnapshot1.getKey()+"   value : "+dataSnapshot1.getValue(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        //mFirebaseDatabase.removeEventListener(x);
    }
}
