 package com.documentorworldke.android;

 import android.annotation.SuppressLint;
 import android.content.ActivityNotFoundException;
 import android.content.Context;
 import android.content.Intent;
 import android.graphics.drawable.Drawable;
 import android.net.Uri;
 import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;
 import android.widget.ImageView;
 import android.widget.TextView;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.app.ActivityOptionsCompat;
 import androidx.core.content.ContextCompat;
 import androidx.core.util.Pair;
 import androidx.recyclerview.widget.GridLayoutManager;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.load.DataSource;
 import com.bumptech.glide.load.engine.GlideException;
 import com.bumptech.glide.request.RequestListener;
 import com.bumptech.glide.request.target.Target;
 import com.documentorworldke.android.adapters.GalleryAdapter;
 import com.documentorworldke.android.constants.Constants;
 import com.documentorworldke.android.listeners.GalleryItemClickListener;
 import com.documentorworldke.android.models.Brand;
 import com.documentorworldke.android.models.Darty;
 import com.documentorworldke.android.models.Promotion;
 import com.google.android.material.appbar.AppBarLayout;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.database.annotations.NotNull;
 import com.luseen.autolinklibrary.AutoLinkMode;
 import com.luseen.autolinklibrary.AutoLinkTextView;

 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Objects;

 public class PromotionDetail extends AppCompatActivity implements View.OnClickListener, GalleryItemClickListener {

     private final Context mContext = PromotionDetail.this;
     private Promotion promotion;
     private boolean collapsed = false;

     private GalleryAdapter adapter;
     private final List<Darty> objectList = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_promotion_detail);

         Intent intent = getIntent();
         Bundle bundle = intent.getExtras();
         promotion = (Promotion) bundle.getSerializable("object");

         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
         Objects.requireNonNull(getSupportActionBar()).setTitle(promotion.getTitle());
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
         toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

         AppBarLayout app_bar = findViewById(R.id.app_bar);
         ImageView coverImageView = findViewById(R.id.coverImageView);
         TextView nameTextView = findViewById(R.id.nameTextView);
         Button button = findViewById(R.id.button);
         ImageView brandImageView = findViewById(R.id.brandImageView);
         TextView websiteTextView = findViewById(R.id.websiteTextView);
         TextView brandTitleTextView = findViewById(R.id.brandTitleTextView);
         AutoLinkTextView summaryTextView = findViewById(R.id.summaryTextView);
         RecyclerView recyclerView = findViewById(R.id.recyclerView);
         websiteTextView.setOnClickListener(this);
         button.setOnClickListener(this);

         app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
             if ( verticalOffset < -26) {
                 if (!collapsed) {
                     collapsed = true;
                 }
             } else {
                 if (collapsed) {
                     collapsed = false;
                 }
             }
         });

         LinearLayoutManager linearLayoutManager = new GridLayoutManager(mContext,3, LinearLayoutManager.VERTICAL,false);
         recyclerView.setLayoutManager(linearLayoutManager);

         coverImageView.setTransitionName(promotion.getPd());
         Glide.with(mContext.getApplicationContext()).load(promotion.getPic()).centerCrop().dontAnimate().listener(new RequestListener<Drawable>() {@Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }@Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }}).into(coverImageView);
         nameTextView.setText(promotion.getTitle());

         summaryTextView.setMentionModeColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
         summaryTextView.setHashtagModeColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
         summaryTextView.setUrlModeColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
         summaryTextView.addAutoLinkMode(AutoLinkMode.MODE_URL);
         summaryTextView.addAutoLinkMode(AutoLinkMode.MODE_MENTION);
         summaryTextView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG);
         summaryTextView.setAutoLinkText(promotion.getSummary().trim());
         button.setText(promotion.getCta());

         adapter = new GalleryAdapter(mContext, objectList, this);
         recyclerView.setAdapter(adapter);
         fetchObject(promotion.getPd());
         fetchBrand(promotion.getBrand(), brandTitleTextView, brandImageView);
     }

     private void fetchBrand(Brand brand, TextView textView, ImageView imageView){
         Glide.with(mContext.getApplicationContext()).load(brand.getPic()).placeholder(R.drawable.placeholder).into(imageView);
         textView.setText(brand.getTitle());
     }

     @SuppressLint("NonConstantResourceId")
     @Override
     public void onClick(View v) {
         switch (v.getId()){
             case R.id.button:
             case R.id.websiteTextView:
                 try {
                     Intent indie = new Intent(Intent.ACTION_VIEW);
                     indie.setData(Uri.parse(promotion.getLink()));
                     startActivity(indie);
                 }catch (ActivityNotFoundException ignored){ }
                 break;
         }
     }


     @Override
     public void onGalleryItemClick(List<Darty> list, int position, ImageView imageView) {

         Intent intent = new Intent(mContext, ViewPicture.class);
         Bundle bundle = new Bundle();
         bundle.putSerializable("object",promotion);
         intent.putExtras(bundle);
         intent.putExtra("list",(Serializable) list);
         intent.putExtra("objectPosition", position);
         ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(imageView, "imageTransition"));
         startActivity(intent,activityOptionsCompat.toBundle());
     }

     private void fetchObject(String objectID){
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.GALLERY).child(objectID);
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                         Darty object = dataSnapshot.getValue(Darty.class);
                         if (!objectList.contains(object)&& (objectList.size() < 9)) {
                             objectList.add(object);
                             adapter.notifyDataSetChanged();
                         }
                     }
                 }
             }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
         });
     }

 }
