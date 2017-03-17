package com.example.talarir.mapproject.NonAdminClasses;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;

public class SubMapActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private ValueEventListener xMain;

    private String mainKey,subKey;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private GoogleMap mMap;

    public static ArrayList<LatLng> latLngArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_map);
        mainKey=getIntent().getStringExtra("MainGroupIntentString");
        subKey=getIntent().getStringExtra("SubGroupIntentString");

        latLngArrayList=new ArrayList<LatLng>();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("SubscriptionList/"+mainKey+"/"+subKey);
        getTheUserIds();

    }

    private void getTheUserIds()
    {

    }

    private void cancelConnection1()
    {
        mFirebaseDatabase.removeEventListener(xMain);
    }
}
