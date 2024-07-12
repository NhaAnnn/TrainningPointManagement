package com.example.qldrl.Mistake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Mistake_Personal extends AppCompatActivity {
    public RecyclerView recycPersonal;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static AdaperPersonal adaperPersonal;
    private SearchView searchPersonal;
    private String className, classID;
    TextView txtVEditMistakeClass;
   private Account account;
    private ImageView imgViewMistake, imgEditMistake,imgBackMistakePersonl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_personal);

        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        classID = intent.getStringExtra("classID");
        account = (Account) intent.getSerializableExtra("account");

      //  Toast.makeText(this, account.getTkID()+"333",Toast.LENGTH_LONG).show();


        txtVEditMistakeClass = findViewById(R.id.txtVEditMistakeClass);

        txtVEditMistakeClass.setText("Cập nhật vi phạm lớp " + className);
        imgBackMistakePersonl = findViewById(R.id.imgBackMistakePersonl);


        getAllStudents();
//
//        imgEditMistake = findViewById(R.id.imgEditMistake);
//        imgViewMistake = findViewById(R.id.imgViewMistake);














        searchPersonal = findViewById(R.id.searchPersonal);
        searchPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycPersonal.setVisibility(View.VISIBLE);

            }
        });
        searchPersonal.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adaperPersonal.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaperPersonal.getFilter().filter(newText);
                return false;
            }
        });



        recycPersonal = findViewById(R.id.recycPersonal);

        // List <Integer> listbearimgids = new arrayList<> ();
        // List <String> listbearNames = new arrayList<> ();
        //for(Bear bear : bearList) {listbearimgids.add(bear.getImgid())
        //listbearNames.add(bear.getName() }

        imgBackMistakePersonl.setOnClickListener(v -> onBackPressed());

    }


    private void getAllStudents() {
        // Lấy tham chiếu đến collection "students"
        CollectionReference studentsRef = db.collection("hocSinh");

// Tạo một danh sách để lưu trữ các học sinh
        List<Student> studentList = new ArrayList<>();

// Tạo một truy vấn để lấy các học sinh có LH_id = "10a1"
        Query query = studentsRef.whereEqualTo("LH_id", classID);

// Lấy dữ liệu từ collection "students"
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Lặp qua các tài liệu trả về
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            // Tạo một đối tượng Student và thêm vào danh sách
                            String hsid = document.getString("HS_id");
                            String hsTen = document.getString("HS_HoTen");
                            String hsNgaySinh = document.getString("HS_NgaySinh");
                            String hsGioiTinh = document.getString("HS_GioiTinh");
                            String hsChucVu = document.getString("HS_ChucVu");
                            String lhid = document.getString("LH_id");
                            String tkid = document.getString("TK_id");



                            Student student = new Student(hsChucVu,hsTen, hsNgaySinh, hsGioiTinh, hsid,lhid,tkid);
                            studentList.add(student);
                        }

                        // Bây giờ bạn có thể sử dụng danh sách studentList ở đâu tùy ý
                        // Ví dụ: hiển thị thông tin của các học sinh
                        adaperPersonal = new AdaperPersonal(studentList, Mistake_Personal.this, account); //truyen vao tuy tung list
                        recycPersonal.setAdapter(adaperPersonal);

                        recycPersonal.setLayoutManager(new GridLayoutManager(Mistake_Personal.this, 1));
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
}