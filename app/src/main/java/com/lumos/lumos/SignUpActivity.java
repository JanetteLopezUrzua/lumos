package com.lumos.lumos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
        private FirebaseAuth firebaseAuth;
        private Button buttonSignUp;
        private EditText email;
        private EditText password;
        private TextView signIn;
        private ProgressDialog progressDialog;
        private String emailText;
        private String passwordText;

        //for TAG
        public static final String TAG = SignUpActivity.class.getSimpleName();

        // for email validation
        private Pattern mailPattern;
        private Matcher mailMatcher;
        /*
        Email address has to start with characters, digits or ‘_’, ‘-‘, ‘+’ symbols
        The above group can be followed with a ‘.’ and the same pattern as the first group.
        Then it must have exactly one ‘@’ character.
        The domain name must start with characters, digits and the ‘-‘ character.
        Then it must be followed by a ‘.’.
        After the ‘.’ you can have characters and digits.
         */
        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


        // for password validation
        private Pattern passPattern;
        private Matcher passMatcher;
        // Password must be between 6 and 40 characters long.
        // Password must contain at least one digit, one upper case character and one lower case character
        private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{6,40})";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);
            firebaseAuth = FirebaseAuth.getInstance();
            buttonSignUp = findViewById(R.id.buttonSignUp);
            email = findViewById(R.id.editTextEmail);
            password = findViewById(R.id.editTextPassword);
            signIn = findViewById(R.id.textViewSignIn);
            progressDialog = new ProgressDialog(this);

            //check if user is already logged in
            if(firebaseAuth.getCurrentUser() != null){
                //start main activity
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

            buttonSignUp.setOnClickListener(this);
            signIn.setOnClickListener(this);

        }

        private void registerUser(){

            emailText = email.getText().toString().trim();
            passwordText = password.getText().toString().trim();

            if(TextUtils.isEmpty(emailText)){
                //email is empty
                Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show();

                //return will stop the function from executing further
                return;
            } else {
                // if the email is not empty
                if (!isEmailValid(emailText)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            if(TextUtils.isEmpty(passwordText)){
                //password is empty
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();

                //return will stop the function from executing further
                return;
            } else {
                // If the password is not empty
                if (!isPasswordValid(passwordText)) {
                    Toast.makeText(getApplicationContext(), "Password must be between 6 and 40 characters long. Password must contain at least one digit, one upper case character and one lower case character!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            //If email and password are valid, first show a progress dialog
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()) {
                                //user is successfully registered and logged in
                                //check if user is already logged in
                                final FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (!user.isEmailVerified()) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                                Log.d("Verification", "Verification email sent to " + user.getEmail());
                                            } else {
                                                Log.e(TAG, "sendEmailVerification failed!", task.getException());
                                                Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                finish();
                                startActivity(new Intent(getApplicationContext(), AddContacts.class));
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Registration Failed. Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        @Override
        public void onClick(View view) {
            if(view == buttonSignUp){
                registerUser();
            }

            if(view == signIn){
                //will open login activity
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        }

        public boolean isEmailValid(String email) {
            mailPattern = Pattern.compile(EMAIL_PATTERN);
            mailMatcher = mailPattern.matcher(email);
            return mailMatcher.matches();
        }

        public boolean isPasswordValid(String password) {
            passPattern = Pattern.compile(PASSWORD_PATTERN);
            passMatcher = passPattern.matcher(password);
            return passMatcher.matches();
        }

    }