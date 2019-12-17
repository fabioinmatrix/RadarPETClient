package com.example.radarpetclient;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.example.radarpetclient.model.Registro;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private List<Registro> mRegistros;
    private SupportMapFragment mapFragment;
    private LatLng latLngOrigin;
    private LatLng latLngDistance;
    private LatLng latLngOriginCircle;
    private LatLng latLngFinalCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRegistros = extras.getParcelableArrayList("mRegistros");
        }

        latLngOrigin = new LatLng(Double.parseDouble(mRegistros.get(0).getLatitude()), Double.parseDouble(mRegistros.get(0).getLongitude()));
        latLngDistance = new LatLng(-10.1829826, -48.3341296);
        int finalList = mRegistros.size();
        latLngOriginCircle = new LatLng(Double.parseDouble(mRegistros.get(0).getLatitude()), Double.parseDouble(mRegistros.get(0).getLongitude()));
        latLngFinalCircle = new LatLng(Double.parseDouble(mRegistros.get(finalList - 1).getLatitude()), Double.parseDouble(mRegistros.get(finalList - 1).getLongitude()));

        for (Registro list : mRegistros) {
            showPointerOnMap(Double.parseDouble(list.getLatitude()), Double.parseDouble(list.getLongitude()), list.getTimeline());
        }
    }

    private void showPointerOnMap(final double latitude, final double longitude, final String timeline) {

        // Add a marker and move the camera
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.footpet);
                LatLng latLng = new LatLng(latitude, longitude);

                double distanceMap = SphericalUtil.computeDistanceBetween(latLngDistance, latLng);

                Circle circleOrigin = googleMap.addCircle(new CircleOptions()
                        .center(latLngOriginCircle)
                        .radius(20)
                        .strokeWidth(0)
                        .strokeColor(Color.GREEN)
                        .fillColor(0x2500ff00));

                if (distanceMap > 10) {
                    googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                                    .target(latLng)
                                    .zoom(18)
                                    .bearing(100)
                                    .tilt(45)
                                    .build()));
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("footpet",120,120)))
                            .title(timeline)
                            .snippet(latitude + " / " + longitude));

                    Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                            .add(latLngOrigin, latLng)
                            .width(5)
                            .color(Color.GREEN));

                    latLngOrigin = new LatLng(latitude, longitude);
                    latLngDistance = new LatLng(latitude, longitude);
                }

                double distanceMapTwo = SphericalUtil.computeDistanceBetween(latLngOrigin, latLngFinalCircle);

                if (distanceMapTwo > 10) {
                    Circle circleFinal = googleMap.addCircle(new CircleOptions()
                            .center(latLngFinalCircle)
                            .radius(20)
                            .strokeWidth(0)
                            .strokeColor(Color.YELLOW)
                            .fillColor(0x25ffff00));
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
