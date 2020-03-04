package com.example.developer.instgramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.example.developer.instgramclone.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResourse;
    private String mAppend;
    private ArrayList<String> imgUrls;

    public GridImageAdapter(Context mContext, int layoutResourse, String mAppend, ArrayList<String> imgUrls) {
        super(mContext, layoutResourse, imgUrls);
        this.mContext = mContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResourse = layoutResourse;
        this.mAppend = mAppend;
        this.imgUrls = imgUrls;
    }


    //holder class for gridView
    private static class ViewHolder {
        SquareImageView image;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResourse, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.progressBar = convertView.findViewById(R.id.gridImageProgrerssBar);
            viewHolder.image = convertView.findViewById(R.id.gridImageView);

            //store widget in the memory
            //save memory
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imgUrl = getItem(position);
        ImageLoader loader = ImageLoader.getInstance();
        loader.displayImage(mAppend + imgUrl, viewHolder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (viewHolder.progressBar != null) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }
        });
        return convertView;
    }
}
