import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-auth.js';
import { getDatabase, ref, get, set ,push } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-database.js';
import { getStorage, ref as storageRef, uploadBytes, getDownloadURL } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-storage.js';

// Declare the place variable at a higher scope
let place;
let bounds = new google.maps.LatLngBounds();

function initMap() {
    const map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 0, lng: 0 }, // Set initial map center coordinates
        zoom: 8, // Set the initial zoom level
    });

    // Create a search box and link it to the UI element.
    const input = document.getElementById("map-search");
    const searchBox = new google.maps.places.SearchBox(input);

    // Bias the SearchBox results towards the current map's viewport.
    map.addListener("bounds_changed", function () {
        searchBox.setBounds(map.getBounds());
    });

// Listen for the event fired when the user selects a prediction and retrieve more details.
searchBox.addListener("places_changed", function () {
    const places = searchBox.getPlaces();

    if (places.length === 0) {
        return;
    }

    // Store the selected place in the higher-scoped variable
    place = places[0];

    // Rest of your code for adding markers and other functionality

    const marker = new google.maps.Marker({
        map,
        title: place.name,
        position: place.geometry.location,
    });

    if (place.geometry.viewport) {
        // Only geocodes have viewport.
        bounds.union(place.geometry.viewport);
    } else {
        bounds.extend(place.geometry.location);
    }

    // Move and zoom the map to the selected place
    map.fitBounds(bounds);
    map.setCenter(place.geometry.location);
});
}

// Call the initMap function when the Google Maps API has loaded
google.maps.event.addDomListener(window, "load", initMap);

// Firebase Configuration
const firebaseConfig = {
    apiKey: "AIzaSyBNWudMKxRFTIyLouSVdnAWVz99fSYawIs",
    authDomain: "itourismly-7c0a0.firebaseapp.com",
    databaseURL: "https://itourismly-7c0a0-default-rtdb.firebaseio.com",
    projectId: "itourismly-7c0a0",
    storageBucket: "itourismly-7c0a0.appspot.com",
    messagingSenderId: "237687395411",
    appId: "1:237687395411:web:3dfdf9ef1bb3025856e2a6",
    measurementId: "G-8THP9FDK15"
  };

// Initialize Firebase
const firebaseApp = initializeApp(firebaseConfig);

// Firebase Authentication
const auth = getAuth(firebaseApp);
const database = getDatabase(firebaseApp);
const storage = getStorage(firebaseApp);

// Handle form submission
const addPlaceForm = document.getElementById("addPlaceForm");

addPlaceForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const placeName = document.getElementById("placeName").value;
    const officialWebsite = document.getElementById("officialWebsite").value;
    const placeAddress = document.getElementById("placeAddress").value;
    const eTiketLink = document.getElementById("eTiketLink").value;
    const placeImage = document.getElementById("placeImage").files[0];

    // Upload image to Firebase Storage
    const imageRef = storageRef(storage, `images/${placeImage.name}`);
    const latitude = place.geometry.location.lat();
    const longitude = place.geometry.location.lng();

    uploadBytes(imageRef, placeImage)
        .then((snapshot) => {
            console.log("Image uploaded successfully");
            // Get the download URL of the uploaded image
            return getDownloadURL(snapshot.ref);
        })
        .then((imageUrl) => {
            // Save place details to Firebase Realtime Database
            const placesRef = ref(database, 'places');
            const newPlaceRef = push(placesRef); // Use push to generate a unique key
            set(newPlaceRef, {
                placeName: placeName,
                officialWebsite: officialWebsite,
                placeAddress: placeAddress,
                eTiketLink: eTiketLink,
                imageUrl: imageUrl,
                rating:"0",
                latitude: latitude,
                longitude: longitude, // URL of the uploaded image
            });
            console.log("Place details saved successfully");
            // Redirect or display a success message
            resetFormAndMap();
        })
        .catch((error) => {
            console.error("Error uploading image:", error);
        });
});

function resetFormAndMap() {

    document.getElementById('placeName').value = '';
    document.getElementById('officialWebsite').value = '';
    document.getElementById('eTiketLink').value = '';
    document.getElementById('placeAddress').value = '';
    document.getElementById('placeImage').value = '';
    document.getElementById('map-search').value = '';

    const map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: 0, lng: 0 },
        zoom: 8,
    });
}
