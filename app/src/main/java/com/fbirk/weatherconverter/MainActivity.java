package com.fbirk.weatherconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int evalue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar myToolbar = findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        Button convert = findViewById(R.id.btnConvert); //get the id for button
        final Button reset = findViewById(R.id.btnReset);
        final EditText inputCelsius = findViewById(R.id.inputCelsius);
        final EditText inputFahrenheit = findViewById(R.id.inputFahrenheit);
        final TextView warning = findViewById(R.id.warning);

        inputCelsius.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                evalue = 1;
                return false;
            }
        });

        inputFahrenheit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                evalue = 2;
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
                if (evalue == 2) {
                    try {
                        double c = (Double.parseDouble(inputFahrenheit.getEditableText().toString()) - 32) * 0.56;
                        inputCelsius.setText((int) c + "");
                    } catch (NumberFormatException e) {
                    }
                } else if (evalue == 1) {
                    try {
                        double f = 1.8 * Double.parseDouble(inputCelsius.getEditableText().toString()) + 32;
                        inputFahrenheit.setText(((int) f) + "");
                    } catch (NumberFormatException e) {
                    }
                }
                reset.setVisibility(View.VISIBLE);
                evalue = 0;
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
}
