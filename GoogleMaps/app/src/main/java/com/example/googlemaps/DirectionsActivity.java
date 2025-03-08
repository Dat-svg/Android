package com.example.googlemaps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DirectionsActivity extends AppCompatActivity {

    private EditText etDestination;
    private Button btnGetDirections;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        etDestination = findViewById(R.id.etDestination);
        btnGetDirections = findViewById(R.id.btnGetDirections);
        tvResult = findViewById(R.id.tvResult);

        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination = etDestination.getText().toString().trim();

                if (!destination.isEmpty()) {
                    openGoogleMaps(destination);
                } else {
                    tvResult.setText("Vui lòng nhập điểm đến.");
                }
            }
        });
    }

    private void openGoogleMaps(String destination) {
        String uri = "https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(destination);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            tvResult.setText("Không thể mở Google Maps. Vui lòng cài đặt ứng dụng.");
        }
    }
}