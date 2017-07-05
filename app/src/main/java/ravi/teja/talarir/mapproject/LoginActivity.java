package ravi.teja.talarir.mapproject;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    // Global instance of the JSON factory
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String KEY_ACCOUNT = "key_account";
    private Account mAccount;
    public String authCode;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private String email,name,fname;
    private Uri user_photo;
    private static int differentLoginFlag=0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private GoogleApiClient mGoogleApiClient;
    Button signUpBtn,loginFireBaseBtn,loginGoogleBtn;
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
        loginFireBaseBtn=(Button)findViewById(R.id.loginBtn);
        signUpBtn= (Button)findViewById(R.id.signUpBtn);

        SignInButton signInButton= (SignInButton) findViewById(R.id.googleSignInButton);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        loginFireBaseBtn.setOnClickListener(this);

        validateServerClientID();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    if (name!=null&&email!=null&&fname!=null)
                    {
                        BasicUserInfoParcelable basicUserInfo= new BasicUserInfoParcelable(name,fname,email,user_photo.toString());
                        Toast.makeText(getApplicationContext(),"GOOGLE: \n"+email+"\n"+name+"\n"+fname, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), NonAdminACtivity.class);
                        intent.putExtra("userInfoFromLoginActivity",basicUserInfo);
                        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                        startActivity(intent);
                        finish();
                    }
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else
                {
                    // User is signed out
                    Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        // Restore instance state
        if (savedInstanceState != null) {
            mAccount = savedInstanceState.getParcelable(KEY_ACCOUNT);
        }

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail()
                .build();
        // [END config_signin]



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signUpBtn.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_ACCOUNT, mAccount);
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

    @Override
    public void onClick(View v)
    {
       if (v.getId()==R.id.loginBtn)
       {
            firebaseLogin();
       }
       if (v.getId()==R.id.googleSignInButton)
       {
            googleLogin();
       }
       if (v.getId()==R.id.signUpBtn)
       {
           firebaseSignUp();
       }
    }

    private void googleLogin()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        if (result.isSuccess())
        {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = result.getSignInAccount().getIdToken();
            authCode = account.getServerAuthCode();
            Log.d(TAG,"google token : "+idToken+"\ngoogle auth code : "+authCode);
            email = account.getEmail();
            name = account.getDisplayName();
            fname = account.getFamilyName();
            user_photo= account.getPhotoUrl();

            mAccount = account.getAccount();

            getPersonalInfo(account);

            firebaseAuthWithGoogle(account);

        }
        else
        {
            // Google Sign In failed, update UI appropriately
            // [START_EXCLUDE]
            mAccount = null;

            Toast.makeText(this, "Google SIGN-IN Fialed!", Toast.LENGTH_SHORT).show();
            // [END_EXCLUDE]
        }
    }

    private void getPersonalInfo(final GoogleSignInAccount account)
    {
        new Thread
                (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Collection<String> scopes = new ArrayList<>(Collections.singletonList(Scopes.PROFILE));
                        GoogleAccountCredential credential =
                                GoogleAccountCredential.usingOAuth2(LoginActivity.this,  scopes);
                        credential.setSelectedAccount(
                                new Account(account.getEmail(), "com.google"));
                        People service = new People.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                                .setApplicationName(name /* whatever you like */)
                                .build();
// All the person details
                        Person meProfile = null;
                        try {
                            meProfile = service.people().get("people/me").execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
// e.g. Gender
                        meProfile.getAgeRange();
                        List<Birthday> birthday=meProfile.getBirthdays();
                        //Log.d("LOGIN ACTIVITY","birthday is "+birthday.get(0).getText());
                        List<Gender> genders = meProfile.getGenders();
                        String gender = null;
                        if (genders != null && genders.size() > 0) {
                            gender = genders.get(0).getValue();
                            Log.d("LOGIN ACTIVITY", "gender is "+gender);
                        }
                        //Log.w("LOGIN ACTIVITY", "gender is "+gender);
                    }
                }
        ).start();
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        cancelProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    private void firebaseSignUp()
    {
        Toast.makeText(LoginActivity.this, "Welcome to SignUpActivity",
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        finish();
    }


    public void firebaseLogin()
    {
        loginFireBaseBtn.setEnabled(false);
        showProgressBar();

        final String email = loginEt.getText().toString();
        final String password = passwordEt.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            cancelProgressBar();
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            loginFireBaseBtn.setEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            cancelProgressBar();
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            loginFireBaseBtn.setEnabled(true);
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
                            loginFireBaseBtn.setEnabled(true);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.d(TAG,"validateServerClientID : "+message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}
