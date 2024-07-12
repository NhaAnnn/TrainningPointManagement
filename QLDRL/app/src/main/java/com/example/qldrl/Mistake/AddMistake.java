package com.example.qldrl.Mistake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMistake extends AppCompatActivity {
    private CardView cardAddMistake, cardAddType;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mistake);

        back = findViewById(R.id.imgBackAddMistake);
        back.setOnClickListener(v -> {

            onBackPressed();
        });

        cardAddMistake = findViewById(R.id.cardAddMistake);
        cardAddType = findViewById(R.id.cardAddType);

        cardAddType.setOnClickListener(v -> openDinalogAddType(Gravity.CENTER));

        cardAddMistake.setOnClickListener(v -> openDinalogAddMis(Gravity.CENTER));

    }

    @SuppressLint("NotifyDataSetChanged")
    private void openDinalogAddType(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_type);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        EditText editCodeType = dialog.findViewById(R.id.editCodeType);

        EditText editNameType = dialog.findViewById(R.id.editNameType);

        Button btnAddType = dialog.findViewById(R.id.btnAddType);
        Button btnExitAddType = dialog.findViewById(R.id.btnExitAddType);


        btnExitAddType.setOnClickListener(v -> dialog.dismiss());



        btnAddType.setOnClickListener( v -> {
            //Toast.makeText(AddMistake.this, "ckhm   " + editCodeType.getText().toString().trim(), Toast.LENGTH_LONG).show();

            FirebaseFirestore db =FirebaseFirestore.getInstance();
            db.collection("loaiViPham")
                    .whereEqualTo("LVP_id", editCodeType.getText().toString().trim())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    // LVP_id không trùng lặp, lưu dữ liệu mới
                                 //   Toast.makeText(AddMistake.this, "Lỗi, eeeehạm", Toast.LENGTH_LONG).show();

                                    editCodeType.setError("Mã đã tồn tại!");
                                } else {
                                    // LVP_id đã tồn tại, hiển thị thông báo lỗi
                                //    Toast.makeText(AddMistake.this, "Lỗi, kkkkkhạm", Toast.LENGTH_LONG).show();

                                    editCodeType.setError(null);
                                    boolean isValid = true;
                                    if(editCodeType.getText().toString().trim().isEmpty()) {
                                        editCodeType.setError("Không thể bỏ trống");
                                        isValid =false;
                                    } else {
                                        editCodeType.setError(null);
                                    }

                                    if(editNameType.getText().toString().trim().isEmpty()) {
                                        editNameType.setError("Không thể bỏ trống!");
                                        isValid = false;
                                    } else {
                                        editNameType.setError(null);
                                    }

                                    if(isValid) {
                                        DocumentReference newDocRef = db.collection("loaiViPham").document(editCodeType.getText().toString().trim());

                                        Map<String, Object> newLoaiViPham = new HashMap<>();
                                        String newLVP_id = editCodeType.getText().toString().trim(); // id mới cần thêm
                                        newLoaiViPham.put("LVP_id", newLVP_id);
                                        newLoaiViPham.put("LVP_TenLoaiViPham", editNameType.getText().toString().trim());
                                        // Lưu dữ liệu vào Firestore
                                        newDocRef.set(newLoaiViPham)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(AddMistake.this, "Tạo Loại Vi Phạm Mới Thành Công!", Toast.LENGTH_LONG).show();
                                                        mistake_detail_edit.adapterMistakeDetailED.notifyDataSetChanged();
                                                        AdapterMistakeDetailED.adapterMistakeED.notifyDataSetChanged();
                                                        dialog.dismiss();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AddMistake.this, "Tạo Loại Vi Phạm Mới Thất Bại!"+e, Toast.LENGTH_LONG).show();

                                                    }
                                                });


                                    }
                                }
                            } else {
                                Toast.makeText(AddMistake.this, "Lỗi, Kiểm tra loại vi phạm", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        });
        dialog.show();
    }





    private void saveVPData(String vpId, Map<String, Object> vpData) {
        // Tạo một DocumentReference để tham chiếu đến document mới
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        DocumentReference newDocRef = db.collection("viPham").document(vpId);

        // Lưu dữ liệu vào Firestore
        newDocRef.set(vpData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddMistake.this, "Tạo Loại Vi Phạm Mới Thành Công!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddMistake.this, "Tạo Loại Vi Phạm Mới Thất Bại!"+e, Toast.LENGTH_LONG).show();

                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void openDinalogAddMis(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_mistake);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);



        Spinner spTypeMistake = dialog.findViewById(R.id.spTypeMistake);
        getListSemester(spTypeMistake);


        final String[] lhNK = new String[1];

        spTypeMistake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = (String) parent.getItemAtPosition(position);
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                // Sử dụng selectedCategory object thay vì cast nó thành String
                String categoryName = selectedCategory.getNameCategory();
                //  Toast.makeText(context, "g"+categoryName, Toast.LENGTH_LONG).show();

                lhNK[0] = categoryName;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        EditText editNameMistake = dialog.findViewById(R.id.editNameMistake);


        EditText editPointMistake = dialog.findViewById(R.id.editPointMistake);


        Button btnAddMistake = dialog.findViewById(R.id.btnAddMistake);
        Button btnExitAddMistake = dialog.findViewById(R.id.btnExitAddMistake);




        btnExitAddMistake.setOnClickListener(v -> dialog.dismiss());



        btnAddMistake.setOnClickListener( v -> {
            boolean isValid = true;

            if(editNameMistake.getText().toString().trim().isEmpty()) {
                editNameMistake.setError("Không thể bỏ trống!");
                isValid = false;
            }else {
                editNameMistake.setError(null);
            }

            if(editPointMistake.getText().toString().trim().isEmpty()) {
                editPointMistake.setError("Không thể bỏ trống!");
                isValid = false;
            }else {
                editPointMistake.setError(null);
            }

            if(isValid) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();




                Map<String, Object> newVP = new HashMap<>();
                newVP.put("VP_TenViPham",editNameMistake.getText().toString().trim());
                newVP.put("VP_DiemTru", editPointMistake.getText().toString().trim());

                db.collection("loaiViPham")
                        .whereEqualTo("LVP_TenLoaiViPham", lhNK[0])
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    String lvpid = doc.getString("LVP_id");
                                    newVP.put("LVP_id", lvpid);

                                    db.collection("viPham")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    int sl = queryDocumentSnapshots.size();
                                                    newVP.put("VP_id", lvpid+sl);

                                                    saveVPData(lvpid+sl, newVP);

                                                }
                                            });
                                }
                            }
                        });

                mistake_detail_edit.adapterMistakeDetailED.notifyDataSetChanged();
                dialog.dismiss();
            }




        });
        dialog.show();
    }


    private void getListSemester(Spinner spinner) {
        // Lấy tham chiếu đến collection "hocKy"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hocKyRef = db.collection("loaiViPham");

        // Tạo một danh sách để lưu trữ các trường HK_HocKy
        List<MistakeType> hocKyList = new ArrayList<>();
        List<Category> tenVPs = new ArrayList<>();

        // Lấy dữ liệu từ collection "hocKy"
        hocKyRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua các tài liệu trong collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy giá trị của trường "NK_NienKhoa"
                            String lvpId = document.getString("LVP_id");
                            String tenLVP = document.getString("LVP_TenLoaiViPham");

                            // Thêm giá trị vào danh sách
                            hocKyList.add(new MistakeType(lvpId,tenLVP));
                            tenVPs.add(new Category(tenLVP));
                        }

                        // Bây giờ bạn có thể sử dụng danh sách hocKyList ở đâu tùy ý
                        // Ví dụ: hiển thị nó trong một Spinner
                        AdapterCategory adapterCategory = new AdapterCategory(AddMistake.this, R.layout.layout_item_selected, tenVPs);
                        spinner.setAdapter(adapterCategory);



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