package com.lumos.lumos;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.text.InputType;


public class AddContacts extends AppCompatActivity implements View.OnClickListener{

    private static final int RESULT_PICK_CONTACT = 1;
    //public static final int REQUEST_CODE_PICK_CONTACT = 1;
    //public static final int  MAX_PICK_CONTACT= 1;
    //private static final String TAG = "MyActivity";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private EditText name;
    private EditText phone;

    private EditText name1;
    private EditText name2;
    private EditText name3;
    private EditText name4;
    private EditText name5;

    private EditText phone1;
    private EditText phone2;
    private EditText phone3;
    private EditText phone4;
    private EditText phone5;

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button save;
    private Button skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        contact1();
        contact2();
        contact3();
        contact4();
        contact5();

        save = findViewById(R.id.buttonSave);
        //skip = findViewById(R.id.buttonSkip);

        save.setOnClickListener(this);
        skip.setOnClickListener(this);
    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (requestCode == RESULT_PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                    contactPicked(data);
            }
        } else {
            Log.e("AddContacts", "Failed to pick contact");
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNumber = null ;
            String contactName = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNumber = cursor.getString(phoneIndex).trim();
            contactName = cursor.getString(nameIndex);
            // Set the value to the textviews
            name.setText(contactName);
            phone.setText(phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contact1(){
        name1 = findViewById(R.id.EditTextContactName1);
        phone1 = (EditText)findViewById(R.id.EditTextPhone1);
        phone1.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button1 = findViewById(R.id.buttonContact1);

        button1.setOnClickListener(this);
    }

    private void contact2(){
        name2 = findViewById(R.id.EditTextContactName2);
        phone2 = (EditText)findViewById(R.id.EditTextPhone2);
        phone2.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button2 = findViewById(R.id.buttonContact2);

        button2.setOnClickListener(this);
    }

    private void contact3() {
        name3 = findViewById(R.id.EditTextContactName3);
        phone3 = (EditText)findViewById(R.id.EditTextPhone3);
        phone3.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button3 = findViewById(R.id.buttonContact3);

        button3.setOnClickListener(this);
    }

    private void contact4(){
        name4 = findViewById(R.id.EditTextContactName4);
        phone4 = (EditText)findViewById(R.id.EditTextPhone4);
        phone4.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button4 = findViewById(R.id.buttonContact4);

        button4.setOnClickListener(this);
    }

    private void contact5(){
        name5 = findViewById(R.id.EditTextContactName5);
        phone5 = (EditText)findViewById(R.id.EditTextPhone5);
        phone5.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        button5 = findViewById(R.id.buttonContact5);

        button5.setOnClickListener(this);
    }

    private void saveContacts() {
        String name1Text = name1.getText().toString().trim();
        String phone1Text = phone1.getText().toString().trim();
            if (TextUtils.isEmpty(name1Text) && TextUtils.isEmpty(phone1Text)){
                Toast.makeText(this, "Please enter Name for contact 1 ", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(phone1Text)){
                Toast.makeText(this, "Please enter Phone Number for contact 1 ", Toast.LENGTH_LONG).show();
                return;
            }

        String name2Text = name2.getText().toString().trim();
        String phone2Text = phone2.getText().toString().trim();
        if (TextUtils.isEmpty(name2Text)|| name2Text == name1Text){
            Toast.makeText(this, "Please enter Name for contact 2", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phone2Text)){
            Toast.makeText(this, "Please enter Phone Number for contact 2 ", Toast.LENGTH_LONG).show();
            return;
        }

        String name3Text = name3.getText().toString().trim();
        String phone3Text = phone3.getText().toString().trim();
        if (TextUtils.isEmpty(name3Text) || name3Text == name2Text || name3Text == name1Text){
            Toast.makeText(this, "Please enter contact3 Name ", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phone3Text)){
            Toast.makeText(this, "Please enter Phone Number for contact 3", Toast.LENGTH_LONG).show();
            return;
        }

        String name4Text = name4.getText().toString().trim();
        String phone4Text = phone4.getText().toString().trim();
        if (TextUtils.isEmpty(name4Text)|| name4Text == name3Text || name4Text == name2Text || name4Text == name1Text){
            Toast.makeText(this, "Please enter contact4 Name ", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phone4Text)){
            Toast.makeText(this, "Please enter Phone Number for contact 4 ", Toast.LENGTH_LONG).show();
            return;
        }

        String name5Text = name5.getText().toString().trim();
        String phone5Text = phone5.getText().toString().trim();
        if (TextUtils.isEmpty(name5Text) || name5Text == name4Text || name5Text == name3Text || name5Text == name2Text || name5Text == name1Text){
            Toast.makeText(this, "Please enter contact5  Name ", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phone5Text)){
            Toast.makeText(this, "Please enter Phone Number for contact 5 ", Toast.LENGTH_LONG).show();
            return;
        }


            ContactsClass contacts = new ContactsClass(name1Text, phone1Text, name2Text, phone2Text,
                    name3Text, phone3Text, name4Text, phone4Text, name5Text, phone5Text);

            FirebaseUser user = firebaseAuth.getCurrentUser();
            databaseReference.child(user.getUid()).child("contacts").setValue(contacts);

            Toast.makeText(this, "Contacts Saved", Toast.LENGTH_LONG).show();
           finish();
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onClick(View view) {
        if(view == button1){
            pickContact(button1);
            name = name1;
            phone = phone1;
        }

        if(view == button2){
            pickContact(button2);
            name = name2;
            phone = phone2;
        }

        if(view == button3){
            pickContact(button3);
            name = name3;
            phone = phone3;
        }

        if(view == button4){
            pickContact(button4);
            name = name4;
            phone = phone4;
        }

        if(view == button5){
            pickContact(button5);
            name = name5;
            phone = phone5;
        }

        if(view == save) {
                saveContacts();

        }

        if(view == skip){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

}
