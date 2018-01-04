package abbasi.com.nixor.portal;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import abbasi.com.nixor.R;

public class pdfviewer extends AppCompatActivity {
PDFView pdfView;
    Toolbar mActionBarToolbar;

    String documentname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
//        mActionBarToolbar =(Toolbar)findViewById(R.id.toolbar);
        Intent ij = getIntent();
        documentname = ij.getStringExtra("document");
  //      mActionBarToolbar.setTitle(documentname);
        pdfView =(PDFView)findViewById(R.id.pdfView);
       File file =new File(Environment.getExternalStorageDirectory() + "/nixorapp/",documentname+".pdf");
        if(file.exists()){
            Log.i("es","ues");
            pdfView.fromFile(file)
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    // allows to draw something on the current page, usually visible in the middle of the screen
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
                    .load();
        }else {
            Log.i("es","no");
        }


    }
}
