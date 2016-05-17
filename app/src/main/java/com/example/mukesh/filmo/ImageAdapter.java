package com.example.mukesh.filmo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mukesh on 20/3/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] image_url;
    private int connectivity;
    public ImageAdapter(Context c, String[] image_url, int connectivity) {

        mContext = c;
        this.image_url=image_url;
        this.connectivity=connectivity;
    }

    public int getCount() {
        return image_url.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {

            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        if(connectivity==1) {
            if (image_url[position] == "empty") {
                Picasso.with(mContext).load(R.drawable.posternotfound).into(imageView);
            } else {
                Picasso.with(mContext).load(image_url[position]).into(imageView);
                System.out.println("i am");
            }
            store_image(mContext,image_url[position]);
        }
        else
            Picasso.with(mContext).load(new File(image_url[position])).into(imageView);
        return imageView;
    }
    public String store_image(Context context,String path){

        final String imagepath=path;
        //Define target point
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),imagepath.replace("http://image.tmdb.org/t/p/w342/",""));
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };

        Picasso.with(context)
                .load(path)
                .into(target);
        return  null;
    }

}
