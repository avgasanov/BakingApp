package com.example.android.bakingapp.BakingUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {
    final public static String RECEPIES_REQUEST_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    final public static String TAG = "NETWORK_UTILS";

    public static URL buildRequestUrlFromString(String requestUrlStr) {
        try {
            return new URL(requestUrlStr);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid url: " + requestUrlStr);
            return null;
        }
    }
    public static String getJsonResponseFromUrl(URL requestUrl) throws IOException{
        if (requestUrl == null) {
            Log.e(TAG, "URL is null");
            return null;
        }

        HttpsURLConnection connection = (HttpsURLConnection) requestUrl.openConnection();

        try {
            InputStream responseStream = connection.getInputStream();

            Scanner scanner = new Scanner(responseStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        } finally {
            connection.disconnect();
        }
    }

    public static boolean isConnected(Context context) {
        try{
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =
                    connectivityManager.getActiveNetworkInfo();
            return networkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
