// firebase.js
import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";
import { getStorage } from "firebase/storage";

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

  const firebaseApp = initializeApp(firebaseConfig);
  const database = getDatabase(firebaseApp);
  const storage = getStorage(firebaseApp);
  
  
  export { database, storage };

