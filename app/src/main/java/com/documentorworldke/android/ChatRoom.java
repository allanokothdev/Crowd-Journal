 package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.documentorworldke.android.adapters.MessageAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.models.Chat;
import com.documentorworldke.android.models.Messages;
import com.documentorworldke.android.models.Notification;
import com.documentorworldke.android.models.Token;
import com.documentorworldke.android.models.User;
import com.documentorworldke.android.utils.GetUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

 public class ChatRoom extends AppCompatActivity implements View.OnClickListener {
     private final Context mContext = ChatRoom.this;
     private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
     private final String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
     private EditText editText;
     private Chat chat;
     private final List<Messages> objectList = new ArrayList<>();
     private MessageAdapter adapter;

     private User user;

     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_chat_room);
         try {
             Intent intent = getIntent();
             Bundle bundle = intent.getExtras();
             chat = (Chat) bundle.getSerializable("object");

             user = GetUser.getUser(mContext, currentUserID);
             ImageView finishImageView = findViewById(R.id.finishImageView);
             ImageView imageView = findViewById(R.id.imageView);
             TextView textView = findViewById(R.id.textView);
             ImageView sendImageView = findViewById(R.id.sendImageView);
             editText = findViewById(R.id.editText);
             ImageView sendChatImageView = findViewById(R.id.sendChatImageView);
             RecyclerView recyclerView = findViewById(R.id.recyclerView);
             finishImageView.setOnClickListener(this);
             sendImageView.setOnClickListener(this);
             sendChatImageView.setOnClickListener(this);
             imageView.setTransitionName(chat.getUd());
             Glide.with(mContext.getApplicationContext()).load(chat.getPic()).placeholder(R.drawable.placeholder).into(imageView);
             textView.setText(chat.getTitle());
             recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
             adapter = new MessageAdapter(mContext,objectList);
             recyclerView.setAdapter(adapter);
             fetchObjects(chat.getUd());
             getChatThread();

             recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                 @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                     super.onScrolled(recyclerView, dx, dy);
                     fetchMoreObjects(chat.getUd(),adapter.getFirstItemKey());
                 }
             });

             editText.addTextChangedListener(new TextWatcher() {
                 @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                 @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                     if (s.length()>0){
                         sendChatImageView.setVisibility(View.VISIBLE);
                     }else {
                         sendChatImageView.setVisibility(View.GONE);
                     }
                 }@Override public void afterTextChanged(Editable s) {
                     if (s.length()>0){
                         sendChatImageView.setVisibility(View.VISIBLE);
                     }else {
                         sendChatImageView.setVisibility(View.GONE);
                     }
                 }
             });

         }catch (Exception e){e.printStackTrace(); }
     }

     @SuppressLint("NonConstantResourceId")
     @Override
     public void onClick(View v) {
         switch (v.getId()){
             case R.id.sendImageView:

                 break;
             case R.id.sendChatImageView:
                 sendMessage(editText.getText().toString());
                 break;
             case R.id.finishImageView:
                 finish();
                 break;
         }
     }

     private void getChatThread(){
         try {
             Chat currentUser = new Chat(chat.getUd(),chat.getPic(),chat.getTitle(), false,System.currentTimeMillis(),"Welcome.");
             Chat recipientUser = new Chat(currentUserID, user.getPic(),user.getName(), false,System.currentTimeMillis(),"Hey.");
             DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(currentUserID).child(chat.getUd());
             databaseReference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if (!snapshot.exists()){
                         FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(currentUserID).child(chat.getUd()).setValue(currentUser);
                         FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(chat.getUd()).child(currentUserID).setValue(recipientUser);
                     } else {
                         HashMap<String, Object> hashMap = new HashMap<>();
                         hashMap.put("read",true);
                         FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(currentUserID).child(chat.getUd()).updateChildren(hashMap);
                         FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(chat.getUd()).child(currentUserID).updateChildren(hashMap);
                     }
                 }@Override public void onCancelled(@NonNull DatabaseError error) { }
             });

         }catch (Exception e){e.printStackTrace(); }
     }

     private void fetchObjects(String objectID){
         Query query = FirebaseDatabase.getInstance().getReference(Constants.MESSAGES).child(currentUserID).child(objectID).orderByKey().limitToLast(15);
         query.addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (!objectList.contains(object)){
                         objectList.add(object);
                         adapter.notifyDataSetChanged();
                     }
                 }
             }@Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (objectList.contains(object)){
                         objectList.set(objectList.indexOf(object), object);
                         adapter.notifyDataSetChanged();
                     }
                 }
             }@Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (objectList.contains(object)){
                         objectList.remove(object);
                         adapter.notifyItemRemoved(objectList.indexOf(object));
                     }
                 }
             }@Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (objectList.contains(object)){
                         objectList.remove(object);
                         objectList.add(object);
                         adapter.notifyDataSetChanged();
                     }
                 }
             }@Override public void onCancelled(@NonNull DatabaseError error) { }
         });
     }

     private void fetchMoreObjects(String objectID, String lastKey){
         Query query = FirebaseDatabase.getInstance().getReference(Constants.MESSAGES).child(currentUserID).child(objectID).orderByKey().endAt(lastKey).limitToLast(15);
         query.addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (!objectList.contains(object)){
                         objectList.add(0,object);
                         adapter.notifyDataSetChanged();
                     }
                 }
             }@Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (objectList.contains(object)){
                         objectList.set(objectList.indexOf(object), object);
                         adapter.notifyDataSetChanged();
                     }
                 }
             }@Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (objectList.contains(object)){
                         objectList.remove(object);
                         adapter.notifyItemRemoved(objectList.indexOf(object));
                     }
                 }
             }@Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 if (snapshot.exists()){
                     Messages object = snapshot.getValue(Messages.class);
                     if (objectList.contains(object)){
                         objectList.remove(object);
                         objectList.add(object);
                         adapter.notifyDataSetChanged();
                     }
                 }
             }@Override public void onCancelled(@NonNull DatabaseError error) { }
         });
     }


     private void sendMessage(final String message){
         try {
             if (!TextUtils.isEmpty(message)){
                 editText.setText("");
                 DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.MESSAGES).child(currentUserID).child(chat.getUd());
                 String messageID = databaseReference.push().getKey();
                 Messages messages = new Messages(messageID,currentUserID,message,null,0,System.currentTimeMillis(),user,null, null);
                 assert messageID != null;
                 FirebaseDatabase.getInstance().getReference(Constants.MESSAGES).child(chat.getUd()).child(currentUserID).child(messageID).setValue(messages);
                 databaseReference.child(messageID).setValue(messages).addOnSuccessListener(aVoid -> {
                     Chat recipientChat = new Chat(currentUserID,user.getPic(),user.getName(),false,System.currentTimeMillis(),message);
                     Chat senderChat = new Chat(chat.getUd(),chat.getPic(),chat.getTitle(),false,System.currentTimeMillis(),message);

                     FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(chat.getUd()).child(currentUserID).setValue(recipientChat);
                     FirebaseDatabase.getInstance().getReference(Constants.CHATS).child(currentUserID).child(chat.getUd()).setValue(senderChat);
                 });
             }else {
                 editText.setError("Write Message");
             }
         }catch (Exception e){e.printStackTrace(); }
     }

 }
