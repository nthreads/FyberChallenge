package com.fyber.challenge.fragment.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.fyber.challenge.R;
import com.fyber.challenge.enums.HostTag;


public class WarningDialogFragment extends DialogFragment {

    /*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event call-backs. Each
	 * method passes the DialogFragment in case the host needs to query it.
	 */
    public interface WarningDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    WarningDialogListener mListener;
    private String title, message, positiveBtnLabel, negativeBtnLabel;
    private HostTag tag;
    private Fragment fragment;

    public WarningDialogFragment() {
        this.title = "";
        this.message = "";
        this.positiveBtnLabel = "";
        this.negativeBtnLabel = "";
        this.tag = HostTag.ACTIVITY;
        this.fragment = null;
    }

    /**
     * Use this constructor for activities
     */
    @SuppressLint("ValidFragment")
    public WarningDialogFragment(String title, String message, String pBtnLabel, String nBtnLabel) {
        this();
        this.title = title;
        this.message = message;
        this.positiveBtnLabel = pBtnLabel;
        this.negativeBtnLabel = nBtnLabel;
        this.tag = HostTag.ACTIVITY;
    }

    /**
     * Use this constructor for fragments
     * For fragment use other constructor with fragment tag as parameter
     */

    @SuppressLint("ValidFragment")
    public WarningDialogFragment(String title, String message, String pBtnLabel, String nBtnLabel, Fragment fragment) {
        this(title, message, pBtnLabel, nBtnLabel);
        this.tag = HostTag.FRAGMENT;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message)
                .setPositiveButton(positiveBtnLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Send the positive button event back to the
                                // host activity/fragment
                                if (mListener != null)
                                    mListener.onDialogPositiveClick(WarningDialogFragment.this);
                            }
                        })
                .setNegativeButton(negativeBtnLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Send the negative button event back to the
                                // host activity/fragment
                                if (mListener != null)
                                    mListener.onDialogNegativeClick(WarningDialogFragment.this);
                            }
                        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            if (tag == HostTag.ACTIVITY) {
                mListener = (WarningDialogListener) activity;
            } else if (tag == HostTag.FRAGMENT) {
                mListener = (WarningDialogListener) fragment;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("You must implement WarningDialogListener");
        }
    }
}
