package com.documentorworldke.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.CreateComment;
import com.documentorworldke.android.CreateSubComment;
import com.documentorworldke.android.R;
import com.documentorworldke.android.TopicDetail;
import com.documentorworldke.android.UserProfile;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.documentorworldke.android.utils.PostGetTimeAgo;
import com.documentorworldke.android.utils.ScreenUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final List<Post> stringList;
    private final PostItemClickListener postItemClickListener;

    public static final int IMAGE = 1;
    public static final int TEXT = 0;

    public HistoryAdapter(Context mContext, List<Post> stringList, PostItemClickListener postItemClickListener) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.postItemClickListener = postItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        Post post = stringList.get(position);
        switch (post.getType()) {
            case Constants.POST_IMAGE:
                return IMAGE;
            default:
                return TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE){
            return new PostImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false));
         } else {
            return new PostTextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try { Post post = stringList.get(position);
            if (post.getType().equals(Constants.POST_IMAGE)){
                ((PostImageViewHolder) holder).bind(position);
            } else {
                ((PostTextViewHolder) holder).bind(position);
            }
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class PostImageViewHolder extends RecyclerView.ViewHolder{

        private final ImageView profileImageView;
        private final CardView profileCardView;
        private final TextView textView;
        private final TextView subTextView;

        private final ImageView imageView;
        private final CardView cardView;
        private final TextView summaryTextView;
        private final TextView locationTextView;

        private final ImageView commentImageView;
        private final TextView commentTextView;
        private final ImageView likeImageView;
        private final TextView likeTextView;
        private final ImageView shareImageView;

        private final RelativeLayout container;
        private final ImageView optionImageView;

        public PostImageViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            profileCardView = itemView.findViewById(R.id.profileCardView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);

            container = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            shareImageView = itemView.findViewById(R.id.shareImageView);
            optionImageView = itemView.findViewById(R.id.optionImageView);
        }

        void bind(int position) {

            try {
                Post post = stringList.get(position);

                User user = post.getUser();
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
                textView.setText(user.getName());
                subTextView.setText(getDate(post.getTimestamp()));

                profileCardView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", post.getUser());
                    mContext.startActivity(new Intent(mContext, UserProfile.class).putExtras(bundle));
                });

                Glide.with(mContext.getApplicationContext()).load(post.getImage()).placeholder(R.drawable.placeholder).into(imageView);
                summaryTextView.setText(post.getText().trim());
                locationTextView.setText(post.getAddress());

                fetchLikes(likeImageView,post);
                likeTextView.setText(String.valueOf(post.getLikes()));
                commentTextView.setText(String.valueOf(post.getComments()));

                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
                commentImageView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", post);
                    mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
                });

                optionImageView.setOnClickListener(view -> {
                    if (post.getPublisher().equals(currentUserID)){
                        commentAdminOptions(post, position);
                    } else {
                        commentNormalOptions(post);
                    }
                });

                cardView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,cardView));

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class PostTextViewHolder extends RecyclerView.ViewHolder{

        private final ImageView profileImageView;
        private final CardView profileCardView;
        private final TextView textView;
        private final TextView subTextView;

        private final ImageView imageView;
        private final TextView summaryTextView;
        private final TextView locationTextView;

        private final ImageView commentImageView;
        private final TextView commentTextView;
        private final ImageView likeImageView;
        private final TextView likeTextView;
        private final ImageView shareImageView;

        private final RelativeLayout container;
        private final ImageView optionImageView;

        public PostTextViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            profileCardView = itemView.findViewById(R.id.profileCardView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);

            container = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            shareImageView = itemView.findViewById(R.id.shareImageView);
            optionImageView = itemView.findViewById(R.id.optionImageView);
        }

        void bind(int position) {

            try {
                Post post = stringList.get(position);

                User user = post.getUser();
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(profileImageView);
                textView.setText(user.getName());
                profileCardView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", post.getUser());
                    mContext.startActivity(new Intent(mContext, UserProfile.class).putExtras(bundle));
                });


                imageView.setVisibility(View.GONE);
                summaryTextView.setText(post.getText().trim());
                locationTextView.setText(post.getAddress());

                fetchLikes(likeImageView,post);
                likeTextView.setText(String.valueOf(post.getLikes()));
                commentTextView.setText(String.valueOf(post.getComments()));
                subTextView.setText(getDate(post.getTimestamp()));

                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),post.getId()));
                commentImageView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", post);
                    mContext.startActivity(new Intent(mContext, CreateComment.class).putExtras(bundle));
                });

                optionImageView.setOnClickListener(view -> {
                    if (post.getPublisher().equals(currentUserID)){
                        commentAdminOptions(post, position);
                    } else {
                        commentNormalOptions(post);
                    }
                });

                summaryTextView.setOnClickListener(v -> postItemClickListener.onPostItemClick(post,profileCardView));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
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

    private void shareImage(Bitmap bitmap, String commentID) {
        final  String appPackageName = mContext.getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        Uri uri = saveImageToShare(bitmap);
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        imageIntent.putExtra(Intent.EXTRA_TEXT, link);
        imageIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        imageIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(imageIntent, "Share Via"));
        addSharesCount(commentID);
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



    private void commentAdminOptions(Post comments, int position){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_options, null);
        LinearLayout deleteLayout = view.findViewById(R.id.deleteLayout);
        LinearLayout editLayout = view.findViewById(R.id.editLayout);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        deleteLayout.setOnClickListener(view1 -> {
            deleteComment(comments,position);
            bottomSheetDialog.dismiss();
        });

        editLayout.setOnClickListener(view12 -> {
            editComment(comments);
            bottomSheetDialog.dismiss();
        });
    }

    private void commentNormalOptions(Post post){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_report, null);
        LinearLayout reportLayout = view.findViewById(R.id.reportLayout);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        reportLayout.setOnClickListener(view1 -> {
            reportComment(post);
            bottomSheetDialog.dismiss();
        });

    }

    private void editComment(Post post){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_edit, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        Button button = view.findViewById(R.id.button);
        TextInputEditText summaryTextInputEditText = view.findViewById(R.id.summaryTextInputEditText);
        summaryTextInputEditText.setText(post.getText());

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        button.setOnClickListener(view1 -> {
            String memoranda = summaryTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(memoranda)){
                Toast.makeText(mContext, "Enter Post", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("text", memoranda);
                firebaseFirestore.collection(Constants.POSTS).document(post.getId()).update(hashMap).addOnSuccessListener(unused -> {
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                });
            }
        });
    }


    private void deleteComment(Post post, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Post");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> FirebaseFirestore.getInstance().collection(Constants.COMMENTS).document(post.getId()).delete().addOnSuccessListener(unused -> {
            if (stringList.contains(post)){
                stringList.remove(post);
                notifyItemRemoved(position);
            }
        }));
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reportComment(Post post){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Report Post");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("reported", true);
            firebaseFirestore.collection(Constants.POSTS).document(post.getId()).update(hashMap).addOnSuccessListener(unused -> {
                Toast.makeText(mContext, "Reported Successfully", Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getDate(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMMM yyyy", cal).toString();
    }

}
