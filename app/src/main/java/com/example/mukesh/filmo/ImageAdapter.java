package com.example.mukesh.filmo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by mukesh on 20/3/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] image_url;
    public ImageAdapter(Context c, String[] image_url) {

        mContext = c;
        this.image_url=image_url;
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


        //imageView.setImageResource(mThumbIds[position]);
        Picasso.with(mContext).load(image_url[position]).into(imageView);
        return imageView;
    }

    // references to our images
    private String[] mThumbIds = {
            "https://www.planwallpaper.com/static/images/9-credit-1.jpg", "https://www.planwallpaper.com/static/images/9-credit-1.jpg",
           "https://www.planwallpaper.com/static/images/9-credit-1.jpg"
    };

}
