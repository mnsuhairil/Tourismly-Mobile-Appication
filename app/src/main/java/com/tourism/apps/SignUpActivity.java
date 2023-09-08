package com.tourism.apps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutConfirmPassword;
    private AutoCompleteTextView autoCompleteTextViewEmail;
    private AutoCompleteTextView autoCompleteTextViewUsername;
    private AutoCompleteTextView autoCompleteTextViewPassword;
    private AutoCompleteTextView autoCompleteTextViewConfirmPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        autoCompleteTextViewEmail = findViewById(R.id.autoCompleteTextViewEmail);
        autoCompleteTextViewPassword = findViewById(R.id.autoCompleteTextViewPassword);
        autoCompleteTextViewConfirmPassword = findViewById(R.id.autoCompleteTextViewConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        textInputLayoutUsername = findViewById(R.id.textInputLayoutUsername);
        autoCompleteTextViewUsername = findViewById(R.id.autoCompleteTextViewUsername);

        CircleImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = autoCompleteTextViewEmail.getText().toString().trim();
                String password = autoCompleteTextViewPassword.getText().toString().trim();
                String confirmPassword = autoCompleteTextViewConfirmPassword.getText().toString().trim();
                String username = autoCompleteTextViewUsername.getText().toString().trim();

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                signUp(email, password, username, selectedImageUri);
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the "Sign In here" button click
                // You can navigate to the Sign In activity here
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private Uri selectedImageUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            CircleImageView profileImage = findViewById(R.id.profileImage);
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void signUp(final String email, String password, String username, Uri profileImageUri) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Get the unique user ID generated by Firebase Authentication
                                String userId = user.getUid();

                                // Create a reference to the Firebase Realtime Database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                // Create a reference to the Firebase Storage location
                                StorageReference imageRef = storageReference.child("profile_images").child(userId + ".jpg");

                                // Upload the image to Firebase Storage
                                imageRef.putFile(profileImageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // Image uploaded successfully, get the download URL
                                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri downloadUri) {
                                                        // Create a user object with the user's data, including the image URL
                                                        User newUser = new User(email, password, username, downloadUri.toString());

                                                        // Store the user object in the database under the user's ID
                                                        databaseReference.child("users").child(userId).setValue(newUser);

                                                        // You can add code here to handle a successful sign-up and data storage.
                                                        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle image upload failure
                                                Toast.makeText(SignUpActivity.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
