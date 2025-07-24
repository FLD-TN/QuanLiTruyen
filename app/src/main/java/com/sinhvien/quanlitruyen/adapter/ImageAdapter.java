package com.sinhvien.quanlitruyen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.sinhvien.quanlitruyen.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<String> imagePaths;
    private int imageQuality;

    // Constants for quality
    private static final int QUALITY_HIGH = 0;
    private static final int QUALITY_MEDIUM = 1;
    private static final int QUALITY_LOW = 2;


    public ImageAdapter(Context context, List<String> imagePaths, int imageQuality) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.imageQuality = imageQuality;
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

        RequestBuilder<android.graphics.drawable.Drawable> requestBuilder = Glide.with(context).load(imagePath);

        switch (imageQuality) {
            case QUALITY_MEDIUM:
                // Giảm 50% độ phân giải
                requestBuilder = requestBuilder.thumbnail(0.5f);
                break;
            case QUALITY_LOW:
                // Giảm 75% độ phân giải và nén chất lượng
                requestBuilder = requestBuilder.thumbnail(0.25f).apply(new RequestOptions().encodeQuality(80));
                break;
            case QUALITY_HIGH:
            default:
                // Không làm gì, tải ảnh gốc
                break;
        }

        requestBuilder
                .placeholder(R.drawable.anh_bia_manga_onepice)
                .error(R.drawable.analytics_icon)
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