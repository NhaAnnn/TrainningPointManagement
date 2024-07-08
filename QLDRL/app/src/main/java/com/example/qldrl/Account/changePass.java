package com.example.qldrl.Account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class changePass extends AppCompatActivity {
    private Account account;
    private Button btnExitAcountMain, btnChangPassWd;
    private EditText editOldPass, editNewPass, editNewPassAgian;
    private LinearLayout layoutErrorPass, layoutErrorPassAgian;
    private ImageView imgBackChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        btnChangPassWd = findViewById(R.id.btnChangPassWd);
        btnExitAcountMain = findViewById(R.id.btnExitAcountMain);
        editOldPass = findViewById(R.id.editOldPass);
        editNewPass = findViewById(R.id.editNewPass);
        editNewPassAgian = findViewById(R.id.editNewPassAgian);
        layoutErrorPass = findViewById(R.id.layoutErrorPass);
        layoutErrorPassAgian = findViewById(R.id.layoutErrorPassAgian);
        imgBackChange = findViewById(R.id.imgBackChange);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");


        btnExitAcountMain.setOnClickListener(v -> onBackPressed());

        imgBackChange.setOnClickListener(v -> onBackPressed());

        btnChangPassWd.setOnClickListener(v -> {
            boolean ivalid = true;
            if(!editOldPass.getText().toString().trim().equals(account.getTkMatKhau())) {
                editOldPass.setError("Mật khẩu cũ không đúng!");
                ivalid = false;
            } else {
                editOldPass.setError(null);
            }

            if(!editNewPassAgian.getText().toString().trim().equals(editNewPass.getText().toString().toString())) {
                editNewPassAgian.setError("Mật khẩu không khớp");
                ivalid = false;
            } else {
                editNewPassAgian.setError(null);
            }

            if(editNewPass.getText().toString().trim().isEmpty()) {
                editNewPass.setError("Không thể bỏ trống");
                ivalid = false;
            } else {
                editNewPass.setError(null);
            }

            if(ivalid) {
                // Lấy reference tới Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Tạo query để tìm document có TK_id = "kkk"
                Query query = db.collection("taiKhoan").whereEqualTo("TK_id", account.getTkID());

                // Thực hiện query và lấy document snapshot
                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            // Cập nhật giá trị trường "TK_MatKhau"
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("TK_MatKhau", editNewPass.getText().toString().trim());

                                            document.getReference().update(updates)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(changePass.this, "sucess", Toast.LENGTH_LONG).show();
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(changePass.this, "fail"+e, Toast.LENGTH_LONG).show();

                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    Log.w("FirestoreQuery", "Lỗi khi truy vấn: ", task.getException());
                                }
                            }
                        });
            }


        });




    }
}