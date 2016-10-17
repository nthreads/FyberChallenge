
package com.fyber.challenge.business;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
/**
 * Created by Nauman Zubair on 15/10/2016.
 */
public class CustomImageLoadingListener extends SimpleImageLoadingListener {
    private ProgressBar progress;

    public CustomImageLoadingListener() {
        progress = null;
    }

    public CustomImageLoadingListener(ProgressBar progress) {
        this.progress = progress;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if(progress != null) {
            progress.setProgress(0);
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        if(progress != null) progress.setVisibility(View.GONE);
        switch (failReason.getType()) {
                case IO_ERROR:
                    break;
                case DECODING_ERROR:
                    break;
                case NETWORK_DENIED:
                    break;
                case OUT_OF_MEMORY:
                    if(ImageLoader.getInstance()!= null) {
                        ImageLoader.getInstance().clearDiskCache();
                        ImageLoader.getInstance().clearMemoryCache();
                    }
                    break;
                case UNKNOWN:
                    break;
            }
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if(progress != null) progress.setVisibility(View.GONE);
    }
}
