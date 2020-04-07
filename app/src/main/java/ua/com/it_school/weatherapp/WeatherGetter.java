package ua.com.it_school.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

class WeatherGetter extends AsyncTask<Void, Void, Void> {
    private MainActivity mainActivity;

    public WeatherGetter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

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

        ConnectivityManager cm = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo.isConnected()) {
            try {
                is = new URL(url).openStream();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    try {
                        mainActivity.jsonIn = readAll(rd);
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
            mainActivity.isConnected = false;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        ConnectAndGetData(mainActivity.currWeatherURL);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mainActivity.ParseWeather();
        mainActivity.drawWeather();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d("", "Process canceling");
    }
}