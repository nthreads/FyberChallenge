package com.fyber.challenge.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.fyber.challenge.R;

/**
 * Created by Nauman Zubair on 13/10/2016.
 */
public class SnackbarUtil {
    private static Snackbar mSnackbar;

    public static void show(View view, String msg, final OnClickListener mCallback) {


        mSnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);

        mSnackbar.show();
        mSnackbar.setAction(R.string.lbl_retry, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClick(v);
            }
        });


    }
}
