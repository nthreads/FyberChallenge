package com.fyber.challenge.business;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.fyber.challenge.ApiException;
import com.fyber.challenge.InvalidHashException;
import com.fyber.challenge.R;
import com.fyber.challenge.utils.NetworkUtil;
import com.fyber.challenge.utils.SnackbarUtil;

import org.json.JSONObject;

import java.util.concurrent.TimeoutException;

import static com.fyber.challenge.business.DoInBackground.MethodType.GET;
import static com.fyber.challenge.business.DoInBackground.MethodType.POST;
import static com.fyber.challenge.business.DoInBackground.MethodType.PUT;

/**
 * Created by Nauman Zubair on 13/10/2016.
 */
public class DoInBackground extends AsyncTask<JSONObject, Void, String> {

    private static final String ERROR_TIMEOUT_EXCEPTION = "100";
    private static final String ERROR_API_EXCEPTION = "101";
    private static final String ERROR_NETWORK = "102";
    private static final String ERROR_INVALID_METHOD = "103";
    private static final String ERROR_INVALID_HASH = "104";

    private MethodType methodType;
    private ProgressDialog mProgress;
    private String url = "";
    private String message = "";
    private Context context;
    private boolean isCancelable = false;
    private OnPostExecutionListener mListener;
    private JSONObject[] paramJsonObjects;
    private View parentViewForSnackBar;
    private int REQUEST_CODE;

    public DoInBackground(Context context, String url, View parentViewForSnackBar) {
        this.context = context;
        this.url = url;
        this.methodType = GET;
        this.parentViewForSnackBar = parentViewForSnackBar;
    }

    public DoInBackground(Context context, String url, String message, View parentViewForSnackBar) {
        this(context, url, parentViewForSnackBar);
        this.message = message;
    }


    public DoInBackground(Context context, String url, String message, MethodType methodType, View parentViewForSnackBar) {
        this(context, url, message, parentViewForSnackBar);
        this.methodType = methodType;
    }

    public DoInBackground(Context context, String url, String message, MethodType methodType, boolean isCancelable, View parentViewForSnackBar) {
        this(context, url, message, methodType, parentViewForSnackBar);
        this.isCancelable = isCancelable;
    }

    public void SetListener(OnPostExecutionListener mListener) {
        this.mListener = mListener;
    }

    public void SetListener(OnPostExecutionListener mListener, int REQUEST_CODE) {
        this.mListener = mListener;
        this.REQUEST_CODE = REQUEST_CODE;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener == null) {
            showToast("Implement OnPostExecutionListener");
            this.cancel(true);
        }

        if (url == null || url.trim().isEmpty()) {
            showToast("Request url can not be empty");
            this.cancel(true);
        }

        if (!message.isEmpty()) {
            try {
                mProgress = ProgressDialog.show(context, "", message);
                mProgress.setCancelable(isCancelable);
                mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        paramJsonObjects = params;

        if (url.contains(" ")) url = url.replaceAll(" ", "%20");

        if (NetworkUtil.isNetworkAvailable(context)) {
            try {
                if (methodType == GET)
                    return WebBinder.doGet(url);
                else
                    return ERROR_INVALID_METHOD;
            } catch (TimeoutException e) {
                e.printStackTrace();
                return ERROR_TIMEOUT_EXCEPTION;
            } catch (ApiException e) {
                e.printStackTrace();
                return ERROR_API_EXCEPTION;
            } catch (InvalidHashException e) {
                e.printStackTrace();
                return ERROR_INVALID_HASH;
            }
        } else {
            return ERROR_NETWORK;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        showToast(context.getString(R.string.toast_task_cancelled));
    }

    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
        showToast(context.getString(R.string.toast_task_cancelled));
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (!message.isEmpty() && mProgress != null && mProgress.isShowing()) mProgress.dismiss();

        if (result == null) {
            retry(context.getString(R.string.toast_something_wrong));
            mListener.onPostExecution("", REQUEST_CODE);
            return;
        }

        if (result.equals(ERROR_TIMEOUT_EXCEPTION)) {
            retry(context.getString(R.string.toast_time_out));
        } else if (result.equals(ERROR_API_EXCEPTION)) {
            retry(context.getString(R.string.toast_something_wrong));
        } else if (result.equals(ERROR_NETWORK)) {
            retry(context.getString(R.string.toast_network_error));
        } else if (result.equals(ERROR_INVALID_METHOD)) {
            showToast("Select valid method type");
        } else if (result.equals(ERROR_INVALID_HASH)) {
            showToast(context.getString(R.string.err_invalid_hash));
        } else if (mListener != null) {
            mListener.onPostExecution(result, REQUEST_CODE);
        }

    }

    private void retry(String errMessage) {
        SnackbarUtil.show(parentViewForSnackBar, errMessage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoInBackground doInBackground = new DoInBackground(context, url, message, methodType, isCancelable, parentViewForSnackBar);
                doInBackground.SetListener(mListener, REQUEST_CODE);
                doInBackground.execute(paramJsonObjects);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public enum MethodType {
        GET(0), POST(1), PUT(2), DELETE(3);

        private int numVal;

        MethodType(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public interface OnPostExecutionListener {
        void onPostExecution(String response, int REQUEST_CODE);
    }
}
