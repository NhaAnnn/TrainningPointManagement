package com.example.qldrl.Homes;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qldrl.Account.account_main;
import com.example.qldrl.Class.ListClass;
import com.example.qldrl.Conduct.ConductInformation;
import com.example.qldrl.Conduct.ListConduct;
import com.example.qldrl.General.Account;
import com.example.qldrl.Mistake.Mistake_Board;
import com.example.qldrl.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainHome extends AppCompatActivity {
    private Account account;
    private TextView txtNameAcc, txtPostion;
    private ImageView imgUpdate, imgUser, imgConduct, imgClass;
    public static String gv = "giáo viên";
    public static String bcs = "ban cán sự";
    public static String hs = "học sinh";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // Tham chieu du lieu
        txtNameAcc = findViewById(R.id.txtNameAcc);
        txtPostion = findViewById(R.id.txtPosition);
        imgUpdate = findViewById(R.id.imgUpdate);
        imgUser = findViewById(R.id.imgUsss);
        imgConduct = findViewById(R.id.imgConduct);
        imgClass = findViewById(R.id.imgClass);

        // Nhan du lieu tu Login
        getIntentData();
        // Chuyen qua activity MainHome
        intentActivity();




        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(MainHome.this, account_main.class);
                intent1.putExtra("account", account);
                startActivity(intent1);
            }
        });

        imgConduct.setOnClickListener(v -> {
            if(account.getTkChucVu().toLowerCase().trim().equals(gv)){
                Intent intent = new Intent(MainHome.this, ListConduct.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else if (account.getTkChucVu().toLowerCase().trim().equals(bcs)
                        || account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                Intent intent = new Intent(MainHome.this, ConductInformation.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainHome.this, ListConduct.class);
                startActivity(intent);
            }

        });

        imgClass.setOnClickListener(v -> {
            if(account.getTkChucVu().toLowerCase().trim().equals(gv)){
                Intent intent = new Intent(MainHome.this, ListClass.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else if (account.getTkChucVu().toLowerCase().trim().equals(bcs)
                    || account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                Toast.makeText(this, "Tài khoản không có quyền truy cập lớp học", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainHome.this, ListClass.class);
                startActivity(intent);
            }
        });
//hello
        getDataSpinner();
    }
    private void intentActivity() {
        imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account.getTkChucVu().toLowerCase().trim().equals(gv) || account.getTkChucVu().toLowerCase().trim().equals(bcs)){
                    Intent intent = new Intent(MainHome.this, Mistake_Board.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                } else if (account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                    Toast.makeText(MainHome.this, "Tài khoản không có quyền truy cập lớp học", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainHome.this, Mistake_Board.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                }


//
//                Intent intent1 = new Intent(MainHome.this, Mistake_Board.class);
//                intent1.putExtra("account", account);
//                startActivity(intent1);
            }
        });
    }

    private void getIntentData() {

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");

        txtNameAcc.setText(account.getTkHoTen());
        txtPostion.setText(account.getTkChucVu());
    }
    public static String[] yearOptions = {"Năm học"};
    public void getDataSpinner() {
        List<String> nienKhoaList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("nienKhoa").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Duyệt qua các tài liệu trong collection
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Lấy giá trị của trường "HK_HocKy"
                String nienKhoa = document.getString("NK_NienKhoa");

                // Thêm giá trị vào danh sách
                nienKhoaList.add(nienKhoa);
            }
            getList(nienKhoaList);
        });
    }

    public void getList(List<String> nienKhoaList) {
        List<String> myList = new ArrayList<>(Arrays.asList(yearOptions));
        myList.addAll(nienKhoaList);
        yearOptions = myList.toArray(new String[0]);
    }
}