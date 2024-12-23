package com.app.homiefy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.homiefy.contract.Contract;
import com.app.homiefy.contract.ContractAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContractManagement extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContractAdapter adapter;
    private List<Contract> contractList;
    private FirebaseFirestore db;
    private String currentUserId;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_management);

        initViews();
        setupRecyclerView();
        loadContracts();
        loadOwnerContracts();
        setupBackButton();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        db = FirebaseFirestore.getInstance("homeify");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contractList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new ContractAdapter(contractList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContracts();
        loadOwnerContracts();
    }


    private void loadContracts() {
        showLoading(true);
        contractList.clear();

        db.collection("deposits")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    contractList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Contract contract = doc.toObject(Contract.class);
                        if (contract != null) {
                            contract.setDepositId(doc.getId());
                            contractList.add(contract);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    showLoading(false);
                    updateEmptyView();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Unable to load contract list: " + e.getMessage());
                });
    }

    private void loadOwnerContracts() {
        // Query cho chủ nhà (ownerId)
        db.collection("deposits")
                .whereEqualTo("owner", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Contract contract = doc.toObject(Contract.class);
                        if (contract != null) {
                            contract.setDepositId(doc.getId());
                            contractList.add(contract);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    showLoading(false);
                    updateEmptyView();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Unable to load landlord contract list: " + e.getMessage());
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void updateEmptyView() {
        if (contractList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}