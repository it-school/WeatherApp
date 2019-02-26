package ua.com.it_school.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng Odessa = new LatLng(Coordinates.latitude, Coordinates.longitude);
        mMap.addMarker(new MarkerOptions().position(Odessa).title("Marker in Odessa"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Odessa));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("New weather place"));
                Intent intent = new Intent();
                intent.putExtra("longitude", latLng.longitude);
                intent.putExtra("latitude", latLng.latitude);
                setResult(1, intent);
                finish();
            }
        });

        /*
        CameraPosition cameraPosition =  mMap.getCameraPosition();
        LatLng currentPosition = cameraPosition.target;
        Coordinates.latitude = currentPosition.latitude;
        Coordinates.longitude = currentPosition.longitude;
        */

//        try {
//                Task locationResult =  (LocationServices.getFusedLocationProviderClient(this)).getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            Location mLastKnownLocation = mMap.getMyLocation();
//                            // Set the map's camera position to the current location of the device.
//                            mLastKnownLocation = (Location) task.getResult();
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), 1));
//                        } else {
//                            String TAG = null;
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Odessa, 1));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//        } catch(SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }


    }
}