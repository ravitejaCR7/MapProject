package com.example.talarir.mapproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class NonAdminACtivity extends AppCompatActivity
{

    RecyclerView nonAdminMainRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_activity);

        initializeView();


    }

    private void initializeView()
    {
        nonAdminMainRecyclerView = (RecyclerView) findViewById(R.id.nonAdminRecyclerViewMainGroup);
    }
}
