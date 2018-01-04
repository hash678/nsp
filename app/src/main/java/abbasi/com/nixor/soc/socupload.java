package abbasi.com.nixor.soc;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import abbasi.com.nixor.misc.Tabs;
import abbasi.com.nixor.soc.adapters.AutofitRecyclerView;
import abbasi.com.nixor.soc.adapters.GridAdapterUpload;
import abbasi.com.nixor.R;
import id.zelory.compressor.Compressor;

public class socupload extends AppCompatActivity {
private StorageReference sr;
    Bitmap photo;
    String location;
    String classname;
    String username;
    String filename;
    String imageEncoded;
    List<String> imagesEncodedList;
    ArrayList<String> mArrayUri;
    ArrayList<String> imageslocation;
    TextView foldername;
    ArrayList<String> downloadlinks;
    ArrayList<String> imageslist;
    ListView soclist;


Button button3;
Button button2;
    ArrayList<String> filenames;
    GridAdapterUpload mAdapter;
    Dialog d;
    FirebaseDatabase db;
    public TextView numimages;
    private AutofitRecyclerView recyclerView;
    //private ArrayList<String> horizontalList;
   // private HorizontalAdapter horizontalAdapter;
    ;
    private int PICK_IMAGE_REQUEST = 1;
    int done=0;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharksoncloud);
        sr = FirebaseStorage.getInstance().getReference();
        db= FirebaseDatabase.getInstance();

    Intent ia = getIntent();
        classname=ia.getStringExtra("Classname");
        username= abbasi.com.nixor.misc.Tabs.Username;
        button3= (Button)findViewById(R.id.button3);
        button2= (Button)findViewById(R.id.button2);
        numimages = (TextView) findViewById(R.id.numofimages);
        recyclerView= (AutofitRecyclerView) findViewById(R.id.recyclerView_myc); //recyclerView_myc will be autofitrecyelrview
        recyclerView.setVisibility(View.VISIBLE);
        imageslocation = new ArrayList<>();
        imageslist = new ArrayList<>();
        downloadlinks = new ArrayList<>();
        foldername = (TextView)findViewById(R.id.folderdir);
        soclist = (ListView)findViewById(R.id.myfolder);
        getuserlist(username,classname);
        verifyStoragePermissions(this);



    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void uploadfilebutton(View view){

        if(mArrayUri!=null){
            for (int i=0;i<mArrayUri.size();i++){
                try {

                    showLoadin();

                  //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse(mArrayUri.get(i)));

File imageb = new File(mArrayUri.get(i));
if(imageb.exists()){
    Log.i("imageb","exists");
}
Log.i("imageb",imageb.getAbsolutePath());
Bitmap compressed = new Compressor(this).compressToBitmap(imageb);
//String location =saveToInternalStorage(compressed,Uri.parse(mArrayUri.get(i)).getLastPathSegment())+"/"+Uri.parse(mArrayUri.get(i)).getLastPathSegment()+".jpg";

                    uploadMyFile(db.getReference().child("soc").child(classname).push().getKey(),filenames.get(i),compressed);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
        }


    }

    public void uploadMyFile( final String fname, final String filenamesss,Bitmap bm){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://nixor-9afe0.appspot.com/");

        StorageReference riversRef = storageRef.child(classname+"/"+username+"/"+fname+"lol");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();



        riversRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        System.out.println(downloadUrl);
                        String documentid= db.getReference().child("soc").child(classname).child(username).child(fname).push().getKey().toString();
                       db.getReference().child("soc").child(classname).child(username).child(fname).child("url").setValue(taskSnapshot.getDownloadUrl().toString());
                       db.getReference().child("soc").child(classname).child(username).child(fname).child("name").setValue(filenamesss);
                   done++;
                        if(done==mArrayUri.size()){
                       if(d!=null){
                           d.dismiss();
                           d=null;
                           Toast.makeText(socupload.this,"Upload complete",Toast.LENGTH_SHORT).show();
                           recyclerView.setAdapter(null);
                       }

                   }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                }

                );
    }
    public void downloadMyFile(){
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sr.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }
    public void uploadMyImage(View view){
        Intent intent = new Intent(this, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
        startActivityForResult(intent, 999);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
Log.i("size",Integer.toString(images.size()));
            mArrayUri = new ArrayList<String>();
            filenames = new ArrayList<String>();
for (int i = 0; i < images.size(); i++) {

                mArrayUri.add(images.get(i).path);
                filenames.add(images.get(i).name);
            }

if(mArrayUri.size()!=0){
    Log.i("size2",Integer.toString(mArrayUri.size()));
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

        }        }
    public void showLoadin(){
        if(d==null){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.customloading);
        dialog.setTitle("Loading");
        dialog.setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);
        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.loadingtext);
        text.setText("Uploading your images...");



        // if button is clicked, close the custom dialog

        dialog.show();
   d=dialog;}
    }



    private String saveToInternalStorage(Bitmap bitmapImage, String name) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        String PATH = Environment.getExternalStorageDirectory() + "/nixorapp/";
        File directory = new File(PATH);
        // Create imageDir
        File mypath=new File(directory,name+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
          //  bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        Log.i("location",directory.getAbsolutePath());
        imageslocation.add(directory.getAbsolutePath());
        return directory.getAbsolutePath();

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"It seems there is a problem loading your current display photo",Toast.LENGTH_SHORT).show();
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }





    public void getuserlist(String bucketusername, String Classname){

        foldername.setText(bucketusername.toUpperCase()+"' "+"BUCKET"+"- "+Classname);
        if(soclist!=null){
            soclist.setAdapter(new abbasi.com.nixor.soc.socUploadAdapter(socupload.this,imageslist,downloadlinks,null,null,classname));
        }

        db.getReference().child("soc").child(Classname).child(bucketusername).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("name").getValue()!=null){
                    if(!imageslist.contains(dataSnapshot.child("name").getValue().toString())){
                        imageslist.add(dataSnapshot.child("name").getValue().toString());
                        downloadlinks.add(dataSnapshot.child("url").getValue().toString());
                        Log.i("dlinks",dataSnapshot.child("url").getValue().toString());
                        soclist.setAdapter(new abbasi.com.nixor.soc.socUploadAdapter(socupload.this,imageslist,downloadlinks,null,null,classname));
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



}
