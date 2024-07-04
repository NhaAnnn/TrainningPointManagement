package com.example.qldrl.Mistake;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.General.Student;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class mistake_edit extends AppCompatActivity {
    private ImageView imgCalen;
    private TextView txtDate, txtNameMistake, txtNamePersonl;
    private AdapterCategory adapterCategory;
    private AdapterClassRom adapterClassRom;
    private Spinner spSubject;
    private LinearLayout layoutTest, layoutErrorTerm;
    private RecyclerView recyctest;
    private Account account;
    private Student student;
    private Mistake mistake;
    private RadioGroup rdGTerm;
    String mistakeName, date, subject, LVPid, VPid, hanhKiem;
    Button btnSaveMistake, btnExitEditMistake;
    int drlhk,diemTRu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_edit);
        imgCalen = findViewById(R.id.imgCalend);

        txtDate = findViewById(R.id.txtVDate);
        date();

        spSubject = findViewById(R.id.spSubject);
        setSpSubject();
        layoutErrorTerm = findViewById(R.id.layoutErrorTerm);
        txtNameMistake = findViewById(R.id.txtNameMistake);
        txtNamePersonl = findViewById(R.id.txtNamePersonl);
        rdGTerm = findViewById(R.id.rdGTerm);

        btnExitEditMistake = findViewById(R.id.btnExitEditMistake);

        btnExitEditMistake.setOnClickListener(v -> {
            finish();
        });


        Intent intent = getIntent();
        mistakeName = intent.getStringExtra("mistakeName");
        String namePersonl = intent.getStringExtra("namePersonl");
        account = (Account) intent.getSerializableExtra("account");
        student = (Student) intent.getSerializableExtra("student");
        mistake = (Mistake) intent.getSerializableExtra("mistake") ;
       // Toast.makeText(this, mistake.getVpDiemtru()+"000111111", Toast.LENGTH_SHORT).show();

        txtNameMistake.setText(mistakeName);
        txtNamePersonl.setText(namePersonl);
        Toast.makeText(this,student.getHsID(),Toast.LENGTH_LONG).show();

      //  Toast.makeText(this, "hloo HK"+hkyd,Toast.LENGTH_LONG).show();
        getIDLVPVP();

        btnSaveMistake = findViewById(R.id.btnSavaMistake);


        btnSaveMistake.setOnClickListener(v -> {
            int selectedRadioButtonId = rdGTerm.getCheckedRadioButtonId();
            boolean isValid = true;
            if(selectedRadioButtonId == -1) {
                layoutErrorTerm.setVisibility(View.VISIBLE);
                isValid = false;

            } else {
                layoutErrorTerm.setVisibility(View.GONE);
            }
            if(isValid) {
                updateHK();
                saveMistake();
            }

        });





    }
    public void date(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dayOfWeekString = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
            String dateString = dayOfWeekString + " " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

            txtDate.setText(dateString);
        };

        imgCalen.setOnClickListener(v -> {
            new DatePickerDialog(this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Cập nhật TextViews với ngày và thứ hiện tại
        String currentDayOfWeekString = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
        String currentDateString = currentDayOfWeekString + " " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
        txtDate.setText(currentDateString);
        date = currentDateString;
    }

    private void setSpSubject() {
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, getListSubject());
        spSubject.setAdapter(adapterCategory);
        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mistake_edit.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
                subject = adapterCategory.getItem(position).getNameCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private List<Category> getListSubject() {
        List <Category> listSubject = new ArrayList<>();
        listSubject.add(new Category(""));
        listSubject.add(new Category("Ngữ văn"));
        listSubject.add(new Category("Toán"));
        listSubject.add(new Category("Ngoại ngữ"));
        listSubject.add(new Category("Giáo dục thể chất"));
        listSubject.add(new Category("Giáo dục quốc phòng và an ninh"));
        listSubject.add(new Category("Hoạt động trải nghiệm, hướng nghiệp"));
        listSubject.add(new Category("Nội dung giáo dục của địa phương"));
        listSubject.add(new Category("Lý"));
        listSubject.add(new Category("Hóa"));
        listSubject.add(new Category("Sinh"));
        listSubject.add(new Category("Sử"));
        listSubject.add(new Category("Địa"));
        listSubject.add(new Category("Giáo dục kinh tế và pháp luật"));
        listSubject.add(new Category("Tin học"));
        listSubject.add(new Category("Công nghệ"));
        listSubject.add(new Category("Nghệ thuật"));
        return listSubject;

    }

    private void updateHK() {
        //Toast.makeText(this, "hkodjđ",Toast.LENGTH_LONG).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int selectedRadioButtonId = rdGTerm.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String selectedValue = selectedRadioButton.getText().toString();
            if(selectedValue.toLowerCase().equals("học kỳ 1"))
            {

                CollectionReference hanhKiemRef = db.collection("hanhKiem");
                Query query = hanhKiemRef.whereEqualTo("HKM_id", "HKI"+student.getHsID());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String hkDRl = documentSnapshot.getString("HKM_DiemRenLuyen");
                                int drl = Integer.parseInt(hkDRl);

                                getDRL(drl);
                                diemTRu = Integer.parseInt(mistake.getVpDiemtru());
                                drlhk -= diemTRu;

                                if(drlhk >= 90 && drlhk <= 100) {
                                    hanhKiem = "Tốt";
                                } else if (drlhk >= 70 && drlhk <= 89) {
                                    hanhKiem = "Khá";
                                }else if(drlhk >= 50 && drlhk <= 69){
                                    hanhKiem = "Trung bình";
                                }else {
                                    hanhKiem = "Yếu";
                                }


                                db.collection("hanhKiem")
                                        .whereEqualTo("HKM_id", "HKI"+student.getHsID())
                                        .get()
                                        .addOnSuccessListener(querySnapshot1 -> {
                                            if (!querySnapshot1.isEmpty()) {
                                                DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                                Map<String, Object> updates = new HashMap<>();
                                                updates.put("HKM_DiemRenLuyen", drlhk+"");
                                                updates.put("HKM_HanhKiem", hanhKiem);
                                                docRef.update(updates)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(mistake_edit.this, "Cập nhật thành công!", Toast.LENGTH_LONG).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(mistake_edit.this, "Cập nhật thất bại, Vui lòng kiểm tra lại!" + e, Toast.LENGTH_LONG).show();

                                                        });
                                            } else {
                                                Toast.makeText(mistake_edit.this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // Lỗi khi truy vấn Firestore
                                        });
                            } else {
                                //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                Log.d("diem tru lay ra da ", diemTRu+"");
                Log.d("diem ren luyen lay ra da xu ly", drlhk+"");

            }
            else
            {
                CollectionReference hanhKiemRef = db.collection("hanhKiem");
                Query query = hanhKiemRef.whereEqualTo("HKM_id", "HKII"+student.getHsID());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()) {
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String hkDRl = documentSnapshot.getString("HKM_DiemRenLuyen");
                                int drl = Integer.parseInt(hkDRl);
                                getDRL(drl);
                                diemTRu = Integer.parseInt(mistake.getVpDiemtru());

                                drlhk -= diemTRu;

                                if(drlhk >= 90 && drlhk <= 100) {
                                    hanhKiem = "Tốt";
                                } else if (drlhk >= 70 && drlhk <= 89) {
                                    hanhKiem = "Khá";
                                }else if(drlhk >= 50 && drlhk <= 69){
                                    hanhKiem = "Trung bình";
                                }else {
                                    hanhKiem = "Yếu";
                                }


                                db.collection("hanhKiem")
                                        .whereEqualTo("HKM_id", "HKII"+student.getHsID())
                                        .get()
                                        .addOnSuccessListener(querySnapshot1 -> {
                                            if (!querySnapshot1.isEmpty()) {
                                                DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                                Map<String, Object> updates = new HashMap<>();
                                                updates.put("HKM_DiemRenLuyen", drlhk+"");
                                                updates.put("HKM_HanhKiem", hanhKiem);
                                                docRef.update(updates)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(mistake_edit.this, "Cập nhật thành công!", Toast.LENGTH_LONG).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(mistake_edit.this, "Cập nhật thất bại, Vui lòng kiểm tra lại!" + e, Toast.LENGTH_LONG).show();
                                                        });
                                            } else {
                                                Toast.makeText(mistake_edit.this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // Lỗi khi truy vấn Firestore
                                        });
                            } else {
                                //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }
    }
    private void saveMistake() {

        // Kết nối với Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference luotViPhamRef = db.collection("luotViPham");

        AtomicInteger count = new AtomicInteger(0);

        luotViPhamRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    count.set(queryDocumentSnapshots.size());
                   int luotViPham = count.get();
                    DocumentReference docRef = db.collection("luotViPham").document("LTVP"+luotViPham);
                    String  hkyd ="";
                    int selectedRadioButtonId = rdGTerm.getCheckedRadioButtonId();
                    if (selectedRadioButtonId != -1) {
                        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                        if (selectedRadioButton != null) {
                            hkyd = selectedRadioButton.getText().toString();
                            // Tiếp tục với code khác
                        } else {
                            // Xử lý trường hợp selectedRadioButton là null
                            // Ví dụ: hiển thị thông báo lỗi
                        }
                    }
// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> data = new HashMap<>();
                    data.put("HS_id", student.getHsID());
                    data.put("VP_id", VPid);
                    data.put("TK_id", account.getTkID());
                    data.put("LTVP_ThoiGian", subject+"-" +date);
                    data.put("LTVP_id", "LTVP"+luotViPham);
                    data.put("HK_HocKy", hkyd);

// Lưu dữ liệu vào Firestore
                    docRef.set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mistake_edit.this, "Cập nhật vi phạm thành công!",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mistake_edit.this, "Cập nhật vi phạm thất bại!"+ e,Toast.LENGTH_LONG).show();

                                }
                            });

                });

        finish();
    }
    private void getDRL(int drl) {
        drlhk = drl;
    }


    private  void getIDLVPVP() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("viPham");

        collectionRef.whereEqualTo("VP_TenViPham", mistakeName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        LVPid = document.getString("LVP_id");
                        VPid = document.getString("VP_id");

                        // Làm gì với lvpId và vpId ở đây
                     //  Toast.makeText(this, LVPid, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                   // Toast.makeText(this, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}