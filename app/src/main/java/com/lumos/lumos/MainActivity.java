package com.lumos.lumos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.graphics.LightingColorFilter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mainButton;
    private Button deactivate;

    private int clickCount = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUserId;
    private String password;
    private AuthCredential credential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton = findViewById(R.id.mainButton);
        deactivate = findViewById(R.id.buttonDeactivate);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser() ;
        mainButton.setOnClickListener(this);
        deactivate.setOnClickListener(this);
        deactivate.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.popup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id =item.getItemId();
        switch(id)
        {
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                break;
            case R.id.logout:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                break;
        }
        return true;

    }


    private void checkPasswordDialog() {
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (currentUserId.getEmail() != null && !input.getText().toString().equals("")) {
                            password = input.getText().toString();
                            credential = EmailAuthProvider.getCredential(currentUserId.getEmail(), password);

                            currentUserId.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Alarm deactivated", Toast.LENGTH_SHORT).show();
                                        //deactivate.setVisibility(View.INVISIBLE);
                                        //buttonLogOut.setEnabled(true);
                                        //settings.setEnabled(true);
                                        //mainButton.getBackground().setColorFilter(new LightingColorFilter(0, getResources().getColor(R.color.colorPrimary)));

                                        // Include map sharing/message sending stopping part here
                                        recreate();
                                    }
                                }
                            });

                            currentUserId.reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Incorrect Password. Try again.", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                    checkPasswordDialog();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            checkPasswordDialog();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter your password").setView(input).setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    @Override
    public void onClick(View view) {

        if (view == mainButton) {
            clickCount++;

            if(clickCount == 1){
                startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                mainButton.getBackground().setColorFilter(new LightingColorFilter(0, getResources().getColor(R.color.colorPrimary)));
                deactivate.setVisibility(View.VISIBLE);


            } else if(clickCount == 2){
                startActivity(new Intent(getApplicationContext(), RecordVideoActivity.class));
                mainButton.setEnabled(false);
                mainButton.getBackground().setColorFilter(new LightingColorFilter(0, getResources().getColor(R.color.gray)));
            }
        }


        if (view == deactivate) {
            checkPasswordDialog();
        }
    }


}