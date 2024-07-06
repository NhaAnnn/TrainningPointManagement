package com.example.qldrl.Homes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.qldrl.Account.account_main;
import com.example.qldrl.Class.ListClass;
import com.example.qldrl.Conduct.ConductInformation;
import com.example.qldrl.Conduct.ListConduct;
import com.example.qldrl.General.Account;
import com.example.qldrl.Mistake.Mistake_Board;
import com.example.qldrl.R;
import com.example.qldrl.Report.Report;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainHome_Edited extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private Account account;
    private CardView cardReport, cardUpdate, cardClass, cardPoint;
    private TextView txtNameAcced, txtPositioned;
    public static String gv = "giáo viên";
    public static String bcs = "ban cán sự";
    public static String hs = "học sinh";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_edited);

        bottomNavigationView = findViewById(R.id.botNavi);
        txtPositioned = findViewById(R.id.txtPostioned);
        txtNameAcced = findViewById(R.id.txtNameAcced);
        cardClass = findViewById(R.id.cardClass);
        cardPoint = findViewById(R.id.cardPoint);
        cardReport = findViewById(R.id.cardReport);
        cardUpdate = findViewById(R.id.cardUpdate);

        getIntentData();
        // Chuyen qua activity MainHome
        intentActivity();

        cardReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, Report.class);
            intent.putExtra("account", account);
            startActivity(intent);
        });

        cardPoint.setOnClickListener(v -> {
            if(account.getTkChucVu().toLowerCase().trim().equals(gv)){
                Intent intent = new Intent(MainHome_Edited.this, ListConduct.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else if (account.getTkChucVu().toLowerCase().trim().equals(bcs)
                    || account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                Intent intent = new Intent(MainHome_Edited.this, ConductInformation.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainHome_Edited.this, ListConduct.class);
                intent.putExtra("account", account);

                startActivity(intent);
            }

        });

        cardClass.setOnClickListener(v -> {
            if(account.getTkChucVu().toLowerCase().trim().equals(gv)){
                Intent intent = new Intent(MainHome_Edited.this, ListClass.class);
                intent.putExtra("account", account);
                startActivity(intent);
            } else if (account.getTkChucVu().toLowerCase().trim().equals(bcs)
                    || account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                Toast.makeText(this, "Tài khoản không có quyền truy cập lớp học", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainHome_Edited.this, ListClass.class);
                intent.putExtra("account", account);

                startActivity(intent);
            }
        });

        getDataSpinner();


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.action_home) {

            }else if(id == R.id.action_help) {

            }else {
                Intent intent1 = new Intent(MainHome_Edited.this, account_main.class);
                intent1.putExtra("account", account);
                startActivity(intent1);
            }
            return true;
        });
    }

    private void intentActivity() {
        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account.getTkChucVu().toLowerCase().trim().equals(gv) || account.getTkChucVu().toLowerCase().trim().equals(bcs)){
                    Intent intent = new Intent(MainHome_Edited.this, Mistake_Board.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                } else if (account.getTkChucVu().toLowerCase().trim().equals(hs)) {
                    Toast.makeText(MainHome_Edited.this, "Tài khoản không có quyền truy cập lớp học", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainHome_Edited.this, Mistake_Board.class);
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

        txtNameAcced.setText(account.getTkHoTen());
        txtPositioned.setText(account.getTkChucVu());
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