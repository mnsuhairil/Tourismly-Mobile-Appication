package com.tourism.apps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView autoCompleteTextViewEmail, autoCompleteTextViewPassword;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        autoCompleteTextViewEmail = findViewById(R.id.autoCompleteTextViewEmail);
        autoCompleteTextViewPassword = findViewById(R.id.autoCompleteTextViewPassword);
        emailTextInputLayout = findViewById(R.id.textInputLayoutEmail);
        passwordTextInputLayout = findViewById(R.id.textInputLayoutPassword);

        Button signInButton = findViewById(R.id.buttonSignIn);
        TextView signUpTextView = findViewById(R.id.textViewSignUp);

        // Check if a user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, redirect to the main activity or any other part of your app
            // For example, you can use an Intent to open the main activity.
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = autoCompleteTextViewEmail.getText().toString();
                String password = autoCompleteTextViewPassword.getText().toString();
                Log.d("Email", "Email: " + email + ", Password: " + password);
                if (validateForm(email, password)) {
                    signIn(email, password);
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the sign-up activity or navigate to the sign-up screen
                // For example, you can use an Intent to open the sign-up activity.
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            emailTextInputLayout.setError("Email is required.");
            valid = false;
        } else {
            emailTextInputLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordTextInputLayout.setError("Password is required.");
            valid = false;
        } else {
            passwordTextInputLayout.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // You can add code here to handle a successful sign-in.
                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
