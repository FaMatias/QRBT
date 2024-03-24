package com.example.qr_bt.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_bt.R;
import com.example.qr_bt.model.Stock;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_bt.CreateStockActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class StockAdapter extends FirestoreRecyclerAdapter<Stock, StockAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;
    private FragmentManager fm;

    public StockAdapter(@NonNull FirestoreRecyclerOptions<Stock> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Stock stock) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.name.setText(stock.getName());
        viewHolder.date.setText(stock.getDate());
        viewHolder.enlace.setText(stock.getEnlace());
        viewHolder.referencia.setText(stock.getReferencia()); // Ahora referencia es un texto
        String photoStock = stock.getPhoto();
        if (photoStock != null && !photoStock.isEmpty()) {
            Picasso.get()
                    .load(photoStock)
                    .resize(150, 150)
                    .into(viewHolder.photo_stock);
        }

        viewHolder.btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(activity, CreateStockActivity.class);
            intent.putExtra("id_stock", id);
            activity.startActivity(intent);
        });

        viewHolder.btn_delete.setOnClickListener(v -> deleteStock(id));
    }

    private void deleteStock(String id) {
        mFirestore.collection("stock").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_stock_single, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, enlace, referencia;
        ImageView btn_delete, btn_edit, photo_stock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nombre);
            date = itemView.findViewById(R.id.fecha);
            enlace = itemView.findViewById(R.id.enlace);
            referencia = itemView.findViewById(R.id.referencia);
            photo_stock = itemView.findViewById(R.id.photo);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editar);
        }
    }
}
