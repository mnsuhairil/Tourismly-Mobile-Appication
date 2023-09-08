import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-auth.js';
import { getDatabase, ref, get, set } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-database.js';
import { getStorage, ref as storageRef, uploadBytes } from 'https://www.gstatic.com/firebasejs/9.6.0/firebase-storage.js';



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

// Add event listener for login button
const loginBtn = document.getElementById("loginBtn");
loginBtn.addEventListener("click", () => {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // Retrieve admin data from Firebase Realtime Database
    const adminRef = ref(database, `users/${username}`);
    get(adminRef)
        .then((snapshot) => {
            if (snapshot.exists()) {
                const adminData = snapshot.val();

                if (adminData.password === password) {
                    // Login successful
                    console.log("Logged in as:", username);
                    
                    // Redirect to the "add-place.html" page
                    window.location.href = "add-place.html";

                } else {
                    // Invalid credentials
                    console.error("Invalid username or password");
                }
            } else {
                // User not found
                console.error("User not found");
            }
        })
        .catch((error) => {
            // Handle database error
            console.error("Database error:", error);
        });
});