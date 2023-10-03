package com.example.palmaryapplication.Class;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendationService {
    private static final String FLASK_SERVER_URL = "http://192.168.8.102:5000/get_recommendations";
    public static void getRecommendations(int transactionNo, RecommendationCallback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Prepare JSON data
        String json = "{\"transaction_no\":" + transactionNo + "}";
        RequestBody body = RequestBody.create(JSON, json);

        // Create an HTTP POST request
        Request request = new Request.Builder()
                .url(FLASK_SERVER_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                try {
                    String responseData = response.body().string();
                    // Process the response data (list of recommended products) here
                    callback.onSuccess(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
            }
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            }
        });
    }
    public interface RecommendationCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
