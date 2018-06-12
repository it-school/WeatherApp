package ua.com.it_school.weatherapp;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        jsonIn = "";//"{\"coord\":{\"lon\":30.73,\"lat\":46.48},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"ясно\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":296.15,\"pressure\":1020,\"humidity\":33,\"temp_min\":296.15,\"temp_max\":296.15},\"visibility\":10000,\"wind\":{\"speed\":3,\"deg\":150},\"clouds\":{\"all\":0},\"dt\":1528381800,\"sys\":{\"type\":1,\"id\":7366,\"message\":0.0021,\"country\":\"UA\",\"sunrise\":1528337103,\"sunset\":1528393643},\"id\":698740,\"name\":\"Odessa\",\"cod\":200}";
        text = "";
        isDataLoaded = false;
        isConnected = true;
        currWeatherURL = "http://api.openweathermap.org/data/2.5/weather?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
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
                sm.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                Date date = new Date(json.getLong("dt") * 1000);

                JSONArray jsonWeather = (JSONArray) json.get("weather");
                String description = jsonWeather.getJSONObject(0).getString("description");

                JSONObject jsonWind = (JSONObject) json.get("wind");
                int speed = jsonWind.getInt("speed");
                int deg = jsonWind.getInt("deg");

                JSONObject jsonClouds = (JSONObject) json.get("clouds");
                int clouds = jsonClouds.getInt("all");

                main = new Main(temp, pressure, humidity, date, description, speed, deg, clouds);
                isDataLoaded = true;
            } catch (JSONException e) {
                e.printStackTrace();
                //drawWeather();
            }
        //   ((TextView)findViewById(R.id.textView)).setText(main.toString());





        /*
  String str=null; String input = "данные ";
  JsonParser parser = new JsonParser();
  JsonObject mainObject = parser.parse(input).getAsJsonObject();
  JsonArray pItem = mainObject.getAsJsonArray("p_item");
  for (JsonElement user : pItem)
  {
  JsonObject userObject = user.getAsJsonObject();
  userObject.get("p_id");
  str = userObject.get("p_id").toString();
  }
         */

    }

    public void btnClick(View view) {
//        wg.ConnectAndGetData(currWeatherURL);
        ParseWeather();
        drawWeather();

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

        } else
            imageView.setImageResource(R.drawable.nodata);
    }


    class WeatherGetter extends AsyncTask<Void, Void, Void>
    {
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public void ConnectAndGetData(String url)
        {

            //String url = "http://api.openweathermap.org/data/2.5/weather?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
            //String urlForecast = "api.openweathermap.org/data/2.5/forecast?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";

            InputStream is = null;


            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if(netInfo.isConnected())

            {
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

            }
            else

            {
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
            url = "http://study.cc.ua";
            try {
                page = Jsoup.parse(new URL(url), 10000);
                jsonIn = "| "+page.text()+ " |";
            } catch (IOException e) {
                e.printStackTrace();
            }

           //textView.setText(textView.getText()+"In2\n");
            try {
                  page = Jsoup.connect(url).get();// Connect to the web site
                  jsonIn = page.text() ;           // Get the html document title

                    //textView.setText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
//            textView.setText(textView.getText()+"In3\n");
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
    }
}
