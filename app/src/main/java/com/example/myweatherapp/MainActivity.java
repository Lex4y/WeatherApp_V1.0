package com.example.myweatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private EditText inputcity;
    private Button button;
    private TextView temperature,humid,windspeed;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputcity = findViewById(R.id.inputcity);
        button = findViewById(R.id.buttonfind);
        temperature = findViewById(R.id.temperature);
        humid = findViewById(R.id.humid);
        windspeed = findViewById(R.id.windspeed);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputcity.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_city_input, Toast.LENGTH_LONG).show();
                } else {
                    String city = inputcity.getText().toString();
                    CityCoordinatesFetcher.fetchCoordinates(city, new CityCoordinatesFetcher.CoordinatesCallback() {
                        @Override
                        public void onCoordinatesFetched(double latitude, double longitude) {
                            Log.d(TAG, "Latitude: " + latitude);
                            Log.d(TAG, "Longitude: " + longitude);

                            WeatherFetcher.fetchTemperature(latitude, longitude, new WeatherFetcher.TemperatureCallback() {
                                @Override
                                public void onTemperatureFetched(double temp) {
                                    Log.d(TAG, "Temperature: " + temp);
                                    temperature.setText(temp+"");
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Log.e(TAG, "Error: " + errorMessage);
                                    Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Error: " + errorMessage);
                            Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}