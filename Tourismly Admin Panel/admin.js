// Import Firebase modules
import { getDatabase, ref, push, set, onChildAdded, remove } from "firebase/database";
import { getStorage, ref as storageRef, uploadBytes, getDownloadURL } from "firebase/storage";
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

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
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const database = getDatabase(app);
const storage = getStorage(app);

const addPlaceForm = document.getElementById("addPlaceForm");
const placesList = document.getElementById("placesList");
const addPlaceSection = document.getElementById("addPlaceSection");
const managePlacesSection = document.getElementById("managePlacesSection");
const addPlaceLink = document.getElementById("addPlaceLink");
const managePlacesLink = document.getElementById("managePlacesLink");
const uploadImageButton = document.getElementById("uploadImageButton");
const imageFileInput = document.getElementById("imageFile");

let uploadedImageUrl = ""; // To store the uploaded image URL

// Function to show the "Add New Place" section
addPlaceLink.addEventListener("click", () => {
    addPlaceSection.style.display = "block";
    managePlacesSection.style.display = "none";
    addPlaceLink.classList.add("active");
    managePlacesLink.classList.remove("active");
});

// Function to show the "Manage Places" section
managePlacesLink.addEventListener("click", () => {
    addPlaceSection.style.display = "none";
    managePlacesSection.style.display = "block";
    managePlacesLink.classList.add("active");
    addPlaceLink.classList.remove("active");
});

// Function to upload an image to Firebase Storage and get its URL
uploadImageButton.addEventListener("click", () => {
    const file = imageFileInput.files[0];

    if (file) {
        // Create a storage reference with a unique name (e.g., timestamp + filename)
        const storageRef = storageRef(storage, `images/${Date.now()}_${file.name}`);

        try {
            // Upload the file to Firebase Storage
            uploadBytes(storageRef, file).then((snapshot) => {
                getDownloadURL(snapshot.ref).then((downloadURL) => {
                    // Use downloadURL to display or store the image URL as needed
                    uploadedImageUrl = downloadURL;
                    document.getElementById("imageURL").value = uploadedImageUrl;
                });
            });
        } catch (error) {
            console.error("Error uploading image:", error);
        }
    } else {
        alert("Please select an image file.");
    }
});

// Function to add a new place to Firebase
addPlaceForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const placeName = document.getElementById("placeName").value;
    const officialWebsite = document.getElementById("officialWebsite").value;
    const placeAddress = document.getElementById("placeAddress").value;
    const imageURL = uploadedImageUrl || document.getElementById("imageURL").value;

    const newPlaceRef = push(ref(database, "places"));
    set(newPlaceRef, {
        placeName,
        officialWebsite,
        placeAddress,
        imageURL
    });

    // Clear the form fields
    addPlaceForm.reset();

    // Reset uploadedImageUrl
    uploadedImageUrl = "";
});

// Function to display places from Firebase
onChildAdded(ref(database, "places"), (childSnapshot) => {
    const place = childSnapshot.val();
    const placeKey = childSnapshot.key;
    placesList.innerHTML += `
        <tr>
            <td>${place.placeName}</td>
            <td>${place.officialWebsite}</td>
            <td>${place.placeAddress}</td>
            <td>${place.imageURL}</td>
            <td>
                <button class="btn btn-danger" onclick="deletePlace('${placeKey}')">Delete</button>
            </td>
        </tr>
    `;
});

// Function to delete a place from Firebase
function deletePlace(placeKey) {
    const placeRef = ref(database, `places/${placeKey}`);
    remove(placeRef);
}
