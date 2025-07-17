package com.sinhvien.quanlitruyen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sinhvien.quanlitruyen.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<String> imagePaths;

    public ImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        // Load hình ảnh bằng Glide để tối ưu hiệu suất
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.anh_bia_manga_onepice) // Ảnh placeholder nếu cần
                .error(R.drawable.analytics_icon) // Ảnh lỗi nếu không tải được
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagePaths != null ? imagePaths.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}