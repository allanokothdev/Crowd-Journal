package com.documentorworldke.android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.adapters.HistoryAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.Filtered;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.utils.CustomDatePicker;
import com.documentorworldke.android.utils.ScreenUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TopicDetail extends AppCompatActivity implements PostItemClickListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private boolean collapsed = false;
    private final Context mContext = TopicDetail.this;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private final List<Post> objectList = new ArrayList<>();
    private HistoryAdapter adapter;
    private String topic;

    private long startDate = System.currentTimeMillis();
    private long endDate = System.currentTimeMillis();
    private String type = Constants.FROM;
    private Button fromButton;
    private Button toButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        topic = getIntent().getStringExtra(Constants.OBJECT_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(topic);
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(mContext,R.color.colorGray));
        getSupportActionBar().setSubtitle(R.string.united_states);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_yellow_24dp);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());

        AppBarLayout app_bar = findViewById(R.id.app_bar);
        ImageView dataImageView = findViewById(R.id.dataImageView);
        TextView textView = findViewById(R.id.textView);
        TextView subTextView = findViewById(R.id.subTextView);
        textView.setText(getString(R.string.conversations, topic));
        dataImageView.setVisibility(View.INVISIBLE);
        subTextView.setText(R.string.united_states);

        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if ( verticalOffset < -26) {
                if (!collapsed) {
                    collapsed = true;
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                }
            } else {
                if (collapsed) {
                    collapsed = false;
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            }
        });

        dataImageView.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.posts_divider)));
        recyclerView.addItemDecoration(divider);
        adapter = new HistoryAdapter(mContext, objectList, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fetchObjects(topic);
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.POSTS).orderBy("timestamp", Query.Direction.DESCENDING).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyItemInserted(objectList.size()-1);
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Post object = documentChange.getDocument().toObject(Post.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Post object = documentChange.getDocument().toObject(Post.class);
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
        fetchObjects(topic);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }



    @Override
    public void onPostItemClick(Post post, CardView cardView) {
        Intent intent = new Intent(mContext, Sidebar.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }

    private void dataSheet(){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_data, null);
        fromButton = view.findViewById(R.id.fromButton);
        toButton = view.findViewById(R.id.toButton);
        Button button = view.findViewById(R.id.button);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(mContext);
        behavior.setPeekHeight(screenUtils.getHeight());

        fromButton.setOnClickListener(v -> {
            type = Constants.FROM;
            CustomDatePicker customDatePicker = new CustomDatePicker();
            customDatePicker.show(getSupportFragmentManager(), "DATE PICK");
        });

        toButton.setOnClickListener(v -> {
            type = Constants.TO;
            CustomDatePicker customDatePicker = new CustomDatePicker();
            customDatePicker.show(getSupportFragmentManager(), "DATE PICK");
        });

        button.setOnClickListener(view1 -> {

            ArrayList<Post> postList = new ArrayList<>();
            for (Post post: objectList){
                if (post.getTimestamp()> startDate && post.getTimestamp() < endDate){
                    postList.add(post);
                }
            }

            Filtered filtered = new Filtered(topic, startDate, endDate);
            startActivity(new Intent(mContext, TimelineView.class).putExtra(Constants.OBJECT_LIST,postList));

        });
    }

    private String getLongDates(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("EEEE, dd MMM yyyy", cal).toString();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        view.setMaxDate(System.currentTimeMillis());
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (type.equals(Constants.FROM)){
            startDate = mCalendar.getTimeInMillis();
            fromButton.setText(getLongDates(startDate));
        } else if (type.equals(Constants.TO)){
            endDate = mCalendar.getTimeInMillis();
            toButton.setText(getLongDates(endDate));
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.dataImageView){
            dataSheet();
        }
    }
}
