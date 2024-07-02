package com.example.qldrl.Mistake;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.Homes.MainHome;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Mistake_Board extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclVClass;
    private Spinner spCategory;
    private Spinner spYear;
    private SearchView searchClass;
    private AdapterCategory adapterCategory;
    private AdapterClassRom adapterClassRom;
    private List<ClassRom> classRomList = new ArrayList<>();
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_board);

        searchClass = findViewById(R.id.searchClass);
        spCategory = findViewById(R.id.spClass);
        spYear = findViewById(R.id.spYear);
        recyclVClass = findViewById(R.id.recyclVClass);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        Toast.makeText(this, account.getTkChucVu(), Toast.LENGTH_LONG).show();

      //  searchClass();

//        sp(getListCategory(), spCategory);
//        sp(getListYear(), spYear);
        if (account.getTkChucVu().toLowerCase().equals(MainHome.gv) || account.getTkChucVu().toLowerCase().equals(MainHome.bcs)) {
            List<ClassRom> classRomList = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Toast.makeText(this, account.getTkID(), Toast.LENGTH_LONG).show();
            db.collection("giaoVien").whereEqualTo("TK_id", account.getTkID())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                    String idLH = documentSnapshot.getString("LH_id");
                                    Toast.makeText(Mistake_Board.this, idLH, Toast.LENGTH_LONG).show();

                                    getAllClassRoomsTeacher(idLH);

                                }
                            }
                        }
                    });


        } else {
            getAllClassRooms();
        }

        // Log.d("helllo" ,classRomList.size() + "");
//                adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
//                recyclVClass.setAdapter(adapterClassRom);
        recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));

        //Set Spinner
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, getListCategory());
        spCategory.setAdapter(adapterCategory);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = adapterCategory.getItem(position).getNameCategory().toString();
                String all = "Tất cả";
                int spaceIndex = selectedOption.indexOf(" ");
                String afterSpace = selectedOption.substring(spaceIndex + 1);
                Log.d(TAG, "onItemSelected: " + afterSpace);
                List<ClassRom> filteredData = new ArrayList<>();
                for (ClassRom item : classRomList) {
                    if (item.getLhTen().substring(0, 2).toLowerCase().contains(afterSpace.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterClassRom = new AdapterClassRom(filteredData, Mistake_Board.this, account); //truyen vao tuy tung list
                recyclVClass.setAdapter(adapterClassRom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AdapterCategory adapterCategory1 = new AdapterCategory(this, R.layout.layout_item_selected, getListYear());
        spYear.setAdapter(adapterCategory1);
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = adapterCategory1.getItem(position).getNameCategory().toString();
                String all = "Tất cả";
                List<ClassRom> filteredData = new ArrayList<>();
                for (ClassRom item : classRomList) {
                    if (item.getNkNienKhoa().toLowerCase().contains(selectedOption.toLowerCase())
                            || selectedOption.equals(all)) {
                        filteredData.add(item);
                    }
                }
                adapterClassRom = new AdapterClassRom(filteredData, Mistake_Board.this, account); //truyen vao tuy tung list
                recyclVClass.setAdapter(adapterClassRom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




//        getAllClassRooms();
    }

    private void updataRecyc(List<ClassRom> classRomLists) {
        classRomList.addAll(classRomLists);
        Toast.makeText(this, "size lop"+classRomLists.size(), Toast.LENGTH_SHORT).show();
        adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
        recyclVClass.setAdapter(adapterClassRom);
        recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));

    }

//    private void searchClass() {
//        searchClass.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapterClassRom.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapterClassRom.getFilter().filter(newText);
//                return false;
//            }
//        });
//    }

    private  void sp(List<Category> listCategory, Spinner spinner) {
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, listCategory);
        spinner.setAdapter(adapterCategory);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(Mistake_Board.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }
    private List<Category> getListCategory(){
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Tất cả"));
        categoryList.add(new Category("Lop 10"));
        categoryList.add(new Category("Lop 11"));
        categoryList.add(new Category("Lop 12"));
        return categoryList;
    }
    private void getListSemester(Spinner spinner){
        // Lấy tham chiếu đến collection "hocKy"
        CollectionReference hocKyRef = db.collection("hocKy");

        // Tạo một danh sách để lưu trữ các trường HK_HocKy
        List<Category> hocKyList = new ArrayList<>();

        // Lấy dữ liệu từ collection "hocKy"
        hocKyRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua các tài liệu trong collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy giá trị của trường "HK_HocKy"
                            String hocKy = document.getString("HK_HocKy");

                            // Thêm giá trị vào danh sách
                            hocKyList.add(new Category(hocKy));
                        }

                        // Bây giờ bạn có thể sử dụng danh sách hocKyList ở đâu tùy ý
                        // Ví dụ: hiển thị nó trong một Spinner
                        adapterCategory = new AdapterCategory(Mistake_Board.this, R.layout.layout_item_selected, hocKyList);
                        spinner.setAdapter(adapterCategory);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(Mistake_Board.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi, ví dụ: hiển thị thông báo lỗi
                        Log.e("FirestoreError", "Lỗi khi lấy dữ liệu từ Firestore: " + e.getMessage());
                    }
                });

    }

    private List<Category> getListYear(){
        List<Category> yearList = new ArrayList<>();
        yearList.add(new Category("Tất cả"));
        yearList.add(new Category("2020-2021"));
        yearList.add(new Category("2021-2022"));
        yearList.add(new Category("2022-2023"));

        return yearList;
    }

    private void getAllClassRooms() {
        List<ClassRom> classRomList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference taiKhoanRef = db.collection("lop");

        taiKhoanRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                    String lhid = documentSnapshot.getString("LH_id");
                    String lhTen = documentSnapshot.getString("LH_TenLop");
                    String lhGVCN = documentSnapshot.getString("LH_GVCN");
                    String nkNienKhoan = documentSnapshot.getString("NK_NienKhoa");


                    ClassRom classRom = new ClassRom( lhid , lhTen, lhGVCN, nkNienKhoan);
                    classRomList.add(classRom);
                }
                updataRecyc(classRomList);
               // Log.d("helllo" ,classRomList.size() + "");
//                adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
//                recyclVClass.setAdapter(adapterClassRom);
//
//                recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));
            } else {
                //Toast.makeText(getApplicationContext(), "Error retrieving accounts", Toast.LENGTH_SHORT).show();
            }
        });
      //  Log.d("helllo" ,classRomList.size() + "");


    }
    private void getAllClassRoomsTeacher(String idlh) {
        List<ClassRom> classRomList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       db.collection("lop").whereEqualTo("LH_id", idlh)
               .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                    String lhid = documentSnapshot.getString("LH_id");
                    String lhTen = documentSnapshot.getString("LH_TenLop");
                    String lhGVCN = documentSnapshot.getString("LH_GVCN");
                    String nkNienKhoan = documentSnapshot.getString("NK_NienKhoa");


                    ClassRom classRom = new ClassRom( lhid , lhTen, lhGVCN, nkNienKhoan);
                    classRomList.add(classRom);
                }
                updataRecyc(classRomList);
                // Log.d("helllo" ,classRomList.size() + "");
//                adapterClassRom = new AdapterClassRom(classRomList, Mistake_Board.this, account); //truyen vao tuy tung list
//                recyclVClass.setAdapter(adapterClassRom);
//
//                recyclVClass.setLayoutManager(new GridLayoutManager(Mistake_Board.this, 1));
            } else {
                //Toast.makeText(getApplicationContext(), "Error retrieving accounts", Toast.LENGTH_SHORT).show();
            }
        });
        //  Log.d("helllo" ,classRomList.size() + "");


    }

}