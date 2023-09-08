package com.tourism.apps;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsDirectionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double placeLat, placeLng;
    private String photoReference;
    private boolean openNow;
    private List<String> typesList;
    private double rating;
    private int userRatingsTotal;
    private Marker placeMarker;
    private String placeName;
    private Marker selectedMarker;
    private PolylineOptions polylineOptions;
    private Polyline currentPolyline;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude,userLongitude;

    private boolean isNavigationMode = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean isMoveCamera=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_direction);

        placeLat = getIntent().getDoubleExtra("placeLat", 0.0);
        placeLng = getIntent().getDoubleExtra("placeLng", 0.0);
        placeName = getIntent().getStringExtra("placeName");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng placeLocation = new LatLng(placeLat, placeLng);
        BitmapDescriptor customMarkerIcon;

        // Load the custom marker image from resources
        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_current_location);

        // Define the desired width and height for the marker
        int width = 280; // Adjust this to your desired width in pixels
        int height = 140; // Adjust this to your desired height in pixels

        // Resize the marker image to the desired dimensions
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(markerBitmap, width, height, false);

        // Convert the resized bitmap to a BitmapDescriptor
        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(placeLocation)
                .title(placeName)
                .icon(customMarkerIcon); // Set the custom marker icon
        placeMarker = mMap.addMarker(markerOptions);
        selectedMarker = placeMarker;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 17));

        getCurrentLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Show the navigation button when the polyline is drawn
                selectedMarker = marker;

                return false;
            }
        });

        Button buttonNavigation = findViewById(R.id.navigationButton);
        buttonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNavigationMode&&selectedMarker!=null)
                {
                    buttonNavigation.setText("Cancel");
                    drawPolyline(selectedMarker);
                    isNavigationMode = true;
                }else {
                    buttonNavigation.setText("Get Direction");
                    isNavigationMode = false;
                    currentPolyline.remove();
                    currentPolyline=null;

                }


            }
        });

        // Enable the user's location on the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Request location permission from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@NonNull Location location) {
                if(isNavigationMode&&currentPolyline!=null){
                    drawPolyline(selectedMarker);
                }

            }
        });

        if (mMap.isMyLocationEnabled()&&mMap!=null){
            drawPolyline(placeMarker);
        }

        // Set up the CameraIdleListener
        assert mMap != null;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Remove any existing callbacks to ensure only one is scheduled
                handler.removeCallbacks(runnable);

                // Define the delay in milliseconds (3 seconds)
                long delayMillis = 3000;

                // Create a new Runnable to execute your function
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Place your code here to run after the map has been idle for 3 seconds
                        // For example, you can call a function:
                        isMoveCamera = isNavigationMode;

                    }
                };

                // Schedule the Runnable to run after the specified delay
                handler.postDelayed(runnable, delayMillis);
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                isMoveCamera = false;
            }
        });

    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        // Use latitude and longitude as needed
                        Log.d("Location", "Latitude: " + userLatitude + ", Longitude: " + userLongitude);
                    } else {
                        // Location is null, handle this situation
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Handle any errors that may occur while getting the location
                });
    }
    private void drawPolyline(Marker selectedPlaceMarker) {
        if (selectedPlaceMarker == null) {
            return;
        }

        Location myLocation = mMap.getMyLocation();
        if (myLocation == null) {
            // Handle the case where user location is not available
            // You might want to display an error message or request location permissions.
            return ;
        }
        userLongitude = mMap.getMyLocation().getLongitude();
        userLatitude = mMap.getMyLocation().getLatitude();

        Log.d("Location", "Latitude: " + userLatitude + ", Longitude: " + userLongitude);

        LatLng origin = new LatLng(userLatitude, userLongitude);
        LatLng destination = selectedPlaceMarker.getPosition();

        // Instantiate the Directions API client
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyDNUMdNN0AtAfT95yf0TICBOe_WWYoOGNU") // Replace with your Directions API key
                .build();

        // Create a request for directions
        DirectionsApiRequest request = DirectionsApi.getDirections(context, origin.latitude + "," + origin.longitude,
                        destination.latitude + "," + destination.longitude)
                .mode(TravelMode.DRIVING); // You can change the travel mode as needed

        // Execute the request asynchronously
        request.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if (result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];
                    List<LatLng> path = new ArrayList<>();

                    // Extract and add all polyline points to the path list
                    for (DirectionsLeg leg : route.legs) {
                        for (DirectionsStep step : leg.steps) {
                            List<com.google.maps.model.LatLng> decodedPolyline = PolylineEncoding.decode(step.polyline.getEncodedPath());
                            for (com.google.maps.model.LatLng point : decodedPolyline) {
                                path.add(new LatLng(point.lat, point.lng));
                            }
                        }
                    }

                    // Draw the polyline on the map
                    runOnUiThread(() -> {
                        if (currentPolyline != null) {
                            currentPolyline.setPoints(path); // Update the existing polyline's points
                        } else {

                            polylineOptions = new PolylineOptions()
                                    .addAll(path)
                                    .color(getResources().getColor(R.color.purple_200))
                                    .width(10);

                            if (isNavigationMode) {
                                currentPolyline = mMap.addPolyline(polylineOptions);
                            }
                        }

                        if (isNavigationMode){
                            if (isMoveCamera){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin,20));

                            }
                        }else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 17));
                        }

                    });
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("DirectionsAPI", "Failed to get directions: " + e.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable the user's location on the map
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission denied, handle this case (e.g., show a message to the user)
            }
        }
    }

    // Add this method to hide the polyline
    private void hidePolyline() {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
    }

}
