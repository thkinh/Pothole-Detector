package com.example.doan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    public static void getPotholes(String user, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("http://34.42.117.215:8080/api/pothole/get?user=" + user);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                callback.onSuccess(response.toString());
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }).start();
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }
}
