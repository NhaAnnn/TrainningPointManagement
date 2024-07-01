package com.example.qldrl.Class;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterSpinner;
import com.example.qldrl.Homes.MainHome;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListClass extends AppCompatActivity implements Serializable {
    private String id, name, nameTeacher, year; // Lớp
    private AdapterListClass adapterListClass;
    private LinearLayout btnCreateClasses,btnCreateOneClass;
    private List<ListClass> listClasses = new ArrayList<>();
    public static Dialog dialog;
    private Account account;
    private AdapterSpinner adapterSpinnerHelper;
    public ListClass() {
    }

    public ListClass(String id, String name, String nameTeacher, String year) {
        this.id = id;
        this.name = name;
        this.nameTeacher = nameTeacher;
        this.year = year;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_class);

        SearchView searchView = findViewById(R.id.searchView);
        Spinner spinnerGrade = findViewById(R.id.spinner_grade);
        Spinner spinnerYear = findViewById(R.id.spinner_year);

        // Tạo instance của GradeSpinnerHelper và thiết lập Spinner
        adapterSpinnerHelper = new AdapterSpinner(spinnerGrade, spinnerYear);
        adapterSpinnerHelper.setupSpinnerGrade(this);
        adapterSpinnerHelper.setupSpinnerYear(this);
//        adapterSpinnerHelper.setupSpinnerYear(this);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("giaoVien").whereEqualTo("TK_id",account.getTkID())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = (String) document.getString("LH_id");

                            if(account.getTkChucVu().toLowerCase().trim().equals(MainHome.gv)){
                                getDataClassForTeacher(id);
                            }else {
                                getData();
                            }
                        }
                    }
                });

        EventBus.getDefault().register(this);


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
                adapterListClass = new AdapterListClass(filteredData,ListClass.this);
                RecyclerView recyclerView = findViewById(R.id.recyclViewClass);
                recyclerView.setAdapter(adapterListClass);
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
                adapterListClass = new AdapterListClass(filteredData,ListClass.this);
                RecyclerView recyclerView = findViewById(R.id.recyclViewClass);
                recyclerView.setAdapter(adapterListClass);
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
                adapterListClass = new AdapterListClass(filteredData,ListClass.this);
                RecyclerView recyclerView = findViewById(R.id.recyclViewClass);
                recyclerView.setAdapter(adapterListClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có lựa chọn nào được chọn
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
                adapterListClass = new AdapterListClass(filteredData,ListClass.this);
                RecyclerView recyclerView = findViewById(R.id.recyclViewClass);
                recyclerView.setAdapter(adapterListClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý trường hợp không có lựa chọn nào được chọn
            }
        });
        btnCreateClasses = findViewById(R.id.btnCreateClasses);
        btnCreateClasses.setOnClickListener(v -> {
            createFileClass();

        });
        btnCreateOneClass = findViewById(R.id.btnCreateOneClass);
        btnCreateOneClass.setOnClickListener(v -> {
            createOneClass(this, listClasses);
        });

        ImageButton btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> onBackPressed());

    }
    public void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                    classroomList = new ArrayList<>();
                List<ListClass> List = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = (String) document.getString("LH_id");
                    String classroomName = (String) document.getString("LH_TenLop");
                    String teacherName = (String) document.getString("LH_GVCN");
                    String year = (String) document.getString("NK_NienKhoa");
                    String term = (String) document.getString("HK_HocKi");
                    ListClass data = new ListClass(id, classroomName, teacherName, year);
                    List.add(data);
                }
                Log.d(TAG, "Success: " + List.size());
                updateData(List);
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
                updateData(List);
            }
        });
    }
    private void updateData(List<ListClass> List) {
        listClasses.addAll(List);
        adapterListClass = new AdapterListClass(List, ListClass.this);
        RecyclerView recyclerView = findViewById(R.id.recyclViewClass);
        recyclerView.setAdapter(adapterListClass);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký EventBus
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileClassUpdated(FileClassUpdatedEvent event) {
        listClasses.addAll(event.updatedClass);
        adapterListClass.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void createFileClass(){
        dialog = new Dialog(ListClass.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_fragment_none);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        ViewPager2 viewPager2 = dialog.findViewById(R.id.viewPager);
        FragmentDialog fragmentDialog = new FragmentDialog(this);;
        viewPager2.setAdapter(fragmentDialog);

        dialog.show();
    }
    public void createOneClass (Context context, List<ListClass> list){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialog_create_class);

        // Làm mờ khu vực xung quanh
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_backgroud));
        dialog.getWindow().setDimAmount(0.5f);


        // Xử lý sự kiện đóng dialog
        Button btnClose = dialog.findViewById(R.id.cancelButton);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        Button btnSave = dialog.findViewById(R.id.saveClass);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText diaClassName = dialog.findViewById(R.id.diaCreateClassName);
                EditText diaTeacherName = dialog.findViewById(R.id.diaCreateTeacherName);
                EditText diaYear = dialog.findViewById(R.id.diaCreateYear);
                String cName = diaClassName.getText().toString();
                String tName = diaTeacherName.getText().toString();
                String y = diaYear.getText().toString();
                String id = (cName.toLowerCase()+y).trim();
                Map<String, Object> newData = new HashMap<>();
                newData.put("LH_id",id);
                newData.put("LH_TenLop", cName);
                newData.put("LH_GVCN", tName);
                newData.put("NK_NienKhoa",y);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                ListClass newClass = new ListClass(id, cName, tName, y);
                db.collection("lop").whereEqualTo("LH_id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean isValid = false;
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                diaClassName.setError("Lớp đã tồn tại!");
                            }
                            else {
                                diaClassName.setError(null);
                                isValid = true;
                                // Kiểm tra trường "className"
                                if (diaClassName.getText().toString().isEmpty()) {
                                    diaClassName.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    diaClassName.setError(null);
                                }

                                // Kiểm tra trường "diaTeacherName"
                                if (diaTeacherName.getText().toString().isEmpty()) {
                                    diaTeacherName.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    diaTeacherName.setError(null);
                                }
                                // Kiểm tra trường "year"
                                if (diaYear.getText().toString().isEmpty()) {
                                    diaYear.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    diaYear.setError(null);
                                }
                                if(isValid) {
                                    db.collection("lop").document(id).set(newData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Thêm lớp học mới thành công", Toast.LENGTH_SHORT).show();
                                            list.add(newClass);
                                            dialog.dismiss();
                                            adapterListClass.notifyItemInserted(list.size() -1);
//
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Thêm lớp học mới thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

            }
        });

        // Hiện dialog
        dialog.show();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameTeacher() {
        return nameTeacher;
    }

    public void setNameTeacher(String nameTeacher) {
        this.nameTeacher = nameTeacher;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

}