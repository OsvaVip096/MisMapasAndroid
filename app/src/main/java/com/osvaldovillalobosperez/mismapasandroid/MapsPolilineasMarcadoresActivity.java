package com.osvaldovillalobosperez.mismapasandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsPolilineasMarcadoresActivity extends FragmentActivity
        implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Button btnMarcador, btnLinea, btnPoligono;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_polilineas_marcadores);

        IniciarUI();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map_markers);
        mapFragment.getMapAsync(this);
    }

    private void IniciarUI() {
        btnMarcador = findViewById(R.id.btnMarcador);
        btnMarcador.setOnClickListener(this);
        btnLinea = findViewById(R.id.btnLineas);
        btnLinea.setOnClickListener(this);
        btnPoligono = findViewById(R.id.btnPoligono);
        btnPoligono.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnPoligono)) {
            MostrarPoligono();
        }

        if (v.equals(btnLinea)) {
            MostrarLineas();
        }

        if (v.equals(btnMarcador)) {
            LatLng angelIndependencia = new LatLng(19.426978, -99.167775);
            CameraPosition position = new CameraPosition.Builder()
                    .target(angelIndependencia)
                    .bearing(45)
                    .zoom(19)
                    .tilt(70)
                    .build();
            CameraUpdate campos = CameraUpdateFactory.newCameraPosition(position);
            mMap.animateCamera(campos);

            mMap.addMarker(
                    new MarkerOptions().position(angelIndependencia)
                            .title("Angel de la Independencia")
            );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Toast.makeText(
                            MapsPolilineasMarcadoresActivity.this,
                            "Mi última ubicación conocida es\n" +
                                    "Latitud: " + location.getLatitude() +
                                    "\nLongitud: " + location.getLongitude(),
                            Toast.LENGTH_LONG
                    ).show();

                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(ll).title("Mi ubicación actual."));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
                } else {
                    Toast.makeText(
                            MapsPolilineasMarcadoresActivity.this,
                            "No existe ninguna ubicación conocida.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    private void MostrarPoligono() {
        PolygonOptions rectangulo = new PolygonOptions().add(
                new LatLng(45.0, -12.0),
                new LatLng(45.0, 5.0),
                new LatLng(34.5, 5.0),
                new LatLng(34.5, -12.0),
                new LatLng(45.0, -12.0)
        );

        PolygonOptions rectOptions = new PolygonOptions().add(
                new LatLng(37.35, -122.0),
                new LatLng(37.45, -122.0),
                new LatLng(37.45, -122.2),
                new LatLng(37.35, -122.2),
                new LatLng(37.35, -122.0)
        );

        rectangulo.strokeWidth(8);
        rectangulo.strokeColor(Color.RED);

        mMap.addPolygon(rectOptions);

        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(
                new LatLng(37.35, -122.0), 10
        );

        mMap.moveCamera(camera);
    }

    private void MostrarLineas() {
        PolylineOptions lineas = new PolylineOptions()
                .add(new LatLng(45.0, -12.0))
                .add(new LatLng(45.0, 5.0))
                .add(new LatLng(34.5, 5.0));

        lineas.width(8);
        lineas.color(Color.RED);

        mMap.addPolyline(lineas);

        LatLng angelInd = new LatLng(45.0, 5.0);
        CameraPosition position = new CameraPosition.Builder()
                .target(angelInd)
                .bearing(45)
                .zoom(19)
                .tilt(70)
                .build();

        CameraUpdate campos = CameraUpdateFactory.newCameraPosition(position);
        mMap.animateCamera(campos);
    }
}
