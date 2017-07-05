package ravi.teja.talarir.mapproject.NonAdminClasses.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ravi.teja.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import ravi.teja.talarir.mapproject.NonAdminACtivity;
import ravi.teja.talarir.mapproject.NonAdminClasses.Interfaces.MainListInterface;
import ravi.teja.talarir.mapproject.NonAdminClasses.NonAdminSubActivity;
import ravi.teja.talarir.mapproject.R;
import ravi.teja.talarir.mapproject.Upload;

/**
 * Created by talarir on 14/04/2017.
 */

public class MainListFragmentNonAdmin extends Fragment
{
    RecyclerView nonAdminMainRecyclerView;

    private MainListInterface mainListInterface;

    private DatabaseReference mFirebaseDatabaseNonAdmin;
    private FirebaseDatabase mFirebaseInstanceNonAdmin;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v= inflater.inflate(R.layout.main_list_fragment_non_admin,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initializeView();
        mFirebaseInstanceNonAdmin = FirebaseDatabase.getInstance();
        mFirebaseDatabaseNonAdmin = mFirebaseInstanceNonAdmin.getReference("MainGroup");
        mainListInterface=(MainListInterface)getActivity();


        mainListInterface.showProgressBar();

        FirebaseRecyclerAdapter<Upload,RecyclerMainGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Upload, RecyclerMainGroupViewHolder>(
                Upload.class,
                R.layout.each_main_element_admin,
                RecyclerMainGroupViewHolder.class,
                mFirebaseDatabaseNonAdmin
        ) {
            @Override
            protected void populateViewHolder(RecyclerMainGroupViewHolder viewHolder, Upload model, int position)
            {

                viewHolder.setMainGroupName(model.getName(),model.getUrl());
            }


        };

        nonAdminMainRecyclerView.setAdapter(firebaseRecyclerAdapter);

        nonAdminMainRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mainListInterface.cancelProgressBar();
                nonAdminMainRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }

        });


        nonAdminMainRecyclerView.addOnItemTouchListener
                (
                        new RecyclerItemClickListener(getActivity().getApplicationContext(), nonAdminMainRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position)
                            {
                                // do whatever
                                TextView tv = (TextView) view.findViewById(R.id.textViewMainGroupAdminRecyclerList);
//                                Toast.makeText(getApplicationContext(),"simple "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
//                                Intent subCategoryIntent= new Intent(getActivity().getApplicationContext(),NonAdminSubActivity.class);
//                                subCategoryIntent.putExtra("MainSelectedStringNonActivity",tv.getText().toString());
//                                startActivity(subCategoryIntent);


                                Bundle mainListToSubListBundle=new Bundle();
                                mainListToSubListBundle.putString("MainListToSubListKey",tv.getText().toString());
                                SubListFragmentNonAdmin subListFragmentNonAdmin=new SubListFragmentNonAdmin();
                                subListFragmentNonAdmin.setArguments(mainListToSubListBundle);

                                FragmentManager fragmentManagerFromMainToSubNonAdmin = getFragmentManager();
                                FragmentTransaction fragmentTransactionFromMainToSubNonAdmin = fragmentManagerFromMainToSubNonAdmin.beginTransaction();

                                fragmentTransactionFromMainToSubNonAdmin.replace(R.id.fragmentContainer, subListFragmentNonAdmin);
                                fragmentTransactionFromMainToSubNonAdmin.addToBackStack(null);
                                fragmentTransactionFromMainToSubNonAdmin.commit();

                            }

                            @Override public void onLongItemClick(View view, int position)
                            {
                                // do whatever
                            }
                        })
                );

    }

    private void initializeView()
    {
        nonAdminMainRecyclerView= (RecyclerView) getActivity().findViewById(R.id.recyclerViewNonAdminMain);
        nonAdminMainRecyclerView.setHasFixedSize(true);
        nonAdminMainRecyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),2));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static class RecyclerMainGroupViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerMainGroupViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String mainGroupName,String mainGroupImageUri)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewMainGroupAdminRecyclerList);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageViewMainGroupAdminRecyclerList);
            textView.setText(mainGroupName);
            Glide.with(mView.getContext()).load(mainGroupImageUri).into(imageView);
        }
    }

}
