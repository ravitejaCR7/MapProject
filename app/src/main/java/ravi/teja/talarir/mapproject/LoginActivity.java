package ravi.teja.talarir.mapproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button signUpBtn,loginBtn;
    EditText loginEt,passwordEt;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginEt=(EditText)findViewById(R.id.emailLoginEt);
        passwordEt=(EditText)findViewById(R.id.passwordLoginEt);

        signUpBtn= (Button)findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Welcome to SignUpActivity",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                finish();
            }
        });

        loginBtn=(Button)findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginBtn.setEnabled(false);
                showProgressBar();

                final String email = loginEt.getText().toString();
                final String password = passwordEt.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    cancelProgressBar();
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    loginBtn.setEnabled(true);
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    cancelProgressBar();
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    loginBtn.setEnabled(true);
                    return;
                }

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful())
                                {
                                    // there was an error
                                    if (password.length() < 6)
                                    {
                                        passwordEt.setError(getString(R.string.minimum_password));
                                    }
                                    else
                                    {
                                        Log.e("error auth",getString(R.string.auth_failed));
                                        Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                    cancelProgressBar();
                                    loginBtn.setEnabled(true);
                                }
                                else
                                {
                                    cancelProgressBar();
                                    if(email.equals("ravi@gmail.com"))
                                    {
                                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(getApplicationContext(), NonAdminACtivity.class);
                                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                        startActivity(intent);
                                    }


                                    finish();
                                }
                            }
                        });
            }
        });

    }

    public void showProgressBar()
    {
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Haha Can't Hack...");
        progressDialog.show();

    }


    public void cancelProgressBar()
    {
        progressDialog.dismiss();

    }
}
