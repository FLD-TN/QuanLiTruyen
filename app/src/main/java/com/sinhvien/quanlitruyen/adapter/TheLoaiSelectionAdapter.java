package com.sinhvien.quanlitruyen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.model.TheLoai;

import java.util.ArrayList;
import java.util.List;

public class TheLoaiSelectionAdapter extends RecyclerView.Adapter<TheLoaiSelectionAdapter.TheLoaiViewHolder> {
    private List<TheLoai> theLoaiList;
    private List<String> selectedTenTheLoai = new ArrayList<>();

    public TheLoaiSelectionAdapter(List<TheLoai> theLoaiList, List<String> preSelected) {
        this.theLoaiList = theLoaiList;
        this.selectedTenTheLoai.addAll(preSelected);
    }

    public TheLoaiSelectionAdapter(List<TheLoai> theLoaiList) {
        this(theLoaiList, new ArrayList<>());
    }

    @NonNull
    @Override
    public TheLoaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_the_loai_selection, parent, false);
        return new TheLoaiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheLoaiViewHolder holder, int position) {
        TheLoai theLoai = theLoaiList.get(position);
        holder.textViewTenTheLoai.setText(theLoai.getTenTheLoai());
        holder.checkBox.setChecked(selectedTenTheLoai.contains(theLoai.getTenTheLoai()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedTenTheLoai.contains(theLoai.getTenTheLoai())) {
                    selectedTenTheLoai.add(theLoai.getTenTheLoai());
                }
            } else {
                selectedTenTheLoai.remove(theLoai.getTenTheLoai());
            }
        });
    }

    @Override
    public int getItemCount() {
        return theLoaiList != null ? theLoaiList.size() : 0;
    }

    public List<String> getSelectedTenTheLoai() {
        return selectedTenTheLoai;
    }

    static class TheLoaiViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTenTheLoai;
        CheckBox checkBox;

        TheLoaiViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTenTheLoai = itemView.findViewById(R.id.textViewTenTheLoai);
            checkBox = itemView.findViewById(R.id.checkBoxTheLoai);
        }
    }
}