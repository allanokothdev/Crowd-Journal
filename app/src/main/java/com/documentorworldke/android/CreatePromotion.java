package com.documentorworldke.android;

import android.Manifest;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Brand;
import com.documentorworldke.android.models.Promotion;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CreatePromotion extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final Context mContext = CreatePromotion.this;
    private ImageView imageView;
    private Brand brand;
    private TextInputEditText titleTextInputEditText;
    private TextInputEditText summaryTextInputEditText;
    private TextInputEditText webTextInputEditText;
    private Spinner spinner;
    private AVLoadingIndicatorView progressBar;
    private Uri mainImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promotion);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        brand = (Brand) bundle.getSerializable("object");

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        titleTextInputEditText = findViewById(R.id.titleTextInputEditText);
        summaryTextInputEditText = findViewById(R.id.summaryTextInputEditText);
        webTextInputEditText = findViewById(R.id.webTextInputEditText);
        spinner = findViewById(R.id.spinner);
        Button button = findViewById(R.id.button);
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

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.button){
            String title = titleTextInputEditText.getText().toString();
            String summary = summaryTextInputEditText.getText().toString();
            String web = webTextInputEditText.getText().toString();
            String cta = spinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(title)){
                titleTextInputEditText.setError("Enter Promotion Title");
                progressBar.setVisibility(View.GONE);
            }else if (TextUtils.isEmpty(summary)){
                summaryTextInputEditText.setError("Enter Promotion's Summary");
                progressBar.setVisibility(View.GONE);
            }else if (TextUtils.isEmpty(web)){
                summaryTextInputEditText.setError("Enter Promotion's Weblink");
                progressBar.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(cta)){
                Toast.makeText(mContext,"Select Promotion's Call to Action", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (mainImageUri==null){
                Toast.makeText(mContext,"Select Promotion Branding Picture", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                postObject(mainImageUri,title,summary,web,cta);
            }
        } else if (v.getId()==R.id.imageView){
            try {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreatePromotion.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else { BringImagePicker(); }
            }catch (Exception e){e.printStackTrace(); }
        }
    }

    private void postObject(Uri mainImageUri,String title, String summary, String web, String cta){
        try {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.PROMOTIONS).child(System.currentTimeMillis() + ".png");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mainImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    String promotionID = firebaseFirestore.collection(Constants.PROMOTIONS).document().getId();
                    Promotion promotion = new Promotion(promotionID,downloadUri.toString(),title,summary,cta,web,currentUserID,brand);
                    firebaseFirestore.collection(Constants.PROMOTIONS).document(promotionID).set(promotion).addOnSuccessListener(aVoid -> {

                        startActivity(new Intent(mContext, CreateGallery.class).putExtra(Constants.OBJECT_ID,promotion.getPd()));
                        progressBar.setVisibility(View.GONE);
                        finishAfterTransition();

                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext, Objects.requireNonNull(e.getMessage()),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            })).addOnFailureListener(e -> {
                Toast.makeText(mContext, Objects.requireNonNull(e.getMessage()),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            });
        } catch (IOException ignored){
        }
    }
}