// Initialize Firebase (use your own Firebase configuration)
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-app.js';
import { getDatabase, ref,remove, onChildAdded, onChildRemoved, get, update } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-database.js';
import { getStorage, getDownloadURL } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-storage.js';

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



// Function to display places from Firebase
onChildAdded(ref(database, "places"), (childSnapshot) => {
    const place = childSnapshot.val();
    const placeKey = childSnapshot.key;

    // Create a new table row
    const newRow = document.createElement("tr");
    // Create table cells for each data field
    const nameCell = document.createElement("td");
    nameCell.textContent = place.placeName;

    const websiteCell = document.createElement("td");
    websiteCell.textContent = place.officialWebsite;

    const eTiketCell = document.createElement("td");
    eTiketCell.textContent = place.eTiketLink;
    
    const addressCell = document.createElement("td");
    addressCell.textContent = place.placeAddress;

    const imageCell = document.createElement("td");
    imageCell.classList.add("text-center", "align-middle");

    // Create an image element for the place's image
    const image = document.createElement("img");
    image.src = place.imageUrl;
    image.alt = place.placeName;
    image.style.maxWidth = "100px"; // Set a max width for the image

    // Append the image to the image cell
    imageCell.appendChild(image);

    const urlCell = document.createElement("td");
    urlCell.textContent = place.imageUrl;

    const actionCell = document.createElement("td");
    actionCell.classList.add("text-center", "align-middle");

    // Create a div to wrap the Edit and Delete buttons
    const buttonContainer = document.createElement("div");
    buttonContainer.style.display = "flex"; // Make the container a flexbox
    buttonContainer.style.justifyContent = "space-between"; // Add space between buttons
    buttonContainer.style.alignItems = "center"; // Center buttons vertically

    // Create Edit and Delete buttons
    const editButton = document.createElement("button");
    editButton.textContent = "Edit";
    editButton.classList.add("btn", "btn-warning", "btn-sm"); // Added btn-sm for button size
    // Call openEditModal() when the Edit button is clicked
    editButton.onclick = () => {
        window.location.href = `edit-place.html?key=${placeKey}`
    };

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.classList.add("btn", "btn-danger", "btn-sm"); // Added btn-sm for button size
    deleteButton.onclick = () => deletePlace(placeKey);

    // Append buttons to the buttonContainer
    buttonContainer.appendChild(editButton);
    buttonContainer.appendChild(deleteButton);

    // Append the buttonContainer to the actionCell
    actionCell.appendChild(buttonContainer);

// Append all cells to the new row
newRow.appendChild(nameCell);
newRow.appendChild(websiteCell);
newRow.appendChild(eTiketCell);
newRow.appendChild(addressCell);
newRow.appendChild(imageCell);
newRow.appendChild(urlCell);
newRow.appendChild(actionCell);


    // Assign a unique ID to each row based on the place key
    newRow.id = `placeRow-${placeKey}`;

    // Append the new row to the table body
    placesList.appendChild(newRow);

    
});

// Function to delete a place from Firebase
function deletePlace(placeKey) {
    const confirmationModal = document.getElementById("confirmationModal");
    const confirmDeleteButton = document.getElementById("confirmDelete");

    confirmDeleteButton.onclick = () => {
        const placeRef = ref(database, `places/${placeKey}`);
        remove(placeRef);
        confirmationModal.style.display = "none"; // Close the modal
    };

    const closeModalButtons = document.querySelectorAll("[data-dismiss='modal']");
    closeModalButtons.forEach((button) => {
        button.onclick = () => {
            confirmationModal.style.display = "none"; // Close the modal
        };
    });

    confirmationModal.style.display = "block"; // Show the modal
}

// Function to listen for child removals (deletions)
onChildRemoved(ref(database, "places"), (childSnapshot) => {
    const placeKey = childSnapshot.key;
    const rowToRemove = document.getElementById(`placeRow-${placeKey}`);

    if (rowToRemove) {
        rowToRemove.remove();
    }
});

// Function to edit a place in Firebase
function editPlace(placeKey) {
    // Retrieve the place data by placeKey from Firebase
    const placeRef = ref(database, `places/${placeKey}`);

    get(placeRef)
        .then((snapshot) => {
            if (snapshot.exists()) {
                const placeData = snapshot.val();

                // Populate the modal form fields with the place data
                document.getElementById('editPlaceName').value = placeData.placeName;
                document.getElementById('editOfficialWebsite').value = placeData.officialWebsite;
                document.getElementById('editPlaceAddress').value = placeData.placeAddress;


                // Additional fields can be populated in a similar manner

                // Show the modal
                openEditModal();

                // Handle form submission to save changes
                document.getElementById('saveEditButton').onclick = () => {
                    const editedPlaceName = document.getElementById('editPlaceName').value;
                    const editedOfficialWebsite = document.getElementById('editOfficialWebsite').value;
                    const editedPlaceAddress = document.getElementById('editPlaceAddress').value;

                    // Update the place data in Firebase
                    update(ref(database, `places/${placeKey}`), {
                        placeName: editedPlaceName,
                        officialWebsite: editedOfficialWebsite,
                        placeAddress: editedPlaceAddress,
                        // Update other fields as needed
                    }).then(() => {
                        // Close the edit modal
                        closeEditModal();
                    }).catch((error) => {
                        // Handle any errors that occur while updating data
                        console.error('Error updating place data:', error);
                    });
                };
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

// Open Edit Place Modal
function openEditModal() {
    const editPlaceModal = document.getElementById('editPlaceModal');
    editPlaceModal.style.display = "block";
}

// Close Edit Place Modal
function closeEditModal() {
    const editPlaceModal = document.getElementById('editPlaceModal');
    editPlaceModal.style.display = "hide";
}
