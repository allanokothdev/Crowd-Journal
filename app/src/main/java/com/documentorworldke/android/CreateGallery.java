package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.documentorworldke.android.adapters.GalleryAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.GalleryItemClickListener;
import com.documentorworldke.android.models.Darty;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateGallery extends AppCompatActivity implements View.OnClickListener, GalleryItemClickListener {

    private final Context mContext = CreateGallery.this;
    private RelativeLayout container;
    private AVLoadingIndicatorView progressBar;
    private final List<Darty> objectList = new ArrayList<>();
    private GalleryAdapter galleryAdapter;
    private String promotionID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gallery);
        try {
            Intent intent = getIntent();
            promotionID = intent.getStringExtra(Constants.OBJECT_ID);

            container = findViewById(R.id.container);
            Button nextTextView = findViewById(R.id.nextTextView);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            progressBar = findViewById(R.id.progressBar);
            Button button = findViewById(R.id.button);
            nextTextView.setOnClickListener(this);
            button.setOnClickListener(this);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            galleryAdapter = new GalleryAdapter(mContext, objectList, this);
            recyclerView.setAdapter(galleryAdapter);
            fetchObject(promotionID);
        }catch (Exception ignored){ }
    }

    private void fetchObject(String objectID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.GALLERY).child(objectID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Darty object = dataSnapshot.getValue(Darty.class);
                        if (!objectList.contains(object)&& (objectList.size() < 9)) {
                            objectList.add(object);
                            galleryAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }@Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextTextView:
                if (objectList != null){
                    if (objectList.size()>=9){
                        finishAfterTransition();
                    }else {
                        Toast.makeText(mContext, "Upload Maximum of 9 Images of your Services/Product",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(mContext, "Upload Maximum of 9 Images of your Services/Product",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.button:

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select 9 Pictures"),23);
                progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 23 && resultCode == RESULT_OK){
                assert data != null;
                if (data.getClipData() != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    int totalItemsSelected = data.getClipData().getItemCount();
                    for (int i = 0; i < totalItemsSelected; i++) {
                        progressBar.setVisibility(View.VISIBLE);
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        String fileName = getFileName(fileUri);
                        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(Constants.GALLERY).child(fileName + ".png");
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        UploadTask uploadTask = ref.putBytes(byteArray);
                        uploadTask.addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Darty darty = new Darty(downloadUri.toString());
                                FirebaseDatabase.getInstance().getReference(Constants.GALLERY).child(promotionID).child(String.valueOf(System.currentTimeMillis())).setValue(darty).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                });
                            }
                        })).addOnFailureListener(e -> {
                            Snackbar snackbar = Snackbar.make(container, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            progressBar.setVisibility(View.INVISIBLE);
                        });
                    }
                }
            }else if (data.getData() != null){

                Snackbar snackbarlog = Snackbar.make(container, "Add a Total of 9 Photos", Snackbar.LENGTH_SHORT);snackbarlog.show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    @Override
    public void onGalleryItemClick(List<Darty> list, int position, ImageView imageView) {

    }
}
