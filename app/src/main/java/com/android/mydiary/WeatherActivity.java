package com.android.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WeatherActivity extends Activity {
    Button Clear;
    Button Partly;
    Button Mostly;
    Button Cloudy;
    Button Rain;
    Button SnowRain;
    Button Snow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Clear = findViewById(R.id.Clear);
        Partly = findViewById(R.id.Partly);
        Mostly = findViewById(R.id.Mostly);
        Cloudy = findViewById(R.id.Cloudy);
        Rain = findViewById(R.id.Rain);
        SnowRain = findViewById(R.id.SnowRain);
        Snow = findViewById(R.id.Snow);

        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Clear");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Partly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Partly Cloudy");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Mostly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Mostly Cloudy");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Cloudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Cloudy");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Rain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Rain");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        SnowRain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Snow/Rain");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("weather", "Snow");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}