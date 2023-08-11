package com.lk.individual_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FishAdapter extends RecyclerView.Adapter<FishAdapter.FishViewHolder> {

    private Context context;
    private List<Fish> fishList;

    public FishAdapter(Context context, List<Fish> fishList) {
        this.context = context;
        this.fishList = fishList;
    }

    @NonNull
    @Override
    public FishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fish_item_layout, parent, false);
        return new FishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FishViewHolder holder, int position) {
        Fish fish = fishList.get(position);

        holder.fishNameTextView.setText(fish.getName());
        holder.countryNameTextView.setText(fish.getCountryName());

        Glide.with(context).load(fish.getImageUrl()).into(holder.fishImageView);
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

    public static class FishViewHolder extends RecyclerView.ViewHolder {
        ImageView fishImageView;
        TextView fishNameTextView, countryNameTextView;

        public FishViewHolder(@NonNull View itemView) {
            super(itemView);
            fishImageView = itemView.findViewById(R.id.imgfish);
            fishNameTextView = itemView.findViewById(R.id.FishName);
            countryNameTextView = itemView.findViewById(R.id.countryname);
        }
    }
}

