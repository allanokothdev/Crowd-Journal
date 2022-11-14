package com.documentorworldke.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import com.documentorworldke.android.CreateSubComment;
import com.documentorworldke.android.R;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Comments;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.documentorworldke.android.utils.PostGetTimeAgo;
import com.documentorworldke.android.utils.ScreenUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final List<Comments> stringList;
    private final Post post;

    public static final int IMAGE = 1;
    public static final int TEXT = 0;

    public CommentAdapter(Context mContext, List<Comments> stringList, Post post) {
        this.stringList = stringList;
        this.mContext = mContext;
        this.post = post;
    }

    @Override
    public int getItemViewType(int position) {
        Comments comments = stringList.get(position);
        switch (comments.getType()) {
            case Constants.POST_IMAGE:
                return IMAGE;
            case Constants.POST_TEXT:
                return TEXT;
            default:
                return TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE){
            return new CommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false));
         } else {
            return new CommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try { Comments comments = stringList.get(position);
            if (comments.getType().equals(Constants.COMMENTS)){
                ((CommentViewHolder) holder).bind(position);
            } else {
                ((CommentViewHolder) holder).bind(position);
            }
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;
        private final ImageView commentImageView;
        private final TextView commentTextView;
        private final ImageView likeImageView;
        private final TextView likeTextView;
        private final ImageView shareImageView;
        private final LinearLayout container;
        private final ImageView optionImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            shareImageView = itemView.findViewById(R.id.shareImageView);
            optionImageView = itemView.findViewById(R.id.optionImageView);
        }

        void bind(int position) {

            try {
                Comments comments = stringList.get(position);
                fetchUserDetails(comments.getPublisherID(),imageView, textView);
                subItemTextView.setText(comments.getText());

                fetchLikes(likeImageView,comments, post);
                likeTextView.setText(String.valueOf(comments.getLikes()));
                commentTextView.setText(String.valueOf(comments.getComments()));
                subTextView.setText(PostGetTimeAgo.postGetTimeAgo(comments.getTimestamp(),mContext));

                shareImageView.setOnClickListener(v -> shareImage(viewToBitmap(container),comments.getId()));
                commentImageView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object", comments);
                    mContext.startActivity(new Intent(mContext, CreateSubComment.class).putExtras(bundle));
                });

                optionImageView.setOnClickListener(view -> {
                    if (comments.getPublisherID().equals(currentUserID)){
                        commentAdminOptions(comments, position);
                    } else {
                        commentNormalOptions(comments);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void fetchUserDetails(String userID, ImageView imageView, TextView textView){
        firebaseFirestore.collection(Constants.USERS).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                Glide.with(mContext.getApplicationContext()).load(user.getPic()).placeholder(R.drawable.placeholder).into(imageView);
                textView.setText(user.getName());
            }
        });
    }


    private void fetchLikes(ImageView imageView, Comments comments, Post post){

        User user = GetUser.getUser(mContext, currentUserID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.LIKES).child(comments.getId()).child(currentUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageView.setImageResource(R.drawable.ic_favorite_red_24dp);
                    imageView.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference(Constants.LIKES).child(comments.getId()).child(currentUserID).removeValue().addOnSuccessListener(unused -> reduceLikeCount(comments.getId())));
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    imageView.setOnClickListener(v -> {
                        addLikeCount(comments.getId());
                        FirebaseDatabase.getInstance().getReference(Constants.LIKES).child(comments.getId()).child(currentUserID).setValue(true);
                    });
                }
            }@Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
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
        DocumentReference reference = firebaseFirestore.collection(Constants.REACTIONS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("shares")+1;
            transaction.update(reference,"shares",newCount);
            return null;
        });
    }

    private void addLikeCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {

            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("likes")+1;
            transaction.update(reference,"likes",newCount);
            return null;
        });
    }


    private void reduceLikeCount(final String objectID){
        DocumentReference reference = firebaseFirestore.collection(Constants.COMMENTS).document(objectID);
        firebaseFirestore.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(reference);
            double newCount = documentSnapshot.getDouble("likes")-1;
            transaction.update(reference,"likes",newCount);
            return null;
        });
    }



    private void commentAdminOptions(Comments comments, int position){
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

    private void commentNormalOptions(Comments comments){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_report, null);
        LinearLayout reportLayout = view.findViewById(R.id.reportLayout);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());
        reportLayout.setOnClickListener(view1 -> {
            reportComment(comments);
            bottomSheetDialog.dismiss();
        });

    }

    private void editComment(Comments comments){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_comment_edit, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        Button button = view.findViewById(R.id.button);
        TextInputEditText summaryTextInputEditText = view.findViewById(R.id.summaryTextInputEditText);
        summaryTextInputEditText.setText(comments.getText());

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        button.setOnClickListener(view1 -> {
            String memoranda = summaryTextInputEditText.getText().toString();

            if (TextUtils.isEmpty(memoranda)){
                Toast.makeText(mContext, "Enter Comment", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("text", memoranda);
                firebaseFirestore.collection(Constants.COMMENTS).document(comments.getId()).update(hashMap).addOnSuccessListener(unused -> {
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                });
            }
        });
    }


    private void deleteComment(Comments comments, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Comment");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> FirebaseFirestore.getInstance().collection(Constants.COMMENTS).document(comments.getId()).delete().addOnSuccessListener(unused -> {
            if (stringList.contains(comments)){
                stringList.remove(comments);
                notifyItemRemoved(position);
            }
        }));
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reportComment(Comments comments){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Report Comment");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("reported", true);
            firebaseFirestore.collection(Constants.COMMENTS).document(comments.getId()).update(hashMap).addOnSuccessListener(unused -> {
                Toast.makeText(mContext, "Reported Successfully", Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
