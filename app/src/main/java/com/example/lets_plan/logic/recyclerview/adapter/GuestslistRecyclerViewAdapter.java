package com.example.lets_plan.logic.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.recyclerview.adapter.GuestslistRecyclerViewAdapter.GuestViewHolder;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.utils.Converter;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GuestslistRecyclerViewAdapter extends Adapter<GuestViewHolder> implements Filterable {
    private List<Guest> allGuests;
    private List<Guest> filteredGuests;
    private List<Guest> checkedGuests;
    private GuestFilter filter;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean changeViewHolderColorOnClick;

    public GuestslistRecyclerViewAdapter(List<Guest> allGuests, boolean changeViewHolderColorOnClick, List<Guest> checkedGuests) {
        this.allGuests = allGuests;
        this.filteredGuests = allGuests;
        this.checkedGuests = checkedGuests;
        this.filter = new GuestFilter();
        this.changeViewHolderColorOnClick = changeViewHolderColorOnClick;
        this.mInflater = LayoutInflater.from(DataHandler.getInstance().getContext());
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.guests_list_recyclerview_listitem, parent, false);
        return new GuestViewHolder(view, changeViewHolderColorOnClick);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Guest guest = filteredGuests.get(position);
        holder.listitem_TXT_fullname.setText(guest.toString());
        holder.listitem_TXT_category.setText(guest.getCategory());
        if (checkedGuests != null && checkedGuests.contains(guest)) {
            holder.setClicked(true);
        } else {
            holder.setClicked(false);
        }
    }

    @Override
    public int getItemCount() {
        if (filteredGuests == null) {
            return 0;
        }
        return filteredGuests.size();
    }

    public Guest getItem(int id) {
        if (id < 0 || id >= getItemCount()) {
            return null;
        }
        return this.filteredGuests.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void addGuest(int position, Guest guest) {
        allGuests.add(guest);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, allGuests.size());
    }

    public void removeGuest(int position) {
        allGuests.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allGuests.size());
    }

    public void updateGuestList(String filter) {
        getFilter().filter(filter);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class GuestFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();
            final List<Guest> newList = new ArrayList<>();
            if (filterString.equals(Constants.ALL)) {
                newList.addAll(allGuests);
            } else {
                for (int i = 0; i < allGuests.size(); i++) {
                    Guest guest = allGuests.get(i);
                    if (guest.getCategory().equals(filterString)) {
                        newList.add(guest);
                    } else {
                        guest.setTable(null);
                        FirebaseFirestore.getInstance()
                                .collection(Constants.USERS_COLLECTION)
                                .document(DataHandler.getInstance().getOwnerID())
                                .collection(Constants.GUESTS_COLLECTION)
                                .document(guest.getPhoneNumber())
                                .update(Converter.objectToMap(guest));
                    }
                }
            }
            results.values = newList;
            results.count = newList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredGuests = (List<Guest>) results.values;
            notifyDataSetChanged();
        }
    }

    public class GuestViewHolder extends ViewHolder {
        private ShapeableImageView listitem_IMG_cover;
        private TextView listitem_TXT_fullname;
        private TextView listitem_TXT_category;
        private ImageView listitem_IMG_check;
        private boolean clicked;

        public GuestViewHolder(@NonNull View itemView, boolean changeBackgroundOnClick) {
            super(itemView);
            this.listitem_IMG_cover = itemView.findViewById(R.id.guest_listitem_IMG_cover);
            this.listitem_TXT_fullname = itemView.findViewById(R.id.guest_listitem_TXT_fullname);
            this.listitem_TXT_category = itemView.findViewById(R.id.guest_listitem_TXT_category);
            this.listitem_IMG_check = itemView.findViewById(R.id.guest_listitem_IMG_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, getAdapterPosition());
                        if (changeBackgroundOnClick && SharedPreferencesSingleton.getInstance().getPrefs().getBoolean(Constants.VALID_GUEST, false)) {
                            setClicked(!clicked);
                        }
                    }
                }
            });
        }

        public void setClicked(boolean answer) {
            this.clicked = answer;
            this.listitem_IMG_check.setVisibility((this.clicked ? View.VISIBLE : View.INVISIBLE));
        }
    }
}
