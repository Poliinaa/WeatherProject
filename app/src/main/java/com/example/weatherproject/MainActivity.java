package com.example.weatherproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE = 123;
    //lat=" + latiude + "lon="+ longitude+
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?/&units=metric&&lang=ru";
    final String APP_ID = "a09514ea0d96e81223489080ecbce550";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

   // private EditText search;
    Button search_button;
    EditText search;
    TextView info, temp, name_city, pressure,humidity, wind, updatedField, feels_like, visibility;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  search = findViewById(R.id.search);
        search_button  = findViewById(R.id.search_button);
        search = findViewById(R.id.search);
        info = findViewById(R.id.info);
        temp = findViewById(R.id.temp);
        name_city = findViewById(R.id.name_city);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);
        updatedField = findViewById(R.id.updatedField);
        feels_like = findViewById(R.id.feels_like);
        visibility = findViewById(R.id.visibility);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (search.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {
                    Intent cityIntent = new Intent(MainActivity.this, MainActivity.class);
                    String city = search.getText().toString();
                    cityIntent.putExtra("city", city);
                    startActivity(cityIntent);
                }
                return false;
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {
                    Intent cityIntent = new Intent(MainActivity.this, MainActivity.class);
                    String city = search.getText().toString();
                    cityIntent.putExtra("city", city);
                    startActivity(cityIntent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("c", "onResume: Получение местоположения пользователя");

        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("city");

        if (city != null) {
            getCityWeather(city);
        } else {
            getWeatherForCurrentLocation();
        }
    }

    private void getCityWeather(String city) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("q", city);
        requestParams.put("appid", APP_ID);
        networking(requestParams);
    }
    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima", "Текущее местоположение");
                String longitude = String.valueOf(location.getLatitude());
                String latiude = String.valueOf(location.getLongitude());

                RequestParams requestParams = new RequestParams();
                requestParams.put("lat", latiude);
                requestParams.put("lon", longitude);
                requestParams.put("appid", APP_ID);

                Log.d("Cima", "широта: " + latiude);
                Log.d("Clima", "долгота: " + longitude);

                //String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latiude + "&lon="+ longitude+ "&appid=a09514ea0d96e81223489080ecbce550&units=metric&lang=ru";

                networking(requestParams);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("Clima", "onProviderDisabled");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    private void networking(RequestParams requestParams) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Clima", "onSuccess: " + response.toString());
                WeatherDataModel weatherDataModel = WeatherDataModel.fromJson(response);

                name_city.setText(weatherDataModel.getName_city());
                temp.setText(weatherDataModel.getTemp());
                pressure.setText("Давление  "+ weatherDataModel.getPressure());
                humidity.setText("Влажность  "+weatherDataModel.getHumidity());
                wind.setText("Ветер  "+weatherDataModel.getWind());
                visibility.setText("Видимость  "+weatherDataModel.getVisibility());
                feels_like.setText("По ощущениям  "+weatherDataModel.getFeels_like());
                updatedField.setText("Обновлено\n " + weatherDataModel.updatedField);
                info.setText(weatherDataModel.cod);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Clima", "onFailure: " + e.toString());
                Toast.makeText(MainActivity.this, "Запрос Не Удался", Toast.LENGTH_SHORT).show();
                Intent cityIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(cityIntent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("clima", "onRequestPermissionsResult: permission granted");
                getWeatherForCurrentLocation();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}