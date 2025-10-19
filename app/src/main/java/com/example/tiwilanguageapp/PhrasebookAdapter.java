package com.example.tiwilanguageapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhrasebookAdapter extends RecyclerView.Adapter<PhrasebookAdapter.VH> {

    public interface OnPlayClick {
        void onPlay(@NonNull PhraseEntry entry);
    }

    private final List<PhraseEntry> phrases;
    private final OnPlayClick onPlayClick;

    public PhrasebookAdapter(List<PhraseEntry> phrases, OnPlayClick onPlayClick) {
        this.phrases = phrases;
        this.onPlayClick = onPlayClick;
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView english;
        final TextView tiwi;
        final ImageButton play;

        VH(@NonNull View itemView) {
            super(itemView);
            english = itemView.findViewById(R.id.tvEnglish);
            tiwi = itemView.findViewById(R.id.tvTiwi);
            play = itemView.findViewById(R.id.btnPlay);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_phrasebook_entry, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PhraseEntry entry = phrases.get(position);
        holder.english.setText(entry.english);
        holder.tiwi.setText(entry.tiwi);

        if (entry.audioResId == null) {
            holder.play.setVisibility(View.GONE);
            holder.play.setOnClickListener(null);
        } else {
            holder.play.setVisibility(View.VISIBLE);
            holder.play.setOnClickListener(v -> {
                if (onPlayClick != null) {
                    onPlayClick.onPlay(entry);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }
}
