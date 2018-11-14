package com.lumos.lumos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final String TAG = "MessageActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUserId;
    private DatabaseReference databaseReference;
    private String cname;
    private String cPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForSmsPermission();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser() ;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        startActivity(new Intent(this, MapsActivity.class));

        databaseReference.child(currentUserId.getUid()).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild("contacts")) {
                  int i;
                  for (i = 1; i <= 5; i++) {
                      cname = dataSnapshot.child("contacts/name" + Integer.toString(i)).getValue(String.class);
                      cPhone = dataSnapshot.child("contacts/phone" + Integer.toString(i)).getValue(String.class);

                      if (!cPhone.equals("")) {
                          String messageToSend = "Hello " + cname + "\nI'm sharing my location with you \n"
                                  + "http://maps.google.com/maps?z=12&t=m&q=loc:" + Double.toString(MapsActivity.getLat()) + "+" + Double.toString(MapsActivity.getLng());
                          String number = cPhone;

                          SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
                      }
                  }
              }

              Toast.makeText(MessageActivity.this,"Message Sent",Toast.LENGTH_SHORT).show();
          }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        finish();
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, getString(R.string.permission_not_granted));
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the message button.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                } else {
                    // Permission denied.
                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(MessageActivity.this,
                            getString(R.string.failure_permission),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
