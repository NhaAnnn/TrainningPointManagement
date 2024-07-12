package com.example.qldrl.Mistake;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Mistake_See extends AppCompatActivity {
    private RecyclerView recycMistakeSee;
    private TextView txtNamePersonlMistake;
    public static AdapterMistakeSee adapterMistakeSee;
    private Student student;
    private Account account;
    private ImageView imgBackMistakeSee;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_see);

        imgBackMistakeSee = findViewById(R.id.imgBackMistakeSee);
        imgBackMistakeSee.setOnClickListener(v -> onBackPressed());

        recycMistakeSee = findViewById(R.id.recycMistakeSee);
        txtNamePersonlMistake = findViewById(R.id.txtNamePersonlMistake);

        Intent intent = getIntent();
        student = (Student) intent.getSerializableExtra("student");
        account = (Account) intent.getSerializableExtra("account");

        assert student != null;
        txtNamePersonlMistake.setText("Vi phạm của "+student.getHsHoTen());

        getListMistakes(student.getHsID());


    }

    private void getListMistakes(String hsID) {
        List<Mistakes> mistakesList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> lvpRef = db.collection("luotViPham")
                .whereEqualTo("HS_id", hsID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String vpID = documentSnapshot.getString("VP_id");
                            String lvpID = documentSnapshot.getString("LTVP_id");
                            String hsIDd = documentSnapshot.getString("HS_id");
                            String tkID = documentSnapshot.getString("TK_id");
                            String lvpTime = documentSnapshot.getString("LTVP_ThoiGian");
                            String lvpHK = documentSnapshot.getString("HK_HocKy");


                            Mistakes mistakes = new Mistakes( hsIDd , vpID, lvpID, tkID,lvpTime,lvpHK);
                            mistakesList.add(mistakes);
                        }
                        adapterMistakeSee = new AdapterMistakeSee(mistakesList, Mistake_See.this, account, student); //truyen vao tuy tung list
                        recycMistakeSee.setAdapter(adapterMistakeSee);

                        recycMistakeSee.setLayoutManager(new GridLayoutManager(Mistake_See.this, 1));
                    }
                });


    }
}