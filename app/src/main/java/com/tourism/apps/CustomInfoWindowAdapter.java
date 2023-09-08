package com.tourism.apps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Objects;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context mContext;

    private String placeNames;

    public CustomInfoWindowAdapter(Context context,String placeName) {
        mContext = context;
        placeNames = placeName;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        // If it's the placeMarker, return null to use the default info window
        if (marker.getTitle() != null && marker.getTitle().equals(placeNames)) {
            return null;
        }

        render(marker, mWindow);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        // If it's the placeMarker, return null to use the default info window
        if (marker.getTitle() != null && marker.getTitle().equals(placeNames)) {
            return null;
        }


        render(marker, mWindow);
        return mWindow;
    }

    private void render(Marker marker, View view) {
        // Retrieve the MarkerData object from the marker's tag
        MarkerData markerData = (MarkerData) marker.getTag();

        // Now you can access the marker data like this:
        assert markerData != null;
        String photoReference = markerData.getPhotoReference();
        boolean openNow = markerData.isOpenNow();
        List<String> typesList = markerData.getTypesList();
        double rating = markerData.getRating();
        int userRatingsTotal = markerData.getUserRatingsTotal();
        String address = markerData.getAddress();

        // Update your custom info window view (view) with data from markerData
        ImageView placeImageView = view.findViewById(R.id.info_window_image);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView openNowTextView = view.findViewById(R.id.openNowTextView);
        TextView typesTextView = view.findViewById(R.id.typesTextView);
        TextView ratingTextView = view.findViewById(R.id.ratingTextView);
        TextView userRatingsTotalTextView = view.findViewById(R.id.userRatingsTotalTextView);
        TextView addressTextView = view.findViewById(R.id.addressTextView);


        // Load place image using Glide library
        if (!Objects.equals(photoReference, "")){
            Glide.with(view)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=AIzaSyDNUMdNN0AtAfT95yf0TICBOe_WWYoOGNU")
                    .error(R.drawable.default_hospital)
                    .into(placeImageView);
        }else {
            Glide.with(view)
                    .load(R.drawable.default_hospital)
                    .into(placeImageView);
        }
        // Set corner radius for the ImageView
        float cornerRadius = view.getResources().getDimension(R.dimen.image_corner_radius); // Define the desired corner radius in dimensions.xml
        placeImageView.setClipToOutline(true);
        placeImageView.setOutlineProvider(new RoundedOutlineProvider(cornerRadius));


        titleTextView.setText(marker.getTitle());

        if (openNow) {
            openNowTextView.setText("Opening Hours: Open Now");
        } else {
            openNowTextView.setText("Opening Hours: Closed");
        }

        // Assuming typesList is a List<String> containing types
        StringBuilder typesBuilder = new StringBuilder();
        for (String type : typesList) {
            typesBuilder.append(type).append(", ");
        }
        // Remove the trailing comma and space
        if (typesBuilder.length() > 0) {
            typesBuilder.setLength(typesBuilder.length() - 2);
        }
        typesTextView.setText("Types: "+typesBuilder);
        ratingTextView.setText("Rating: " + rating);
        userRatingsTotalTextView.setText("Total Ratings: " + userRatingsTotal);
        addressTextView.setText("Address: " + address);
    }
}
