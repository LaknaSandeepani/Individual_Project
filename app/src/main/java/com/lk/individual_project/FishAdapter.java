package com.lk.individual_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FishAdapter extends RecyclerView.Adapter<FishAdapter.FishViewHolder> {
    private List<Fish> fishList;

    public FishAdapter(List<Fish> fishList) {
        this.fishList = fishList;
    }

    @NonNull
    @Override
    public FishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fish_item_layout, parent, false);
        return new FishViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FishViewHolder holder, int position) {
        Fish fish = fishList.get(position);
        holder.textFishName.setText(fish.getName());
        holder.textFishDescription.setText(fish.getDescription());
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

    static class FishViewHolder extends RecyclerView.ViewHolder {
        TextView textFishName;
        TextView textFishDescription;

        FishViewHolder(@NonNull View itemView) {
            super(itemView);
            textFishName = itemView.findViewById(R.id.textFishName);
            textFishDescription = itemView.findViewById(R.id.textFishDescription);
        }
    }
}
