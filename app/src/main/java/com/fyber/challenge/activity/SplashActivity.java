package com.fyber.challenge.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import com.fyber.challenge.FyberChallenge;
import com.fyber.challenge.R;
import com.fyber.challenge.fragment.widget.WarningDialogFragment;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * Created by Nauman Zubair on 13/10/2016.
 */
public class SplashActivity extends AppCompatActivity implements WarningDialogFragment.WarningDialogListener {

    private static final String DIALOG_TAG = "GaidError";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GetAdvertisingInfo().execute(); //Find Device Id in AsyncTask
    }

    //AsyncTask to find google advertising id
    //incase of exception it will use Android/Device identifier
    private class GetAdvertisingInfo extends AsyncTask<Void, Void, String> {

        String errorMessage;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... voids) {

            String advertisingId = "";
            try {
                advertisingId = AdvertisingIdClient.getAdvertisingIdInfo(SplashActivity.this).getId();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = getString(R.string.err_gaid_io_exception);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                errorMessage = getString(R.string.err_gaid_gps_repairable);
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
                errorMessage = getString(R.string.err_gaid_gps_not_available);
            }

            //Incase google play services are not available
            if (advertisingId == null || advertisingId.isEmpty()) {
                TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephony.getDeviceId() != null) {
                    advertisingId = mTelephony.getDeviceId(); // use for mobiles
                } else {
                    advertisingId = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID); // use for tablets/non-mobile
                }
            }

            return advertisingId;
        }

        @Override
        protected void onPostExecute(String deviceId) {
            super.onPostExecute(deviceId);

            if (errorMessage != null) {
                WarningDialogFragment dialog = new WarningDialogFragment(getString(R.string.lbl_gaid), errorMessage, "OK", "");
                dialog.show(getSupportFragmentManager(), DIALOG_TAG);
            } else {
                Intent intent = new Intent(SplashActivity.this, ChallengeActivity.class);
                intent.putExtra(FyberChallenge.EXTRAS_DEVICE_ID, deviceId);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog.getTag().equals(DIALOG_TAG)) finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

}
