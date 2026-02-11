package com.example.realtimeroadwarningapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimeroadwarningapp.R;
import com.example.realtimeroadwarningapp.model.AccidentModel;

import java.util.List;

public class AccidentAdapter extends RecyclerView.Adapter<AccidentAdapter.ViewHolder> {

    private final List<AccidentModel> accidentModels;

    public AccidentAdapter(List<AccidentModel> accidentModels) {
        this.accidentModels = accidentModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_accident_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRegion.setText(accidentModels.get(position).getRegion());
        holder.tvDeathToll.setText(accidentModels.get(position).getPassAway());
        holder.tvNumberOfInjury.setText(accidentModels.get(position).getInjury());
        holder.tvNumberOfPieces.setText(accidentModels.get(position).getNumberOfPieces());
    }

    @Override
    public int getItemCount() {
        return accidentModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRegion, tvDeathToll, tvNumberOfInjury, tvNumberOfPieces;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRegion = itemView.findViewById(R.id.tv_region);
            tvDeathToll = itemView.findViewById(R.id.tv_death_toll);
            tvNumberOfInjury = itemView.findViewById(R.id.tv_number_of_injury);
            tvNumberOfPieces = itemView.findViewById(R.id.tv_number_of_pieces);
        }
    }
}
