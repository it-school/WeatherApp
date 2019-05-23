package ua.com.it_school.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
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
        wg = new WeatherGetter();
        wg.execute();
    }

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

    public void btnLoadData(View view) {

        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        //currWeatherURL = "https://api.openweathermap.org/data/2.5/forecast/daily?lat="+Coordinates.latitude+"&lon="+Coordinates.longitude+"&appid=b1b15e88fa797225412429c1c50c122a1";

        if (wg.getStatus() == AsyncTask.Status.RUNNING)
            wg.cancel(true);

        wg = new WeatherGetter();
        wg.execute();
        ParseWeather();
        drawWeather();
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

            textViewMain.setText(String.format(Locale.ENGLISH, "%.2f, %.2f", Coordinates.longitude, Coordinates.latitude));
        }
    }


    public void btnStretView(View view) {
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


    public void btnGPS(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textViewMain.setText("Current coordinates: " + Coordinates.getCoordinates());
            return;
        }
        Coordinates.latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
        Coordinates.longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
        textViewMain.setText("Current coordinates: " + Coordinates.getCoordinates());
    }

    class WeatherGetter extends AsyncTask<Void, Void, Void> {
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public void ConnectAndGetData(String url) {
            InputStream is = null;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo.isConnected()) {
                try {
                    is = new URL(url).openStream();
                    try {
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                        try {
                            jsonIn = readAll(rd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                isConnected = false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectAndGetData(currWeatherURL);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ParseWeather();
            drawWeather();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d("", "Process canceling");
        }
    }
}
