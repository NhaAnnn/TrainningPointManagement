package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.AdapterSpinner;
import com.example.qldrl.Class.ListClass;
import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListConduct extends AppCompatActivity {
    private String className, quantity, teacherName, conduct; // Hạnh Kiểm
    private Account account;
    private AdapterListConduct adapterListConduct;
    private AdapterSpinner adapterSpinnerHelper;
    private String semester = "Học kỳ 1";
    private Spinner spinnerSemester;

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
        spinnerSemester = findViewById(R.id.spinnerSemester);
        Spinner spinnerYear = findViewById(R.id.spinnerYear);

        // Tạo instance của GradeSpinnerHelper và thiết lập Spinner
        adapterSpinnerHelper = new AdapterSpinner(spinnerGrade, spinnerYear);
        adapterSpinnerHelper.setupSpinnerGrade(this);
        adapterSpinnerHelper.setupSpinnerYear(this);
        setupSpinnerSemester(this);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");



//        getDataClass();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
//                    classroomList = new ArrayList<>();
                    List<ListClass> List = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = (String) document.getString("LH_id");
                        String classroomName = (String) document.getString("LH_TenLop");
                        String teacherName = (String) document.getString("LH_GVCN");
                        String year = (String) document.getString("NK_NienKhoa");

                        ListClass data = new ListClass(id,classroomName,teacherName,year);
//                        Classlist data = document.toObject(Classlist.class);
                        List.add(data);

                    }
                    Log.d(TAG, "Success: "+ List.size());
//                    listData = new ArrayList<>();
//                    listData.addAll(List);

                    adapterListConduct = new AdapterListConduct(List,ListConduct.this, account, semester);
                    RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
                    recyclerView.setAdapter(adapterListConduct);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            // Xử lý khi người dùng nhấn "Search"
                            List<ListClass> filteredData = new ArrayList<>();
                            for (ListClass item : List) {
                                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                                    filteredData.add(item);
                                }
                            }
                            adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account,semester);
                            RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
                            recyclerView.setAdapter(adapterListConduct);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            // Xử lý khi người dùng thay đổi từ khóa tìm kiếm
                            List<ListClass> filteredData = new ArrayList<>();
                            for (ListClass item : List) {
                                if (item.getName().toLowerCase().contains(newText.toLowerCase())) {
                                    filteredData.add(item);
                                }
                            }
                            adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account, semester);
                            RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
                            recyclerView.setAdapter(adapterListConduct);
                            return true;
                        }

                    });

                    spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedOption = (String) parent.getItemAtPosition(position);
                            String all = "Tất cả";
                            int spaceIndex = selectedOption.indexOf(" ");
                            String afterSpace = selectedOption.substring(spaceIndex + 1);
                            Log.d(TAG, "onItemSelected: "+afterSpace);
                            List<ListClass> filteredData = new ArrayList<>();
                            for (ListClass item : List) {
                                if (item.getName().substring(0,2).toLowerCase().contains(afterSpace.toLowerCase())
                                        || selectedOption.equals(all)) {
                                    filteredData.add(item);
                                }
                            }
                            adapterListConduct = new AdapterListConduct(filteredData,ListConduct.this, account, semester);
                            RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
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
                              for (ListClass item : List) {
                                      filteredData.add(item);

                              }
                              adapterListConduct = new AdapterListConduct(filteredData, ListConduct.this, account, selectedOption);
                              RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
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
                            String all = "Tất cả";
                            List<ListClass> filteredData = new ArrayList<>();
                            for (ListClass item : List) {
                                if (item.getYear().toLowerCase().contains(selectedOption.toLowerCase())
                                        || selectedOption.equals(all)) {
                                    filteredData.add(item);
                                }
                            }
                            adapterListConduct = new AdapterListConduct(filteredData, ListConduct.this, account, semester);
                            RecyclerView recyclerView = findViewById(R.id.recyclViewConduct);
                            recyclerView.setAdapter(adapterListConduct);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Xử lý trường hợp không có lựa chọn nào được chọn
                        }
                    });
                }
            }
        });

        ImageButton btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());
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

    public void setupSpinnerSemester(Context context) {
        // Tạo danh sách các lựa chọn cho Spinner
        String[] gradeOptions = {"Học kỳ 1", "Học kỳ 2"};

        // Tạo ArrayAdapter với các lựa chọn
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, gradeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        spinnerSemester.setAdapter(adapter);
        spinnerSemester.setSelection(0);

    }

}