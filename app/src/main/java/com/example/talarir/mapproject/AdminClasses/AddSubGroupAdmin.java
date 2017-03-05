package com.example.talarir.mapproject.AdminClasses;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSubGroupAdmin extends AppCompatActivity implements View.OnClickListener
{
    private String mainGroupSelected;
    private static String mainGroupSelectedKey;

    private DatabaseReference mFirebaseDatabaseSub;
    private FirebaseDatabase mFirebaseInstanceSub;

    private DatabaseReference mFirebaseDatabaseSub1;
    private FirebaseDatabase mFirebaseInstanceSub1;

    ValueEventListener xSub;

    private EditText theSubGroupNameEditText;
    private Button saveToFireBaseButtonSub;
    private RecyclerView recyclerSubView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_group_admin);

        mainGroupSelectedKey=null;

        initializeViewInCreate();

        getTheMainGroupSelectedKey();
    }


    private void initializeViewInCreate()
    {
        theSubGroupNameEditText= (EditText) findViewById(R.id.editTextSubGroup);
        saveToFireBaseButtonSub= (Button) findViewById(R.id.btnAddSubGroupAdmin);
        recyclerSubView= (RecyclerView) findViewById(R.id.recyclerViewAdminSub);
        recyclerSubView.setHasFixedSize(true);
        recyclerSubView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getTheMainGroupSelectedKey()
    {

        mainGroupSelected=getIntent().getStringExtra("MainSelectedString");
        mFirebaseInstanceSub = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSub = mFirebaseInstanceSub.getReference("MainGroup");
        xSub=mFirebaseDatabaseSub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    if (mainGroupSelected.equals(dataSnapshot1.getValue()))
                    {
                        Toast.makeText(getApplicationContext(),"key : "+dataSnapshot1.getKey()+"   value : "+dataSnapshot1.getValue(),Toast.LENGTH_SHORT).show();
                        mainGroupSelectedKey=dataSnapshot1.getKey();
                    }
                }
                if (mainGroupSelectedKey!=null)
                {
                    setTheSaveButtonListener();
                    recyclerPopulate();
                }
                cancelConnection();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setTheSaveButtonListener()
    {
        saveToFireBaseButtonSub.setOnClickListener(this);
    }


    private void cancelConnection()
    {
        mFirebaseDatabaseSub.removeEventListener(xSub);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId()==R.id.btnAddSubGroupAdmin)
        {
            if (theSubGroupNameEditText.getText().toString().length()>0 && (!theSubGroupNameEditText.getText().toString().equals("")) && theSubGroupNameEditText.getText().toString()!=null)
            {
                if (mainGroupSelectedKey.length()<=0||mainGroupSelectedKey==null||mainGroupSelectedKey.equals(""))
                {
                    theSubGroupNameEditText.setError("haven't received the key yet!");
                }
                else
                {
                    getTheSubListForUniqueness(theSubGroupNameEditText.getText().toString().toLowerCase().trim());
                }
            }
            else
            {
                theSubGroupNameEditText.setError("can't be null");
            }

        }
    }

    private void getTheSubListForUniqueness(final String s)
    {
        mFirebaseInstanceSub = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSub = mFirebaseInstanceSub.getReference("SubGroup/"+mainGroupSelectedKey);
        xSub=mFirebaseDatabaseSub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int counter=0;
                if (dataSnapshot.getChildrenCount()>0)
                {
                    for ( DataSnapshot da : dataSnapshot.getChildren())
                    {
                        if (s.equals(da.getValue()))
                        {
                            counter++;
                        }
                    }
                    if (counter!=0)
                    {
                        theSubGroupNameEditText.setError("not unique!");
                    }
                    else
                    {
                        createNewSubGroup(s);
                        theSubGroupNameEditText.setText("");
                    }
                }
                else
                {
                    createNewSubGroup(s);
                    theSubGroupNameEditText.setText("");
                }

                cancelConnection();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void createNewSubGroup(String theSubGroupNameEditText)
    {
        mFirebaseInstanceSub = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSub = mFirebaseInstanceSub.getReference("SubGroup/"+mainGroupSelectedKey);

        if (theSubGroupNameEditText.equals("")||theSubGroupNameEditText.length()<=0)
        {
            Toast.makeText(getApplicationContext(),"woah",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mFirebaseDatabaseSub.push().setValue(theSubGroupNameEditText.toLowerCase());
        }

    }


    public void recyclerPopulate()
    {

        mFirebaseInstanceSub1 = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSub1 = mFirebaseInstanceSub1.getReference("SubGroup/"+mainGroupSelectedKey);

        FirebaseRecyclerAdapter<String,RecyclerSubGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<String, RecyclerSubGroupViewHolder>(
                String.class,
                R.layout.each_sub_element_admin,
                RecyclerSubGroupViewHolder.class,
                mFirebaseDatabaseSub1
        ) {
            @Override
            protected void populateViewHolder(RecyclerSubGroupViewHolder viewHolder, String model, int position)
            {

                viewHolder.setMainGroupName(model);

            }
        };

        recyclerSubView.setAdapter(firebaseRecyclerAdapter);

        recyclerSubView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerSubView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        // do whatever
                        TextView tv = (TextView) view.findViewById(R.id.textViewSubGroupAdminRecyclerList);
                        Toast.makeText(getApplicationContext(),"sub lo "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onLongItemClick(View view, int position)
                    {
                        // do whatever
                        Toast.makeText(getApplicationContext(),"sub lo long",Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }

    public static class RecyclerSubGroupViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerSubGroupViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String mainGroupName)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewSubGroupAdminRecyclerList);
            textView.setText(mainGroupName);
        }
    }
}
