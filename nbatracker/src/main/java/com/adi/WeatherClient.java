package com.adi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherClient {

    private String city;

    public void setCity(String city) {
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty.");
        }
        if (city.matches(".*\\d.*")) {
            throw new IllegalArgumentException("City name cannot contain numbers.");
        }
        if (city.indexOf(" ") > 0) {
            String newCity = city.replace(" ", "%20");
            this.city = newCity;
        } else {
            this.city = city;
        }
    }

    public void getWeather(String startTime) throws Exception{
        
        String geoURL =
                "https://geocoding-api.open-meteo.com/v1/search?name="
                + city +
                "&count=1";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geoURL))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        String json = response.body();

        JSONObject obj = new JSONObject(json);
        
        JSONArray results = obj.getJSONArray("results");

        double latitude = results.getJSONObject(0).getDouble("latitude");
        double longitude = results.getJSONObject(0).getDouble("longitude"); 

        String weatherURL =
                "https://api.open-meteo.com/v1/forecast?latitude="
                + latitude
                + "&longitude="
                + longitude
                + "&hourly=temperature_2m,precipitation_probability,precipitation,wind_speed_10m,visibility&forecast_days=1&wind_speed_unit=mph&temperature_unit=fahrenheit&precipitation_unit=inch";

        HttpRequest weatherRequest = HttpRequest.newBuilder()
        .uri(URI.create(weatherURL))
        .build();

        HttpResponse<String> weatherResponse = client.send(
                weatherRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        String weatherJson = weatherResponse.body();

        JSONObject weatherObj = new JSONObject(weatherJson);
        JSONObject hourly = weatherObj.getJSONObject("hourly");

        JSONArray timeArray = hourly.getJSONArray("time");
        JSONArray tempArray = hourly.getJSONArray("temperature_2m");
        JSONArray probability = hourly.getJSONArray("precipitation_probability");
        JSONArray precipitation = hourly.getJSONArray("precipitation");
        JSONArray wind = hourly.getJSONArray("wind_speed_10m");
        JSONArray visibility = hourly.getJSONArray("visibility");

        double temperature = 0.0;
        int prob = 0;
        double precipitationAmount = 0.0;
        double windSpeed = 0.0;
        double visibilityDistance = 0.0;

        String time = TimeConverter.convertToRegular(startTime);

        for (int i = 0; i < timeArray.length(); i++) {
            String realTime = TimeConverter.convertFromISO(timeArray.getString(i));
            if (realTime.equals(time)) {
                temperature = tempArray.getDouble(i);
                prob = probability.getInt(i);
                windSpeed = wind.getDouble(i);
                visibilityDistance = visibility.getDouble(i);
            }
            precipitationAmount += precipitation.getDouble(i);
        }

        System.out.println("\n===== WEATHER REPORT =====");
        System.out.println("Time: " + time);
        System.out.println("Temperature: " + temperature + "°F");
        System.out.println("Rain Chance: " + prob + "%");
        System.out.println("Precipitation: " + String.format("%.2f", precipitationAmount) + " in");
        System.out.println("Wind Speed: " + windSpeed + " mph");
        System.out.println("Visibility: " + (int)(visibilityDistance/5280) + " mi");

        System.out.println("\n===== CONDITIONS =====");

        // Temperature
        if (temperature < 40) {
            System.out.println("- Very cold. Wear a heavy coat.");
        }
        else if (temperature < 60) {
            System.out.println("- Cool weather. Bring a hoodie or jacket.");
        }
        else if (temperature < 80) {
            System.out.println("- Comfortable temperature.");
        }
        else {
            System.out.println("- Hot weather. Stay hydrated.");
        }

        // Rain
        if (prob >= 70) {
            System.out.println("- High chance of rain. Bring an umbrella.");
        }
        else if (prob >= 40) {
            System.out.println("- Possible rain during travel.");
        }
        else {
            System.out.println("- Low chance of rain.");
        }

        // Precipitation
        if (precipitationAmount > 7.2) {
            System.out.println("- Heavy downpour. Consider leaving early and drive slowly.");
        }
        else if (precipitationAmount > 2.4) {
            System.out.println("- Moderate rain. Drive carefully.");
        }
        else if (precipitationAmount > 0) {
            System.out.println("- Light rain. Watch for slick roads.");
        }
        else {
            System.out.println("- No precipitation expected.");
        }

        // Wind
        if (windSpeed >= 20) {
            System.out.println("- Very windy conditions.");
        }
        else if (windSpeed >= 10) {
            System.out.println("- Moderate wind outside.");
        }
        else {
            System.out.println("- Wind conditions are calm.");
        }

        // Visibility
        if (visibilityDistance < 1) {
            System.out.println("- Poor visibility. Drive carefully.");
        }
        else if (visibilityDistance < 5) {
            System.out.println("- Moderate visibility. Stay alert while driving.");
        }
        else {
            System.out.println("- Visibility is clear.");
        }

        System.out.println("\n===== RECOMMENDATION =====");

        // Overall travel suggestion
        if (prob >= 70 || visibilityDistance < 1) {
            System.out.println("\nTravel Recommendation: Leave early and prepare for delays.");
        }
        else if (precipitationAmount > 7.2 || windSpeed >= 20) {
            System.out.println("\nTravel Recommendation: Exercise caution while driving.");
        }
        else if (temperature < 40) {
            System.out.println("\nTravel Recommendation: Dress warmly and check your car's antifreeze.");
        }
        else if (temperature > 90) {
            System.out.println("\nTravel Recommendation: Stay hydrated and check your car's coolant.");
        }
        else {
            System.out.println("\nTravel Recommendation: Conditions look good for traveling.");
        }


    }

    
}
