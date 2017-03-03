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

public class AddSubGroupAdmin extends AppCompatActivity implements AdapterView.OnItemSelectedListener,InputSubGroupDialogClass.CommunicateWithMainMethod {

    public Map<String,String> subGroupMap;
    public List<String> keys = new ArrayList<String>();
    public List<String> values=new ArrayList<String>();
    private Spinner spinner;
    private Boolean bool;
    public String mainCategorySelectedString;
    public String subCategoryEnteredString;

    private DatabaseReference mFirebaseDatabaseSub;
    private FirebaseDatabase mFirebaseInstanceSub;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_group_admin);

        spinner= (Spinner) findViewById(R.id.spinner);
        subGroupMap=new HashMap<String, String>();
        createNewSubGroup();
    }

    public void createNewSubGroup()//remember this function
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("SubGroup");
        getTheMainGroupListForSubList();
    }

    public void getTheMainGroupListForSubList()
    {
        final Map<String,String> localMapVariable= new HashMap<String, String>();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    localMapVariable.put(dataSnapshot1.getKey(), String.valueOf(dataSnapshot1.getValue()));
                }
                populateTheCreateNewSubGroup(localMapVariable);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void populateTheCreateNewSubGroup(Map<String, String> localMapVariable)
    {
        subGroupMap=localMapVariable;
        if (subGroupMap.size()>0)
        {
            for (Map.Entry<String,String> entry : subGroupMap.entrySet())
            {
                String mainKey=entry.getKey();
                String mainValue=entry.getValue();
                keys.add(mainKey);
                values.add(mainValue);
                //Toast.makeText(getApplicationContext(),"key : "+mainKey+"\n"+"value :"+mainValue,Toast.LENGTH_SHORT).show();
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keys);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        mainCategorySelectedString = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), "Selected: " + mainCategorySelectedString, Toast.LENGTH_LONG).show();

        FragmentManager fragmentManager=getFragmentManager();
        InputSubGroupDialogClass inputSubGroupDialogClass=new InputSubGroupDialogClass();
        inputSubGroupDialogClass.show(fragmentManager,"SubDialog");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void sendTheSubCategoryToAdminActivity(String subCategoryName)
    {
        subCategoryEnteredString=subCategoryName;
        createTheMainList();
        if (!subCategoryName.equals("haha"))
        {
            createTheMainAndSubGroupFromAdminSide(mainCategorySelectedString.toLowerCase(),subCategoryEnteredString.toLowerCase());
        }
    }
    private void createTheMainList()
    {
        mFirebaseInstanceSub= FirebaseDatabase.getInstance();
        mFirebaseDatabaseSub= mFirebaseInstanceSub.getReference("MainGroup");
        mFirebaseDatabaseSub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getChildrenCount()<=0)
                {
                    Toast.makeText(getApplicationContext(),"no main",Toast.LENGTH_SHORT).show();
                    bool=false;
                }
                else
                {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        mFirebaseDatabase = mFirebaseInstance.getReference("MainAll/"+dataSnapshot1.getKey().toLowerCase());
                        //mFirebaseDatabase.setValue("null");
                    }
                    bool=true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
    private void createTheMainAndSubGroupFromAdminSide(String mainCategorySelectedString, String subCategoryEnteredString)
    {
        if (bool)
        {
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseDatabase = mFirebaseInstance.getReference("MainAll/"+mainCategorySelectedString);
            mFirebaseDatabase.child(subCategoryEnteredString).setValue("null");
        }
    }

}
