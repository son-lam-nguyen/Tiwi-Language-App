package com.example.tiwilanguageapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VH> {
    private final Context ctx;
    private final List<VideoItem> items;

    public VideoAdapter(Context ctx, List<VideoItem> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_video, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        VideoItem it = items.get(pos);
        h.tvTitle.setText(it.getTitle());
        h.tvChannel.setText(it.getChannel());
        Glide.with(ctx).load(it.getThumbnailUrl()).into(h.imgThumb);


        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, PlayerWebActivity.class);
            i.putExtra(PlayerWebActivity.EXTRA_VIDEO_ID, it.getVideoId());
            ctx.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle;
        TextView tvChannel;

        VH(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvChannel = itemView.findViewById(R.id.tvChannel);
        }
    }
}
