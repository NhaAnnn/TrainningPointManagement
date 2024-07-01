package com.example.qldrl.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.qldrl.General.Account;
import com.example.qldrl.Homes.MainHome;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editNameCount, editPasswd;
    private TextView txtForgetPass, txtErrorPass, txtErrorNameAccount;
    private ImageView btnLogin, imgTest, imgErroNameAccount, imgErroPass;

    private ConstraintLayout layoutTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Tham chieu
        imgTest = findViewById(R.id.imgTEST);
        layoutTest = findViewById(R.id.layoutTest);
        editPasswd = findViewById(R.id.editPassCount);
        editNameCount = findViewById(R.id.editNameCount);
        btnLogin = findViewById(R.id.imgBtnLogin);
        txtForgetPass = findViewById(R.id.txtForgetPass);
        imgErroPass = findViewById(R.id.imgErroPass);
        imgErroNameAccount = findViewById(R.id.imgErroNameAccount);
        txtErrorPass = findViewById(R.id.txtErrorPass);
        txtErrorNameAccount = findViewById(R.id.txtErrorNameAcount);

        // Dang nhap
        logIn();

    }


    private void checkLogin(String tenTK, String matKhau) {
        CollectionReference taiKhoanRef = db.collection("taiKhoan");

        Query query = taiKhoanRef.whereEqualTo("TK_TenTaiKhoan", tenTK)
                .whereEqualTo("TK_MatKhau", matKhau);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String id = documentSnapshot.getId();
                        String tenTaiKhoan = documentSnapshot.getString("tenTaiKhoan");
                        String hoTen = documentSnapshot.getString("hoTen");
                        String chucVu = documentSnapshot.getString("chucVu");
                        String matKhau = documentSnapshot.getString("matKhau");
                        String ngaySinh = documentSnapshot.getString("ngaySinh");

                        Account account = new Account(id,tenTaiKhoan,matKhau,hoTen,ngaySinh,chucVu);

//                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                        MainHomeFrag mainHomeFrag = new MainHomeFrag();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("account_data", account);
//                        mainHomeFrag.setArguments(bundle);
//                        transaction.replace(R.id.fragment_container, mainHomeFrag).commit();



                        Intent intent = new Intent(Login.this, MainHome.class);
                        intent.putExtra("account",account);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void checkLogin1(String tenTK, String matKhau) {
        CollectionReference taiKhoanRef = db.collection("taiKhoan");

        Query query = taiKhoanRef.whereEqualTo("TK_TenTaiKhoan", tenTK);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String id = documentSnapshot.getString("TK_id");
                        String tenTaiKhoan = documentSnapshot.getString("TK_TenTaiKhoan");
                        String matKhauDB = documentSnapshot.getString("TK_MatKhau");
                        String hoTen = documentSnapshot.getString("TK_HoTen");
                        String chucVu = documentSnapshot.getString("TK_ChucVu");
                        String ngaySinh = documentSnapshot.getString("TK_NgaySinh");

                        if (matKhauDB.equals(matKhau)) {
                            Account account = new Account(id, tenTaiKhoan, ngaySinh, matKhauDB, hoTen, chucVu);
                            Intent intent = new Intent(Login.this, MainHome.class);
                            intent.putExtra("account", account);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void logIn() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenTK = editNameCount.getText().toString();
                String matKhau = editPasswd.getText().toString();

                checkLogin1(tenTK, matKhau);
            }
        });
    }
}