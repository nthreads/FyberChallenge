package com.fyber.challenge.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fyber.challenge.FyberChallenge;
import com.fyber.challenge.R;
import com.fyber.challenge.business.DoInBackground;
import com.fyber.challenge.utils.HashingUtil;
import com.fyber.challenge.utils.NetworkUtil;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by Nauman Zubair on 13/10/2016.
 */
public class ChallengeActivity extends AppCompatActivity implements DoInBackground.OnPostExecutionListener {

    private static final String TAG = "ChallengeActivity";

    final String BASE_URL = "http://api.fyber.com/feed/v1/offers.json";

    final String KEY_FORMAT = "format"; //Keys to append in query.
    final String KEY_APP_ID = "appid";
    final String KEY_UID = "uid";
    final String KEY_DEVICE_ID = "device_id";
    final String KEY_LOCALE = "locale";
    final String KEY_IP = "ip";
    final String KEY_HASH_KEY = "hashkey";
    final String KEY_TIMESTAMP = "timestamp";
    final String KEY_PUB = "pub0";
    final String KEY_PS_TIME = "ps_time";

    String deviceId = "";

    TextInputLayout tilUid, tilAppId, tilApiKey, tilPub;
    EditText etUid, etAppId, etApiKey, etPub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        tilUid = (TextInputLayout) findViewById(R.id.tilUid);
        tilAppId = (TextInputLayout) findViewById(R.id.tilAppId);
        tilApiKey = (TextInputLayout) findViewById(R.id.tilApiKey);
        tilPub = (TextInputLayout) findViewById(R.id.tilPub);

        etUid = (EditText) findViewById(R.id.etUid);
        etAppId = (EditText) findViewById(R.id.etAppId);
        etApiKey = (EditText) findViewById(R.id.etApiKey);
        etPub = (EditText) findViewById(R.id.etPub);

        etUid.setText(FyberChallenge.UID);
        etAppId.setText(FyberChallenge.APP_ID);
        etApiKey.setText(FyberChallenge.API_KEY);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(FyberChallenge.EXTRAS_DEVICE_ID)) {
            deviceId = extras.getString(FyberChallenge.EXTRAS_DEVICE_ID);
        }
    }


    //Get offers response in background, callback will be returned in onPostExecution
    public void btnGetOffersOnClick(View view) {
        if (!validateInput(etUid, tilUid, getString(R.string.err_msg, getString(R.string.hint_uid)))
                || !validateInput(etAppId, tilAppId, getString(R.string.err_msg, getString(R.string.hint_app_id)))
                || !validateInput(etApiKey, tilApiKey, getString(R.string.err_msg, getString(R.string.hint_api_key)))
                /*|| !validateInput(etPub, tilPub, getString(R.string.err_msg, getString(R.string.hint_pub)))*/ //Optional
                )
            return;

        String uid = etUid.getText().toString().trim();
        String appId = etAppId.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();
        String pub = etPub.getText().toString().trim();

        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("?");

        //Alphabetical order to make hash
        StringBuilder sb = new StringBuilder();
        sb.append(KEY_APP_ID).append("=").append(appId).append("&");
        sb.append(KEY_DEVICE_ID).append("=").append(deviceId).append("&");
        sb.append(KEY_IP).append("=").append(NetworkUtil.getIPAddress(true)).append("&");
        sb.append(KEY_LOCALE).append("=").append("de").append("&");
        sb.append(KEY_PS_TIME).append("=").append(String.valueOf(System.currentTimeMillis() / 1000)).append("&");
        sb.append(KEY_PUB).append("=").append(pub).append("&");
        sb.append(KEY_TIMESTAMP).append("=").append(String.valueOf(System.currentTimeMillis() / 1000)).append("&");
        sb.append(KEY_UID).append("=").append(uid).append("&");

        //Append API_KEY (Provided by fyber) with Concatenated query request
        String queryString = sb.toString() + apiKey;

        Log.d(TAG, "Query String = " + queryString);

        String hashKey = HashingUtil.makeHashKeyUsingSHA1(queryString);
        Log.d(TAG, "QS HASH = " + hashKey);

        //Complete URL using concatenated string and hash key
        urlBuilder.append(sb.toString());
        urlBuilder.append(KEY_HASH_KEY).append("=").append(hashKey);

        Log.d(TAG, "URL = " + urlBuilder.toString());

        DoInBackground doInBackground = new DoInBackground(this, urlBuilder.toString(), getString(R.string.toast_loading_offers), tilPub);
        doInBackground.SetListener(this);
        doInBackground.execute();

    }

    private boolean validateInput(EditText etInput, TextInputLayout til, String errMsg) {
        if (etInput.getText().toString().trim().isEmpty()) {
            til.setError(errMsg);
            requestFocus(etInput);
            return false;
        } else {
            til.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onPostExecution(String response, int REQUEST_CODE) {

        try {
            JSONObject responseObject = new JSONObject(response);
            if(responseObject.has("offers")) {
                JSONArray jArrayOffers = responseObject.getJSONArray("offers");
                if(jArrayOffers.length() > 0) {
                    Intent intent = new Intent(this, OffersActivity.class);
                    intent.putExtra(FyberChallenge.EXTRAS_OFFERS, jArrayOffers.toString());
                    startActivity(intent);
                } else {
                    showToast(getString(R.string.msg_no_offer));
                }
            } else if(responseObject.has("message")) {
                showToast(responseObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error " + response);
        }

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
