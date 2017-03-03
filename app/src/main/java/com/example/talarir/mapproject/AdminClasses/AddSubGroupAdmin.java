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


    private DatabaseReference mFirebaseDatabaseSub;
    private FirebaseDatabase mFirebaseInstanceSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_group_admin);

        //mFirebaseInstance = FirebaseDatabase.getInstance();
        //mFirebaseDatabase = mFirebaseInstance.getReference("SubGroup");
    }
}
