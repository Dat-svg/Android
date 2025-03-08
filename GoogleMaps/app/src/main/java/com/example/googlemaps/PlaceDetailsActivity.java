package com.example.googlemaps;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_detail);

        String title = getIntent().getStringExtra("title");
        String snippet = getIntent().getStringExtra("snippet");

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView snippetTextView = findViewById(R.id.snippetTextView);

        titleTextView.setText(title);
        snippetTextView.setText(snippet);
    }
}
