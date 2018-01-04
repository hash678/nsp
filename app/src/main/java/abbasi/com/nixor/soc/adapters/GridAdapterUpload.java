package abbasi.com.nixor.soc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abbasi.com.nixor.R;
import id.zelory.compressor.Compressor;

/**
 * Created by Anjum on 12/3/2017.
 */

public class GridAdapterUpload  extends RecyclerView.Adapter<GridAdapterUpload.ViewHolder> {
    private ArrayList<String> mItems;
    private  MyClickListener myClickListener;
    private Context mContext;
    private String TAG="GridAdapter";
    //private ProgressBar mProgressBar;

    public GridAdapterUpload(ArrayList<String> items,Context mContext) {
        this.mItems = items;
        this.mContext= mContext;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //viewHolder.mProgbar.setVisibility(View.VISIBLE);
/*
        Picasso.with(mContext)
                .load(new File(mItems.get(position)))
                .into(viewHolder.imgThumbnail, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.mProgbar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError() {
                        viewHolder.mProgbar.setVisibility(View.INVISIBLE);
                    }
                });

*/
        File ndnd = new File(mItems.get(position));
        try {

            Bitmap compressed   = new Compressor(mContext)

                    .setQuality(25)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToBitmap(ndnd);
            viewHolder.imgThumbnail.setImageBitmap(compressed);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgThumbnail;
        public ProgressBar mProgbar;
        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            mProgbar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            // itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            myClickListener.onItemClick(getLayoutPosition(), v);
        }
    }
    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


}