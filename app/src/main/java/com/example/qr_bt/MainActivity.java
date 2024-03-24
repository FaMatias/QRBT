package com.example.qr_bt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.qr_bt.adapter.StockAdapter;
import com.example.qr_bt.model.Stock;

public class MainActivity extends AppCompatActivity {

    Button btn_add, btn_add_fragment, btn_exit;
    StockAdapter mAdapter;
    RecyclerView mRecycler;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
  //      search_view = findViewById(R.id.search);

        btn_add = findViewById(R.id.btn_add);
        btn_add_fragment = findViewById(R.id.btn_add_fragment);
        btn_exit = findViewById(R.id.btn_close);

        btn_add.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateImageWithTextActivity.class)));

        btn_add_fragment.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateQRActivity.class)));

        btn_exit.setOnClickListener(view -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        setUpRecyclerView();
        setupSearchView();
    }

    private void setUpRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("stock");

        FirestoreRecyclerOptions<Stock> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Stock>().setQuery(query, Stock.class).build();

        mAdapter = new StockAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
        mRecycler.setAdapter(mAdapter);
    }

    private void setupSearchView() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                performSearch(s);
                return false;
            }
        });
    }

    private void performSearch(String s) {
        query = mFirestore.collection("stock").orderBy("name").startAt(s).endAt(s + "~");
        FirestoreRecyclerOptions<Stock> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Stock>().setQuery(query, Stock.class).build();
        mAdapter.updateOptions(firestoreRecyclerOptions); // Actualiza las opciones del adapter existente
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
