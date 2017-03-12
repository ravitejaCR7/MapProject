package com.example.talarir.mapproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talarir.mapproject.AdminClasses.AddMainGroupAdmin;
import com.example.talarir.mapproject.AdminClasses.AddSubGroupAdmin;
import com.example.talarir.mapproject.AdminClasses.RecyclerItemClickListener;
import com.example.talarir.mapproject.NonAdminClasses.MapActivty;
import com.example.talarir.mapproject.NonAdminClasses.NonAdminSubActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NonAdminACtivity extends AppCompatActivity
{

    RecyclerView nonAdminMainRecyclerView;


    private DatabaseReference mFirebaseDatabaseNonAdmin;
    private FirebaseDatabase mFirebaseInstanceNonAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_admin_activity);

        mFirebaseInstanceNonAdmin = FirebaseDatabase.getInstance();
        mFirebaseDatabaseNonAdmin = mFirebaseInstanceNonAdmin.getReference("MainGroup");
        initializeView();
    }

    private void initializeView()
    {
        nonAdminMainRecyclerView= (RecyclerView) findViewById(R.id.recyclerViewNonAdminMain);
        nonAdminMainRecyclerView.setHasFixedSize(true);
        nonAdminMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<String,RecyclerMainGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<String, RecyclerMainGroupViewHolder>(
                String.class,
                R.layout.each_main_element_admin,
                RecyclerMainGroupViewHolder.class,
                mFirebaseDatabaseNonAdmin
        ) {
            @Override
            protected void populateViewHolder(RecyclerMainGroupViewHolder viewHolder, String model, int position)
            {

                viewHolder.setMainGroupName(model);

            }
        };

        nonAdminMainRecyclerView.setAdapter(firebaseRecyclerAdapter);

        nonAdminMainRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), nonAdminMainRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        // do whatever
                        TextView tv = (TextView) view.findViewById(R.id.textViewMainGroupAdminRecyclerList);
                        Toast.makeText(getApplicationContext(),"simple "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
                        Intent subCategoryIntent= new Intent(getApplicationContext(),NonAdminSubActivity.class);
                        subCategoryIntent.putExtra("MainSelectedStringNonActivity",tv.getText().toString());
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

    public static class RecyclerMainGroupViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RecyclerMainGroupViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setMainGroupName(String mainGroupName)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewMainGroupAdminRecyclerList);
            textView.setText(mainGroupName);
        }
    }
}
