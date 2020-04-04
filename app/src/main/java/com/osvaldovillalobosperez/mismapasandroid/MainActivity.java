package com.osvaldovillalobosperez.mismapasandroid;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    LocationSettingsRequest.Builder builder;
    private boolean requestingLocationUpdates = true;

    TextView textView;

    public void click(View view) {
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                String msj = "Latitud: " + location.getLatitude() +
                        "\nLongitud: " + location.getLongitude();
                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_LONG).show();

                Log.i("POSICION", msj);
                textView.setText(msj);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.txt);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(
                this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String msj = "Latitud: " + location.getLatitude() +
                                    "\nLongitud: " + location.getLongitude();
                            Toast.makeText(
                                    MainActivity.this,
                                    msj,
                                    Toast.LENGTH_LONG
                            ).show();
                            Log.i("MIUBI", msj);
                        } else {
                            Log.i("MIUBI", "Sin ubicación.");
                        }
                    }
                });

        CreateLocationRequest();

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i("PRUEBA", locationSettingsResponse.toString());
                Toast.makeText(
                        MainActivity.this,
                        locationSettingsResponse.toString(),
                        Toast.LENGTH_LONG
                ).show();

                LocationSettingsStates locationSettingsStates =
                        locationSettingsResponse.getLocationSettingsStates();
                locationSettingsStates.isBlePresent();
                locationSettingsStates.isBleUsable();
                locationSettingsStates.isGpsPresent();
                locationSettingsStates.isGpsUsable();
                locationSettingsStates.isLocationUsable();
                locationSettingsStates.isNetworkLocationPresent();

                requestingLocationUpdates = true;
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(
                                MainActivity.this,
                                REQUEST_CHECK_SETTINGS
                        );
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignorar el error.
                    }
                }
            }
        });
    }

    protected void CreateLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.REQUEST_CHECK_SETTINGS) {
            Log.i("PRUEBA", String.valueOf(resultCode));
            Toast.makeText(this, String.valueOf(resultCode), Toast.LENGTH_LONG).show();
            if (resultCode == RESULT_OK) {
                this.requestingLocationUpdates = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            StartLocationUpdates();
        }
    }

    private void StartLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
