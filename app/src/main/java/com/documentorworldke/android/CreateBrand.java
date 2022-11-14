package com.documentorworldke.android;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.documentorworldke.android.utils.GetUser;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Brand;
import com.documentorworldke.android.models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CreateBrand extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = CreateBrand.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private User user;
    private Uri mainImageUri = null;

    private RelativeLayout container;
    private Button button;
    private ImageView imageView;
    private TextInputEditText titleTextInputEditText;
    private AVLoadingIndicatorView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_brand);

        user = GetUser.getUser(mContext,currentUserID);
        container = findViewById(R.id.container);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        titleTextInputEditText = findViewById(R.id.titleTextInputEditText);
        progressBar = findViewById(R.id.progressBar);
        imageView.setOnClickListener(this);
        button.setOnClickListener(this);

    }

    private void BringImagePicker() {

        try {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }catch (Exception ignored){ }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK){
                    assert result != null;
                    mainImageUri = result.getUri();
                    imageView.setImageURI(mainImageUri);
                }else assert resultCode != CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE || result != null;
            }

        }catch (Exception ignored){ }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:

                String name = titleTextInputEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name)){

                    titleTextInputEditText.setError("Enter Brand Name");
                    progressBar.setVisibility(View.GONE);
                    break;

                }else if (mainImageUri==null){

                    Snackbar snackbar = Snackbar.make(container, "Upload Image", Snackbar.LENGTH_SHORT);snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    break;

                }else {

                    button.setEnabled(false);
                    postBusiness(mainImageUri, name);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }

            case R.id.imageView:
                try {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateBrand.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else { BringImagePicker();break; }
                }catch (Exception e){ break; }
        }
    }

    private void postBusiness(Uri mainImageUri,String title){
        try {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.BRANDS).child(System.currentTimeMillis() + ".png");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String brandID = firebaseFirestore.collection(Constants.BRANDS).document().getId();
                    Brand brand = new Brand(brandID,title,downloadUri.toString(),currentUserID);
                    firebaseFirestore.collection(Constants.BRANDS).document(brandID).set(brand).addOnSuccessListener(aVoid -> {
                        finish();
                        progressBar.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbar.show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            })).addOnFailureListener(e -> {
                Snackbar snackbarlog = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);snackbarlog.show();
                progressBar.setVisibility(View.INVISIBLE);
            });

        } catch (IOException ignored){


        }
    }
}

