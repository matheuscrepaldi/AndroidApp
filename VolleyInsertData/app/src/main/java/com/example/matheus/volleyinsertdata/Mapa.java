package com.example.matheus.volleyinsertdata;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        Intent intent = getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    //================================== Pega Posição por GPS ==================================
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.889,-87.622),18));

        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.mega)).anchor(0.0f, 1.0f).position(
                new LatLng(41.889,-87.622)));

        map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));


    }
    //==========================================================================================
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng local = new LatLng(-21.399, -50.477);
        mMap.addMarker(new MarkerOptions().position(local).title("Você está aqui!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
    }*/
}
