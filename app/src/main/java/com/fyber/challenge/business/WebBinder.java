package com.fyber.challenge.business;

import android.util.Base64;
import android.util.Log;

import com.fyber.challenge.ApiException;
import com.fyber.challenge.FyberChallenge;
import com.fyber.challenge.InvalidHashException;
import com.fyber.challenge.utils.HashingUtil;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nauman Zubair on 13/10/2016.
 */
public class WebBinder {

    private static String AUTH_USERNAME = "";
    private static String AUTH_PASSWORD = "";

    private static final String HEADER_KEY_SIGNATURE = "X-Sponsorpay-Response-Signature";

    private static String TAG = "WebBinder";

    private static String addAuthHeader() {
        String authStr = AUTH_USERNAME + ":" + AUTH_PASSWORD;
        // Encode authentication values, and add to header
        String authStrEncoded = "";
        try {
            authStrEncoded = new String(Base64.encode(authStr.getBytes("UTF-8"), Base64.NO_WRAP));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return authStrEncoded;
    }

    public static String doGet(String urlStr) throws TimeoutException, ApiException, InvalidHashException {
        String resultResponse = null;

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlStr.replaceAll(" ", "%20"));

            urlConnection = httpURLConnectionWithBasicSetup(url, "GET");
            resultResponse = readStream(urlConnection);

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                throw new ApiException(resultResponse);
            } else {

                //Validate Response Signatures
                String responseSignature = urlConnection.getHeaderField(HEADER_KEY_SIGNATURE);
                String hash = HashingUtil.makeHashKeyUsingSHA1(resultResponse + FyberChallenge.API_KEY);

                Log.d(TAG, responseSignature + " : " + hash);

                if(!hash.equals(responseSignature)) {
                    throw new InvalidHashException();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultResponse = "Bad Request";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return resultResponse;
    }


    private static HttpURLConnection httpURLConnectionWithBasicSetup(URL url, String methodType) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(methodType);
        urlConnection.setConnectTimeout(30000);
        //urlConnection.addRequestProperty("Authorization", "Basic " + addAuthHeader());

        if (!methodType.equalsIgnoreCase("GET")) {
            urlConnection.setDoInput(true);
            urlConnection.addRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "application/json");
        }
        return urlConnection;
    }


    @SuppressWarnings("unused")
    private static String StringValidation(String result) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;

        if (SDK_INT < 14) {
            String expression = "[^a-zA-Z0-9\",.@!\\s-:\\[|\\]\\{|\\}]";
            result = result.replaceAll(expression, "");
        }

        return result;
    }

    private static void writeStream(HttpURLConnection urlConnection, JSONObject data) {
        try {
            Log.d(TAG, "Data ==> " + data.toString());

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.write(data.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readStream(HttpURLConnection urlConnection) throws IllegalStateException, IOException {
        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = inputStream.read(b);
            if (n > 0)
                out.append(new String(b, 0, n));
        }
        inputStream.close();

        Log.d(TAG, "Response : " + out.toString());
        return out.toString();
    }
}
