package com.example.lets_plan.logic.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.recyclerview.adapter.TablesarrangementRecyclerViewAdapter.TableViewHolder;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.utils.Constants;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class TablesarrangementRecyclerViewAdapter extends Adapter<TableViewHolder> implements Filterable {
    private List<Table> allTables;
    private List<Table> filteredTables;
    private final TableFilter filter;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public TablesarrangementRecyclerViewAdapter(List<Table> allTables) {
        this.allTables = allTables;
        this.filteredTables = allTables;
        this.filter = new TableFilter();
        this.mInflater = LayoutInflater.from(DataHandler.getInstance().getContext());
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tables_arrangement_recyclerview_listitem, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = filteredTables.get(position);
        holder.listitem_TXT_name.setText(table.getName());
        holder.listitem_TXT_capacity.setText(
                table.sum() + "/" + table.getMaxCapacity().toString());
        holder.listitem_TXT_category.setText(table.getCategory());
    }

    @Override
    public int getItemCount() {
        if (filteredTables == null) {
            return 0;
        }
        return filteredTables.size();
    }

    public Table getItem(int id) {
        if (id < 0 || id >= getItemCount()) {
            return null;
        }
        return this.filteredTables.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void updateTables(String filter) {
        getFilter().filter(filter);
    }

    public void setAllTables(List<Table> tables) {
        this.allTables = tables;
        updateTables(Constants.ALL);
    }

    public void removeTable(Table table) {
        int position = 0;
        for (; position < allTables.size(); position++) {
            if (allTables.get(position).equals(table)) {
                break;
            }
        }
        allTables.remove(table);
        updateTables(Constants.ALL);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, filteredTables.size());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class TableFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();
            final List<Table> newList = new ArrayList<>();
            if (filterString.equals(Constants.ALL)) {
                newList.addAll(allTables);
            } else {
                for (int i = 0; i < allTables.size(); i++) {
                    Table table = allTables.get(i);
                    if (table.getCategory().equals(filterString)) {
                        newList.add(table);
                    }
                }
            }
            results.values = newList;
            results.count = newList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredTables = (List<Table>) results.values;
            notifyDataSetChanged();
        }
    }

    public class TableViewHolder extends ViewHolder {
        private final ShapeableImageView listitem_IMG_cover;
        private final TextView listitem_TXT_name;
        private final TextView listitem_TXT_capacity;
        private final TextView listitem_TXT_category;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            this.listitem_IMG_cover = itemView.findViewById(R.id.table_listitem_IMG_cover);
            this.listitem_TXT_name = itemView.findViewById(R.id.table_listitem_TXT_name);
            this.listitem_TXT_capacity = itemView.findViewById(R.id.table_listitem_TXT_capacity);
            this.listitem_TXT_category = itemView.findViewById(R.id.table_listitem_TXT_category);
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
