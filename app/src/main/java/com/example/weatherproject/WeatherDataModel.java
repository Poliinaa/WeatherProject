package com.example.weatherproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class WeatherDataModel{

        public String cod;
        public String temp, name_city, updatedField;
        public String pressure, humidity, wind, feels_like;
        public int visibility;

        public static WeatherDataModel fromJson(JSONObject jsonObject) {
            try {
                WeatherDataModel weatherDataModel = new WeatherDataModel();

                weatherDataModel.name_city = jsonObject.getString("name");
                weatherDataModel.pressure = String.valueOf(jsonObject.getJSONObject("main").getDouble("pressure"));
                weatherDataModel.humidity = String.valueOf(jsonObject.getJSONObject("main").getDouble("humidity"));
                weatherDataModel.wind = String.valueOf(jsonObject.getJSONObject("wind").getDouble("speed"));
                weatherDataModel.visibility =jsonObject.getInt("visibility")/1000;
                weatherDataModel.feels_like = String.valueOf(jsonObject.getJSONObject("main").getDouble("feels_like"));
                weatherDataModel.temp = String.valueOf(jsonObject.getJSONObject("main").getDouble("temp"));


                DateFormat df = DateFormat.getDateTimeInstance();
                weatherDataModel.updatedField = df.format(new Date(jsonObject.getLong("dt")*1000));

                JSONArray weather = jsonObject.getJSONArray("weather");
                for (int i = 0; i < weather.length(); i++) {
                    JSONObject j = weather.getJSONObject(i);
                    weatherDataModel.cod  = j.getString("description");
                }

                return weatherDataModel;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("clima", "fromJson: error catch");
                return null;
            }

        }

        public String getTemp() {
            return temp + "°C";
        }

        public String getName_city() {
            return name_city;
        }
        public String getPressure() {
        return   pressure + " мм";
        }
        public String getHumidity() {
        return  humidity + " %";
        }
        public String getWind() {
        return  wind + " м/с";
        }
        public String getVisibility() {
        return visibility + " км";
        }
        public String getFeels_like() {
        return feels_like + "°C";
        }

}