package com.example.okis.fiwareproba;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final long LOCATION_REFRESH_TIME = 1;
    private static final long LOCATION_REFRESH_DISTANCE = 1;

    private float coord_sirina = 0; //------ Ekvador ----- latituda
    private float coord_duzina = 0; // ----- Grinic ----- longituda
    private String id_senzora = "";

    private LocationListener locationListener;
    private LocationManager locationManager;

    private Location trenutna_lokacija=null;
    private String vrati_podatke=null;

    private LatLng coord_point;
    private MarkerOptions marker1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        //uzimam koordinate koje sam prosledio
        String[] koordinate = getIntent().getStringExtra("kordinate").split("[,]");
        //uzimam naziv markera
        id_senzora = getIntent().getStringExtra("id_senozra");
       // mMap.clear();
        coord_sirina = Float.parseFloat(koordinate[0]);
        coord_duzina = Float.parseFloat(koordinate[1]);
            //zatim setujem tacku sa tim kordaimata
        coord_point = new LatLng(coord_sirina, coord_duzina); //lokacija senzora
            //a onda postavljam marker sa tim koordinata
        marker1=new MarkerOptions().position(coord_point).title(id_senzora);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                trenutna_lokacija=location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            } else {

            }
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener); //na 5s, ako je pomeranje 0m
        vrati_podatke=null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton(){
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MapsActivity.this,MainActivity.class);
        i.putExtra("nova_lokacija_senzora",vrati_podatke);
        startActivityForResult(i, 1);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(marker1); //naziv
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord_point));//stavljanje tacke na mapu
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));//zum

        mMap.setMyLocationEnabled(true);//ovo je dugmence za sopstevnu lokaciju
    }
    void azuriranjeLokacijeSenzora(View v) {
        if(trenutna_lokacija!=null){
            vrati_podatke=trenutna_lokacija.getLatitude()+","+trenutna_lokacija.getLongitude();
            Toast.makeText(getBaseContext(),"Promena evidentirana", Toast.LENGTH_LONG).show();
            mMap.clear();
            coord_point = new LatLng(trenutna_lokacija.getLatitude(),trenutna_lokacija.getLongitude()); //lokacija senzora
            marker1=new MarkerOptions().position(coord_point).title(id_senzora);
            mMap.addMarker(marker1); //naziv
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));//zum

        }else
            Toast.makeText(getBaseContext(),"Nije evidentirano pomeranje", Toast.LENGTH_LONG).show();
    }
    void prikazTrenutnePozicije(View v) {
        if(trenutna_lokacija!=null){
            String prikaz="Sirina:"+trenutna_lokacija.getLatitude()+"\n"+"Duzina:"+trenutna_lokacija.getLongitude();
            Toast.makeText(getBaseContext(),prikaz, Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(getBaseContext(),"Nema podatka sa senzora", Toast.LENGTH_LONG).show();
    }

}
