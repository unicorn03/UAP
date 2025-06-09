package com.example.uap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TanamanAdapter extends RecyclerView.Adapter<TanamanAdapter.ViewHolder> {
    Context context;
    List<Tanaman> list;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onHapus(int pos);
        void onDetail(int pos);
        void onUpdate(int pos);
    }

    public TanamanAdapter(Context context, List<Tanaman> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tanaman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tanaman t = list.get(position);
        holder.txtNama.setText(t.getNama());
        holder.txtHarga.setText(t.getHarga());
        holder.imgTanaman.setImageResource(t.getGambarResId());

        holder.btnHapus.setOnClickListener(v -> listener.onHapus(position));
        holder.btnDetail.setOnClickListener(v -> listener.onDetail(position));

        // Add long click for update functionality
        holder.itemView.setOnLongClickListener(v -> {
            listener.onUpdate(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHarga;
        ImageView imgTanaman;
        Button btnHapus, btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            imgTanaman = itemView.findViewById(R.id.imgTanaman);
            btnHapus = itemView.findViewById(R.id.btnHapus);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}