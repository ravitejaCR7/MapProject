package com.example.talarir.mapproject;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.talarir.mapproject.AdminClasses.AddMainGroupAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener
{
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    Button buttonSaveMainGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user=firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    initializeButton();
                    Log.d("CooActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Log.d("CooActivity", "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();

                }
            }
        };
    }

    private void initializeButton()
    {
        buttonSaveMainGroup= (Button) findViewById(R.id.addMainGroupAdmin);

        buttonSaveMainGroup.setOnClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId()==R.id.addMainGroupAdmin)
        {
            startActivity(new Intent(this, AddMainGroupAdmin.class));
        }
    }
}
