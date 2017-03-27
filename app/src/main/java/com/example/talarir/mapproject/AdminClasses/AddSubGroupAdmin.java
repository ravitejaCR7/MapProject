package com.example.talarir.mapproject.AdminClasses;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.talarir.mapproject.R;
import com.example.talarir.mapproject.Upload;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
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
    private Button saveToFireBaseButtonSub,chooseImageSubAdminButton;
    private RecyclerView recyclerSubView;

    private static final int PICK_IMAGE_REQUEST_SUB = 300;
    private int imageSubFlag=0;
    private Uri filePathSubAdmin;
    private ImageView subAdminImageView;
    private StorageReference mStorageRefMainAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_group_admin);

        mStorageRefMainAdmin = FirebaseStorage.getInstance().getReference();

        mainGroupSelectedKey=null;
        getTheMainGroupSelectedKey();

        subAdminImageView = (ImageView) findViewById(R.id.imageViewSubAdmin);
        theSubGroupNameEditText= (EditText) findViewById(R.id.editTextSubGroup);
        saveToFireBaseButtonSub= (Button) findViewById(R.id.btnAddSubGroupAdmin);
        chooseImageSubAdminButton= (Button) findViewById(R.id.btnAddImageSubGroupAdmin);
        recyclerSubView= (RecyclerView) findViewById(R.id.recyclerViewAdminSub);
        recyclerSubView.setHasFixedSize(true);
        recyclerSubView.setLayoutManager(new GridLayoutManager(this,2));

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
                    Upload retrievingMainUploadObject = dataSnapshot1.getValue(Upload.class);

                    if (mainGroupSelected.equals(retrievingMainUploadObject.getName()))
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
        chooseImageSubAdminButton.setOnClickListener(this);
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
        if (v==chooseImageSubAdminButton)
        {
            startImagesIntentSubAdmin();
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
                        Upload retrievingSubUploadObject = da.getValue(Upload.class);
                        if (s.equals(retrievingSubUploadObject.getName()))
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

    public void createNewSubGroup(final String theSubGroupNameEditText)
    {
        mFirebaseInstanceSub = FirebaseDatabase.getInstance();
        mFirebaseDatabaseSub = mFirebaseInstanceSub.getReference("SubGroup/"+mainGroupSelectedKey);

        if (theSubGroupNameEditText.equals("")||theSubGroupNameEditText.length()<=0)
        {
            Toast.makeText(getApplicationContext(),"woah",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (imageSubFlag==1)
            {
                final String subKey=mFirebaseDatabaseSub.push().getKey();
                if (filePathSubAdmin!=null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading");
                    progressDialog.show();
                    StorageReference sRefSubAdmin = mStorageRefMainAdmin.child("Images" + "/" + "Sub"+"/"+mainGroupSelectedKey+"/"+subKey);
                    sRefSubAdmin.putFile(filePathSubAdmin).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            Upload uploadSubAdmin = new Upload(theSubGroupNameEditText.toLowerCase(), downloadUrl.toString());

                            mFirebaseDatabaseSub.child(subKey).setValue(uploadSubAdmin);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            @SuppressWarnings("VisibleForTests") long bytesTransfered= taskSnapshot.getBytesTransferred();
                            @SuppressWarnings("VisibleForTests") long totalBytesTransfered= taskSnapshot.getTotalByteCount();
                            double progress = (100.0 * bytesTransfered) / totalBytesTransfered;
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
                }
                Toast.makeText(this, "Sub Key : "+subKey, Toast.LENGTH_LONG).show();
            }
            else if (imageSubFlag==0)
            {
                Toast.makeText(this, "please select an image from gallery", Toast.LENGTH_LONG).show();
            }
        }

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
            protected void populateViewHolder(RecyclerSubGroupViewHolder viewHolder, Upload model, int position)
            {

                viewHolder.setMainGroupName(model.getName(),model.getUrl());

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

        public void setMainGroupName(String subGroupName,String subGroupImageUri)
        {
            TextView textView= (TextView) mView.findViewById(R.id.textViewSubGroupAdminRecyclerList);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageViewSubGroupAdminRecyclerList);
            textView.setText(subGroupName);
            Glide.with(mView.getContext()).load(subGroupImageUri).into(imageView);
        }
    }

    private void startImagesIntentSubAdmin()
    {
        Intent imagesIntent=new Intent();
        imagesIntent.setType("image/*");
        imagesIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imagesIntent, "Select Picture"), PICK_IMAGE_REQUEST_SUB);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_SUB && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePathSubAdmin = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathSubAdmin);
                subAdminImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageSubFlag=1;
        }
        else
        {
            imageSubFlag=0;
        }
    }
}
