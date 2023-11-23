package com.nimmble.rgpro.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtil {
    public static boolean isCellularDataAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    public static void makeHttpGetRequest(String urlString, Context context, NetworkResponseCallback callback) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

            cm.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    callback.onError(new IOException("Cellular network not available"));
                }

                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    try {
                        URL url = new URL(urlString);
                        HttpURLConnection urlConnection = (HttpURLConnection) network.openConnection(url);
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            callback.onResponse(response.toString());
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    callback.onError(new IOException("Cellular network lost"));
                }
            });
        } catch (Exception e) {
            callback.onError(new IOException("Cellular network lost"));
        }
    }

    public interface NetworkResponseCallback {
        void onResponse(String response);

        void onError(Exception e);
    }
}

