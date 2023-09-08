package com.tourism.apps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ReviewActivity extends AppCompatActivity {

    private double placeLat,placeLng;
    private String placeName,placeImageUrl,placeId,username,email;

    double totalRating = 0.0;
    int numRatings = 0;
    double averageRating = 0;
    private String userImageUrl;
    private String uid;
    private float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        placeLat = getIntent().getDoubleExtra("placeLat", 0.0);
        placeLng = getIntent().getDoubleExtra("placeLng", 0.0);
        placeName = getIntent().getStringExtra("placeName");
        placeId = getIntent().getStringExtra("placeId");
        placeImageUrl = getIntent().getStringExtra("placeImageUrl");

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(placeName + "'s Review");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            uid = currentUser.getUid();
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity
            }
        });


        ImageView imageView = findViewById(R.id.imageView);
        // Define Glide options
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.default_profile_image) // Placeholder image while loading (optional)
                .error(R.drawable.default_profile_image); // Error image if the loading fails (optional)

        // Load and display the image using Glide
        Glide.with(this)
                .load(placeImageUrl)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade()) // Smooth cross-fade transition (optional)
                .into(imageView);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        username = user.getUsername();
                        userImageUrl = user.getImgUrl();
                        TextInputEditText nameEditText = findViewById(R.id.nameEditText);
                        nameEditText.setText(username);
                    }
                } else {
                    // User data does not exist under the specified UID
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if any
            }
        });

        Button buttonSubmitReview = findViewById(R.id.submitButton);
        buttonSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReview();
            }
        });


        fetchRatings(placeId);
    }

    private void saveReview() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reviewsRef = database.getReference("reviews").child(placeId).push();


        RatingBar ratingBar = findViewById(R.id.ratingBar);

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Define the date format you want (e.g., "yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Format the current date as a string
        String formattedDate = dateFormat.format(currentDate);

        TextInputEditText reviewWord = findViewById(R.id.descriptionEditText);

        String userId = uid; // Replace with the actual user ID
        String userImgUrl = userImageUrl; // Replace with the actual user image URL
        String currentusername = username; // Replace with the actual username
        rating = ratingBar.getRating(); // Replace with the actual rating
        String reviewDescription = Objects.requireNonNull(reviewWord.getText()).toString(); // Replace with the actual review description

        Review review = new Review(userId, userImgUrl, currentusername, rating, formattedDate, reviewDescription);

        reviewsRef.setValue(review);

        saveNewAverageRating();

        finish();
    }

    private void saveNewAverageRating() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference placeRef = database.getReference("places").child(placeId);

        placeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Calculate the new average rating
                    double newAverageRating=0.0;
                    String formattedRating="0";

                    newAverageRating = (totalRating +rating)/(numRatings+1);
                    // Format the newAverageRating with two decimal places
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    formattedRating = decimalFormat.format(newAverageRating);

                    if (formattedRating.equals("0.00")){
                        formattedRating = "0";
                    }

                    // Update the "averageRating" field in the database with the new average rating
                    placeRef.child("rating").setValue(formattedRating);

                    Log.d("MyTag", "New average rating saved: " + newAverageRating);
                } else {
                    // Handle the case when the place data does not exist
                    Log.d("MyTag", "Place data does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the update
                Log.e("MyTag", "Database error: " + databaseError.getMessage());
            }
        });
    }

    public void fetchRatings(String placeId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ratingsRef = database.getReference("reviews").child(placeId);

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalRating = 0.0;
                numRatings = 0;

                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    Double rating = ratingSnapshot.child("rating").getValue(Double.class);
                    if (rating != null) {
                        totalRating += rating;
                        numRatings++;
                    }
                }

                if (numRatings > 0) {
                    averageRating = totalRating / numRatings;
                    Log.d("MyTag", "Number of ratings: " + numRatings);
                    Log.d("MyTag", "Average rating: " + averageRating);
                } else {
                    // Handle the case when there are no ratings
                    Log.d("MyTag", "No ratings available.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur during the fetch
                Log.e("MyTag", "Database error: " + databaseError.getMessage());
            }
        });
    }

}