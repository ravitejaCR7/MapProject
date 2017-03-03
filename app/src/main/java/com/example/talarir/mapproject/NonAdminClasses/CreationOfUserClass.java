package com.example.talarir.mapproject.NonAdminClasses;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by talarir on 24/02/2017.
 */

public class CreationOfUserClass {
    private Map<String,GetSetLocation> retrieveGetSetLocation;
    private Map<String,GetSetLocation> getSetLocationMap;
    public Context context;
    public CreationOfUserClass(Context context)
    {
        this.context=context;
        getSetLocationMap=new HashMap<String, GetSetLocation>();
        retrieveGetSetLocation= new HashMap<String , GetSetLocation>();
    }

    public void createUsers(FirebaseUser user, DatabaseReference mFirebaseDatabase, FirebaseDatabase mFirebaseInstance)
    {
        getSetLocationMap.put(user.getUid()+"1",new GetSetLocation(1100,1100));
        getSetLocationMap.put(user.getUid()+"2",new GetSetLocation(1110,1110));
        getSetLocationMap.put(user.getUid()+"3",new GetSetLocation(1120,1120));
        getSetLocationMap.put(user.getUid()+"4",new GetSetLocation(1130,1130));
        fireDataInput(getSetLocationMap,mFirebaseDatabase,mFirebaseInstance);
    }

    public void fireDataInput(Map<String, GetSetLocation> getSetLocationMap, DatabaseReference mFirebaseDatabase, FirebaseDatabase mFirebaseInstance)
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        mFirebaseDatabase.setValue(getSetLocationMap);
    }

    public void retrieveUserDataFromFireBase(DatabaseReference mFirebaseDatabase, FirebaseDatabase mFirebaseInstance)
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    retrieveGetSetLocation.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(GetSetLocation.class));
                }
                populateDataRetrieved(retrieveGetSetLocation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void populateDataRetrieved(Map<String, GetSetLocation> retrieveGetSetLocation)
    {
        if (retrieveGetSetLocation.size()>0)
        {
            for (Map.Entry<String,GetSetLocation> entry : this.retrieveGetSetLocation.entrySet())
            {
                String key=entry.getKey();
                GetSetLocation getSetLocation=entry.getValue();
                int lati=getSetLocation.getLat();
                int lngu=getSetLocation.getLng();
                Toast.makeText(context.getApplicationContext(),"key : "+key+"\n"+"lat and lng :"+lati+"  "+lngu,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class GetSetLocation
    {
        public int lat;
        public int lng;

        public GetSetLocation()
        {

        }

        public GetSetLocation(int lat,int lng)
        {
            this.lat=lat;
            this.lng=lng;
        }
        public int getLat()
        {
            return lat;
        }
        public int getLng()
        {
            return lng;
        }
    }
}
