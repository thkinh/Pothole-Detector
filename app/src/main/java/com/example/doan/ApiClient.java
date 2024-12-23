package com.example.doan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    public interface ApiCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }

    public static void getPotholes(String user, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("http://171.249.144.189:8080/api/pothole/get?user=" + user);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    callback.onSuccess(response.toString());
                } else {
                    callback.onFailure(new Exception("Failed to fetch data: " + responseCode));
                }
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }).start();
    }
}
