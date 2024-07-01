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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class mistake_detail extends AppCompatActivity {
    private AdapterMistake adapterMistake;
    private TextView txtVCC, txtVTT, txtVHT, txtVNT;
    private RecyclerView recycCC, recycTT, recycHT, recycNT, recycDetail;
    private int clickCount = 0;
    private TextView txtNamePersonl;
    private ImageView imgDropCC, imgDropTT, imgDropHT, imgDropNT;
    private LinearLayout layoutCC;
    String namePersonl;
    private Account account;
    private Student student;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_detail);


        txtNamePersonl = findViewById(R.id.txtNamePersonl);

        recycDetail = findViewById(R.id.recycDetails);

        Intent intent = getIntent();
        namePersonl = intent.getStringExtra("mistakePersonl");
        account = (Account) intent.getSerializableExtra("account");
        student = (Student) intent.getSerializableExtra("student");

        Toast.makeText(this, account.getTkID()+"11111",Toast.LENGTH_LONG).show();


        txtNamePersonl.setText(namePersonl);


        getLoaiVP(recycDetail);



    }



    private void getCC(RecyclerView recycCC, String loaiVP) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("viPham");
        List<Mistake> vpTenViPhams = new ArrayList<>();

        collectionRef.whereEqualTo("LVP_id", loaiVP)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String vpTenViPham = document.getString("VP_TenViPham");
                        if (vpTenViPham != null) {
                            vpTenViPhams.add(new Mistake(vpTenViPham));
                        }
                    }

                    // Xử lý vpTenViPhams ở đây
                    Log.d("TAG", String.valueOf(vpTenViPhams.size()));

                    AdapterMistake adapterMistake = new AdapterMistake(vpTenViPhams, mistake_detail.this, namePersonl, account, student);
                    recycCC.setAdapter(adapterMistake);
                    recycCC.setLayoutManager(new GridLayoutManager(this, 1));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mistake_detail.this, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

                    AdapterMistakeDetail adapterMistakeDetail = new AdapterMistakeDetail(loaiVP, mistake_detail.this, namePersonl, account, student);
                    recycDetail.setAdapter(adapterMistakeDetail);
                    recycDetail.setLayoutManager(new GridLayoutManager(this, 1));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mistake_detail.this, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public List<Mistake> getList() {
         List<Mistake> mistakeList = new ArrayList<>();
         CountDownLatch latch = new CountDownLatch(1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference viPhamRef = db.collection("viPham");
        viPhamRef.whereEqualTo("loaiVP", "t").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String tenViPham = document.getString("tenViPham");
                                mistakeList.add(new Mistake(tenViPham));
                            }
                            Toast.makeText(mistake_detail.this, mistakeList.size(), Toast.LENGTH_LONG).show();

                        } else {
                            // Handle the error, for example log it
                            System.err.println("Error getting documents: " + task.getException());
                        }
                        latch.countDown();
                    }
                });

        try {
            // Wait for the Firebase query to complete
            latch.await(1, TimeUnit.SECONDS); // Adjust the timeout as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mistakeList;
    }
    private List<Mistake> getTTList() {
        List<Mistake> ttList = new ArrayList<>();
        ttList.add(new Mistake("12"));
        ttList.add(new Mistake("12"));
        ttList.add(new Mistake("12"));
        ttList.add(new Mistake("12"));
        ttList.add(new Mistake("12"));
        ttList.add(new Mistake("12"));
        ttList.add(new Mistake("12"));

        return ttList;
    }

    private List<Mistake> getHTList() {
        List<Mistake> htList = new ArrayList<>();
        htList.add(new Mistake("12553"));
        htList.add(new Mistake("12253"));
        htList.add(new Mistake("12553"));
        htList.add(new Mistake("18523"));
        htList.add(new Mistake("123"));
        htList.add(new Mistake("125523"));
        htList.add(new Mistake("12523"));
        htList.add(new Mistake("13"));
        htList.add(new Mistake("120023"));
        htList.add(new Mistake("23"));
        htList.add(new Mistake("102"));
        htList.add(new Mistake("123"));
        htList.add(new Mistake("130000"));
        htList.add(new Mistake("123"));
        htList.add(new Mistake("12583"));
        htList.add(new Mistake("12003"));
        htList.add(new Mistake("23"));
        htList.add(new Mistake("12053"));
        htList.add(new Mistake("12883"));
        htList.add(new Mistake("1203"));


        return htList;
    }

    private List<Mistake> getNTList() {
        List<Mistake> NTList = new ArrayList<>();
        NTList.add(new Mistake("1234"));
        NTList.add(new Mistake("1234"));
        NTList.add(new Mistake("1234"));
        NTList.add(new Mistake("1234"));
        NTList.add(new Mistake("1234"));
        NTList.add(new Mistake("1234"));
        NTList.add(new Mistake("1234"));
        return NTList;
    }
}