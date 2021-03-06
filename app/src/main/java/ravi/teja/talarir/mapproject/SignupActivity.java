package ravi.teja.talarir.mapproject;


import android.app.DatePickerDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Calendar;


public class SignupActivity extends AppCompatActivity
{
    private static final int PICK_IMAGE_REQUEST=100;
    private Uri uri=null;

    private FirebaseAuth mAuth;
    private ImageView signUpImageView;
    private EditText signUpEmail,signUpPassword,signUpUserName;
    private Button backToLoginButton,signUpButton;
    private ProgressDialog progressDialog;
    private int day=0,month=0,year=0;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private String sex=null;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        instantiateView();

        signUpImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Welcome Home",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int selectedId = radioSexGroup.getCheckedRadioButtonId();
                radioSexButton = (RadioButton) findViewById(selectedId);
                sex= radioSexButton.getText().toString();

                signUpButton.setEnabled(false);
                showProgressBar();
                String email = signUpEmail.getText().toString().trim();
                String password = signUpPassword.getText().toString().trim();
                String userName = signUpUserName.getText().toString().trim();

                if (TextUtils.isEmpty(userName))
                {
                    cancelProgressBar();
                    showToastMessage("Enter Username");
                    signUpButton.setEnabled(true);
                    return;
                }
                if (!userName.matches("[A-Za-z0-9]+"))
                {
                    cancelProgressBar();
                    showToastMessage("No Special characters allowed!");
                    signUpButton.setEnabled(true);
                    return;
                }
                if (uri==null)
                {
                    cancelProgressBar();
                    showToastMessage("please select a profile pic");
                    signUpButton.setEnabled(true);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    cancelProgressBar();
                    showToastMessage("Enter email address!");
                    signUpButton.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    cancelProgressBar();
                    showToastMessage("Enter password!");
                    signUpButton.setEnabled(true);
                    return;
                }

                if (password.length() < 6) {
                    cancelProgressBar();
                    showToastMessage("Password too short, enter minimum 6 characters!");
                    signUpButton.setEnabled(true);
                    return;
                }

                if (sex==null||sex.length()<=0)
                {
                    cancelProgressBar();
                    Toast.makeText(getApplicationContext(), "select Gender!", Toast.LENGTH_SHORT).show();
                    signUpButton.setEnabled(true);
                    return;
                }

                if (day==0||month==0||year==0)
                {
                    cancelProgressBar();
                    showToastMessage("select Date!");
                    signUpButton.setEnabled(true);
                    return;
                }

                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    cancelProgressBar();
                                    showToastMessage("authentication failed : "+task.getException());
                                    signUpButton.setEnabled(true);
                                } /*else {
                                    showToastMessage("Kindly Login!.");
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                    finish();
                                }*/
                            }
                        });
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    user.getDisplayName();
                    // User is signed in
                    showProgressBar();
                    Log.d("SignUpActivity","mAuthGettingCalled");
                    cancelProgressBar();
                }
                else
                {
                    // User is signed out
                    Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.signUp_imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            showToastMessage("please select a profile pic");
        }
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
    private void showToastMessage(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void instantiateView()
    {
        mAuth = FirebaseAuth.getInstance();
        signUpEmail=(EditText)findViewById(R.id.editTextSignUpLogin);
        signUpPassword=(EditText)findViewById(R.id.editTextSignUpPassword);
        signUpUserName= (EditText) findViewById(R.id.editTextSignUpUserName);
        backToLoginButton=(Button)findViewById(R.id.backToLoginActivityBtn);
        signUpButton=(Button)findViewById(R.id.registerSignUpActivityBtn);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        signUpImageView = (ImageView) findViewById(R.id.signUp_imageView);
    }
    public void showProgressBar()
    {
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Welcome lads...");
        progressDialog.show();
    }


    public void cancelProgressBar()
    {
        progressDialog.dismiss();
    }

    @SuppressWarnings("deprecation")
    public void showDatePickerDialog(View v)
    {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();

    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    year=arg1;
                    month=arg2+1;
                    day=arg3;
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        Toast.makeText(getApplicationContext(),"selected : "+day+" "+month+" "+year,Toast.LENGTH_LONG).show();
    }

}
