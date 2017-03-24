package com.example.talarir.mapproject.AdminClasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


public class AddMainGroupAdmin extends AppCompatActivity implements View.OnClickListener
{

    private ImageView mainAdminImageView;
    private int imageMainFlag=0;
    private Uri filePathMainAdmin;
    private static final int PICK_IMAGE_REQUEST = 100;

    private Button saveToFireBaseButton,chooseImageMainAdminButton;
    private EditText theMainGroupNameEditText;
    private RecyclerView recyclerMainView;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorageRef;

    ValueEventListener x;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_main_group_admin);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mainAdminImageView = (ImageView) findViewById(R.id.imageViewMainAdmin);
        saveToFireBaseButton= (Button) findViewById(R.id.btnAddMainGroupAdmin);
        chooseImageMainAdminButton = (Button) findViewById(R.id.btnAddImageMainGroupAdmin);
        theMainGroupNameEditText= (EditText) findViewById(R.id.editTextMainGroup);

        recyclerMainView= (RecyclerView) findViewById(R.id.recyclerViewAdminMain);
        recyclerMainView.setHasFixedSize(true);
        recyclerMainView.setLayoutManager(new LinearLayoutManager(this));

        saveToFireBaseButton.setOnClickListener(this);
        chooseImageMainAdminButton.setOnClickListener(this);


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

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Upload,RecyclerMainGroupViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Upload, RecyclerMainGroupViewHolder>
                (
                Upload.class,
                R.layout.each_main_element_admin,
                RecyclerMainGroupViewHolder.class,
                mFirebaseDatabase
                )
        {
            @Override
            protected void populateViewHolder(RecyclerMainGroupViewHolder viewHolder, Upload model, int position)
            {

                viewHolder.setMainGroupName(model.getName(),model.getUrl());


            }
        };

        recyclerMainView.setAdapter(firebaseRecyclerAdapter);

        recyclerMainView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerMainView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position)
                    {
                        // do whatever
                        TextView tv = (TextView) view.findViewById(R.id.textViewMainGroupAdminRecyclerList);
                        //Toast.makeText(getApplicationContext(),"simple "+tv.getText().toString(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v)
    {
        if (v.getId()==R.id.btnAddMainGroupAdmin )
        {
            if (theMainGroupNameEditText.getText().toString().length()>0 && (!theMainGroupNameEditText.getText().equals("")) && theMainGroupNameEditText.getText().toString()!=null)
            {
                    getTheMainListForUniqueness(theMainGroupNameEditText.getText().toString().toLowerCase().trim());
            }
            else
            {
                theMainGroupNameEditText.setError("can't be null");
            }
        }
        if (v.getId()==R.id.btnAddImageMainGroupAdmin)
        {
            startImagesIntent();
        }
    }

    private void getTheMainListForUniqueness(final String s)
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");
        x=mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
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
                        createNewMainGroup(s);
                        theMainGroupNameEditText.setText("");
                    }
                }
                else
                {
                    createNewMainGroup(s);
                    theMainGroupNameEditText.setText("");
                }

                cancelConnection();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void cancelConnection()
    {
        mFirebaseDatabase.removeEventListener(x);
    }
    public void createNewMainGroup(final String theMainGroupNameEditText)
    {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("MainGroup");

        if (theMainGroupNameEditText.equals("")||theMainGroupNameEditText.length()<=0)
        {
            Toast.makeText(getApplicationContext(),"woah",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (imageMainFlag==1)
            {
                final String mainKey=mFirebaseDatabase.push().getKey();
                if (filePathMainAdmin!=null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading");
                    progressDialog.show();
                    StorageReference sRef = mStorageRef.child("Images" + "." + "Main"+"."+ mainKey);
                    sRef.putFile(filePathMainAdmin).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            Upload upload = new Upload(theMainGroupNameEditText.toLowerCase(), downloadUrl.toString());

                            mFirebaseDatabase.child(mainKey).setValue(upload);
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
                Toast.makeText(this, "main Key : "+mainKey, Toast.LENGTH_LONG).show();
            }
            else if (imageMainFlag==0)
            {
                Toast.makeText(this, "please select an image from gallery", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void startImagesIntent()
    {
        Intent imagesIntent=new Intent();
        imagesIntent.setType("image/*");
        imagesIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imagesIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePathMainAdmin = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathMainAdmin);
                mainAdminImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageMainFlag=1;
        }
        else
        {
            imageMainFlag=0;
        }
    }
}
