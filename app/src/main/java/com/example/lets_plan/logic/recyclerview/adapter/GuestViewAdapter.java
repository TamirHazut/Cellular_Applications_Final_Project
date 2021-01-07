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
import com.example.lets_plan.logic.recyclerview.adapter.GuestViewAdapter.GuestViewHolder;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.utils.Converter;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GuestViewAdapter extends Adapter<GuestViewHolder> implements Filterable {
    private final List<Guest> allGuests;
    private List<Guest> filteredGuests;
    private final List<Guest> checkedGuests;
    private final GuestFilter filter;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final boolean hiddenCheckMark;

    public GuestViewAdapter(List<Guest> allGuests) {
        this(allGuests, false, null);
    }

    public GuestViewAdapter(List<Guest> allGuests, boolean hiddenCheckMark, List<Guest> checkedGuests) {
        this.allGuests = allGuests;
        this.filteredGuests = allGuests;
        this.checkedGuests = checkedGuests;
        this.filter = new GuestFilter();
        this.hiddenCheckMark = hiddenCheckMark;
        this.mInflater = LayoutInflater.from(DataHandler.getInstance().getContext());
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.guests_list_recyclerview_listitem, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Guest guest = filteredGuests.get(position);
        holder.listitem_TXT_fullname.setText(guest.toString());
        holder.listitem_TXT_category.setText(guest.getCategory());
        if (hiddenCheckMark) {
            holder.setClicked(checkedGuests != null && checkedGuests.contains(guest));
        }
    }

    @Override
    public int getItemCount() {
        if (filteredGuests == null) {
            return 0;
        }
        return filteredGuests.size();
    }

    public Guest getItem(int index) {
        if (index < 0 || index >= getItemCount()) {
            return null;
        }
        return this.filteredGuests.get(index);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

//    public void addGuest(int position, Guest guest) {
//        allGuests.add(guest);
//        notifyItemInserted(position);
//        notifyItemRangeChanged(position, allGuests.size());
//    }
//
//    public void removeGuest(int position) {
//        allGuests.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, allGuests.size());
//    }

    public void updateList(String filter) {
        getFilter().filter(filter);
    }

    public Filter getFilter() {
        return filter;
    }

    public class GuestFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();
            final List<Guest> newList = new ArrayList<>();
            if (filterString.equals(Constants.ALL) || (hiddenCheckMark && filterString.equals(Constants.OTHER_CATEGORY))) {
                newList.addAll(allGuests);
            } else {
                for (int i = 0; i < allGuests.size(); i++) {
                    Guest guest = allGuests.get(i);
                    if (guest.getCategory().equals(filterString)) {
                        newList.add(guest);
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
        private final ShapeableImageView listitem_IMG_cover;
        private final TextView listitem_TXT_fullname;
        private final TextView listitem_TXT_category;
        private final ImageView listitem_IMG_check;
        private boolean clicked;

        public GuestViewHolder(@NonNull View itemView) {
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
                        if (hiddenCheckMark && SharedPreferencesSingleton.getInstance().getPrefs().getBoolean(Constants.VALID_GUEST, false)) {
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
