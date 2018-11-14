package com.lumos.lumos;

import android.accounts.Account;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "Settings";
    private Button buttonEditContacts;
    private Button buttonDelete;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUserId;
    private String password;
    private AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        buttonEditContacts = findViewById(R.id.buttonEditContacts);
        buttonDelete = findViewById(R.id.buttonDelete);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser() ;

        buttonEditContacts.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
    }

    private void deleteAccount() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        checkPasswordDialog();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("Are you sure you want to delete your account?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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
                        if(currentUserId.getEmail()!=null && !input.getText().toString().equals("")) {
                            password = input.getText().toString();
                            credential = EmailAuthProvider.getCredential(currentUserId.getEmail(), password);

                            currentUserId.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        DatabaseReference drUser = FirebaseDatabase.getInstance().getReference().child(currentUserId.getUid());
                                        drUser.removeValue();

                                        currentUserId.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Account has been deleted");
                                                    Toast.makeText(AccountActivity.this, "Account has been deleted", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AccountActivity.this, SignInActivity.class));
                                                    finish();
                                                } else {
                                                    Log.w(TAG, "Something went wrong");
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            currentUserId.reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AccountActivity.this, "Incorrect Password. Try again.", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                    checkPasswordDialog();
                                }
                            });
                        }
                        else {
                            Toast.makeText(AccountActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
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
        if(view == buttonEditContacts){
            startActivity(new Intent(getApplicationContext(), AddContacts.class));
        }

        if(view == buttonDelete){
            deleteAccount();
        }
    }
}

