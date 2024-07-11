package com.example.qldrl.Mistake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class mistake_detail_edit extends AppCompatActivity {
    public static AdapterMistakeDetailED adapterMistakeDetailED;
    private TextView txtVCC, txtVTT, txtVHT, txtVNT;
    private RecyclerView  recycDetail;
    private int clickCount = 0;
    private TextView txtNamePersonl;
    private ImageView imgBackDetail;
    private LinearLayout layoutAddMis;
    String namePersonl;
    private Account account;
    private Student student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_detail_edit);

        layoutAddMis = findViewById(R.id.layoutAddMis);
        txtNamePersonl = findViewById(R.id.txtNamePersonl);
        imgBackDetail = findViewById(R.id.imgBackDetail);
        imgBackDetail.setOnClickListener(v -> onBackPressed());

        recycDetail = findViewById(R.id.recycDetails);

        Intent intent = getIntent();
        namePersonl = intent.getStringExtra("mistakePersonl");
        account = (Account) intent.getSerializableExtra("account");
        student = (Student) intent.getSerializableExtra("student");

        //Toast.makeText(this, account.getTkID()+"11111",Toast.LENGTH_LONG).show();

        layoutAddMis.setOnClickListener( v -> {
            Intent intent1 = new Intent(this, AddMistake.class);
            startActivity(intent1);
        });



        getLoaiVP(recycDetail);



    }




    private void getLoaiVP(RecyclerView recycDetail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("loaiViPham");
        List<MistakeType> loaiVP = new ArrayList<>();

        collectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String lvpTen = document.getString("LVP_TenLoaiViPham");
                        String lvpID = document.getString("LVP_id");

                        MistakeType mistakeType = new MistakeType(lvpID, lvpTen);

                        loaiVP.add(mistakeType);
                    }

                    // Xử lý vpTenViPhams ở đây
                    Log.d("LOOOOOOO", String.valueOf(loaiVP.size()));

                   adapterMistakeDetailED = new AdapterMistakeDetailED(loaiVP, mistake_detail_edit.this, namePersonl, account, student);
                    recycDetail.setAdapter(adapterMistakeDetailED);
                    recycDetail.setLayoutManager(new GridLayoutManager(this, 1));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mistake_detail_edit.this, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}