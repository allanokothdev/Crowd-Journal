package com.documentorworldke.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.Sidebar;
import com.documentorworldke.android.adapters.HistoryAdapter;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.PostItemClickListener;
import com.documentorworldke.android.models.MarkerClusterItem;
import com.documentorworldke.android.models.Post;
import com.documentorworldke.android.utils.MarkerClusterRenderer;
import com.documentorworldke.android.utils.ScreenUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment implements PostItemClickListener {

    private List<Post> objectList = new ArrayList<>();
    private final List<MarkerOptions> listMarkers = new ArrayList<>();
    private ClusterManager<MarkerClusterItem> clusterManager;
    private GoogleMap googleMap;

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment getInstance(List<Post> objectList){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.OBJECT_LIST, (Serializable) objectList);
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;

        objectList = (ArrayList<Post>) getArguments().getSerializable(Constants.OBJECT_LIST);
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(@NotNull GoogleMap map) {
            googleMap = map;
            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setCompassEnabled(true);
            clusterManager = new ClusterManager<>(requireContext(), map);
            addMarkers();
            setupClusterManager();

            try {
                GeoJsonLayer layer = new GeoJsonLayer(googleMap, R.raw.unitedstatesstatesboundary, requireContext());
                layer.addLayerToMap();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void addMarkers(){
        for (Post post: objectList){
            LatLng latLng = new LatLng(post.getLatitude(), post.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(post.getTopic()).snippet(post.getId()).icon(BitmapDescriptorFactory.fromResource(R.drawable.outline_location_on_24));
            listMarkers.add(markerOptions);
            googleMap.addMarker(markerOptions);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(listMarkers.get(0).getPosition(),13.0f));

    }

    private void addClusterItems() {
        for(MarkerOptions markerOptions : listMarkers){
            assert markerOptions.getSnippet() != null;
            MarkerClusterItem clusterItem = new MarkerClusterItem(markerOptions.getPosition(), markerOptions.getTitle(),markerOptions.getSnippet());
            clusterManager.addItem(clusterItem);
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void setupClusterManager() {
        setRenderer();
        addClusterItems();
        setClusterManagerClickListener();
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.cluster();
    }

    private void setRenderer() {
        MarkerClusterRenderer<MarkerClusterItem> clusterRenderer = new MarkerClusterRenderer<>(requireContext(), googleMap, clusterManager);
        clusterManager.setRenderer(clusterRenderer);
    }

    private void setClusterManagerClickListener() {
        clusterManager.setOnClusterClickListener(cluster -> {
            Collection<MarkerClusterItem> listItems = cluster.getItems();
            List<String> commentList = new ArrayList<>();
            for (MarkerClusterItem item : listItems){
                commentList.add(item.getSnippet());
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    showTopicsBottomSheet(commentList);
                }
                @Override
                public void onCancel() { }
            });
            return true;
        });
    }

    private void showTopicsBottomSheet(List<String> commentList) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_conversations, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ImageView imageView = view.findViewById(R.id.closeImageView);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        ArrayList<Post> comments = new ArrayList<>();
        for (Post comment: objectList){
            for (String commentID: commentList){
                if (comment.getId().equals(commentID)){
                    comments.add(comment);
                }
            }
        }

        HistoryAdapter commentAdapter = new HistoryAdapter(requireContext(), comments, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(commentAdapter);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        ScreenUtils screenUtils = new ScreenUtils(requireContext());
        behavior.setPeekHeight(screenUtils.getHeight());
        imageView.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onPostItemClick(Post post, CardView cardView) {
        Intent intent = new Intent(requireContext(), Sidebar.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",post);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(cardView, post.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}