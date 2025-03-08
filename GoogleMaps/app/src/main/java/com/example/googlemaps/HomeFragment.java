package com.example.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView btnXemBanDo = view.findViewById(R.id.btnXemBanDo);
        btnXemBanDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        CardView btnGiaoHang = view.findViewById(R.id.btnGiaoHang);
        btnGiaoHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        CardView btnThuThach = view.findViewById(R.id.btnThuThach);
        btnThuThach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        CardView btnHoiVien = view.findViewById(R.id.btnHoiVien);
        btnHoiVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
