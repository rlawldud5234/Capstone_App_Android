package com.example.proto2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {
    private ArrayList<Dictionary> poiList;

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name_view;

        public RecyclerViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p = getAdapterPosition();
                    if(p != RecyclerView.NO_POSITION) {
                        Log.d("----TAG", String.valueOf(poiList.get(p).getPOI_latlng()));
                    }
                }
            });
            this.name_view = (TextView) v.findViewById(R.id.poiView);
        }
    }

    public Adapter(ArrayList<Dictionary> list) {
        this.poiList = list;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        RecyclerViewHolder rvh = new RecyclerViewHolder(view);

        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.name_view.setText(poiList.get(position).getPOI_name());
    }

    @Override
    public int getItemCount() {
        return (null != poiList ? poiList.size() : 0);
    }

}
