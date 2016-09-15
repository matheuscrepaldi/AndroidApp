package com.example.matheus.volleyinsertdata;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        Intent intent = getIntent();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    //==========================================================================================


    //================================== Pega Posição por GPS ==================================
    @Override
    public void onMapReady(GoogleMap map) {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mMap = map;

        mMap.setMyLocationEnabled(true);

         double lat = location.getLatitude();
         double lon =  location.getLongitude();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.889, -87.622), 18));

        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.mega)).anchor(0.0f, 1.0f).position(
                new LatLng(lat, lon)));

        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

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
