package com.example.qldrl.Class;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListStudentOfClass extends AppCompatActivity implements Serializable {
    List<ListStudentOfClass> listStudentOfClasses = new ArrayList<>();
    private String id, idTK, idLH, name, date, gender, position; //Học Sinh
    private AdapterListStudentOfClass adapterListStudentOfClass;
    public ListStudentOfClass() {
    }

    public ListStudentOfClass(String id, String idTK, String idLH, String name, String date, String gender, String position) {
        this.id = id;
        this.idTK = idTK;
        this.idLH = idLH;
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.position = position;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student_of_class);

        Intent intent = getIntent();
        String nameClass = intent.getStringExtra("className");
        String idClass = intent.getStringExtra("classId");
        TextView txtCLassName = findViewById(R.id.pageTitle);
        txtCLassName.setText(nameClass);

        getData(idClass, nameClass);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query1) {
                // Xử lý khi người dùng nhấn "Search"
                List<ListStudentOfClass> filteredData = new ArrayList<>();
                for (ListStudentOfClass item : listStudentOfClasses) {
                    if (item.getName().toLowerCase().contains(query1.toLowerCase())
                            || item.getId().toLowerCase().contains(query1.toLowerCase())) {
                        filteredData.add(item);
                    }
                }
                adapterListStudentOfClass = new AdapterListStudentOfClass(filteredData, ListStudentOfClass.this, nameClass);
                RecyclerView recyclerView = findViewById(R.id.recyclViewStudent);
                recyclerView.setAdapter(adapterListStudentOfClass);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng thay đổi từ khóa tìm kiếm
                List<ListStudentOfClass> filteredData = new ArrayList<>();
                for (ListStudentOfClass item : listStudentOfClasses) {
                    if (item.getName().toLowerCase().contains(newText.toLowerCase())
                            || item.getId().toLowerCase().contains(newText.toLowerCase())) {
                        filteredData.add(item);
                    }
                }
                adapterListStudentOfClass = new AdapterListStudentOfClass(filteredData, ListStudentOfClass.this, nameClass);
                RecyclerView recyclerView = findViewById(R.id.recyclViewStudent);
                recyclerView.setAdapter(adapterListStudentOfClass);
                return true;
            }

        });

        ImageView btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());

        EventBus.getDefault().register(this);

    }
    public void getData(String idClass, String nameClass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("hocSinh").whereEqualTo("LH_id", idClass);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ListStudentOfClass> List = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String id = (String) documentSnapshot.getString("HS_id");
                    String idTK = (String) documentSnapshot.getString("TK_id");
                    String idLH = (String) documentSnapshot.getString("LH_id");
                    String name = (String) documentSnapshot.getString("HS_HoTen");
                    String date = (String) documentSnapshot.getString("HS_NgaySinh");
                    String gender = (String) documentSnapshot.getString("HS_GioiTinh");
                    String position = (String) documentSnapshot.getString("HS_ChucVu");

                    ListStudentOfClass data = new ListStudentOfClass(id, idTK, idLH, name, date, gender, position);
                    List.add(data);
                }
                updateData(List, nameClass);
            }
        });
    }
    private void updateData(List<ListStudentOfClass> List, String nameClass) {
        listStudentOfClasses.addAll(List);
        adapterListStudentOfClass = new AdapterListStudentOfClass(List, ListStudentOfClass.this, nameClass);
        RecyclerView recyclerView = findViewById(R.id.recyclViewStudent);
        recyclerView.setAdapter(adapterListStudentOfClass);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký EventBus
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStudentInformationUpdated(StudentInformationUpdatedEvent event) {
        ListStudentOfClass updatedStudent = event.updatedStudent;
        int position = event.position;
        listStudentOfClasses.set(position, updatedStudent);
        adapterListStudentOfClass.notifyItemChanged(position);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTK() {
        return idTK;
    }

    public void setIdTK(String idTK) {
        this.idTK = idTK;
    }

    public String getIdLH() {
        return idLH;
    }

    public void setIdLH(String idLH) {
        this.idLH = idLH;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}