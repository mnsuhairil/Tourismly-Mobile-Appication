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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
    // Variables for location updates
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private boolean isNavigationMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        placeLat = getIntent().getDoubleExtra("placeLat", 0.0);
        placeLng = getIntent().getDoubleExtra("placeLng", 0.0);
        placeName = getIntent().getStringExtra("placeName");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Call a method to fetch nearby hospitals or clinics
        fetchNearbyHospitalsOrClinics();
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15));

        getCurrentLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Show the navigation button when the polyline is drawn
                Button buttonNavigation = findViewById(R.id.navigationButton);
                selectedMarker = marker;
                buttonNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (selectedMarker==null){
                            Toast.makeText(MapsActivity.this, "Please select facilities marker first",Toast.LENGTH_LONG).show();
                        }
                        else if (!isNavigationMode)
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
                return false;
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

    }

    private double calculateDistance(Location location1, Location location2) {
        if (location1 == null || location2 == null) {
            return 0.0;
        }
        return location1.distanceTo(location2);
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
    private void drawPolyline(Marker hospitalMarker) {
        if (hospitalMarker == null) {
            return;
        }
        userLongitude = mMap.getMyLocation().getLongitude();
        userLatitude = mMap.getMyLocation().getLatitude();

        Log.d("Location", "Latitude: " + userLatitude + ", Longitude: " + userLongitude);

        LatLng origin = new LatLng(userLatitude, userLongitude);
        LatLng destination = hospitalMarker.getPosition();

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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
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
    private void fetchNearbyHospitalsOrClinics() {
        // Use OkHttpClient for making HTTP requests
        OkHttpClient client = new OkHttpClient();

        // Replace YOUR_API_KEY with your actual Google Maps API key
        String apiKey = "AIzaSyDNUMdNN0AtAfT95yf0TICBOe_WWYoOGNU";

        // Define the Nearby Search URL
        String nearbySearchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + placeLat + "," + placeLng +
                "&radius=1000" + // You can adjust the radius as needed (in meters)
                "&type=hospital|clinic" +
                "&key=" + apiKey;

        // Create a request to the Nearby Search URL
        Request request = new Request.Builder()
                .url(nearbySearchUrl)
                .build();

        // Make the HTTP request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network errors here
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray resultsArray = jsonObject.getJSONArray("results");

                        // Process the results and add markers for nearby places
                        for (int i = 0; i < resultsArray.length(); i++) {

                            JSONObject placeObject = resultsArray.getJSONObject(i);
                            // Extract data for the marker
                             photoReference = ""; // Extract photo reference here
                             openNow = false; // Extract open_now here
                             typesList = new ArrayList<>(); // Extract types as a list here
                             rating = 0.0; // Extract rating here
                             userRatingsTotal = 0; // Extract user_ratings_total here

                            // Get photo_reference
                            JSONArray photosArray = placeObject.optJSONArray("photos");

                            if (photosArray != null && photosArray.length() > 0) {
                                JSONObject firstPhoto = photosArray.getJSONObject(0);
                                photoReference = firstPhoto.optString("photo_reference", "");
                            }

                            // Get opening_hours
                            JSONObject openingHoursObject = placeObject.optJSONObject("opening_hours");
                            if (openingHoursObject != null) {
                                openNow = openingHoursObject.optBoolean("open_now", false);
                            }

                            // Get types as a list
                            JSONArray typesArray = placeObject.optJSONArray("types");
                            if (typesArray != null) {
                                typesList = new ArrayList<>();
                                for (int j = 0; j < typesArray.length(); j++) {
                                    if (typesArray.getString(j).equals("health")||typesArray.getString(j).equals("clinic")||typesArray.getString(j).equals("hospital")) {
                                        typesList.add(typesArray.getString(j));
                                    }
                                }
                            }

                            // Get rating
                            rating = placeObject.optDouble("rating", 0.0);
                            // Get user_ratings_total
                            userRatingsTotal = placeObject.optInt("user_ratings_total", 0);

                            //Get location latlng
                            JSONObject locationObject = placeObject.getJSONObject("geometry")
                                    .getJSONObject("location");

                            double lat = locationObject.getDouble("lat");
                            double lng = locationObject.getDouble("lng");

                            //get name
                            String name = placeObject.getString("name");


                            //address
                            String address = placeObject.getString("vicinity");

                            // Log the extracted data
                            Log.d("PlaceInfo", "Photo Reference: " + photoReference);
                            Log.d("PlaceInfo", "Open Now: " + openNow);
                            Log.d("PlaceInfo", "Types List: " + typesList.toString());
                            Log.d("PlaceInfo", "Rating: " + rating);
                            Log.d("PlaceInfo", "User Ratings Total: " + userRatingsTotal);
                            Log.d("Address", "place Address: " + address);


                            // Inside your onResponse callback
                            MarkerData markerData = new MarkerData(photoReference, openNow, typesList, rating, userRatingsTotal,address);
                            // Add a marker for the nearby place
                            BitmapDescriptor customMarkerIcon;

                            // Load the custom marker image from resources
                            Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_hospital);

                            // Define the desired width and height for the marker
                            int width = 140; // Adjust this to your desired width in pixels
                            int height = 140; // Adjust this to your desired height in pixels

                            // Resize the marker image to the desired dimensions
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(markerBitmap, width, height, false);

                            // Convert the resized bitmap to a BitmapDescriptor
                            customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);

                            // Add a marker for the nearby place with the custom marker icon
                            runOnUiThread(() -> {
                                LatLng placeLocation = new LatLng(lat, lng);
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(placeLocation)
                                        .title(name)
                                        .icon(customMarkerIcon); // Set the custom marker icon
                                selectedMarker = mMap.addMarker(markerOptions);

                                // Set marker tag with data
                                assert selectedMarker != null;
                                // Set marker tag with MarkerData object
                                selectedMarker.setTag(markerData);

                                // Set custom info window adapter for each marker
                                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this,placeName));

                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
