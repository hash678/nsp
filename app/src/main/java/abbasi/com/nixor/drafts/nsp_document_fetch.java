package abbasi.com.nixor.drafts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import abbasi.com.nixor.R;
import abbasi.com.nixor.portal.nsp_weblogin;
import abbasi.com.nixor.portal.pdfviewer;

public class nsp_document_fetch extends AppCompatActivity {
String userid;
    ProgressDialog pd;
TextView nsp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_page);
        nsp=(TextView)findViewById(R.id.nsptitle);
        Button profile = (Button)findViewById(R.id.profile);
        Button gate = (Button)findViewById(R.id.gate);
        Button attendance = (Button)findViewById(R.id.atttendance);
        Button marks = (Button)findViewById(R.id.Marks);
        Button sche = (Button)findViewById(R.id.schedule);
        buttonEffect(profile);
        buttonEffect(gate);
        buttonEffect(attendance);
        buttonEffect(marks);
        buttonEffect(sche);

        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/tg.otf");
        nsp.setTypeface(typeFace);
        pd = new ProgressDialog(this);
        pd.setTitle("Loading");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("guid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userid = dataSnapshot.getValue().toString();
                if(pd.isShowing()){
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void clickButton(View view){
    if (userid != null) {
        if (!userid.equals("null")) {
            try {
                SendfeedbackJob job = new SendfeedbackJob(this);
                switch (view.getId()) {
                    case R.id.profile:
                        job.execute(userid, "Profile");
                        break;
                    case R.id.gate:
                        job.execute(userid, "Gate Attendance");
                        break;
                    case R.id.financial:
                        job.execute(userid, "Finance");
                        break;
                    case R.id.Marks:
                        job.execute(userid, "Student Marks");
                        break;
                    case R.id.schedule:
                        job.execute(userid, "Schedule");
                        break;

                    case R.id.atttendance:
                        job.execute(userid, "Attendance");
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    }
    private class SendfeedbackJob extends AsyncTask<String, String, String> {
        private Activity activity;
        public SendfeedbackJob(Activity activity) {
            this.activity = activity;

            dialog = new ProgressDialog(activity);
        }

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        AlertDialog alertDialog;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
            this.dialog.setCancelable(false);

            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(activity).create();

        }
        private Exception exceptionToBeThrown;
        String documentname;

        @Override
        protected String doInBackground(final String[] params) {
            try {


                String fileName="response";
                String fileExtension=".pdf";

//           download pdf file.




                String POST_PARAMS = "meth="+params[1]+"&guid="+params[0];

                URL url = new URL("http://nsp.braincrop.net/Students/Reports");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                OutputStream os = c.getOutputStream();
                os.write(POST_PARAMS.getBytes());

                os.flush();
                os.close();
                Log.i("yellow",Integer.toString(c.getResponseCode()
                ));
                if(c.getResponseCode()==500){
                    Intent ik = new Intent(nsp_document_fetch.this,nsp_weblogin.class);
                    startActivity(ik);
                    finish();
                }

                // Toast.makeText(activity,Integer.toString(c.getResponseCode()),Toast.LENGTH_SHORT).show();
                String PATH = Environment.getExternalStorageDirectory() + "/nixorapp/";

                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, fileName+fileExtension);
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }

                fos.close();
                is.close();
                documentname=params[1];
                File from = new File(Environment.getExternalStorageDirectory() + "/nixorapp/","response.pdf");
                File to = new File(Environment.getExternalStorageDirectory() + "/nixorapp/",params[1]+".pdf");
                if(to.exists()){
                    to.delete();
                }
                from.renameTo(to);
                Intent ij = new Intent(activity,pdfviewer.class);
                ij.putExtra("document",params[1]);
                startActivity(ij);
                //  System.out.println("--pdf downloaded--ok--"+"http://nsp.braincrop.net/Students/Reports");
            } catch (Exception e) {
exceptionToBeThrown=e;
                e.printStackTrace();
               }
            return params[1];
        }
        @Override
        protected void onPostExecute(final String message) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
if(exceptionToBeThrown!=null){
    Log.i("exceptiion","yes");
    File zubaida = new File(Environment.getExternalStorageDirectory() + "/nixorapp/",message+".pdf");
    Log.i("exceptiion",message);
    if(zubaida.exists()){

        if(pd.isShowing()){
            pd.dismiss();
        }
        Date lastModDate = new Date(zubaida.lastModified());
        alertDialog.setTitle("Unable to fetch new version");
        alertDialog.setMessage("We are unable to connect to server, however we have an older version of the document (From "+lastModDate+").");
        //If he selects accept
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show older version",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent ij = new Intent(activity,pdfviewer.class);
                        ij.putExtra("document",message);
                        startActivity(ij);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Never mind",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }

        });
        alertDialog.show();
    }else{
        alertDialog.setTitle("Unable to fetch new version");
        alertDialog.setMessage("We are unable to connect to server and no older versions of the document are available at this time");
        //If he selects accept

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }

        });
        alertDialog.show();
    }
}
            Log.i("message",message);

        }
    }

    public  String[] extractText(String yourPdfPath){

        try {
            String parsedText="";
            PdfReader reader = new PdfReader(yourPdfPath);

            int n = reader.getNumberOfPages();
            Log.i("info",reader.getInfo().toString());//name
           // Log.i("metadata",reader.getMetadata().toString());//name

            for (int i = 0; i <n ; i++) {
                parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n"; //Extracting the content from the different pages
//                Log.i("textf",parsedText);//name

            }
            String[] formatted = parsedText.split("\n");

            System.out.println(formatted[1]);

            Log.i("textf",formatted[5]);//name

            String textStr[] = formatted[1].split("\n");
            Log.i("name",textStr[0]);
            reader.close();
            String text="";
            return formatted;
        } catch (Exception e) {
            System.out.println(e);

        }
        return null;
    }
    public void buttonEffect(View button) {


        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(getResources().getColor(R.color.colorcontrasrt), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });}




}
//3 CHM.J CHM107 MTH103 PHY.I, Monday Start  1:00:00PM Start 11:00:00AM, End  2:00:00PM End 12:00:00PM, CL-2007 CL-2001, AAILA GHANI NASIR GHANI, Tuesday Start  1:00:00PM Start 11:00:00AM, End  2:00:00PM End 12:00:00PM, CL-2007 CL-2001, AAILA GHANI NASIR GHANI, Wednesday Start  1:00:00PM Start 11:00:00AM Start  8:00:00AM, End  2:00:00PM End 12:00:00PM End  9:30:00AM, CL-2007 CL-2001 PHYSICS LAB, AAILA GHANI NASIR GHANI AFTAB SAAD SOHAIL, Thursday Start  1:00:00PM Start 11:00:00AM, End  2:00:00PM End 12:00:00PM, CL-2007 CL-2001, AAILA GHANI NASIR GHANI, Friday Start  1:00:00PM Start 11:00:00AM, End  2:00:00PM End 12:00:00PM, CL-2007 CL-2001, AAILA GHANI NASIR GHANI, PHY106, Start  2:00:00PM, End  3:00:00PM, CL-2004, NAUSHAD KARAMALI, Start  2:00:00PM, End  3:00:00PM, CL-2004, NAUSHAD KARAMALI, Start  2:00:00PM, End  3:00:00PM, CL-2004, NAUSHAD KARAMALI, Start  2:00:00PM, End  3:00:00PM, CL-2004, NAUSHAD KARAMALI, CHM.J CHM107 MTH103 PHY.I, Start 11:00:00AM, Saturday, End 12:30:00PM, BIO/CHM LAB, ASIA NIAZI, 2, PHY106]
