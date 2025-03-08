package com.example.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        CardView btnXemBanDo = findViewById(R.id.btnXemBanDo);
        btnXemBanDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });

        CardView btnGiaoHang = findViewById(R.id.btnGiaoHang);
        btnGiaoHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        CardView btnThuThach = findViewById(R.id.btnThuThach);
        btnThuThach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        CardView btnHoiVien = findViewById(R.id.btnHoiVien);
        btnHoiVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });
    }
}