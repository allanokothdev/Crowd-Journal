package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.documentorworldke.android.adapters.AdvertAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.documentorworldke.android.adapters.PromotionAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PromotionItemClickListener;
import com.documentorworldke.android.models.Brand;
import com.documentorworldke.android.models.Promotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrandProfile extends AppCompatActivity implements View.OnClickListener, PromotionItemClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final Context mContext = BrandProfile.this;
    private AdvertAdapter adapter;
    private final List<Promotion> objectList = new ArrayList<>();
    private Brand brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        brand = (Brand) bundle.getSerializable("object");

        ImageView imageView = findViewById(R.id.imageView);
        ImageView finishImageView = findViewById(R.id.finishImageView);
        TextView textView = findViewById(R.id.textView);

        imageView.setTransitionName(brand.getBd());
        Glide.with(mContext.getApplicationContext()).load(brand.getPic()).centerCrop().dontAnimate().listener(new RequestListener<Drawable>() {@Override public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }@Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) { supportStartPostponedEnterTransition();return false; }}).into(imageView);
        textView.setText(brand.getTitle());
        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        finishImageView.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        adapter = new AdvertAdapter(mContext, objectList, this);
        recyclerView.setAdapter(adapter);
        fetchObjects();
    }

    private void fetchObjects(){
        Query query = firebaseFirestore.collection(Constants.PROMOTIONS).orderBy("pd",Query.Direction.DESCENDING);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Promotion object = documentChange.getDocument().toObject(Promotion.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Promotion object = documentChange.getDocument().toObject(Promotion.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Promotion object = documentChange.getDocument().toObject(Promotion.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyItemRemoved(objectList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.floatingActionButton){
            Bundle updateBundle = new Bundle();
            updateBundle.putSerializable("object", brand);
            startActivity(new Intent(mContext, CreatePromotion.class).putExtras(updateBundle));
        } else if (v.getId()==R.id.finishImageView){
            finishAfterTransition();
        }
    }

    @Override
    public void onPromotionItemClick(Promotion promotion, ImageView imageView) {
        Intent intent = new Intent(mContext, PromotionDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",promotion);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(imageView, promotion.getPd()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}