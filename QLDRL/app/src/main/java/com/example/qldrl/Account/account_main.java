package com.example.qldrl.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qldrl.General.Account;
import com.example.qldrl.Homes.MainHome;
import com.example.qldrl.Login.Login;
import com.example.qldrl.Mistake.AddMistake;
import com.example.qldrl.Mistake.mistake_detail_edit;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class account_main extends AppCompatActivity {
    private TextView txtVNamePosition, txtBrithUser, txtGenderUser, txtClassUser, txtCodeUser, txtVNameUser;
    private Button btnListAcc, btnLogOut;
    private Account account;
    private String tkID;
    private LinearLayout layoutChagePass, layoutMistakeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main);

        txtVNamePosition = findViewById(R.id.txtVNamePosition);
        txtBrithUser = findViewById(R.id.txtBrithUser);
        txtGenderUser = findViewById(R.id.txtGenderUser);
        txtClassUser = findViewById(R.id.txtClassUser);
        txtCodeUser = findViewById(R.id.txtCodeUser);
        txtVNameUser = findViewById(R.id.txtVNameUser);
        btnListAcc = findViewById(R.id.btnListAcc);
        btnLogOut = findViewById(R.id.btnLogOut);
        layoutChagePass = findViewById(R.id.layoutChangePass);
        layoutMistakeEdit = findViewById(R.id.layoutMistakeEdit);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        tkID = account.getTkID();

        if(!account.getTkChucVu().toLowerCase().equals("ban giám hiệu")) {
            btnListAcc.setVisibility(View.GONE);
        }

        getDataPersonal();

        btnListAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(account_main.this, listAcc.class);

                startActivity(intent1);
            }
        });

        btnLogOut.setOnClickListener(v -> {
            Intent intent1 = new Intent(account_main.this, Login.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            finish();
        });



        layoutChagePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(account_main.this, changePass.class);
                intent1.putExtra("account", account);
                startActivity(intent1);
            }
        });

        layoutMistakeEdit.setOnClickListener( v -> {
            Intent intent1 = new Intent(account_main.this, mistake_detail_edit.class);
            startActivity(intent1);
        });
    }

    private void setInfo(String namUser, String codeUser, String gioiTinh, String ngaySinh, String chucVu){
        txtVNamePosition.setText(chucVu);
        txtBrithUser.setText(ngaySinh);
        txtGenderUser.setText(gioiTinh);
        txtCodeUser.setText(codeUser);
        txtVNameUser.setText(namUser);
    }

    private void setClassName(String className) {
        txtClassUser.setText(className);
    }

    private void getDataPersonal() {
        // Kết nối với Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy thông tin giáo viên
        CollectionReference giaoVienRef = db.collection("giaoVien");
        Query giaoVienQuery = giaoVienRef.whereEqualTo("TK_id", tkID);
        giaoVienQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        // Tìm thấy giáo viên, không cần lọc qua hocSinh
                        for (DocumentSnapshot document : querySnapshot) {
                            // Lấy thông tin giáo viên và làm gì đó với chúng
                            String tenGiaoVien = document.getString("GV_HoTen");
                            String gioiTinh = document.getString("GV_GioiTinh");
                            String ngaysinh = document.getString("GV_NgaySinh");
                            String lhID = document.getString("LH_id");

                            setInfo(account.getTkHoTen(),
                                    account.getTkTenTK(),
                                    gioiTinh,
                                    ngaysinh,
                                    "Giáo viên");

                            CollectionReference lopRef = db.collection("lop");
                            Query lopQuery = lopRef.whereEqualTo("LH_id", lhID);
                            lopQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            // Tìm thấy lớp học
                                            for (DocumentSnapshot document : querySnapshot) {
                                                // Lấy LH_TenLop
                                                String LH_TenLop = document.getString("LH_TenLop");
                                                setClassName(LH_TenLop);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        // Không tìm thấy giáo viên, lọc qua hocSinh
                        CollectionReference hocSinhRef = db.collection("hocSinh");
                        Query hocSinhQuery = hocSinhRef.whereEqualTo("TK_id", tkID);
                        hocSinhQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (!querySnapshot.isEmpty()) {
                                        // Tìm thấy học sinh
                                        for (DocumentSnapshot document : querySnapshot) {
                                            // Lấy thông tin học sinh và làm gì đó với chúng
                                            String tenHocSinh = document.getString("HS_HoTen");
                                            String gioiTinh = document.getString("HS_GioiTinh");
                                            String chucVu = document.getString("HS_ChucVu");
                                            String ngasinh = document.getString("HS_NgaySinh");
                                            String lhid = document.getString("LH_id");

                                            setInfo(account.getTkHoTen(),
                                                    account.getTkTenTK(),
                                                    gioiTinh,
                                                    ngasinh,
                                                    chucVu);

                                            CollectionReference lopRef = db.collection("lop");
                                            Query lopQuery = lopRef.whereEqualTo("LH_id", lhid);
                                            lopQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        if (!querySnapshot.isEmpty()) {
                                                            // Tìm thấy lớp học
                                                            for (DocumentSnapshot document : querySnapshot) {
                                                                // Lấy LH_TenLop
                                                                String LH_TenLop = document.getString("LH_TenLop");
                                                                setClassName(LH_TenLop);

                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        // Không tìm thấy cả giáo viên và học sinh
                                        // Xử lý trường hợp đặc biệt này
                                        txtVNameUser.setText(account.getTkHoTen());
                                        txtBrithUser.setText(   account.getTkNgaySinh());
                                        txtVNamePosition.setText(account.getTkChucVu());
                                        txtCodeUser.setText(account.getTkTenTK());
                                        txtClassUser.setText(" ");
                                        txtGenderUser.setText("");

                                    }
                                } else {
                                    // Xử lý lỗi
                                }
                            }
                        });
                    }
                } else {
                    // Xử lý lỗi
                }
            }
        });
    }
}