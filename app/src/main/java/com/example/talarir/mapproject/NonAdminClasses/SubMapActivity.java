package com.example.talarir.mapproject.NonAdminClasses;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.R;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;

public class SubMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference mFirebaseDatabaseUserIds;
    private FirebaseDatabase mFirebaseInstanceUserIds;

    private DatabaseReference mFirebaseDatabaseUserIdsLocationRetrieval;
    private FirebaseDatabase mFirebaseInstanceUserIdsLocationRetrieval;

    private ValueEventListener xUserIdsList, xUserIdsRetrievalList;

    private String mainKey, subKey;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private GoogleMap mMap;

    private ArrayList<LatLng> latLngArrayList;
    private ArrayList<String> userIdsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_map);
        mainKey = getIntent().getStringExtra("MainGroupIntentString");
        subKey = getIntent().getStringExtra("SubGroupIntentString");

        latLngArrayList = new ArrayList<LatLng>();
        userIdsList = new ArrayList<String>();

        mFirebaseInstanceUserIds = FirebaseDatabase.getInstance();
        mFirebaseDatabaseUserIds = mFirebaseInstanceUserIds.getReference("SubscriptionList/" + mainKey + "/" + subKey);

        setUpMap();
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.subMap)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;

        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getTheUserIds();

    }


    private void getTheUserIds()
    {
              xUserIdsList=mFirebaseDatabaseUserIds.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot)
                  {
                      for (DataSnapshot childNode : dataSnapshot.getChildren())
                      {
                          userIdsList.add(String.valueOf(childNode.getValue()));
                      }

                      if (userIdsList!=null&&userIdsList.size()>0)
                      {
                          populateTheLatLngArrayList();
                      }

                      cancelConnectionUserList();
                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {

                  }
              });
    }

    private void populateTheLatLngArrayList()
    {
        mFirebaseInstanceUserIdsLocationRetrieval = FirebaseDatabase.getInstance();
        mFirebaseDatabaseUserIdsLocationRetrieval = mFirebaseInstanceUserIdsLocationRetrieval.getReference("UsersLocation");
        xUserIdsRetrievalList=mFirebaseDatabaseUserIdsLocationRetrieval.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot userLocationsNodes : dataSnapshot.getChildren())
                {
                    for (int i=0;i<userIdsList.size();i++)
                    {
                        if (userLocationsNodes.getKey().equals(userIdsList.get(i)))
                        {
                            UserLocationClass userLocationObj =  userLocationsNodes.getValue(UserLocationClass.class);
                            Double lat= Double.valueOf(userLocationObj.getmLatitude());
                            Double lng=Double.valueOf(userLocationObj.getmLongitude());
                            latLngArrayList.add(new LatLng(lat,lng));
                        }
                    }
                }
                if (latLngArrayList.size()>0)
                {
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(latLngArrayList)
                            .build();
                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                }

                cancelConnectionUserRetrievalList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cancelConnectionUserList()
    {
        mFirebaseDatabaseUserIds.removeEventListener(xUserIdsList);
    }
    private void cancelConnectionUserRetrievalList()
    {
        mFirebaseDatabaseUserIdsLocationRetrieval.removeEventListener(xUserIdsRetrievalList);
    }

    public static class UserLocationClass
    {
        String mLatitude,mLongitude;
        public UserLocationClass()
        {

        }
        public UserLocationClass(String mLatitude,String mLongitude)
        {
            this.mLatitude=mLatitude;
            this.mLongitude=mLongitude;
        }

        public String getmLatitude()
        {
            return mLatitude;
        }

        public void setmLatitude(String mLatitude)
        {
            this.mLatitude = mLatitude;
        }

        public String getmLongitude()
        {
            return mLongitude;
        }

        public void setmLongitude(String mLongitude)
        {
            this.mLongitude = mLongitude;
        }
    }
}
