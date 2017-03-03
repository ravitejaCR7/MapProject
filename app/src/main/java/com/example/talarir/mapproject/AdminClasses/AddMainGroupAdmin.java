package com.example.talarir.mapproject.AdminClasses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMainGroupAdmin extends AppCompatActivity implements View.OnClickListener
{

    private Button saveToFireBaseButton;
    private EditText theMainGroupNameEditText;
    private RecyclerView recyclerMainView;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_main_group_admin);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");
        saveToFireBaseButton= (Button) findViewById(R.id.btnAddMainGroupAdmin);
        theMainGroupNameEditText= (EditText) findViewById(R.id.editTextMainGroup);
        recyclerMainView= (RecyclerView) findViewById(R.id.recyclerViewAdminMain);
        recyclerMainView.setHasFixedSize(true);
        recyclerMainView.setLayoutManager(new LinearLayoutManager(this));

        saveToFireBaseButton.setOnClickListener(this);


    }

    public static class RecyclerMainGroupViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerMainGroupViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String mainGroupName)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewMainGroupAdmin);
            textView.setText(mainGroupName);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<String,RecyclerMainGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<String, RecyclerMainGroupViewHolder>(
                String.class,
                R.layout.each_main_element_admin,
                RecyclerMainGroupViewHolder.class,
                mFirebaseDatabase
        ) {
            @Override
            protected void populateViewHolder(RecyclerMainGroupViewHolder viewHolder, String model, int position)
            {

                viewHolder.setMainGroupName(model);

            }
        };

        recyclerMainView.setAdapter(firebaseRecyclerAdapter);

        recyclerMainView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerMainView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        // do whatever
                        TextView tv = (TextView) view.findViewById(R.id.textViewMainGroupAdmin);
                        Toast.makeText(getApplicationContext(),"simple "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                        Intent subCategoryIntent= new Intent(getApplicationContext(),AddSubGroupAdmin.class);
                        subCategoryIntent.putExtra("MainSelectedString",tv.getText().toString());
                        startActivity(subCategoryIntent);
                    }

                    @Override public void onLongItemClick(View view, int position)
                    {
                        // do whatever
                        Toast.makeText(getApplicationContext(),"long",Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }

    public void createNewMainGroup(String theMainGroupNameEditText)
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");

        mFirebaseDatabase.push().setValue(theMainGroupNameEditText.toLowerCase());

    }

    @Override
    public void onClick(View v)
    {
        if (v.getId()==R.id.btnAddMainGroupAdmin )
        {
            if (theMainGroupNameEditText.getText().toString().length()>0 )
            {
                    getTheMainListForUniqueness(theMainGroupNameEditText.getText().toString());
            }
            else
            {
                theMainGroupNameEditText.setError("can't be null");
            }
        }
    }

    private void getTheMainListForUniqueness(final String s)
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
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
                        theMainGroupNameEditText.setError("not unique!");
                    }
                    else
                    {
                        createNewMainGroup(theMainGroupNameEditText.getText().toString());
                        theMainGroupNameEditText.setText("");
                    }
                }
                else
                {
                    createNewMainGroup(theMainGroupNameEditText.getText().toString());
                    theMainGroupNameEditText.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
