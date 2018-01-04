package abbasi.com.nixor.drafts;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import abbasi.com.nixor.R;

public class old_draft2 extends AppCompatActivity {
    String sid;
    EditText editText;
    ProgressBar pb;
    FirebaseDatabase ref;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {

            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
         };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE

            );
        }}
    private void openPdf(String urddl) {

        File file = new File(urddl);
        Uri path = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(path);
        intent.setType("application/pdf");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found",
                    Toast.LENGTH_SHORT).show();
        }

    }
    int x =981;
    public void onget(View view){
        sid=editText.getText().toString();
        for(int z=2;z<2264;z++){

            String name = extractText(Environment.getExternalStorageDirectory() + "/nixor/"+z+".pdf");
            if(name!=null){

                File from = new File(Environment.getExternalStorageDirectory() + "/nixor/",z+".pdf");
                File to = new File(Environment.getExternalStorageDirectory() + "/nixor/",name+".pdf");
                if(from.exists()){
                from.renameTo(to);}
                System.out.println(name);
     ref.getReference().child("students").child(Integer.toString(z)).setValue(name);




            }
        }

        /*
        for(int y= 256; x<2264;x++){
    sid=Integer.toString(x);
pb.setMax(2263);
    try {
        SendfeedbackJob job = new SendfeedbackJob();
        job.execute(sid);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
*/
    }
public void download(View view){
    for(int y= 981; x<2264;x++){
        sid=Integer.toString(x);
        pb.setMax(2263);
        try {
            SendfeedbackJob job = new SendfeedbackJob();
            job.execute(sid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    public  String extractText(String yourPdfPath){

        try {
            String parsedText="";
            PdfReader reader = new PdfReader(yourPdfPath);
            int n = reader.getNumberOfPages();
            for (int i = 0; i <n ; i++) {
                parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n"; //Extracting the content from the different pages
            }
            String[] formatted = parsedText.split("\n");

            System.out.println(formatted[1]);

            Log.i("textf",formatted[5]);//name

            String textStr[] = formatted[1].split("\n");
            Log.i("name",textStr[0]);
            reader.close();
            return formatted[5];
        } catch (Exception e) {
            System.out.println(e);

        }
    return null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        verifyStoragePermissions(this);
ref = FirebaseDatabase.getInstance();

        editText =(EditText)findViewById(R.id.email);
//pb = (ProgressBar)findViewById(R.id.progressBar2);

    }
    private class SendfeedbackJob extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            try {


                String fileName="response";
                String fileExtension=".pdf";

//           download pdf file.




        String POST_PARAMS = "meth=Profile&sid="+params[0];

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
                String PATH = Environment.getExternalStorageDirectory() + "/nixor/";
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
                File from = new File(Environment.getExternalStorageDirectory() + "/nixor/","response.pdf");
                File to = new File(Environment.getExternalStorageDirectory() + "/nixor/",params[0]+".pdf");
                from.renameTo(to);

              //  System.out.println("--pdf downloaded--ok--"+"http://nsp.braincrop.net/Students/Reports");
            } catch (Exception e) {
                e.printStackTrace();

            }
        return "done";
        }
        @Override
        protected void onPostExecute(String message) {

old_draft2 pl = new old_draft2();

        }
    }
}
/*
*
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            button3.setEnabled(false);
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        mArrayUri = new ArrayList<String>();
                        filenames = new ArrayList<String>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);

                            Uri uri = item.getUri();


                            filenames.add(uri.getLastPathSegment());
mArrayUri.add(uri.toString());


                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                        }
                        mAdapter = new GridAdapterUpload(mArrayUri, getApplicationContext());
                        recyclerView.setAdapter(mAdapter);


                        // horizontal_recycler_view= (RecyclerView) findViewById(R.id.horizontal_recycler_view);
                        //horizontalAdapter=new HorizontalAdapter(mArrayUri,this,mArrayUri.size());
                        numimages.setText("Number of images selected: "+mArrayUri.size());
                        if(mArrayUri.size()>0){
                            button3.setEnabled(true);
                            button2.setText("Reselect Images");
                        }

                       // horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                       // horizontal_recycler_view.setAdapter(horizontalAdapter);
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());


                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

* */