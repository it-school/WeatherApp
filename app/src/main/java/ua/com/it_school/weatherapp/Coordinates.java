package ua.com.it_school.weatherapp;

import java.util.Locale;

public class Coordinates {
    public static double longitude = 30.749985;
    public static double latitude = 46.460367;

    public static void getCurrentLocation()
    {
        // https://www.androdocs.com/java/getting-current-location-latitude-longitude-in-android-using-java.html
    }

    public static String getCoordinates() {
        return String.format(Locale.ENGLISH, "%.3f, %.3f", Coordinates.longitude, Coordinates.latitude);
    }
}