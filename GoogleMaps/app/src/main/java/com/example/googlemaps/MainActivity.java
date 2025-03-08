package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private Button btnMenu;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng myLocation;
    private EditText searchBar;
    private ArrayList<LatLng> points;
    private Circle zoneCircle;
    private boolean isPolygonMode = true;
    private LatLng origin;
    private LatLng destination;
    private boolean is3DMode = false;
    private LatLng lastClickedPoint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMenu = findViewById(R.id.btn_menu);
        searchBar = findViewById(R.id.search_bar);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        points = new ArrayList<>();

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String location = searchBar.getText().toString().trim();
                if (!location.isEmpty()) {
                    searchLocation(location);
                    return true;
                }
            }
            return false;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnMenu.setOnClickListener(this::showMenu);

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch();
                return true;
            }
            return false;
        });

        buttonSearch.setOnClickListener(v -> performSearch());

        Button btnToggleMode = findViewById(R.id.btnToggleMode);

        btnToggleMode.setOnClickListener(v -> {
            isPolygonMode = !isPolygonMode;
            btnToggleMode.setText(isPolygonMode ? "Chế độ vẽ đa giác" : "Chế độ vẽ vùng");
            Toast.makeText(this, isPolygonMode ? "Chế độ vẽ đa giác" : "Chế độ vẽ vùng", Toast.LENGTH_SHORT).show();
        });

        ImageButton toggle3DButton = findViewById(R.id.btn_toggle_3d);
        toggle3DButton.setOnClickListener(v -> toggle3DView());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    private void showMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.map_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_my_location) {
                enableMyLocation();
                return true;
            } else if (item.getItemId() == R.id.action_directions) {
                LatLng origin = new LatLng(10.8231, 106.6297);
                LatLng destination = new LatLng(10.762622, 106.660172);
                showDirections(origin, destination);
                return true;
            } else if (item.getItemId() == R.id.action_map_type) {
                toggleMapType();
                return true;
            } else if (item.getItemId() == R.id.action_transit_directions) {
                LatLng origin1 = new LatLng(10.8231, 106.6297);
                LatLng destination1 = new LatLng(10.762622, 106.660172);
                showDirectionsWithDurationAndDistance(origin1, destination1);
                return true;
            } else {
                return false;
            }
        });

        popup.show();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(myLocation).title("Vị trí của tôi"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                        Toast.makeText(this, "Vị trí của bạn đã được xác định", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Không thể xác định vị trí của bạn", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDirections(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyDX6Pov0dpZQskxY8b2_YKWyVhR1iZu1Qw";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.has("routes") && response.getJSONArray("routes").length() > 0) {
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);

                            if (route.has("overview_polyline")) {
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String encodedPolyline = overviewPolyline.getString("points");

                                List<LatLng> path = decodePolyline(encodedPolyline);
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(path)
                                        .color(Color.BLUE)
                                        .width(10);

                                mMap.addPolyline(polylineOptions);
                            } else {
                                Toast.makeText(this, "Không tìm thấy polyline trong tuyến đường", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy tuyến đường", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi phân tích dữ liệu chỉ đường: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Lỗi khi lấy dữ liệu chỉ đường";
                    if (error.networkResponse != null) {
                        errorMessage = "Mã lỗi: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonObjectRequest);
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng((lat / 1E5), (lng / 1E5));
            polyline.add(point);
        }

        return polyline;
    }

    private void toggleMapType() {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Toast.makeText(this, "Đã đổi sang bản đồ vệ tinh", Toast.LENGTH_SHORT).show();
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(this, "Đã đổi sang bản đồ thường", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performSearch() {
        String location = searchBar.getText().toString().trim();
        if (location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên địa điểm", Toast.LENGTH_SHORT).show();
            return;
        }

        searchLocation(location);
    }

    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(this, "Đã tìm thấy: " + location, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không tìm thấy địa điểm: " + location, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tìm kiếm địa điểm", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawPolylineForDistance(LatLng startPoint, LatLng endPoint) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .width(5)
                .color(Color.BLUE);

        mMap.addPolyline(polylineOptions);
    }

    private void drawPolygon() {
        PolygonOptions polygonOptions = new PolygonOptions().addAll(points).strokeColor(0xFFFF0000).fillColor(0x7F00FF00).strokeWidth(5);

        Polygon polygon = mMap.addPolygon(polygonOptions);

        Toast.makeText(this, "Đã tạo đa giác với " + points.size() + " điểm", Toast.LENGTH_SHORT).show();
    }

    private void drawZone(LatLng latLng) {
        if (zoneCircle != null) {
            zoneCircle.remove();
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 255, 0, 0))
                .strokeWidth(2);

        zoneCircle = mMap.addCircle(circleOptions);
    }

    private void showDirectionsWithDurationAndDistance(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyDX6Pov0dpZQskxY8b2_YKWyVhR1iZu1Qw";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                            String distance = leg.getJSONObject("distance").getString("text");
                            String duration = leg.getJSONObject("duration").getString("text");

                            Toast.makeText(this, "Khoảng cách: " + distance + ", Thời gian: " + duration, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi tính toán thời gian và khoảng cách", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi khi lấy dữ liệu chỉ đường", Toast.LENGTH_SHORT).show()
        );

        queue.add(jsonObjectRequest);
    }

    private void toggle3DView() {
        CameraPosition currentCameraPosition = mMap.getCameraPosition();
        LatLng currentLocation = currentCameraPosition.target;

        if (is3DMode) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)
                    .tilt(0)
                    .zoom(currentCameraPosition.zoom)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            is3DMode = false;
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)
                    .tilt(60)
                    .zoom(currentCameraPosition.zoom)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            is3DMode = true;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(10.7769, 106.7009);
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("TP. Hồ Chí Minh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14));

        mMap.setOnMapClickListener(latLng -> {
            if (origin == null) {
                origin = latLng;
                Toast.makeText(this, "Đã chọn điểm bắt đầu", Toast.LENGTH_SHORT).show();
            } else if (destination == null) {
                destination = latLng;
                Toast.makeText(this, "Đã chọn điểm kết thúc", Toast.LENGTH_SHORT).show();

                showDirections(origin, destination);
            }
        });

        mMap.setOnMapClickListener(latLng -> {
                if (isPolygonMode) {
                    points.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Điểm " + points.size()));

                    if (points.size() >= 3) {
                        drawPolygon();
                    }
                } else {
                    drawZone(latLng);
                }
        });

        mMap.setOnMapLongClickListener(latLng -> {
            if (myLocation != null) {
                drawPolylineForDistance(myLocation, latLng);

                lastClickedPoint = latLng;

                double distance = calculateDistance(myLocation, lastClickedPoint);
                Toast.makeText(this, "Khoảng cách: " + distance + " m", Toast.LENGTH_SHORT).show();

                mMap.addMarker(new MarkerOptions().position(latLng).title("Điểm đã chọn"));
            } else {
                Toast.makeText(this, "Không thể xác định vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Xử lý sự kiện khi nhấn vào marker
        mMap.setOnMarkerClickListener(marker -> {
            showPlaceDetails(marker);
            return true;
        });
    }

    private void showPlaceDetails(Marker marker) {
        Intent intent = new Intent(this, PlaceDetailsActivity.class);
        intent.putExtra("title", marker.getTitle());
        intent.putExtra("snippet", marker.getSnippet());
        startActivity(intent);
    }

    private double calculateDistance(LatLng start, LatLng end) {
        double R = 6371;
        double lat1 = Math.toRadians(start.latitude);
        double lon1 = Math.toRadians(start.longitude);
        double lat2 = Math.toRadians(end.latitude);
        double lon2 = Math.toRadians(end.longitude);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;

        return distance * 1000;
    }

}