package com.example.realtimeroadwarningapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.RecyclerViewClickInterface;
import com.example.realtimeroadwarningapp.model.CommonAddressModel;

import java.util.List;

public class CommonAddressAdapter extends RecyclerView.Adapter<CommonAddressAdapter.ViewHolder> {

    private static final int ITEM_NORMAL_CONTAINER = 0;
    private static final int ITEM_DIALOG_CONTAINER = 1;

    private List<CommonAddressModel> addressModels;
    private RecyclerViewClickInterface recyclerViewInterface;
    private int layoutType;

    public CommonAddressAdapter(List<CommonAddressModel> addressModels,
                                RecyclerViewClickInterface recyclerViewInterface, int layoutType) {
        this.addressModels = addressModels;
        this.recyclerViewInterface = recyclerViewInterface;
        this.layoutType = layoutType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (layoutType == ITEM_NORMAL_CONTAINER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_common_address,
                    parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_container,
                    parent, false);
        }
        return new ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(addressModels.get(position).getTitle());
        holder.tvAddress.setText(addressModels.get(position).getAddress());
    }


    @Override
    public int getItemCount() {
        return addressModels.size();
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
