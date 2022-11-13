package com.documentorworldke.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreatePost extends AppCompatActivity implements LocationListener, View.OnClickListener {

    public static final int PICK_IMAGE = 1;
    private long selectedDate = System.currentTimeMillis();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = CreatePost.this;
    private TextInputEditText locationTextInputEditText;
    private ImageView imageView;
    private CardView imageCardView;
    private TextInputEditText textInputEditText;
    private TextView subTextView;
    private TextView button;
    private AVLoadingIndicatorView progressBar;
    private Uri mainImageUri = null;
    private String downloadUrlString = null;
    private User user;
    private double latitude;
    private double longitude;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        user = GetUser.getUser(mContext,currentUserID);

        progressBar = findViewById(R.id.progressBar);
        subTextView = findViewById(R.id.subTextView);
        ImageView profileImageView = findViewById(R.id.profileImageView);
        locationTextInputEditText = findViewById(R.id.locationTextInputEditText);
        TextView textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        imageCardView = findViewById(R.id.imageCardView);
        textInputEditText = findViewById(R.id.textInputEditText);
        button = findViewById(R.id.button);
        ImageView galleryImageView = findViewById(R.id.galleryImageView);
        button.setOnClickListener(this);
        subTextView.setText(getDate(selectedDate));
        galleryImageView.setOnClickListener(this);

        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
        textView.setText(user.getName());
        grantPermissions();
    }


    private String getDate(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("dd MMMM yyy", cal).toString();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.galleryImageView:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
            case R.id.button:

                String summary = textInputEditText.getText().toString();
                String address = locationTextInputEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(summary)){

                    textInputEditText.setError("Document your World..");
                    progressBar.setVisibility(View.GONE);
                    break;

                }else if (TextUtils.isEmpty(address)) {

                    Toast.makeText(mContext,"Tag Location", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;

                }else if (mainImageUri == null) {

                    Toast.makeText(mContext,"Upload Picture", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;

                }else {

                    button.setEnabled(false);
                    postObject(summary,address,selectedDate);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
        }
    }



    private void postObject(String summary, String location, long selectedDate){
        try {

            @SuppressLint("SimpleDateFormat") String today = new SimpleDateFormat("ddMMMM").format(selectedDate);
            @SuppressLint("SimpleDateFormat") String year = new SimpleDateFormat("yyyy").format(selectedDate);
            @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat("MMM").format(selectedDate);

            ArrayList<String> tags = new ArrayList<>();
            tags.add(location);
            tags.add(currentUserID);
            tags.add(today);
            tags.add(year);
            tags.add(month);
            tags.add(user.getCountry());
            if (downloadUrlString != null) {

                DocumentReference documentReference = firebaseFirestore.collection(Constants.POSTS).document();
                String objectID = documentReference.getId();
                Post post = new Post(objectID,objectID,"",tags,currentUserID,user,summary,downloadUrlString,selectedDate,location,user.getCountry(),Constants.POST_IMAGE,user.getToken(),latitude,longitude,0,0,0);
                progressBar.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.OBJECT,post);
                startActivity(new Intent(mContext, SelectTopics.class).putExtras(bundle));
                finishAfterTransition();
            } else {
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.POSTS).child(System.currentTimeMillis() + ".png");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mainImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = ref.putBytes(data);
                uploadTask.addOnProgressListener(taskSnapshot -> {
                    //float percentage = taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                    //progressBar.setProgress((int)percentage);
                }).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        DocumentReference documentReference = firebaseFirestore.collection(Constants.POSTS).document();
                        String objectID = documentReference.getId();
                        Post post = new Post(objectID,objectID,"",tags,currentUserID,user,summary,downloadUri.toString(),selectedDate,location,user.getCountry(),Constants.POST_IMAGE,user.getToken(),latitude,longitude,0,0,0);
                        progressBar.setVisibility(View.GONE);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.OBJECT,post);
                        startActivity(new Intent(mContext, SelectTopics.class).putExtras(bundle));
                        finishAfterTransition();
                    }
                })).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Image failed to Upload... Try Again", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (IOException e){e.printStackTrace(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
                mainImageUri = data.getData();
                imageView.setImageURI(mainImageUri);
                imageCardView.setVisibility(View.VISIBLE);
                postImage(mainImageUri);
            }
        }catch (Exception e){ e.printStackTrace();}
    }


    @SuppressLint("MissingPermission")
    private void getLocation(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500, 5,this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void grantPermissions(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreatePost.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }else {
            getLocation();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            assert addresses != null;
            locationTextInputEditText.setText(addresses.get(0).getAddressLine(0));
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        grantPermissions();
    }

    private void postImage(Uri mainImageUri){
        try {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.POSTS).child(System.currentTimeMillis() + ".png");
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
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                button.setEnabled(true);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

