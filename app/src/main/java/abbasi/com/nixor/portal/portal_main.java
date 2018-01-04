package abbasi.com.nixor.portal;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.Date;

import abbasi.com.nixor.R;



public class portal_main extends Fragment{
    String userid;
    ProgressDialog pd;
    TextView nsp;

    ListView nsplist;
    ArrayList nameofbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nsp_main, container, false);
        nsp=(TextView)rootView.findViewById(R.id.nsptitle);
       nsplist = (ListView)rootView.findViewById(R.id.nsplist);
       nameofbutton = new ArrayList();


        Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(),"fonts/tg.otf");
        nsp.setTypeface(typeFace);
        pd = new ProgressDialog(getActivity());
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
        intializensplist();
return rootView;
    }




    public void onItemClick(String view, Activity a) {
        if (userid != null) {
            if (!userid.equals("null")) {
                try {SendfeedbackJob job = new SendfeedbackJob(a);{
                            job.execute(userid, view);
                         }
                } catch (Exception e) {
                    e.printStackTrace();
                }}}
    }

    private class SendfeedbackJob extends AsyncTask<String, String, String> {
        private Activity activity;
        public SendfeedbackJob(Activity activity) {
            this.activity = activity;

            dialog = new ProgressDialog(activity);
        }
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
                String POST_PARAMS = "meth="+params[1]+"&guid="+params[0];
                URL url = new URL("http://nsp.braincrop.net/Students/Reports");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                OutputStream os = c.getOutputStream();
                os.write(POST_PARAMS.getBytes());
                os.flush();
                os.close();
                Log.i("yellow",Integer.toString(c.getResponseCode()));
                if(c.getResponseCode()==500){
                    Intent ik = new Intent(getContext(),nsp_weblogin.class);
                    startActivity(ik);
                    getActivity().finish();}
                String PATH = Environment.getExternalStorageDirectory() + "/nixorapp/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, fileName+fileExtension);
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {fos.write(buffer, 0, len1);}
                fos.close();
                is.close();
                documentname=params[1];
                File from = new File(Environment.getExternalStorageDirectory() + "/nixorapp/","response.pdf");
                File to = new File(Environment.getExternalStorageDirectory() + "/nixorapp/",params[1]+".pdf");
                if(to.exists()){to.delete();}
                from.renameTo(to);
                Intent ij = new Intent(activity,pdfviewer.class);
                ij.putExtra("document",params[1]);
                startActivity(ij);
            } catch (Exception e) {exceptionToBeThrown=e;
                e.printStackTrace();}
            return params[1];
            }
        @Override
        protected void onPostExecute(final String message) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(exceptionToBeThrown!=null){
                Log.i("exception","yes");
                File zubaida = new File(Environment.getExternalStorageDirectory() + "/nixorapp/",message+".pdf");
                Log.i("exceptiion",message);
                if(zubaida.exists()){
                    if(pd.isShowing()){
                        pd.dismiss();}
                    Date lastModDate = new Date(zubaida.lastModified());
                    alertDialog.setTitle("Unable to fetch new version");
                    alertDialog.setMessage("We are unable to connect to server, however we have an older version of the document (From "+lastModDate+").");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show older version",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent ij = new Intent(activity,pdfviewer.class);
                                    ij.putExtra("document",message);
                                    startActivity(ij);}
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Never mind",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {}});
                    alertDialog.show();
                }else{
                    alertDialog.setTitle("Unable to fetch new version");
                    alertDialog.setMessage("We are unable to connect to server and no older versions of the document are available at this time");
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {}
                    });
                    alertDialog.show();}}
                 Log.i("message",message);}}

    public  String[] extractText(String yourPdfPath){
        try {String parsedText="";
            PdfReader reader = new PdfReader(yourPdfPath);
            int n = reader.getNumberOfPages();
            Log.i("info",reader.getInfo().toString());
            for (int i = 0; i <n ; i++) {
                parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n";}
            String[] formatted = parsedText.split("\n");
            System.out.println(formatted[1]);
            Log.i("textf",formatted[5]);
            String textStr[] = formatted[1].split("\n");
            Log.i("name",textStr[0]);
            reader.close();
            return formatted;
        } catch (Exception e) {
            System.out.println(e);}
        return null; }


public void intializensplist(){
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        ref.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("buttons").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue()!=null){
                    if(!nameofbutton.contains(dataSnapshot.getValue())){
                nameofbutton.add(dataSnapshot.getValue().toString());}
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue()!=null){
                    if(!nameofbutton.contains(dataSnapshot.getValue())){
                        nameofbutton.add(dataSnapshot.getValue().toString());}
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    if(!nameofbutton.contains(dataSnapshot.getValue())){
                        nameofbutton.add(dataSnapshot.getValue().toString());}
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue()!=null){
                    if(!nameofbutton.contains(dataSnapshot.getValue())){
                        nameofbutton.add(dataSnapshot.getValue().toString());}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        nsplist.setAdapter(new nspadapter(nameofbutton,this));

}
}
