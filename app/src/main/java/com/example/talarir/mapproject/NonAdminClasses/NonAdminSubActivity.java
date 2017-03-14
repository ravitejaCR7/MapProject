package com.example.talarir.mapproject.NonAdminClasses;

import android.content.Intent;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import com.example.talarir.mapproject.LoginActivity;
import com.example.talarir.mapproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NonAdminSubActivity extends AppCompatActivity implements SubDialogToActivityInterface
{

    RecyclerView rec;

    private String mainGroupSelected;
    private static String mainGroupSelectedKey;

    private ValueEventListener xSub,xSub1;

    FirebaseUser user;

    private DatabaseReference mFirebaseDatabaseNonAdminMain;
    private FirebaseDatabase mFirebaseInstanceNonAdminMain;

    private DatabaseReference mFirebaseDatabaseSub1;
    private FirebaseDatabase mFirebaseInstanceSub1;

    private DatabaseReference mFirebaseDatabaseForUser;
    private FirebaseDatabase mFirebaseInstanceForUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_sub);


        initializeView();
        getTheMainGroupSelectedKey();

    }

    private void getTheMainGroupSelectedKey()
    {
        mainGroupSelected=getIntent().getStringExtra("MainSelectedStringNonActivity");
        mFirebaseInstanceNonAdminMain = FirebaseDatabase.getInstance();
        mFirebaseDatabaseNonAdminMain = mFirebaseInstanceNonAdminMain.getReference("MainGroup");
        xSub=mFirebaseDatabaseNonAdminMain.addValueEventListener(new ValueEventListener()
        {
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

        rec.setAdapter(firebaseRecyclerAdapter);

        rec.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rec ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        // do whatever
                        TextView tv = (TextView) view.findViewById(R.id.textViewSubGroupAdminRecyclerList);
                        Toast.makeText(getApplicationContext(),"sub lo "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user!=null)
                        {
                            checkForUser(user.getUid());
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Try logging in!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position)
                    {
                        // do whatever
                        Toast.makeText(getApplicationContext(),"sub lo long",Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }

    private void checkForUser(final String uid)
    {

        mFirebaseInstanceForUser = FirebaseDatabase.getInstance();
        mFirebaseDatabaseForUser = mFirebaseInstanceForUser.getReference("UsersLocation");
        xSub1=mFirebaseDatabaseForUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int counter=0;

                for (DataSnapshot userIdChild : dataSnapshot.getChildren())
                {
                    if (uid.equals(userIdChild.getKey()))
                    {
                        counter++;
                    }
                }
                if (counter>0)
                {
                    //user present so call the main MAP activity
                    Toast.makeText(getApplicationContext(),"user already exists",Toast.LENGTH_SHORT).show();
                    //the dialog fragment must be non-cancellabe
                    FragmentManager subDialogFragmentManager=getFragmentManager();
                    SubMapDialogue subMapDialogue=new SubMapDialogue();
                    subMapDialogue.show(subDialogFragmentManager,"Dialog Fragment");
                    //if selected accept in dialog fragment
                    // Intent subMapGroupIntent=new Intent(getApplicationContext(),SubMapActivity.class);
                    //if selected decline, then just dismiss()***

                }
                else if (counter == 0)
                {
                    //user not present so call the SUB MAP activity to get his location
                    Toast.makeText(getApplicationContext(),"Starting Map Activity",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MapActivty.class));
                }
                cancelConnection1();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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



    private void cancelConnection()
    {
        mFirebaseDatabaseNonAdminMain.removeEventListener(xSub);
    }
    private void cancelConnection1()
    {
        mFirebaseDatabaseNonAdminMain.removeEventListener(xSub1);
    }

    private void initializeView()
    {
        rec= (RecyclerView) findViewById(R.id.subNonAdminRecycler);
        rec.setHasFixedSize(true);
        rec.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void checkAcceptDecline(Boolean flag)
    {
        if (flag)
        {
            Toast.makeText(this,"true",Toast.LENGTH_SHORT).show();

        }
        else if (!flag)
        {
            Toast.makeText(this,"false",Toast.LENGTH_SHORT).show();
        }
    }
}
