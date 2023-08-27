package com.lk.individual_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtFirstName, txtLastName, txtEmail, txtPassword, txtConfirmPassword;
    private RadioGroup rbGender;
    private RadioButton rbMale, rbFemale;
    private Button btnRegister;

    // Firebase Realtime Database
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        txtFirstName = findViewById(R.id.txtfname);
        txtLastName = findViewById(R.id.txtlname);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_pwd);
        txtConfirmPassword = findViewById(R.id.txt_confirmpwd);
        rbGender = findViewById(R.id.rb_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        btnRegister = findViewById(R.id.btn_register);

        // Set OnClickListener for the Register button
        btnRegister.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String firstName = txtFirstName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        // Validate email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected gender
        String gender = rbGender.getCheckedRadioButtonId() == R.id.rb_male ? "Male" : "Female";

        // Register user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success, update Firebase Realtime Database
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Create a new UserHelperClass object
                            UserHelperClass newUser = new UserHelperClass(firstName, lastName, gender, email);

                            // Push the user data to Firebase Realtime Database
                            databaseReference.child(userId).setValue(newUser);

                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            // Clear the input fields
                            clearInputFields();


                        }
                    } else {
                        // Registration failed, show an error message or toast
                        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearInputFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        rbGender.clearCheck();
    }
}
