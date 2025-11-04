package com.example.pickleballshopapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
    private List<CarouselImage> images;

    public CarouselAdapter(List<CarouselImage> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carousel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarouselImage image = images.get(position);
        if (image != null && image.getImage_url() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(image.getImage_url())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(holder.imageView);
            
            // Hiển thị overlay text nếu có
            if (image.getOverlay_text() != null && !image.getOverlay_text().isEmpty()) {
                holder.overlayText.setText(image.getOverlay_text());
                holder.overlayText.setVisibility(View.VISIBLE);
            } else {
                holder.overlayText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView overlayText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carouselImageView);
            overlayText = itemView.findViewById(R.id.carouselOverlayText);
        }
    }
}

