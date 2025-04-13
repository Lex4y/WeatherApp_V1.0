package com.example.myweatherapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CityCoordinatesFetcher {

    private static final String TAG = "CityCoordinatesFetcher";
    private static final String API_KEY = "ziZmgl75E7ZATXiSlduERw==4CppXGM88FviqpQK";

    public interface CoordinatesCallback {
        void onCoordinatesFetched(double latitude, double longitude);
        void onError(String errorMessage);
    }

    public static void fetchCoordinates(String cityName, CoordinatesCallback callback) {
        String apiUrl = "https://api.api-ninjas.com/v1/city?name=" + cityName;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .addHeader("X-Api-Key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Request failed: " + e.getMessage());
                callback.onError("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject cityInfo = jsonArray.getJSONObject(0);
                            double latitude = cityInfo.getDouble("latitude");
                            double longitude = cityInfo.getDouble("longitude");

                            callback.onCoordinatesFetched(latitude, longitude);
                        } else {
                            callback.onError("City not found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Request failed with code: " + response.code());
                    callback.onError("Request failed with code: " + response.code());
                }
            }
        });
    }
}
