package com.documentorworldke.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Comments;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateComment extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final Context mContext = CreateComment.this;
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private TextInputEditText summaryTextInputText;
    private AVLoadingIndicatorView progressBar;
    private Post post;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        user = GetUser.getUser(mContext, currentUserID);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        post = (Post) bundle.getSerializable("object");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        ImageView imageView = findViewById(R.id.profileImageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        TextView subItemTextView = findViewById(R.id.subItemTextView);
        progressBar = findViewById(R.id.progressBar);
        summaryTextInputText = findViewById(R.id.summaryTextInputText);
        subItemTextView.setOnClickListener(this);
        Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
        textView.setText(user.getName());
        subTextView.setText(getTime(System.currentTimeMillis()));
    }



    private String getTime(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.subItemTextView){
            String text = summaryTextInputText.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                summaryTextInputText.setError("Share your Comment");
                progressBar.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.VISIBLE);
                ArrayList<String> tags = new ArrayList<>();
                tags.add(currentUserID);
                tags.add(post.getId());
                String commentID = firebaseFirestore.collection(Constants.COMMENTS).document().getId();
                Comments comments = new Comments(commentID,currentUserID,user,user.getToken(),null,text,System.currentTimeMillis(),Constants.POST_TEXT, false,0,0,0,tags);

                firebaseFirestore.collection(Constants.COMMENTS).document(commentID).set(comments).addOnSuccessListener(unused -> {
                    addUpCommentsCount(post.getId());
                    createNotification(post.getToken(),user, post);
                    progressBar.setVisibility(View.GONE);
                    finishAfterTransition();
                });
            }
        }
    }


    private void createNotification(String token, User user, Post post){

       }

    private void addUpCommentsCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.POSTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("comments")+1;
            transaction.update(reference,"comments",newCount);
            return null;
        });
    }

}