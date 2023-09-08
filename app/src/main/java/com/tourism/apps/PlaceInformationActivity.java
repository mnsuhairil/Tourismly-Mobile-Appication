package com.tourism.apps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PlaceInformationActivity extends AppCompatActivity {

    private ReviewAdapter reviewAdapter;
    private WebView webView;
    private int reviewCount=0;
    private String averageRating;
    private Place selectedPlace;
    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_information);

        // Retrieve the Intent that started this activity
        Intent intent = getIntent();

        // Check if the Intent contains the "selected_place" extra
        if (intent.hasExtra("selected_place")) {
            // Retrieve the Place object from the Intent
             selectedPlace = intent.getParcelableExtra("selected_place");

            // Now you have the selectedPlace object, and you can access its properties
            String placeName = selectedPlace.getPlaceName();

            String placeWebsite = selectedPlace.getOfficialWebsite();
            String placeETiket = selectedPlace.getETiketLink();
            String placeAddress = selectedPlace.getPlaceAddress();
            double placeLatitude = selectedPlace.getLatitude();
            double placeLongitude = selectedPlace.getLongitude();
            String placeImageUrl = selectedPlace.getImageUrl();
            placeId = intent.getStringExtra("placeId");
            Log.d("PlaceInfo", "Place Name: " + placeName);
            Log.d("PlaceInfo", "Place Rating: " );
            Log.d("PlaceInfo", "Official Website: " + placeWebsite);
            Log.d("PlaceInfo", "eTiket Link: " + placeETiket);
            Log.d("PlaceInfo", "Place Address: " + placeAddress);
            Log.d("PlaceInfo", "Latitude: " + placeLatitude);
            Log.d("PlaceInfo", "Longitude: " + placeLongitude);
            Log.d("PlaceInfo", "Image URL: " + placeImageUrl);
            Log.d("PlaceId", "Place ID: " + placeId);

            boolean relodPage;
            relodPage = intent.getBooleanExtra("relodPage",false);
            if (relodPage){
                reloadActivity();
            }
            // Access the views in your layout
            ImageView placeImage = findViewById(R.id.placeImage);
            TextView placeNameTextView = findViewById(R.id.placeName);
            Button buyTicketButton = findViewById(R.id.buyTicketButton);
            ImageView backButton = findViewById(R.id.backButton);
            ImageView menuButton = findViewById(R.id.menuButton);
            TextView averageCountTextView = findViewById(R.id.averageCount);
            RatingBar averageStarRatingBar = findViewById(R.id.averageStarRating);
            TextView userReviewCountTextView = findViewById(R.id.userReviewCount);
            TextView descTextView = findViewById(R.id.descTextView);
            // Find the WebView by its ID
            webView = findViewById(R.id.webView);

            // Load place image using Glide library
            Glide.with(this)
                    .load(placeImageUrl)
                    .into(placeImage);

            placeNameTextView.setText(placeName);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference placeRef = database.getReference("places").child(placeId);

            // Add a ValueEventListener to continuously listen for changes in the data
            placeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the average rating value
                        String averageRating = dataSnapshot.child("rating").getValue(String.class);

                        if (averageRating != null) {
                            averageCountTextView.setText(averageRating);
                            averageStarRatingBar.setRating(Float.parseFloat(averageRating)); // Assuming rating is a String
                        } else {
                            averageCountTextView.setText("0");
                            averageStarRatingBar.setRating(Float.parseFloat("0"));
                        }
                    } else {
                        averageCountTextView.setText("0");
                        averageStarRatingBar.setRating(Float.parseFloat("0"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that may occur during the retrieval
                    Log.e("MyTag", "Database error: " + databaseError.getMessage());
                }
            });

            //descTextView.setText("Ratings and reviews are verified..."); // Set your description text

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView.getVisibility() == View.VISIBLE) {
                        webView.setVisibility(View.GONE);
                    } else {
                        finish(); // Close the activity
                    }
                }
            });

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(PlaceInformationActivity.this, menuButton);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.menu_main, popupMenu.getMenu());

                    // Set a listener to handle when the menu is dismissed
                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            // Change the menu button image when the menu is dismissed
                            menuButton.setImageResource(R.drawable.ic_menu);
                            menuButton.setTag("closed");
                        }
                    });

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.action_facilities:
                                    // Handle "Nearby Facilities" item click
                                    // Navigate to the nearby facilities page
                                    Intent intent = new Intent(PlaceInformationActivity.this,MapsActivity.class);
                                    intent.putExtra("placeLat",placeLatitude);
                                    intent.putExtra("placeLng",placeLongitude);
                                    intent.putExtra("placeName",placeName);
                                    startActivity(intent);
                                    break;
                                case R.id.action_direction:
                                    // Handle "Get Directions" item click
                                    // Navigate to the get directions page
                                    Intent directionIntent = new Intent(PlaceInformationActivity.this,MapsDirectionActivity.class);
                                    directionIntent.putExtra("placeLat",placeLatitude);
                                    directionIntent.putExtra("placeLng",placeLongitude);
                                    directionIntent.putExtra("placeName",placeName);
                                    startActivity(directionIntent);
                                    break;
                                case R.id.action_website:
                                    // Handle "Official Website" item click
                                    // Navigate to the official website page
                                    webView.setVisibility(View.VISIBLE);
                                    // Enable JavaScript (if needed)
                                    WebSettings webSettings = webView.getSettings();
                                    webSettings.setJavaScriptEnabled(true);

                                    // Load a website URL
                                    webView.loadUrl(placeWebsite);

                                    // Set a WebViewClient to handle page navigation within the WebView
                                    webView.setWebViewClient(new WebViewClient());

                                    break;

                                case R.id.reviewPlace:
                                    // Handle "Nearby Facilities" item click
                                    // Navigate to the nearby facilities page
                                    Intent reviewIntent = new Intent(PlaceInformationActivity.this,ReviewActivity.class);
                                    reviewIntent.putExtra("placeLat",placeLatitude);
                                    reviewIntent.putExtra("placeLng",placeLongitude);
                                    reviewIntent.putExtra("placeName",placeName);
                                    reviewIntent.putExtra("placeId",placeId);
                                    reviewIntent.putExtra("placeImageUrl",placeImageUrl);
                                    startActivity(reviewIntent);
                                    break;
                            }

                            return true;
                        }
                    });

                    popupMenu.show();
                    menuButton.setImageResource(R.drawable.ic_menu_open);
                }
            });

            // Handle button click (e.g., open the website)
            buyTicketButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the button click event (e.g., open the website)

                    webView.setVisibility(View.VISIBLE);
                    // Enable JavaScript (if needed)
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                    // Load a website URL

                    webView.loadUrl(placeETiket);

                    // Set a WebViewClient to handle page navigation within the WebView
                    webView.setWebViewClient(new WebViewClient());


                }
            });
            // Initialize the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recyclerViewReview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Create a list to store reviews
            List<Review> reviewList = new ArrayList<>();

            // Initialize the ReviewAdapter with an empty list
            reviewAdapter = new ReviewAdapter(reviewList,this);
            recyclerView.setAdapter(reviewAdapter);

            // Firebase Database Reference
            DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
            DatabaseReference placeReviewsRef = reviewsRef.child(placeId);

            // Add a ValueEventListener to fetch reviews from Firebase Realtime Database
            placeReviewsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear the previous review data
                    reviewList.clear();
                    reviewCount=0;
                    // Check if reviews exist
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            Log.d("debug 1", "debug 1: " + dataSnapshot);
                            // Access each review's data
                            Review review = reviewSnapshot.getValue(Review.class);
                            reviewList.add(review);

                            reviewCount++;
                        }

                        userReviewCountTextView.setText("("+reviewCount+" Reviews)"); // You can set the actual review count here
                        // Notify the adapter that data has changed
                        reviewAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Log.e("Firebase", "Database Error: " + databaseError.getMessage());
                }
            });

            // Set other TextViews and properties accordingly
        }
    }

    private void ratingSelectedPlace(String placeId, RatingCallback callback) {

    }
    public interface RatingCallback {
        void onRatingRetrieved(String rating);
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        intent.putExtra("selected_place", selectedPlace);
        intent.putExtra("placeId",placeId);// Finish the current activity
        startActivity(intent); // Start a new instance of the activity
        finish();
    }


    // Handle back button press to navigate within WebView
   /* @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }*/
}