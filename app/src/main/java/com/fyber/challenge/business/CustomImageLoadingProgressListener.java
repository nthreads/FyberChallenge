package com.fyber.challenge.business;

import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
/**
 * Created by Nauman Zubair on 15/10/2016.
 */
public class CustomImageLoadingProgressListener  implements ImageLoadingProgressListener {

    ProgressBar progress;

    public CustomImageLoadingProgressListener() {
        progress = null;
    }

    public CustomImageLoadingProgressListener(ProgressBar progress) {
        this.progress = progress;
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
        if(progress != null) progress.setProgress(Math.round(100.0f * current / total));
    }
}