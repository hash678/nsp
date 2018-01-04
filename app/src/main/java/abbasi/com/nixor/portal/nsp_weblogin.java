package abbasi.com.nixor.portal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import abbasi.com.nixor.R;

public class nsp_weblogin extends AppCompatActivity implements View.OnClickListener {
    Button ok, back, exit;
    TextView result;
    EditText username;
    EditText password;
    Button login;
    String username_log;
    String Password_log;

    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    ArrayList classlist;
    String scancode;
    FirebaseAuth mAuth;
    Boolean getusername=false;
    ArrayList<String> buttons;
    private static final int RC_SIGN_IN = 9001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE

            );
        }}
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
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        buttons = new ArrayList<>();
        username = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login =(Button)findViewById(R.id.login);
        verifyStoragePermissions(this);
        checkPlayServices();
        scanBtn = (Button)findViewById(R.id.scancard);
        mAuth = FirebaseAuth.getInstance();
        getusername = getIntent().getBooleanExtra("getusername",false);
classlist = new ArrayList<>();
classlist.add("CHM101");
classlist.add("CHM102");
classlist.add("CHM103");
classlist.add("CHM104");
classlist.add("CHM105");
classlist.add("CHM106");
classlist.add("CHM107");
classlist.add("MTH101");
classlist.add("MTH102");
classlist.add("MTH103");
classlist.add("MTH104");
classlist.add("MTH105");
classlist.add("MTH106");
classlist.add("MTH107");
classlist.add("MTH108");
classlist.add("PHY101");
classlist.add("PHY102");
classlist.add("PHY103");
classlist.add("PHY104");
classlist.add("PHY105");
classlist.add("PHY106");
classlist.add("PHY107");
        scanBtn.setOnClickListener(this);


  //      mytoolbar=(Toolbar)findViewById(R.id.toolbar);
//mytoolbar.setTitle("My Nixor Portal");

        // Login button clicked
        //  ok = (Button)findViewById(R.id.btn_login);
        //  ok.setOnClickListener(this);

        //result = (TextView)findViewById(R.id.lbl_result);

    }

    public void onClick(View v){
        if(v.getId()==R.id.scancard){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
           scancode=scanContent;
           if(scancode!=null){
         if (!scancode.equals(""))
            new FetchItemsTask2(this).execute();
        }}
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,String> {
        private Activity activity;


        @Override
        protected void onPostExecute(String s) {
if(this.dialog.isShowing()){
    this.dialog.dismiss();
}
            super.onPostExecute(s);
        }
        public FetchItemsTask(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
            this.dialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Connection.Response loginForm = Jsoup.connect("http://nsp.braincrop.net/Students/Login")
                        .method(Connection.Method.GET)
                        .timeout(1000000000)
                        .execute();

                Document doc = loginForm.parse();
                //String ltValue = doc.select("input[name=lt]").attr("value");
                String executionValue = doc.select("input[name=__RequestVerificationToken]").attr("value");
                // Log.i("Second",executionValue);
                // Log.i("Second",doc.toString());
                // Log.i("Second",loginForm.cookies().toString());

                Connection.Response logged = Jsoup.connect("http://nsp.braincrop.net/Students/Login")
                        .method(Connection.Method.POST)
                        .timeout(100000000)
                        .cookies(loginForm.cookies())
                        .data("Email",username_log ) //fix this  username_log Password_log
                        .data("Password", Password_log)
                        .data("log", "1")
                        .data("__RequestVerificationToken", executionValue)
                        .data("btn btn-default", "Log in")
                        .followRedirects(true)
                        .execute();
                Document userid = logged.parse();
                //String ltValue = doc.select("input[name=lt]").attr("value");
                String useridstring = userid.select("input[name=guid]").attr("value").toString();




                for (int x=0; x<userid.getElementsByClass("btn btn-default").size();x++){
                    buttons.add(userid.getElementsByClass("btn btn-default").get(x).val().toString());
                }
                for(int x=0;x<buttons.size();x++){
                    Log.i("test",buttons.get(x));

                }


                if(userid.select(".alert").toString().contains("Email/Password Incorrect.")){
                    Log.i("pas","Email/Password Incorrect. ");
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(nsp_weblogin.this, "Are your login details correct?", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    //<input type="hidden" name="guid" value="d1adcef1-81c0-4c46-8289-cbf7fd06084b">


                   // Log.i("u", userid.toString());
                    Log.i("userid", useridstring);
                    FirebaseDatabase ref= FirebaseDatabase.getInstance();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ref.getReference().child("users").child(uid).child("guid").setValue(useridstring);

                    ref.getReference().child("users").child(uid).child("Name").setValue(getValue("Name",userid));
                    ref.getReference().child("users").child(uid).child("StudentID").setValue(getValue("Student ID",userid));
                    if(buttons.size()!=0) {
                        ref.getReference().child("users").child(uid).child("buttons").setValue(buttons);
                    }
                    if(getusername){
                       if(username.getText()!=null){
                           String usernamew =username.getText().toString().replaceAll("@nixorcollege.edu.pk","").replace(".","-");
                          FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(usernamew);
                           Intent ij = new Intent(activity, abbasi.com.nixor.misc.Tabs.class);
                           ij.putExtra("username",usernamew);
                         startActivity(ij);
                          finish();
                       }
                          }else{
                        Intent ij = new Intent(activity, abbasi.com.nixor.misc.Tabs.class);
                        startActivity(ij);
                        finish();
                    }


                   


                }
            } catch (IOException e) {
                Log.i("test","Failed");
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(nsp_weblogin.this, "Are your connected to the internet?", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();

            }

            return null;



        }}
    private class FetchItemsTask2 extends AsyncTask<Void,Void,String> {
        private Activity activity;


        @Override
        protected void onPostExecute(String s) {
            if(this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            super.onPostExecute(s);
        }
        public FetchItemsTask2(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
            this.dialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Connection.Response loginForm = Jsoup.connect("http://nsp.braincrop.net/Students/Login")
                        .method(Connection.Method.GET)
                        .timeout(1000000000)
                        .execute();

                Document doc = loginForm.parse();
                //String ltValue = doc.select("input[name=lt]").attr("value");
                String executionValue = doc.select("input[name=__RequestVerificationToken]").attr("value");
                // Log.i("Second",executionValue);
                // Log.i("Second",doc.toString());
                // Log.i("Second",loginForm.cookies().toString());

                Connection.Response logged = Jsoup.connect("http://nsp.braincrop.net/Students/Login")
                        .method(Connection.Method.POST)
                        .timeout(100000000)
                        .cookies(loginForm.cookies())
                        .data("Barcode", scancode)
                        .data("log", "0")
                        .data("__RequestVerificationToken", executionValue)
                        .data("btn btn-default", "Log in")
                        .followRedirects(true)
                        .execute();
                Document userid = logged.parse();
                //String ltValue = doc.select("input[name=lt]").attr("value");
                String useridstring = userid.select("input[name=guid]").attr("value").toString();
                for (int x=0; x<userid.getElementsByClass("btn btn-default").size();x++){
                    buttons.add(userid.getElementsByClass("btn btn-default").get(x).val().toString());
                }
                for(int x=0;x<buttons.size();x++){
                    Log.i("test",buttons.get(x));

                }

                if(userid.select(".alert").toString().contains("Email/Password Incorrect.")){
                    Log.i("pas","Email/Password Incorrect. ");
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(nsp_weblogin.this, "Are your login details correct?", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    //<input type="hidden" name="guid" value="d1adcef1-81c0-4c46-8289-cbf7fd06084b">


                    Log.i("u", userid.toString());
                    Log.i("userid", useridstring);
                    FirebaseDatabase ref= FirebaseDatabase.getInstance();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ref.getReference().child("users").child(uid).child("guid").setValue(useridstring);
                    ref.getReference().child("users").child(uid).child("Name").setValue(getValue("Name",userid));
                    ref.getReference().child("users").child(uid).child("StudentID").setValue(getValue("Student ID",userid));

                    if(buttons.size()!=0){
                    ref.getReference().child("users").child(uid).child("buttons").setValue(buttons);}
                    if(getusername){
                        String[] split = userid.select("dl[class=dl-horizontal]").tagName("dd").toString().split("Nixor Email");
                        String[] split2 = split[1].split(".pk");
                        String[] split3 = split2[0].split("<dd>");

                       String usernamew =split3[1].replaceAll("@nixorcollege.edu","").replaceAll("\\s+","").replace(".","-");
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(usernamew);

                        Intent ij = new Intent(activity, abbasi.com.nixor.misc.Tabs.class);
                        ij.putExtra("username",usernamew);
                        startActivity(ij);
                        finish();
                    }else{
                        Intent ij = new Intent(activity, abbasi.com.nixor.misc.Tabs.class);
                        startActivity(ij);
                        finish();
                    }



                }
            } catch (IOException e) {
                Log.i("test","Failed");
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(nsp_weblogin.this, "Are your connected to the internet?", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();

            }

            return null;



        }}
    public void loginIntiaite(View view){
        if(username.getText()!=null&&password.getText()!=null){
            username_log=username.getText().toString();
            Password_log=password.getText().toString();
            if(!username_log.equals("")&&!Password_log.equals("")){
            new FetchItemsTask(this).execute();}

        }


    }
public String getValue(String Name, Document userid){
    String tester = userid.getElementsByClass("dl-horizontal").toString();
    String[] splitz=  tester.split(Name);
    String[] splitx=   splitz[1].split(" </dd> ");
 String value =splitx[0].replaceAll("</dt>","").replaceAll("<dd>","").replaceAll("(?m)^[ \t]*\r?\n", "").trim();
Log.i("Values",value);
 return value;
}

}
