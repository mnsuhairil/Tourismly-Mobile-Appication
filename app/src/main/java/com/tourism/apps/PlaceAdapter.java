package com.tourism.apps;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<Place> places;
    private String placeId;
    private Context context;

    public PlaceAdapter(List<Place> places,String placeId) {
        this.places = places;
        this.placeId = placeId;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        final Place place = places.get(position);

        holder.placeNameTextView.setText(place.getPlaceName());

        // Set default rating if not available
        float rating = 0.0f; // Default rating value
        if (place.getRating() != null && !place.getRating().isEmpty()) {
            try {
                rating = Float.parseFloat(place.getRating());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        holder.ratingBar.setRating(rating);

        // Load place image using Glide library
        Glide.with(context)
                .load(place.getImageUrl())
                .into(holder.placeImageView);

        // Set corner radius for the ImageView
        float cornerRadius = context.getResources().getDimension(R.dimen.image_corner_radius); // Define the desired corner radius in dimensions.xml
        holder.placeImageView.setClipToOutline(true);
        holder.placeImageView.setOutlineProvider(new RoundedOutlineProvider(cornerRadius));

        // Add click listener to the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference("places");

                String searchQuery = place.getPlaceName(); // Replace with the search query you want

                placesRef.orderByChild("placeName").equalTo(searchQuery).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This will be called when the data is found
                        for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                            String placeKey = placeSnapshot.getKey();
                            String placeName = placeSnapshot.child("placeName").getValue(String.class);
                            // Handle the found place data (e.g., display in UI or store in a list)
                            Log.d("PlaceSearch", "Place Key: " + placeKey);
                            Log.d("PlaceSearch", "Place Name: " + placeName);

                            // Create an intent to start the new activity
                            Intent intent = new Intent(context, PlaceInformationActivity.class);
                            // Put the Place object as an extra
                            intent.putExtra("selected_place", place);
                            intent.putExtra("placeId",placeKey);
                            // Start the new activity
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                        Log.e("PlaceSearch", "Database Error: " + databaseError.getMessage());
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void setPlaces(List<Place> places, String placeId) {
        this.places = places;
        this.placeId = placeId;
        notifyDataSetChanged();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView;
        RatingBar ratingBar;
        ImageView placeImageView;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            placeImageView = itemView.findViewById(R.id.placeImageView);
        }
    }
}
