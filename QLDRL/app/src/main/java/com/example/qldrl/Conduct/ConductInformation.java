package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConductInformation extends AppCompatActivity {
    private String idStudent, idMistake, idAccount, idMistakeType, timeMistake; //Loai Vi Pham
    private List<ConductInformation> conductInformationList;
    private Account account;
    public ConductInformation() {
    }

    public ConductInformation(String idStudent, String idMistake, String idAccount, String idMistakeType, String timeMistake) {
        this.idStudent = idStudent;
        this.idMistake = idMistake;
        this.idAccount = idAccount;
        this.idMistakeType = idMistakeType;
        this.timeMistake = timeMistake;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conduct_information);

        Intent intent = getIntent();
        String StudentName = intent.getStringExtra("StudentName");
        String StudentId = intent.getStringExtra("StudentId");
        String StudentTrainningPoint = intent.getStringExtra("StudentTrainningPoint");
        String StudentConduct = intent.getStringExtra("StudentConduct");
        account = (Account) intent.getSerializableExtra("account");
        String semester = intent.getStringExtra("semester");
        Log.d(TAG, "onCreate: HK "+semester);

        TextView txtPageTitle = findViewById(R.id.pageTitle);
        txtPageTitle.setText(StudentName);
        TextView txtNameStudent = findViewById(R.id.txtNameStudent);
        txtNameStudent.setText(StudentName);
        TextView txtSemester = findViewById(R.id.txtSemester);
        txtSemester.setText(semester);
        TextView txtTrainingPointStudent = findViewById(R.id.txtTrainingPointStudent);
        txtTrainingPointStudent.setText(StudentTrainningPoint);
        TextView txtConductStudent = findViewById(R.id.txtConductStudent);
        txtConductStudent.setText(StudentConduct);

//        getDataMistake(StudentId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("luotViPham").whereEqualTo("HS_id",StudentId).whereEqualTo("HK_HocKy",semester)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<ConductInformation> list = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                            String idS = (String) queryDocumentSnapshot.getString("HS_id");
                            String idM = (String) queryDocumentSnapshot.getString("VP_id");
                            String idA = (String) queryDocumentSnapshot.getString("TK_id");
                            String idMT = (String) queryDocumentSnapshot.getString("LVPM_id");
                            String time = (String) queryDocumentSnapshot.getString("LTVP_ThoiGian");
                            ConductInformation data = new ConductInformation(idS, idM, idA, idMT, time);
                            list.add(data);
                        }

                        conductInformationList = new ArrayList<>();
                        conductInformationList.addAll(list);
                        AdapterConductInformation adapterConductInformation =
                                new AdapterConductInformation(ConductInformation.this, conductInformationList, account);
                        RecyclerView recyclerView = findViewById(R.id.recyclerViewMistake);
                        recyclerView.setAdapter(adapterConductInformation);
                    }
                });
        ImageButton btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getIdMistake() {
        return idMistake;
    }

    public void setIdMistake(String idMistake) {
        this.idMistake = idMistake;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }

    public String getIdMistakeType() {
        return idMistakeType;
    }

    public void setIdMistakeType(String idMistakeType) {
        this.idMistakeType = idMistakeType;
    }

    public String getTimeMistake() {
        return timeMistake;
    }

    public void setTimeMistake(String timeMistake) {
        this.timeMistake = timeMistake;
    }
}