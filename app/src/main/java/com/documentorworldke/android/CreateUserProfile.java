package com.documentorworldke.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class CreateUserProfile extends AppCompatActivity implements View.OnClickListener {

    public final static int PICK_IMAGE = 1;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = CreateUserProfile.this;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Uri mainImageUri = null;
    private String downloadUrlString = null;

    private RelativeLayout container;
    private ImageView imageView;
    private TextInputEditText nameTextInputEditText;
    private TextInputEditText usernameTextInputEditText;
    private AVLoadingIndicatorView progressBar;
    private Button button;
    private CountryCodePicker countryCodePicker;
    public static final Pattern USERNAME = Pattern.compile("^[a-z0-9_]{3,30}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);

        TextView privacyTextView = findViewById(R.id.privacyTextView);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        EditText editText = findViewById(R.id.editText);
        container = findViewById(R.id.container);
        button = findViewById(R.id.button);
        Button uploadButton = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        nameTextInputEditText = findViewById(R.id.nameTextInputEditText);
        usernameTextInputEditText = findViewById(R.id.usernameTextInputEditText);
        nameTextInputEditText.setText(firebaseUser.getDisplayName());
        countryCodePicker.detectLocaleCountry(true);
        countryCodePicker.detectSIMCountry(true);
        countryCodePicker.detectNetworkCountry(true);
        countryCodePicker.registerCarrierNumberEditText(editText);
        button.setOnClickListener(this);
        privacyTextView.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        usernameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                usernameTextInputEditText.setError("use only small letters, underscore, numbers");
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                Query username = FirebaseDatabase.getInstance().getReference().child(Constants.USERNAME).child(usernameTextInputEditText.getText().toString());
                username.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            usernameTextInputEditText.setError("Taken");
                            button.setVisibility(View.GONE);
                        }else if (!USERNAME.matcher(usernameTextInputEditText.getText().toString()).matches()){
                            usernameTextInputEditText.setError("Invalid character or length... Only small letters/digits/underscore are allowed");
                            button.setVisibility(View.GONE);
                        }else if (!dataSnapshot.exists() && usernameTextInputEditText.getText().toString().length()<15 && usernameTextInputEditText.getText().toString().length()>3){
                            button.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        nameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameTextInputEditText.setText(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:

                String title = Objects.requireNonNull(nameTextInputEditText.getText()).toString().trim();
                String username = Objects.requireNonNull(usernameTextInputEditText.getText()).toString().trim();
                String country = countryCodePicker.getSelectedCountryName();
                String phone = countryCodePicker.getFullNumber();

                if (TextUtils.isEmpty(title)) {
                    nameTextInputEditText.setError("Enter your Name");
                    progressBar.setVisibility(View.GONE);
                    break;
                }else if (TextUtils.isEmpty(phone)){
                    Snackbar snackbar = Snackbar.make(container,"Enter phone Number", Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    break;
                }else if (TextUtils.isEmpty(username)){
                    nameTextInputEditText.setError("Enter your Username");
                    progressBar.setVisibility(View.GONE);
                    break;

                }else if (TextUtils.isEmpty(country)){
                    Snackbar snackbar = Snackbar.make(container,R.string.select_your_country, Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    break;

                }else {

                    button.setEnabled(false);
                    createUser(title, firebaseUser.getEmail() ,phone,username, country);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
            case R.id.privacyTextView:
                Intent indie = new Intent(Intent.ACTION_VIEW);
                indie.setData(Uri.parse(Constants.PRIVACY_POLICY));
                startActivity(indie);
                break;
            case R.id.uploadButton:
                try { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateUserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else { BringImagePicker();break; }
                }else { BringImagePicker();break; }
                }catch (Exception e){ break; }


        }
    }

    private void createUser(String name, String email, String phone, String username, String country){
        try {
            if (downloadUrlString != null) {
                User user = new User(currentUserID,downloadUrlString,name,username,email,phone,country,"",false, false);

                firebaseFirestore.collection(Constants.USERS).document(currentUserID).set(user).addOnSuccessListener(aVoid -> {

                    SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                    editor.putString(Constants.PIC, user.getPic());
                    editor.putString(Constants.NAME, user.getName());
                    editor.putString(Constants.USERNAME, user.getUsername());
                    editor.putString(Constants.EMAIL, user.getEmail());
                    editor.putString(Constants.PHONE_NUMBER, user.getPhone());
                    editor.putString(Constants.COUNTRY, user.getCountry());
                    editor.putString(Constants.TOKEN, user.getToken());
                    editor.putBoolean(Constants.VERIFICATION, user.isVerification());
                    editor.putBoolean(Constants.REPORTED, user.isReported());
                    editor.apply();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(mContext, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }).addOnFailureListener(e -> {
                    Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.USERS).child(System.currentTimeMillis() + ".png");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        User user = new User(currentUserID,downloadUri.toString(),name,username,email,phone,null,"",false, false);
                        firebaseFirestore.collection(Constants.USERS).document(currentUserID).set(user).addOnSuccessListener(aVoid -> {

                            SharedPreferences.Editor editor = getSharedPreferences(Constants.USERS,Context.MODE_PRIVATE).edit();
                            editor.putString(Constants.PIC, user.getPic());
                            editor.putString(Constants.USERNAME, user.getUsername());
                            editor.putString(Constants.NAME, user.getName());
                            editor.putString(Constants.EMAIL, user.getEmail());
                            editor.putString(Constants.PHONE_NUMBER, user.getPhone());
                            editor.putString(Constants.COUNTRY, user.getCountry());
                            editor.putString(Constants.TOKEN, user.getToken());
                            editor.putBoolean(Constants.VERIFICATION, user.isVerification());
                            editor.putBoolean(Constants.REPORTED, user.isReported());
                            editor.apply();

                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(mContext, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }).addOnFailureListener(e -> {
                            Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                })).addOnFailureListener(e -> {
                    Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        } catch (IOException e){e.printStackTrace(); }
    }

    private void BringImagePicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Pic"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode ==PICK_IMAGE && resultCode == RESULT_OK && data != null){
                imageView.setImageURI(data.getData());
                mainImageUri = data.getData();
                postImage(mainImageUri);
            }
        }catch (Exception e){e.printStackTrace(); }
    }


    private void postImage(Uri mainImageUri){
        try {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.USERS).child(System.currentTimeMillis() + ".png");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    downloadUrlString = downloadUri.toString();
                }
            })).addOnFailureListener(e -> {
                Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
