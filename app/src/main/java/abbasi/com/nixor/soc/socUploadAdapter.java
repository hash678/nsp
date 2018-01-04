package abbasi.com.nixor.soc;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import abbasi.com.nixor.R;
import pl.droidsonroids.gif.GifImageView;


public class socUploadAdapter extends BaseAdapter implements OnClickListener {
    ArrayList<String> result;
    Context context;
    socupload acti;
   ArrayList imageId;
String listtype;
    String groupid;
    String classname;
    private static LayoutInflater inflater=null;
    public socUploadAdapter(socupload mainActivity, ArrayList<String> prgmNameList, ArrayList<String>  prgmImages, String downloa, String group, String classname1) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        acti=mainActivity;
        imageId=prgmImages;
        listtype=downloa;
        groupid=group;
        classname= classname1;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public void onClick(View view) {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.customloading);
            dialog.setTitle("Loading");


            // set the custom dialog components - text, image and button
            TextView text = (TextView) dialog.findViewById(R.id.loadingtext);
            GifImageView daa = (GifImageView) dialog.findViewById(R.id.imgdi);
            text.setText("");
            Glide.with(context)
                    .load(view.getTag().toString())
                    .into(daa);
            daa.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Dialog dialoga = new Dialog(context);
                    dialoga.setTitle("Download image?");
                    dialoga.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    // set the custom dialog components - text, image and button


                    return false;
                }
            });


            // if button is clicked, close the custom dialog

            dialog.show();


        }
           // Intent ij = new Intent(context,abbasi.com.nixor.soc.socclass.class);
           // ij.putExtra("Classname",view.getTag().toString());
            //context.startActivity(ij);
           // Log.i("Gothere","yes");





    public class Holder
    {
        TextView tv;
        TextView td;
        ImageView img;
        RelativeLayout thumbnail;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.musicrow, null);

try{
            holder.thumbnail=(RelativeLayout) rowView.findViewById(R.id.thumbnail);
            holder.img =(ImageView)rowView.findViewById(R.id.imagerowmusic);
            holder.tv=(TextView) rowView.findViewById(R.id.Hour);
            holder.td=(TextView) rowView.findViewById(R.id.buddyrowmusic);

            holder.tv.setText(result.get(position));
if(imageId.size()!=0) {
    Glide.with(context)
            .load(imageId.get(position))
            .into(holder.img);
}
            holder.thumbnail.setOnClickListener(this);
if(imageId.size()!=0){
            holder.thumbnail.setTag(imageId.get(position));
            holder.img.setTag(imageId.get(position));
            holder.img.setTag(imageId.get(position));
            holder.td.setTag(imageId.get(position));
            holder.tv.setTag(imageId.get(position));
        }}catch (Exception e){e.printStackTrace();}
        return rowView;


    }





}