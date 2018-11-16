package com.lumos.lumos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button buttonSignIn;
    private EditText email;
    private EditText password;
    private TextView signUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = firebaseAuth.getInstance();

        //check if user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            //start main activity
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        buttonSignIn = findViewById(R.id.buttonSignIn);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        signUp = findViewById(R.id.textViewSignUp);
        progressDialog = new ProgressDialog(this);

        buttonSignIn.setTransformationMethod(null);

        buttonSignIn.setOnClickListener(this);
        signUp.setOnClickListener(this);

    }

    private void userLogIn() {

        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailText)) {
            //email is empty
            Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show();

            //return will stop the function from executing further
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
            //password is empty
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();

            //return will stop the function from executing further
            return;
        }

        //If email and password are valid, first show a progress dialog
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            //start user profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(SignInActivity.this, "Something Failed. Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogIn();
        }

        if (view == signUp) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        }
    }
}