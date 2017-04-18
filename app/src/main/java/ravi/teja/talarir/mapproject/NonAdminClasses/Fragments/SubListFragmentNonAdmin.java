package ravi.teja.talarir.mapproject.NonAdminClasses.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ravi.teja.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import ravi.teja.talarir.mapproject.LoginActivity;
import ravi.teja.talarir.mapproject.NonAdminClasses.MapActivty;
import ravi.teja.talarir.mapproject.NonAdminClasses.NonAdminSubActivity;
import ravi.teja.talarir.mapproject.NonAdminClasses.SubDialogToActivityInterface;
import ravi.teja.talarir.mapproject.NonAdminClasses.SubMapActivity;
import ravi.teja.talarir.mapproject.NonAdminClasses.SubMapDialogue;
import ravi.teja.talarir.mapproject.R;
import ravi.teja.talarir.mapproject.Upload;

/**
 * Created by talarir on 16/04/2017.
 */

public class SubListFragmentNonAdmin extends Fragment implements SubDialogToActivityInterface
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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.sub_list_fragment_non_admin,container,false);
        mainGroupSelected=getArguments().getString("MainListToSubListKey");
        initializeView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTheMainGroupSelectedKey();
    }
    private void initializeView(View view)
    {
        rec= (RecyclerView) view.findViewById(R.id.subNonAdminRecycler);
        rec.setHasFixedSize(true);
        rec.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),2));
    }
    private void getTheMainGroupSelectedKey()
    {
//        mainGroupSelected=getIntent().getStringExtra("MainSelectedStringNonActivity");
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
                new RecyclerItemClickListener(getActivity().getApplicationContext(), rec ,new RecyclerItemClickListener.OnItemClickListener() {
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
                            Toast.makeText(getActivity().getApplicationContext(),"Try logging in!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position)
                    {
                        // do whatever
                        Toast.makeText(getActivity().getApplicationContext(),"sub lo long",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity().getApplicationContext(),"Starting Map Activity",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity().getApplicationContext(),MapActivty.class));
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
                        Intent subMapActivity=new Intent(getActivity().getApplicationContext(),SubMapActivity.class);
                        subMapActivity.putExtra("MainGroupIntentString",mainGroupSelectedKey);
                        subMapActivity.putExtra("SubGroupIntentString",subGroupSelectedKey);
                        startActivity(subMapActivity);
                    }
                    else if (counter==0)
                    {
                        //the dialog fragment must be non-cancellabe
                        FragmentManager subDialogFragmentManager=getChildFragmentManager();
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
                    FragmentManager subDialogFragmentManager=getChildFragmentManager();
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
                    Toast.makeText(getActivity().getApplicationContext(),"ERROR!!",Toast.LENGTH_SHORT).show();
                }
                cancelConnectionSubString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    @Override
    public void checkAcceptDecline(Boolean flag)
    {
        if (flag)
        {
            Toast.makeText(getActivity().getApplicationContext(),"true",Toast.LENGTH_SHORT).show();
            addToTheSubscriptionList();
            Intent subMapActivity=new Intent(getActivity().getApplicationContext(),SubMapActivity.class);
            subMapActivity.putExtra("MainGroupIntentString",mainGroupSelectedKey);
            subMapActivity.putExtra("SubGroupIntentString",subGroupSelectedKey);
            startActivity(subMapActivity);


        }
        else if (!flag)
        {
            Toast.makeText(getActivity().getApplicationContext(),"false",Toast.LENGTH_SHORT).show();
        }
    }

}
