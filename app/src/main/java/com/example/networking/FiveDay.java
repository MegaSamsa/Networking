package com.example.networking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FiveDay extends Activity {

    private EditText user_field;
    private TextView result_info;
    private Button main_btn;
    DecimalFormat df = new DecimalFormat("#.#");
    public String city = "";

    public void onClick1 (View v) {
        Intent i = new Intent(FiveDay.this, MainActivity.class);
        startActivity(i);
    }
    public void onClick2 (View v) {
        Intent i = new Intent(FiveDay.this, NextDay.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.five_day);

        user_field = findViewById(R.id.user_field);
        result_info = findViewById(R.id.result);
        main_btn = findViewById(R.id.main_btn);

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals(""))
                    Toast.makeText(getApplicationContext(), "Введите название города", Toast.LENGTH_LONG).show();
                else {
                    city = user_field.getText().toString().toLowerCase().trim();
                    Map<String,String> cityDict = new HashMap<String,String>();
                    cityDict.put("ханты-мансийск", "288460");
                    cityDict.put("москва", "294021");
                    cityDict.put("сургут", "288459");
                    cityDict.put("югорск", "288471");

                    String key = "rcGAMcyGNpZsiUgnLxfrJB71J0htWo8V";
                    //String key = "SOsQpxryQjMzln6mpMoka65cSdAoA4hi";
                    String url = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/" + cityDict.get(city) + "?apikey=" + key + "&language=ru-ru&details=true";

                    new FiveDay.GetUrlData().execute(url);
                }
            }
        });
    }

    private class GetUrlData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Обновляем...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                result_info.setText("Прогноз на 5 дней \n" + city.toUpperCase());

                for (int i = 0; i < 5; i++)
                {
                    JSONObject day = jsonObject.getJSONArray("DailyForecasts").getJSONObject(i);
                    String date = jsonObject.getJSONArray("DailyForecasts").getJSONObject(i).getString("Date");
                    String[] date1 = date.split("[-:T+]");

                    double min = day.getJSONObject("Temperature").getJSONObject("Minimum")
                            .getDouble("Value");
                    double max = day.getJSONObject("Temperature").getJSONObject("Maximum")
                            .getDouble("Value");

                    result_info.append("\n\nДата " + (Integer.parseInt(date1[2])) + "." + date1[1] + "." + date1[0]);
                    result_info.append("\nТемпература " + df.format((max - 32) * 5 / 9) + "\u00B0/" + df.format((min - 32) * 5 / 9) + "\u00B0 (C)");
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }
}