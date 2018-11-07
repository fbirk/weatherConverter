package com.fbirk.weatherconverter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.support.design.widget.Snackbar;

import com.fbirk.weatherconverter.model.Weather;

public class MainActivity extends AppCompatActivity {

    private int eval = 0;
    private static String key = BuildConfig.API_Key;
    private SimpleLocation location;
    private SimpleLocation lm;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean permissionGranted = false;

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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Snackbar.make(findViewById(R.id.mainView), "Call!", Snackbar.LENGTH_SHORT).show();

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;

                    // construct a new instance of SimpleLocation
                    location = new SimpleLocation();
                    lm = location.getLocationManager(this);
                }
                break;
             default:
                permissionGranted = false;
                System.err.println("Permission denied!");
                Snackbar.make(findViewById(R.id.mainView), "You Denied permission", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    PERMISSION_REQUEST_CODE);
        } else {
            Snackbar.make(findViewById(R.id.mainView), "What?!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    PERMISSION_REQUEST_CODE);
        }

        Button convert = findViewById(R.id.btnConvert); //get the id for button
        final Button reset = findViewById(R.id.btnReset);
        final Button btnLiveWeather = findViewById(R.id.btnLiveWeather);

        final EditText inputCelsius = findViewById(R.id.inputCelsius);
        final EditText inputFahrenheit = findViewById(R.id.inputFahrenheit);
        final TextView warning = findViewById(R.id.txtWarning);

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

        btnLiveWeather.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (permissionGranted) {

                    Location l = lm.getCoordinates();

                    final double latitude = l.getLatitude();
                    final double longitude = l.getLongitude();

                    System.out.println(longitude + "  " + latitude);

                    String payload = "lat=" + latitude + "&lon=" + longitude;

                    JSONWeatherTask task = new JSONWeatherTask();
                    task.execute(new String[]{payload});
                } else {
                    onResume();
                    Snackbar.make(findViewById(R.id.mainView), "Permission Granted = False", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], key));
            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            double tmp;
            TextView cityText = findViewById(R.id.cityText);
            TextView condDescr = findViewById(R.id.descr);
            TextView temp = findViewById(R.id.temp);
            EditText inputCelsius = findViewById(R.id.inputCelsius);
            EditText inputFahrenheit = findViewById(R.id.inputFahrenheit);
            ImageView imgView = findViewById(R.id.img_icon);
            Button reset = findViewById(R.id.btnReset);


            if (weather.iconData != null) {

                imgView.setImageBitmap(weather.iconData);
            }
            if (weather.location != null && weather.currentCondition != null && weather.temperature != null) {

                cityText.setText(weather.location.getCity() + ", " + weather.location.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                tmp = Math.round(weather.temperature.getTemp());
                temp.setText("" +  Math.round(tmp - 273.15) + "°C");

                inputCelsius.setText(Math.round(tmp-273.15) + "");
                inputFahrenheit.setText(Math.round(1.8 * (tmp-273.15) + 32) + "");

                reset.setVisibility(View.VISIBLE);
            }
        }
    }
}
