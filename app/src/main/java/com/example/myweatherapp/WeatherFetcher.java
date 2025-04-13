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

public class WeatherFetcher {

    private static final String TAG = "WeatherFetcher";

    public interface TemperatureCallback {
        void onTemperatureFetched(double temperature);
        void onError(String errorMessage);
    }

    public static void fetchTemperature(double latitude, double longitude, TemperatureCallback callback) {
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m&forecast_hours=1";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
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
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject hourly = jsonObject.getJSONObject("hourly");
                        JSONArray temperatures = hourly.getJSONArray("temperature_2m");
                        if (temperatures.length() > 0) {
                            double temperature = temperatures.getDouble(0);
                            callback.onTemperatureFetched(temperature);
                        } else {
                            callback.onError("Temperature data not found");
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
