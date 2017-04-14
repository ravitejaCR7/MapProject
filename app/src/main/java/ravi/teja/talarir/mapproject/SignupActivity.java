package ravi.teja.talarir.mapproject;


import android.app.DatePickerDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;


public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText signUpEmail,signUpPassword;
    Button backToLoginButton,signUpButton;
    private ProgressDialog progressDialog;
    private int day=0,month=0,year=0;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private String sex=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        instantiateView();

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

                if (TextUtils.isEmpty(email)) {
                    cancelProgressBar();
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    signUpButton.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    cancelProgressBar();
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    signUpButton.setEnabled(true);
                    return;
                }

                if (password.length() < 6) {
                    cancelProgressBar();
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "select Date!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    signUpButton.setEnabled(true);
                                } else {
                                    Toast.makeText(SignupActivity.this, "Kindly Login!.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                    finish();
                                }
                            }
                        });
            }
        });
    }


    private void instantiateView()
    {
        mAuth = FirebaseAuth.getInstance();
        signUpEmail=(EditText)findViewById(R.id.editTextSignUpLogin);
        signUpPassword=(EditText)findViewById(R.id.editTextSignUpPassword);
        backToLoginButton=(Button)findViewById(R.id.backToLoginActivityBtn);
        signUpButton=(Button)findViewById(R.id.registerSignUpActivityBtn);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
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
