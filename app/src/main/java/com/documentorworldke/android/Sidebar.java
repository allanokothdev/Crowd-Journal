package com.documentorworldke.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.documentorworldke.android.utils.PostGetTimeAgo;
import com.documentorworldke.android.viewpager.CommentPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class Sidebar extends AppCompatActivity implements View.OnClickListener {

    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = Sidebar.this;
    private Post post;
    private RelativeLayout container;
    private static String[] tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable("object");
        tabList = getResources().getStringArray(R.array.topics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sidebar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
        container = findViewById(R.id.container);

        CardView profileCardView = findViewById(R.id.profileCardView);
        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView subItemTextView = findViewById(R.id.subItemTextView);

        ImageView imageView = findViewById(R.id.imageView);
        TextView locationTextView = findViewById(R.id.locationTextView);
        AutoLinkTextView summaryTextView = findViewById(R.id.summaryTextView);

        ImageView commentImageView = findViewById(R.id.commentImageView);
        TextView commentTextView = findViewById(R.id.commentTextView);
        ImageView likeImageView = findViewById(R.id.likeImageView);
        TextView likeTextView = findViewById(R.id.likeTextView);
        ImageView shareImageView = findViewById(R.id.shareImageView);
        profileCardView.setTransitionName(post.getId());
        CardView cardView = findViewById(R.id.cardView);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        setUpViewPager(viewPager,tabLayout);

        User user = post.getUser();
        cardView.setTransitionName(post.getId());
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
        textView.setText(user.getName());
        subItemTextView.setText(mContext.getString(R.string.username,user.getUsername()));

        Glide.with(mContext.getApplicationContext()).load(post.getImage()).placeholder(R.drawable.placeholder).into(imageView);
        locationTextView.setText(post.getAddress());
        summaryTextView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG);
        summaryTextView.addAutoLinkMode(AutoLinkMode.MODE_MENTION);
        summaryTextView.setAutoLinkText(post.getText());

        fetchLikes(likeImageView,post);
        likeTextView.setText(String.valueOf(post.getLikes()));
        commentTextView.setText(String.valueOf(post.getComments()));
        subTextView.setText(PostGetTimeAgo.postGetTimeAgo(post.getTimestamp(),mContext));

        shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
        commentImageView.setOnClickListener(v -> {
            Bundle bundled = new Bundle();
            bundled.putSerializable("object", post);
            mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundled));
        });
    }


    private void setUpViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        CommentPagerAdapter adapter = new CommentPagerAdapter(this, post);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabList[position])).attach();
    }


    private void fetchLikes(ImageView imageView, Post post){

        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.LIKES).child(post.getId()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.ic_favorite_red_24dp);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.LIKES).child(post.getId()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceLikeCount(post.getId())));
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    imageView.setOnClickListener(v -> {
                        addLikeCount(post.getId());
                        FirebaseDatabase.getInstance().getReference(Constants.LIKES).child(post.getId()).child(currentUserID).setValue(true);
                        String title = "Post Like";
                        String message = user.getName()+ " liked your post";
                        createNotification(post.getToken(),user, title, message);
                    });
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }


    private void createNotification(String token, User user, String title, String message){

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext, "com.documentorworldke.android.fileprovider", file);
        }catch (Exception e){
            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uri;
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void shareImage(Bitmap bitmap, String postID) {
        final  String appPackageName = mContext.getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        Uri uri = saveImageToShare(bitmap);
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        imageIntent.putExtra(Intent.EXTRA_TEXT, link);
        imageIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        imageIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(imageIntent, "Share Via"));
        addSharesCount(postID);
    }

    private void addSharesCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("shares")+1;
            transaction.update(reference,"shares",newCount);
            return null;
        });
    }

    private void addLikeCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {

            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("likes")+1;
            transaction.update(reference,"likes",newCount);
            return null;
        });
    }


    private void reduceLikeCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("likes")-1;
            transaction.update(reference,"likes",newCount);
            return null;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.floatingActionButton){
            Bundle bundled = new Bundle();
            bundled.putSerializable(Constants.OBJECT,post);
            startActivity(new Intent(mContext,CreateComment.class).putExtras(bundled));
        }
    }
}