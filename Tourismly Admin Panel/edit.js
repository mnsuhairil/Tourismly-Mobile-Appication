// Initialize Firebase (use your own Firebase configuration)
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-app.js';
import { getDatabase, ref, get, set ,push,update } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-database.js';
import { getStorage, ref as storageRef, uploadBytes, getDownloadURL } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-storage.js';

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

// Firebase Database and Storage references
const database = getDatabase(firebaseApp);
const storage = getStorage(firebaseApp);

// Reference to the "placesList" tbody element in HTML
const placesList = document.getElementById("placesList");

// Declare the place variable at a higher scope
let place;
let bounds = new google.maps.LatLngBounds();
// Reference to the progress bar element
const progressBar = document.getElementById("progress");

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

// Reference to form fields
const editPlaceForm = document.getElementById("editPlaceForm");
const editPlaceNameField = document.getElementById("editPlaceName");
const editOfficialWebsiteField = document.getElementById("editOfficialWebsite");
const editETiketLink = document.getElementById("editETiketLink");
const editPlaceAddressField = document.getElementById("editPlaceAddress");
const editPlaceImageField = document.getElementById("editPlaceImage");
const cancelButton = document.getElementById("cancelEditButton");


// Add a click event listener to the "Cancel" button
cancelButton.addEventListener("click", () => {
    // Redirect back to the manage places page
    window.location.href = "manage-places.html";
});

// Retrieve the place key from the query parameter in the URL
const urlParams = new URLSearchParams(window.location.search);
const placeKey = urlParams.get('key'); // Assuming 'key' is the query parameter name

// Function to populate form fields with existing place data
function populateFormFields(placeData) {
    editPlaceNameField.value = placeData.placeName;
    editOfficialWebsiteField.value = placeData.officialWebsite;
    editPlaceAddressField.value = placeData.placeAddress;
    // Additional fields can be populated similarly
}


// Function to load existing place data and populate the form fields
function loadExistingPlaceData() {
    const placeRef = ref(database, `places/${placeKey}`);

    get(placeRef)
        .then((snapshot) => {
            if (snapshot.exists()) {
                const placeData = snapshot.val();
                populateFormFields(placeData);
            } else {
                // Handle the case where the place data does not exist
                console.error('Place data not found.');
            }
        })
        .catch((error) => {
            // Handle any errors that occur while retrieving data
            console.error('Error retrieving place data:', error);
        });
}

// Function to handle form submission
function handleFormSubmit(event) {
    event.preventDefault();

    // Retrieve updated values from form fields
    const editedPlaceName = editPlaceNameField.value;
    const editedOfficialWebsite = editOfficialWebsiteField.value;
    const editedETiketLink = editETiketLink.value;
    const editedPlaceAddress = editPlaceAddressField.value;
    // Additional fields can be retrieved similarly
    const placeImage = document.getElementById("editPlaceImage").files[0];

    // Upload image to Firebase Storage
    const imageRef = storageRef(storage, `images/${placeImage.name}`);
    const latitude = place.geometry.location.lat();
    const longitude = place.geometry.location.lng();
    showProgressBar();
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
            // Update the place data in Firebase
            update(ref(database, `places/${placeKey}`), {
                    placeName: editedPlaceName,
                    officialWebsite: editedOfficialWebsite,
                    eTiketLink: editedETiketLink,
                    placeAddress: editedPlaceAddress,
                    imageUrl: imageUrl,
                    latitude: latitude,
                    longitude: longitude,
                    // Update other fields as needed
                })
                .then(() => {
                    hideProgressBar();
                    // Redirect back to the manage places page after successful update
                    window.location.href = 'manage-places.html';
                })
                .catch((error) => {
                    hideProgressBar();
                    // Handle any errors that occur while updating data
                    console.error('Error updating place data:', error);
                });
             })
        .catch((error) => {
            hideProgressBar();
            console.error("Error uploading image:", error);
        });
    
}

// Add event listeners
window.addEventListener('load', () => {
    initMap(); // Initialize the map
    loadExistingPlaceData(); // Load and populate existing place data

    // Add a submit event listener to the edit place form
    editPlaceForm.addEventListener('submit', handleFormSubmit);
});

// Function to show the progress bar
function showProgressBar() {
    progressBar.style.display = "block";
}

// Function to hide the progress bar
function hideProgressBar() {
    progressBar.style.display = "none";
}