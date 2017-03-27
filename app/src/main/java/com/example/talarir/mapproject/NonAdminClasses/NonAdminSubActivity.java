package com.example.talarir.mapproject.NonAdminClasses;

import android.content.Intent;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import com.example.talarir.mapproject.LoginActivity;
import com.example.talarir.mapproject.R;
import com.example.talarir.mapproject.Upload;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NonAdminSubActivity extends AppCompatActivity implements SubDialogToActivityInterface
{

    private RecyclerView rec;

    private String mainGroupSelected;
    private static String mainGroupSelectedKey,subGroupSelectedKey;

    private ValueEventListener xMain,xSub1,xSub,xSubscription;

    private FirebaseUser user;

    private DatabaseReference mFirebaseDatabaseNonAdminMain;
    private FirebaseDatabase mFirebaseInstanceNonAdminMain;

    private DatabaseReference mFirebaseDatabaseNonAdminSub;
    private FirebaseDatabase mFirebaseInstanceNonAdminSub;

    private DatabaseReference mFirebaseDatabaseSub1;
    private FirebaseDatabase mFirebaseInstanceSub1;

    private DatabaseReference mFirebaseDatabaseForUser;
    private FirebaseDatabase mFirebaseInstanceForUser;

    private DatabaseReference mFirebaseDatabaseSubscription;
    private FirebaseDatabase mFirebaseInstanceSubscription;

    private DatabaseReference mFirebaseDatabaseSubscriptionAddition;
    private FirebaseDatabase mFirebaseInstanceSubscriptionAddition;



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
        xMain=mFirebaseDatabaseNonAdminMain.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Upload retrievingSubUploadObject = dataSnapshot1.getValue(Upload.class);
                    if (mainGroupSelected.equals(retrievingSubUploadObject.getName()))
                    {
                        //Toast.makeText(getApplicationContext(),"key : "+dataSnapshot1.getKey()+"   value : "+dataSnapshot1.getValue(),Toast.LENGTH_SHORT).show();
                        mainGroupSelectedKey=dataSnapshot1.getKey();
                    }
                }
                if (mainGroupSelectedKey!=null)
                {
                    recyclerPopulate();
                }
                cancelConnectionMainString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void getTheSubGroupSelectedKey(final String subViewSelectedString)
    {
        mFirebaseInstanceNonAdminSub = FirebaseDatabase.getInstance();
        mFirebaseDatabaseNonAdminSub = mFirebaseInstanceNonAdminSub.getReference("SubGroup/"+mainGroupSelectedKey);
        xSub=mFirebaseDatabaseNonAdminSub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                   for (DataSnapshot list : dataSnapshot.getChildren())
                   {
                       Upload u = list.getValue(Upload.class);
                       if (subViewSelectedString.equals(u.getName()))
                       {
                           subGroupSelectedKey=list.getKey();
                       }
                   }
                   if (subGroupSelectedKey==null)
                   {
                       Toast.makeText(getApplicationContext(),"ERROR!!",Toast.LENGTH_SHORT).show();
                   }
                   cancelConnectionSubString();
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

        FirebaseRecyclerAdapter<Upload,RecyclerSubGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Upload, RecyclerSubGroupViewHolder>(
                Upload.class,
                R.layout.each_sub_element_admin,
                RecyclerSubGroupViewHolder.class,
                mFirebaseDatabaseSub1
        ) {
            @Override
            protected void populateViewHolder(RecyclerSubGroupViewHolder viewHolder, Upload  model, int position)
            {

                viewHolder.setMainGroupName(model.getName(),model.getUrl());

            }
        };

        rec.setAdapter(firebaseRecyclerAdapter);

        rec.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rec ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        // do whatever
                        TextView tv = (TextView) view.findViewById(R.id.textViewSubGroupAdminRecyclerList);
                        //Toast.makeText(getApplicationContext(),"sub lo "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user!=null)
                        {
                            getTheSubGroupSelectedKey(tv.getText().toString());
                            //this checking must be done prior to populating the main category list (but first check that GPS error)
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
                    //Toast.makeText(getApplicationContext(),"user already exists",Toast.LENGTH_SHORT).show();
                    walkThroughSubscriptionList();


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

    private void walkThroughSubscriptionList()
    {
        mFirebaseInstanceSubscription = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSubscription = mFirebaseInstanceSubscription.getReference("SubscriptionList/"+mainGroupSelectedKey+"/"+subGroupSelectedKey);
        xSubscription=mFirebaseDatabaseSubscription.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int counter=0;

                if (dataSnapshot.getChildrenCount()>0)
                {
                    for (DataSnapshot childList : dataSnapshot.getChildren())
                    {
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childList.getValue()))
                        {
                            counter++;
                        }
                    }
                    if (counter>0)
                    {
                        Intent subMapActivity=new Intent(getApplicationContext(),SubMapActivity.class);
                        subMapActivity.putExtra("MainGroupIntentString",mainGroupSelectedKey);
                        subMapActivity.putExtra("SubGroupIntentString",subGroupSelectedKey);
                        startActivity(subMapActivity);
                    }
                    else if (counter==0)
                    {
                        //the dialog fragment must be non-cancellabe
                        FragmentManager subDialogFragmentManager=getFragmentManager();
                        SubMapDialogue subMapDialogue=new SubMapDialogue();
                        subMapDialogue.show(subDialogFragmentManager,"Dialog Fragment");
                        //if selected accept in dialog fragment
                        // Intent subMapGroupIntent=new Intent(getApplicationContext(),SubMapActivity.class);
                        //if selected decline, then just dismiss()***
                    }

                }
                else
                {
                    //the dialog fragment must be non-cancellabe
                    FragmentManager subDialogFragmentManager=getFragmentManager();
                    SubMapDialogue subMapDialogue=new SubMapDialogue();
                    subMapDialogue.show(subDialogFragmentManager,"Dialog Fragment");
                    //if selected accept in dialog fragment
                    // Intent subMapGroupIntent=new Intent(getApplicationContext(),SubMapActivity.class);
                    //if selected decline, then just dismiss()***
                }

                cancelConnectionSubscription();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addToTheSubscriptionList()
    {
        mFirebaseInstanceSubscriptionAddition = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSubscriptionAddition = mFirebaseInstanceSubscriptionAddition.getReference("SubscriptionList/"+mainGroupSelectedKey+"/"+subGroupSelectedKey);
        mFirebaseDatabaseSubscriptionAddition.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    public static class RecyclerSubGroupViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerSubGroupViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String subGroupName,String subGroupImageUri)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewSubGroupAdminRecyclerList);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageViewSubGroupAdminRecyclerList);
            textView.setText(subGroupName);
            Glide.with(mView.getContext()).load(subGroupImageUri).into(imageView);
        }
    }



    private void cancelConnectionMainString()
    {
        mFirebaseDatabaseNonAdminMain.removeEventListener(xMain);
    }
    private void cancelConnection1()
    {
        mFirebaseDatabaseForUser.removeEventListener(xSub1);
    }
    private void cancelConnectionSubscription()
    {
        mFirebaseDatabaseSubscription.removeEventListener(xSubscription);
    }
    private void cancelConnectionSubString()
    {
        mFirebaseDatabaseNonAdminSub.removeEventListener(xSub);
    }

    private void initializeView()
    {
        rec= (RecyclerView) findViewById(R.id.subNonAdminRecycler);
        rec.setHasFixedSize(true);
        rec.setLayoutManager(new GridLayoutManager(this,2));
    }

    @Override
    public void checkAcceptDecline(Boolean flag)
    {
        if (flag)
        {
            Toast.makeText(this,"true",Toast.LENGTH_SHORT).show();
            addToTheSubscriptionList();
            Intent subMapActivity=new Intent(getApplicationContext(),SubMapActivity.class);
            subMapActivity.putExtra("MainGroupIntentString",mainGroupSelectedKey);
            subMapActivity.putExtra("SubGroupIntentString",subGroupSelectedKey);
            startActivity(subMapActivity);


        }
        else if (!flag)
        {
            Toast.makeText(this,"false",Toast.LENGTH_SHORT).show();
        }
    }
}
