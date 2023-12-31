package com.documentorworldke.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.documentorworldke.android.R;
import com.documentorworldke.android.constants.Constants;
import com.documentorworldke.android.listeners.SpaceItemClickListener;
import com.documentorworldke.android.listeners.UserItemClickListener;
import com.documentorworldke.android.models.Space;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SpaceAdapter extends RecyclerView.Adapter{

    private final Context mContext;
    private final SpaceItemClickListener spaceItemClickListener;
    private final UserItemClickListener userItemClickListener;
    private final List<Space> stringList;
    public static final int LIVE_SESSION = 1;

    public SpaceAdapter(Context mContext, List<Space> stringList, SpaceItemClickListener spaceItemClickListener, UserItemClickListener userItemClickListener) {
        this.mContext = mContext;
        this.stringList = stringList;
        this.userItemClickListener = userItemClickListener;
        this.spaceItemClickListener = spaceItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return LIVE_SESSION;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveViewHolder(LayoutInflater.from(mContext).inflate(R.layout.space_item, parent, false));
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        try {
            ((LiveViewHolder) holder).bind(position);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
    


    public class LiveViewHolder extends RecyclerView.ViewHolder{

        private final CardView cardView;
        private final LinearLayout linearLayout;
        private final TextView textView;
        private final TextView subTextView;
        private final TextView subItemTextView;
        private final TextView locationTextView;
        private final RecyclerView recyclerView;


        private LiveViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            textView = itemView.findViewById(R.id.textView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            subTextView = itemView.findViewById(R.id.subTextView);
            subItemTextView = itemView.findViewById(R.id.subItemTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }

        void bind(int position) {
            Space space = stringList.get(position);
            cardView.setTransitionName(space.getId());
            linearLayout.setBackgroundColor(Color.parseColor(space.getColor()));
            textView.setText(space.getTitle());
            subTextView.setText(Constants.SPACE_SUMMARY);
            subItemTextView.setText(space.getTopic());
            locationTextView.setText(space.getDate());
            recyclerView.setAdapter(new UserAdapter(mContext, space.getPanel(),userItemClickListener));
            itemView.setOnClickListener(view -> spaceItemClickListener.onSpaceItemClick(space,cardView));
        }
    }

    private String getDate(long time){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return DateFormat.format("dd MMMM yyyy", cal).toString();
    }

}
