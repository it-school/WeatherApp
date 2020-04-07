package ua.com.it_school.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    ImageView windImage;
    Button button;
    String jsonIn, text;
    TextView textViewMain;
    TextView windTextView;
    TextView windTextView2;
    TextView tempTextView;
    WebView webView;
    Resources res;
    Main main;
    boolean isDataLoaded;
    boolean isConnected;
    String currWeatherURL;
    Document page = null;
    private String FLAG;
    WeatherGetter wg;
    String message;
    private LocationManager locationManager;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            textViewMain.setText(mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());
        }
    };

    /**
     * Parsing of loaded weather data
     */
    public void ParseWeather() {
        boolean cont = false;
        JSONObject json = null;
        try {
            json = new JSONObject(jsonIn);
            cont = true;
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        if (cont)
            try {
                String temp1 = "";
                JSONObject jsonMain = (JSONObject) json.get("main");
                double temp = jsonMain.getDouble("temp") - 273.15;
                int pressure = jsonMain.getInt("pressure");
                int humidity = jsonMain.getInt("humidity");

                SimpleDateFormat sm = new SimpleDateFormat("d.M.Y H:m");  // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
                sm.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                Date date = new Date(json.getLong("dt") * 1000);

                JSONArray jsonWeather = (JSONArray) json.get("weather");
                String description = jsonWeather.getJSONObject(0).getString("description");

                JSONObject jsonWind = (JSONObject) json.get("wind");
                int speed = jsonWind.getInt("speed");
                int deg = jsonWind.getInt("deg");

                JSONObject jsonClouds = (JSONObject) json.get("clouds");
                int clouds = jsonClouds.getInt("all");

                String name = json.getString("name");

                main = new Main(temp, pressure, humidity, date, description, speed, deg, clouds, name);
                isDataLoaded = true;
            } catch (JSONException e) {
                isDataLoaded = false;
                e.printStackTrace();
            }
    }

    /**
     * Loading current weather data for current coordinates
     *
     * @param view
     */
    public void btnLoadWeatherData(View view) {

        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        //currWeatherURL = "https://api.openweathermap.org/data/2.5/forecast/daily?lat="+Coordinates.latitude+"&lon="+Coordinates.longitude+"&appid=b1b15e88fa797225412429c1c50c122a1";

        if (wg.getStatus() == AsyncTask.Status.RUNNING)
            wg.cancel(true);

        wg = new WeatherGetter(this);
        wg.execute();
    }

    public void drawWeather() {
        if (isDataLoaded) {
            if (isConnected) {
                if (main.getClouds() < 12) {
                    imageView.setImageResource(R.drawable.transparent);
                } else if (main.getClouds() < 25) {
                    imageView.setImageResource(R.drawable.c1);
                } else if (main.getClouds() < 37) {
                    imageView.setImageResource(R.drawable.c2);
                } else if (main.getClouds() < 50) {
                    imageView.setImageResource(R.drawable.c3);
                } else if (main.getClouds() < 62) {
                    imageView.setImageResource(R.drawable.c4);
                } else if (main.getClouds() < 75) {
                    imageView.setImageResource(R.drawable.c5);
                } else if (main.getClouds() < 87) {
                    imageView.setImageResource(R.drawable.c6);
                } else
                    imageView.setImageResource(R.drawable.c7);

                imageView.setBackgroundResource(R.drawable.sun);

                // draw wind direction
                windImage.setImageResource(R.drawable.w);
                windImage.setRotation(main.getDeg());
                windImage.setScaleX(0.8f);
                windImage.setScaleY(0.8f / (16 / (main.getSpeed() > 0.1 ? main.getSpeed() : 0.1f)));
                imageView.getBackground().setAlpha(175);

                windTextView.setText("" + main.getSpeed());
                windTextView2.setText(R.string.windSpeed);
                tempTextView.setText(String.format(Locale.getDefault(), "%.1f°C", main.getTemp()));

                Toast.makeText(this.getBaseContext(), R.string.weatherUpdate, Toast.LENGTH_SHORT).show();
            } else
                imageView.setImageResource(R.drawable.nodata);
        } else
            Toast.makeText(this.getBaseContext(), R.string.noData, Toast.LENGTH_SHORT).show();
    }

    /**
     * Opens Google Maps to get new coordinates using long tap
     *
     * @param view
     */
    public void btnMapOpen(View view) {
// Clean Google Map
/*
        String geoUriString = "geo:46.460323,30.749954?z=3";
        //String geoUriString = "geo:0,0?q=ONPU";
        //geo:0,0?q=address
        //String geoUriString = "google.streetview:cbll=46.460323,30.749954&cbp=1,99.56,,1,2.0&mz=19";

        Uri geoUri = Uri.parse(geoUriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(mapIntent, 1);
}*/


        Intent map = new Intent(MainActivity.this, MapsActivity.class);
        startActivityForResult(map, 1);

//        MapsActivity map = new MapsActivity();
//        setContentView(R.layout.activity_maps);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1) && (resultCode == 1)) {
            Coordinates.longitude = data.getDoubleExtra("longitude", Coordinates.longitude);
            Coordinates.latitude = data.getDoubleExtra("latitude", Coordinates.latitude);

            textViewMain.setText(getString(R.string.selectedCoords) + Coordinates.getCoordinates());
            btnLoadWeatherData(getCurrentFocus());
        }
    }

    /**
     * Opens Google Street using view for current coordinates
     *
     * @param view
     */
    public void btnStreetView(View view) {
        // Street View
//        google.streetview:cbll=lat,lng&cbp=1,yaw,,pitch,zoom&mz=mapZoom
//        lat - широта
//        lng	- долгота
//        yaw	- центр панорамы в градусах по часовой стрелке с севера. Обязательно используйте две запятые.
//        pitch - центр обзора панорамы в градусах от -90 (взор вверх) до 90 (взгляд вниз)
//        zoom - масштаб панорамы. 1.0 = нормальный, 2.0 = приближение в 2 раза, 3.0 = в 4 раза и так далее
//        mapZoom	- масштабирование места карты, связанное с панорамой. Это значение используется при переходе на Карты.

        String geoUriString = "google.streetview:cbll=46.4600233,30.749909&cbp=1,90,,0,1.0&mz=19";
        // String geoUriString = "google.streetview:cbll=" + Coordinates.getCoordinates() + "8&cbp=1,90,,0,1.0&mz=19";
        Uri geoUri = Uri.parse(geoUriString);
        Intent streetIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (streetIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(streetIntent);
        }
    }

    /**
     * Shows description of current device coordinates
     *
     * @param view
     */
    public void btnCurrentDeviceGPSCoordinates(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Coordinates.latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
            Coordinates.longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
        }
        textViewMain.setText(getString(R.string.currentCoords) + Coordinates.getCoordinates());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        windImage = findViewById(R.id.windImage);
        button = findViewById(R.id.buttonLoadData);
        textViewMain = findViewById(R.id.textViewMain);
        windTextView = findViewById(R.id.windTextView);
        windTextView2 = findViewById(R.id.windTextView2);
        tempTextView = findViewById(R.id.tempTextView);
        jsonIn = "{\"coord\":{\"lon\":30.73,\"lat\":46.48},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"ясно\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":296.15,\"pressure\":1020,\"humidity\":33,\"temp_min\":296.15,\"temp_max\":296.15},\"visibility\":10000,\"wind\":{\"speed\":15,\"deg\":180},\"clouds\":{\"all\":0},\"dt\":1528381800,\"sys\":{\"type\":1,\"id\":7366,\"message\":0.0021,\"country\":\"UA\",\"sunrise\":1528337103,\"sunset\":1528393643},\"id\":698740,\"name\":\"Odessa\",\"cod\":200}";
        text = "";
        isDataLoaded = false;
        isConnected = true;
        message = "";
        //   currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Location location = getLastLocation();
        if (location == null) {
            btnCurrentDeviceGPSCoordinates(this.getCurrentFocus());
        }
        wg = new WeatherGetter(this);
        wg.execute();

    }

    //--------------------------------
    @SuppressLint("MissingPermission")
    private Location getLastLocation() {
        final Location[] location = {null};
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                location[0] = task.getResult();
                                if (location[0] == null) {
                                    requestNewLocationData();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
        return location[0];
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}