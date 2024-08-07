package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.AdapterSpinner;
import com.example.qldrl.Class.ListClass;
import com.example.qldrl.General.Account;
import com.example.qldrl.Homes.MainHome_Edited;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListConduct extends AppCompatActivity {
    private String className, quantity, teacherName, conduct;
    private Account account;
    private AdapterListConduct adapterListConduct;
    private AdapterSpinner adapterSpinnerHelper;
    private List<ListClass> listClasses = new ArrayList<>();
    private String semester = "Học kỳ 1";

    public ListConduct() {
    }

    public ListConduct(String className, String quantity, String teacherName, String conduct) {
        this.className = className;
        this.quantity = quantity;
        this.teacherName = teacherName;
        this.conduct = conduct;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_conduct);

        SearchView searchView = findViewById(R.id.searchView);
        Spinner spinnerGrade = findViewById(R.id.spinnerGrade);
        Spinner spinnerSemester = findViewById(R.id.spinnerSemester);
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);

        // Tạo instance của GradeSpinnerHelper và thiết lập Spinner
        adapterSpinnerHelper = new AdapterSpinner(spinnerGrade, spinnerSemester, spinnerYear);
        adapterSpinnerHelper.setupSpinnerGrade(this);
        adapterSpinnerHelper.setupSpinnerSemester(this);
        adapterSpinnerHelper.setupSpinnerYear(this);


        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        Log.d(TAG, "onCreate: "+account.getTkID());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("giaoVien").whereEqualTo("TK_id",account.getTkID())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = (String) document.getString("LH_id");

                            if(account.getTkChucVu().toLowerCase().trim().equals(MainHome_Edited.gv)){
                                getDataClassForTeacher(id);
                            }else {
                                getDataClass();
                            }
                        }
                    }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý khi người dùng nhấn "Search"
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account,semester);
                recyclerView.setAdapter(adapterListConduct);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi người dùng thay đổi từ khóa tìm kiếm
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account, semester);
                recyclerView.setAdapter(adapterListConduct);
                return true;
            }

        });

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                String all = "Khối";
                int spaceIndex = selectedOption.indexOf(" ");
                String afterSpace = selectedOption.substring(spaceIndex + 1);
                Log.d(TAG, "onItemSelected: "+afterSpace);
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getName().substring(0,2).toLowerCase().contains(afterSpace.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account, semester);
                recyclerView.setAdapter(adapterListConduct);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có lựa chọn nào được chọn
            }
        });
        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  String selectedOption = (String) parent.getItemAtPosition(position);
                  List<ListClass> filteredData = new ArrayList<>();
                  semester = selectedOption;
                  for (ListClass item : listClasses) {
                          filteredData.add(item);

                  }
                  adapterListConduct = new AdapterListConduct(filteredData, ListConduct.this, account, selectedOption);
                  recyclerView.setAdapter(adapterListConduct);
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                String all = "Năm học";
                List<ListClass> filteredData = new ArrayList<>();
                for (ListClass item : listClasses) {
                    if (item.getYear().toLowerCase().contains(selectedOption.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterListConduct = new AdapterListConduct(filteredData, ListConduct.this, account, semester);
                recyclerView.setAdapter(adapterListConduct);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có lựa chọn nào được chọn
            }
        });



        ImageView btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());
    }
    public void setList(List<ListClass> List){
        listClasses.addAll(List);
        adapterListConduct = new AdapterListConduct(List,ListConduct.this, account, semester);
        RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
        recyclerView.setAdapter(adapterListConduct);
    }
    public void getDataClass(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    classroomList = new ArrayList<>();
                    List<ListClass> List = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = (String) document.getString("LH_id");
                        String classroomName = (String) document.getString("LH_TenLop");
                        String teacherName = (String) document.getString("LH_GVCN");
                        String year = (String) document.getString("NK_NienKhoa");

                        ListClass data = new ListClass(id, classroomName, teacherName, year);
//                        Classlist data = document.toObject(Classlist.class);
                        List.add(data);

                    }
                    Log.d(TAG, "Success: " + List.size());
                    setList(List);
                }
            }
        });
    }
    public void getDataClassForTeacher(String idLH){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop").whereEqualTo("LH_id",idLH).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                    classroomList = new ArrayList<>();
                List<ListClass> List = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = (String) document.getString("LH_id");
                    String classroomName = (String) document.getString("LH_TenLop");
                    String teacherName = (String) document.getString("LH_GVCN");
                    String year = (String) document.getString("NK_NienKhoa");

                    ListClass data = new ListClass(id, classroomName, teacherName, year);
//                        Classlist data = document.toObject(Classlist.class);
                    List.add(data);

                }
                Log.d(TAG, "Success: " + List.size());
                setList(List);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getConduct() {
        return conduct;
    }

    public void setConduct(String conduct) {
        this.conduct = conduct;
    }


}