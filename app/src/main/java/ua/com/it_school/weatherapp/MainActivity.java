package ua.com.it_school.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ImageView windImage;
    Button button;
    String jsonIn, text;
    TextView textView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        windImage = findViewById(R.id.windImage);
        button = findViewById(R.id.buttonLoadData);
        textView = findViewById(R.id.textView);
        jsonIn = "";//"{\"coord\":{\"lon\":30.73,\"lat\":46.48},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"ясно\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":296.15,\"pressure\":1020,\"humidity\":33,\"temp_min\":296.15,\"temp_max\":296.15},\"visibility\":10000,\"wind\":{\"speed\":3,\"deg\":150},\"clouds\":{\"all\":0},\"dt\":1528381800,\"sys\":{\"type\":1,\"id\":7366,\"message\":0.0021,\"country\":\"UA\",\"sunrise\":1528337103,\"sunset\":1528393643},\"id\":698740,\"name\":\"Odessa\",\"cod\":200}";
        text = "";
        isDataLoaded = false;
        isConnected = true;
        message = "";
        //   currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";

        wg = new WeatherGetter();
        wg.execute();
    }

    public void RefreshWeather() {

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
                e.printStackTrace();
                //drawWeather();
            }
        textView.setText(main.toString());
        drawWeather();
    }

    public void btnLoadData(View view) {

        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + Coordinates.latitude + "&lon=" + Coordinates.longitude + "&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
        //currWeatherURL = "https://api.openweathermap.org/data/2.5/forecast/daily?lat="+Coordinates.latitude+"&lon="+Coordinates.longitude+"&appid=b1b15e88fa797225412429c1c50c122a1";
        if (wg.getStatus() == AsyncTask.Status.RUNNING)
            wg.cancel(true);

        wg = new WeatherGetter();
        wg.execute();
        ParseWeather();
//        drawWeather();
    }

    public void drawWeather() {

        if (isConnected) {
            if (main.getClouds() < 5) {
                imageView.setImageResource(R.drawable.transparent);
            } else if (main.getClouds() < 25) {
                imageView.setImageResource(R.drawable.cloud1);
            } else if (main.getClouds() < 50) {
                imageView.setImageResource(R.drawable.cloud2);
            } else if (main.getClouds() < 75) {
                imageView.setImageResource(R.drawable.cloud3);
            } else
                imageView.setImageResource(R.drawable.cloud4);

            imageView.setBackgroundResource(R.drawable.sun);

            // draw wind direction
            windImage.setImageResource(R.drawable.arrow);
            windImage.setRotation(main.getDeg() + 90);
            windImage.setScaleX(0.5f);
            windImage.setScaleY(0.5f);
            windImage.animate();
        } else
            imageView.setImageResource(R.drawable.nodata);
    }

    public void btnClickCity(View view) {
        // Street View
/*
        String geoUriString = "google.streetview:cbll="+Coordinates.getCoordinates()+"&cbp=1,99.56,,1,2.0&mz=19";
        Uri geoUri = Uri.parse(geoUriString);
        Intent streetIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (streetIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(streetIntent);
        }
*/
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

//        google.streetview:cbll=lat,lng&cbp=1,yaw,,pitch,zoom&mz=mapZoom
//        lat - широта
//        lng	- долгота
//        yaw	- центр панорамы в градусах по часовой стрелке с севера. Обязательно используйте две запятые.
//        pitch - центр обзора панорамы в градусах от -90 (взор вверх) до 90 (взгляд вниз)
//        zoom - масштаб панорамы. 1.0 = нормальный, 2.0 = приближение в 2 раза, 3.0 = в 4 раза и так далее
//        mapZoom	- масштабирование места карты, связанное с панорамой. Это значение используется при переходе на Карты.

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

            textView.setText(Coordinates.longitude + ", " + Coordinates.latitude);
        }
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

            //String url = "http://api.openweathermap.org/data/2.5/weather?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
            //String urlForecast = "api.openweathermap.org/data/2.5/forecast?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";

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
/*
            String url = "http://study.cc.ua";

            try {
                  page = Jsoup.connect(url).get();// Connect to the web site
                  message = page.text() ;           // Get the html document title

                  page = Jsoup.parse(new URL(url), 10000);
                  message = "| "+page.text()+ " |";

                  textView.setText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textView.setText("\n------------------\n" + jsonIn+"\n--------------------\n");
            ParseWeather();
/*
            Element tableWth = page.select("table").first();
            Elements dates = tableWth.select("th[colspan=4]"); // даты дней недели для прогноза (их 3)
            Elements rows = tableWth.select("tr");

            // извлекаем даты
            date = "";
            for (Element d : dates)
                date += "\t\t\t" + d.text();

            // извлекаем температуру и темп. по ощущениям
            int i = 0;
            int r = 2;
            Elements temperatures = tableWth.select("span[class=value m_temp c]");
            for (Element t : temperatures) {
                wt[r][i++] = t.text();
                if (i > 12) {
                    r = 6;
                    i = 0;
                }
            }
            */

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d("", "Process canceling");
        }
    }
}
