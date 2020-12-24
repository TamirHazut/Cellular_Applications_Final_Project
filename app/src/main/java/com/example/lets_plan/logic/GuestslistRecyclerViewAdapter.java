package com.example.lets_plan.logic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.GuestslistRecyclerViewAdapter.GuestViewHolder;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class GuestslistRecyclerViewAdapter extends RecyclerView.Adapter<GuestViewHolder> {
    private List<Guest> guests;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public GuestslistRecyclerViewAdapter(Context context, List<Guest> guests) {
        this.guests = guests;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.guestslist_recyclerview_listitem, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Guest guest = guests.get(position);
        holder.listitem_TXT_fullname.setText(guest.toString());
        holder.listitem_TXT_category.setText(guest.getCategory());
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    public Guest getItem(int id) {
        if (id < 0 || id >= getItemCount()) {
            return null;
        }
        return this.guests.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void updateGuestList(List<Guest> guests) {
        if (guests != null) {
            this.guests = guests;
            notifyDataSetChanged();
        }
    }

    public class GuestViewHolder extends ViewHolder {
        private ShapeableImageView listitem_IMG_cover;
        private TextView listitem_TXT_fullname;
        private TextView listitem_TXT_category;

        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            this.listitem_IMG_cover = itemView.findViewById(R.id.listitem_IMG_cover);
            this.listitem_TXT_fullname = itemView.findViewById(R.id.listitem_TXT_fullname);
            this.listitem_TXT_category = itemView.findViewById(R.id.listitem_TXT_category);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }
}
