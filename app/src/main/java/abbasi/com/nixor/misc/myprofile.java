package abbasi.com.nixor.misc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import abbasi.com.nixor.R;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class myprofile extends AppCompatActivity {
    String Username;
    String studentid;
    String name;
    String location;
    Bitmap photo;
    TextView username;
    TextView email;
    TextView studentiddd;
    ProgressDialog progress;
    FirebaseDatabase ref;

    CircleImageView dp;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        verifyStoragePermissions(this);
        ref=FirebaseDatabase.getInstance();
        username = (TextView)findViewById(R.id.username);
        email = (TextView)findViewById(R.id.email);
        studentiddd = (TextView)findViewById(R.id.studentid);
        getPreviousdp();
        dp =(CircleImageView)findViewById(R.id.dp);
    Username = getIntent().getStringExtra("username");
    name = getIntent().getStringExtra("Name");
    studentid = getIntent().getStringExtra("studentid");
    username.setText(Username);
    username.setAllCaps(true);
    email.setText(Username.replaceAll("-",".")+"@nixorcollege.edu.pk");
    studentiddd.setText(studentid);

    }
    public void uploadPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);

        startActivityForResult(intent,9);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 9&&resultCode==RESULT_OK&&data!=null) {
            final Bundle extras = data.getExtras();

            if (extras != null) {

                photo = extras.getParcelable("data");


                Glide.get(this).clearMemory();



                dp.setImageBitmap(photo);
                try {
                    location= saveToInternalStorage(photo)+"/profile.jpg";

                } catch (IOException e) {
                    e.printStackTrace();
                }
//            MainActivity mainActivity = new MainActivity();
                //         location = mainActivity.getRealPathFromURI(this, savebitmap());

            }
        }

    }
    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
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
    public void uploadFile(String string, final String username1){


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://nixor-9afe0.appspot.com/");
        File file = new File(string);
       Uri filename = Uri.fromFile(file);
        byte[] data=null;
        try {
            Bitmap compressed = new Compressor(this).compressToBitmap(file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressed.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageReference riversRef = storageRef.child(username1+"/"+"Dp"+filename.getLastPathSegment());
if (data!=null){
        UploadTask uploadTask = riversRef.putBytes(data);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progress.dismiss();
                Log.i("log","failsss");
                Toast.makeText(getApplicationContext(),"Profile picture upload failed. Check you connection.",Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(myprofile.this)
                        .setTitle("Problem setting picture")
                        .setMessage("Profile picture cannot be set")
                        //If he selects accept
                        .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        //If he declines
                        .setNegativeButton("Never mind", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                location = null;
                                ref.getReference().child("users").child(username1).child("photourl").setValue("null");
                                startActivity(new Intent(myprofile.this, Tabs.class));
                                finish();
                            }
                        })
                        //Sets happy icon on Session request
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.


                Log.i("log","Success");
                progress.dismiss();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                ref.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photourl").setValue(downloadUrl.toString());
                Log.i("Log",downloadUrl.toString());
                Glide.get(myprofile.this).clearMemory();
                photo.recycle();
                startActivity(new Intent(myprofile.this, Tabs.class));
                finish();

            }
        });


    }}
    public void onSetbtn(View v){

        progress = ProgressDialog.show(this, "Please wait",
                "UPDATING", true);

            if (location != null) {
                uploadFile(location,Username);

            }else{
                startActivity(new Intent(myprofile.this, Tabs.class));
                finish();

            }

        }


public void getPreviousdp(){
        ref.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("photourl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Glide.with(myprofile.this)
                            .load(dataSnapshot.getValue().toString())
                            .error(R.drawable.profile) // will be displayed if the image cannot be loaded
                            .crossFade()
                            .into(dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



}
    }
