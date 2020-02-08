package com.example.proto2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;



public class Adapter extends RecyclerView.Adapter<Adapter.RecyclerViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private ArrayList<Dictionary> poiList;
    private OnItemClickListener itemListener = null;
    TMapPoint endpoint;

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name_view;

        public RecyclerViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        itemListener.onItemClick(v,pos);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }
}
