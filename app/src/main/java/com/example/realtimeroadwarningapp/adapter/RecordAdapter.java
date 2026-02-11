package com.example.realtimeroadwarningapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.RecyclerViewClickInterface;
import com.example.realtimeroadwarningapp.model.RecordModel;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<RecordModel> recordModels;
    private RecyclerViewClickInterface recyclerViewClickInterface;

    public RecordAdapter(List<RecordModel> recordModels, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.recordModels = recordModels;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_record, parent, false);
        return new ViewHolder(view, recyclerViewClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(recordModels.get(position).getTime());
        holder.tvAddress.setText(recordModels.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        if (recordModels != null) {
            return recordModels.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvAddress;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickInterface recyclerViewInterface) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAddress = itemView.findViewById(R.id.tv_address);

            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
                return false;
            });
        }
    }
}
