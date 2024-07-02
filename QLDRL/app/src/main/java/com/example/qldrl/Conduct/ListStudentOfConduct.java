package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.Class.ListStudentOfClass;
import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListStudentOfConduct extends AppCompatActivity implements Serializable {
    private String idConduct,trainingPoint, conduct, term, idStudent; //Hạnh Kiểm
    public List<ListStudentOfClass> listStudentOfClass;
    private Account account;
    private AdapterListStudentOfConduct adapterListStudentOfConduct;
    public ListStudentOfConduct() {
    }

    public ListStudentOfConduct(String idConduct, String idStudent, String trainingPoint, String conduct, String term) {
       this.idConduct = idConduct;
        this.trainingPoint = trainingPoint;
        this.conduct = conduct;
        this.term = term;
        this.idStudent = idStudent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student_of_conduct);

        Intent intent = getIntent();
        String semester = intent.getStringExtra("semester");
        TextView classTitle = findViewById(R.id.pageTitle);
        classTitle.setText("Hạnh kiểm lớp "+intent.getStringExtra("className")+" "+semester);

        TextView txtNameClass = findViewById(R.id.txtNameClass);
        txtNameClass.setText(intent.getStringExtra("className"));

        TextView txtNameTeacher = findViewById(R.id.txtNameTeacher);
        txtNameTeacher.setText(intent.getStringExtra("teacherName"));

        String idC = intent.getStringExtra("classId");

        account = (Account) intent.getSerializableExtra("account");

//        getDataConduct(idC);
        SearchView searchView = findViewById(R.id.searchView);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query1 = db.collection("hocSinh").whereEqualTo("LH_id", idC);
        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<ListStudentOfClass> List = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()){

                        String id = (String) documentSnapshot.getString("HS_id");
                        String idTK = (String) documentSnapshot.getString("TK_id");
                        String idLH = (String) documentSnapshot.getString("LH_id");
                        String name = (String) documentSnapshot.getString("HS_HoTen");
                        String date = (String) documentSnapshot.getString("HS_NgaySinh");
                        String gender = (String) documentSnapshot.getString("HS_GioiTinh");
                        String position = (String) documentSnapshot.getString("HS_ChucVu");

                        ListStudentOfClass data = new ListStudentOfClass(id, idTK, idLH, name, date, gender, position);
                        List.add(data);
                        Log.d(TAG, "idHS: "+id);


                    }
                    listStudentOfClass = new ArrayList<>();
                    listStudentOfClass.addAll(List);
                    adapterListStudentOfConduct = new AdapterListStudentOfConduct(List,ListStudentOfConduct.this, account, semester);

                    RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
                    recyclerView.setAdapter(adapterListStudentOfConduct);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            // Xử lý khi người dùng nhấn "Search"
                            List<ListStudentOfClass> filteredData = new ArrayList<>();
                            for (ListStudentOfClass item : List) {
                                if (item.getName().toLowerCase().contains(query.toLowerCase())
                                        || item.getId().toLowerCase().contains(query.toLowerCase())) {
                                    filteredData.add(item);
                                }
                            }
                            adapterListStudentOfConduct = new AdapterListStudentOfConduct(filteredData, ListStudentOfConduct.this, account, semester);
                            recyclerView.setAdapter(adapterListStudentOfConduct);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            // Xử lý khi người dùng thay đổi từ khóa tìm kiếm
                            List<ListStudentOfClass> filteredData = new ArrayList<>();
                            for (ListStudentOfClass item : List) {
                                if (item.getName().toLowerCase().contains(newText.toLowerCase())
                                        || item.getId().toLowerCase().contains(newText.toLowerCase())) {
                                    filteredData.add(item);
                                }
                            }
                            adapterListStudentOfConduct = new AdapterListStudentOfConduct(filteredData, ListStudentOfConduct.this, account, semester);
                            recyclerView.setAdapter(adapterListStudentOfConduct);
                            return true;
                        }

                    });
                }
            }
        });
        ImageView btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public String getTrainingPoint() {
        return trainingPoint;
    }

    public void setTrainingPoint(String trainingPoint) {
        this.trainingPoint = trainingPoint;
    }

    public String getConduct() {
        return conduct;
    }

    public void setConduct(String conduct) {
        this.conduct = conduct;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }
}