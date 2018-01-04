
/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package abbasi.com.nixor.auth;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.concurrent.TimeUnit;

import abbasi.com.nixor.R;
import abbasi.com.nixor.misc.Tabs;


public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, View.OnKeyListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private TextView mTextField;
    private Button mSignInButton;
    EditText edit;
    EditText code1;
    Button next;
    ProgressDialog progress;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public  static final int RequestPermissionCode1  = 1 ;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    private GoogleApiClient mGoogleApiClient;
    private String phoneNumber;
    private String mVerificationId;
    String code;
    // Firebase instance variables
    private FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);


        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE

            );
        }}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TextView pf = (TextView)findViewById(R.id.pf);
     //   Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/tg.otf");
       // pf.setTypeface(typeFace);
        edit = (EditText)findViewById(R.id.email);
        next =(Button)findViewById(R.id.login);
        checkPlayServices();
        verifyStoragePermissions(this);
        // Assign fields
        mSignInButton = (Button) findViewById(R.id.sms);
        //ccp = (CountryCodePicker) findViewById(R.id.ccp);
        mTextField=(TextView)findViewById(R.id.time) ;
        code1 = (EditText)findViewById(R.id.code);
        // Set click listeners
        mSignInButton.setOnClickListener(this);
        mSignInButton.setEnabled(false);

        //ImageView imageView1 = (ImageView)findViewById(R.id.logo);
       // imageView1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));


        // Configure Google Sign In


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    checkifUser(firebaseAuth.getCurrentUser().getUid());
                }
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSubmitIfReady();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSubmitIfReady();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableSubmitIfReady();
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                Toast.makeText(Login.this,"Hurray!!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    progress.dismiss();
                    Toast.makeText(Login.this,e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    // / Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    progress.dismiss();
                    Toast.makeText(Login.this,"Try again in a few minutes", Toast.LENGTH_LONG).show();


                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                //On Code being sent, the button is disabled to prevent abuse.

                edit.setEnabled(false);
                mSignInButton.setEnabled(false);
                mTextField.setVisibility(View.VISIBLE);
                code1.setEnabled(true);
                code1.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                progress.dismiss();
                enableSms();
                checkforSms();


                timer();
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                //mResendToken = token;

                // ...
            }
        };
        edit.setOnKeyListener(this);

    }

    @Override
    protected void onDestroy() {
        ComponentName receiver = new ComponentName(Login.this, SmsReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ComponentName receiver = new ComponentName(Login.this, SmsReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        super.onPause();
    }

    @Override
    protected void onResume() {
        ComponentName receiver = new ComponentName(Login.this, SmsReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sms:
                phoneNumber = edit.getText().toString();
                signIn();

                break;

        }
    }
    public  void timer() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {

                mTextField.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                edit.setEnabled(true);
                mSignInButton.setEnabled(true);
                mTextField.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    public void signIn(){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS,this,mCallBacks);
        progress = ProgressDialog.show(this, "Please wait",
                "Loading", true);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    public void verifymethod(){ Log.i("LEL","101");
      if(code1.getText()!=null) {
          if (!code1.getText().toString().equals("")) {

              code = code1.getText().toString();
              Log.i("LEL", mVerificationId);
              Log.i("LEL", code);
              progress = ProgressDialog.show(this, "Please wait",
                      "Loading", true);
              PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
              signInWithPhoneAuthCredential(credential);
          }
      }}
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            checkifUser(user.getUid());


                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            progress.dismiss();

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Login.this,"The verification code entered was invalid",Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }else{ Toast.makeText(Login.this,task.getException().toString(),Toast.LENGTH_SHORT).show();}
                        }
                    }
                });}
    public  void verify(View view){

        verifymethod();
    }
    public  void checkifUser(final String uid){

        Log.i("LOg","lel");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // Log.i("Tester",dataSnapshot.toString());
                if(!dataSnapshot.toString().contains(uid)){
                    
                    Intent intent = new Intent(Login.this, Tabs.class);
                    startActivity(intent);
                   finish();
                }else{
                    
                    startActivity(new Intent(Login.this,Tabs.class));
                   finish();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),"Device not supported :(",Toast.LENGTH_SHORT).show();
                Log.i("Log", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;


    }


    public void enableSubmitIfReady() {

        boolean isReady =edit.getText().toString().length()>1;
        mSignInButton.setEnabled(isReady);
    }

    public void checkforSms(){
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text",messageText);
                String str=messageText;
                String numberOnly= str.replaceAll("[^0-9]", "");
                code1.setText(numberOnly);
                verifymethod();
                Toast.makeText(Login.this,"Message: "+messageText,Toast.LENGTH_LONG).show();



            }
        });

    }
    public  void enableSms(){
        ComponentName receiver = new ComponentName(Login.this, SmsReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Log.i("sms","Enabled broadcst receiver");

    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if(i== KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            phoneNumber = edit.getText().toString();
            signIn();



        }
        return false;
    }}
