package com.fbirk.weatherconverter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;

import com.fbirk.weatherconverter.model.Weather;

public class MainActivity extends AppCompatActivity {

    int eval = 0;
    private static String key = "0e1f2e5af82ca9d8d5a6291311a84096";
    String city = "London,uk";

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;

    private ImageView imgView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_credits:
                Intent intent = new Intent(getApplicationContext(), Credits.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button convert = findViewById(R.id.btnConvert); //get the id for button
        final Button reset = findViewById(R.id.btnReset);
        final EditText inputCelsius = findViewById(R.id.inputCelsius);
        final EditText inputFahrenheit = findViewById(R.id.inputFahrenheit);
        final TextView warning = findViewById(R.id.warning);

        TextView cityText = findViewById(R.id.cityText);
        TextView condDescr = findViewById(R.id.descr);
        TextView temp = findViewById(R.id.temp);
        ImageView imgView = findViewById(R.id.img_icon);

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});

        inputCelsius.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                eval = 1;
                return false;
            }
        });

        inputFahrenheit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                eval = 2;
                return false;
            }
        });

        inputCelsius.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                inputCelsius.setText("");
            }
        });
        inputFahrenheit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                inputFahrenheit.setText("");
            }
        });

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eval == 2) {
                    try {
                        double c = (Double.parseDouble(inputFahrenheit.getEditableText().toString()) - 32) * 0.56;
                        inputCelsius.setText((int) c + "");
                    } catch (NumberFormatException e) {
                    }
                } else if (eval == 1) {
                    try {
                        double f = 1.8 * Double.parseDouble(inputCelsius.getEditableText().toString()) + 32;
                        inputFahrenheit.setText(((int) f) + "");
                    } catch (NumberFormatException e) {
                    }

                } else {
                    warning.setVisibility(View.VISIBLE);
                }
                reset.setVisibility(View.VISIBLE);
                eval = 0;
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                inputCelsius.setText("");
                inputFahrenheit.setText("");
                warning.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);
            }
        });
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            System.out.println("Created Object");
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], key));
            System.out.println("HTTP Call");
            try {
                System.out.println(data);
               // weather = JSONWeatherParser.getWeather(data);

                System.out.println("Got data");

                // Let's retrieve the icon
                //weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }
            if (weather.location != null) {
                cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
            }
        }
    }
}
