package com.example.qldrl.Mistake;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qldrl.Account.changePass;
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

public class MistakeUpdateMistake extends AppCompatActivity {
    private TextView txtVDate, txtNameMistake, txtNamePersonl;
    private AdapterCategory adapterCategory;
    private RadioGroup rdGTerm;
    private Button btnExitUpdateMistake, btnUpdateMistake;
    private ImageView imgCalend, imgBackMistakeUpdate;
    private Spinner spSubject;
    Account account;
    Mistakes mistakes;
    Student student;
    String date, hanhKiem = "", subject;
    int sttsub, diemTru;
    RadioButton rdTerm2, rdTerm1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_update_mistake);

        imgBackMistakeUpdate = findViewById(R.id.imgBackMistakeUpdate);
        imgBackMistakeUpdate.setOnClickListener(v -> onBackPressed());

        txtNameMistake = findViewById(R.id.txtNameMistake);
        txtVDate = findViewById(R.id.txtVDate);
        txtNamePersonl = findViewById(R.id.txtNamePersonl);
        rdGTerm = findViewById(R.id.rdGTerm);
        btnExitUpdateMistake = findViewById(R.id.btnExitUpdateMistake);
        btnUpdateMistake = findViewById(R.id.btnUpdateMistake);
        imgCalend = findViewById(R.id.imgCalend);
        spSubject = findViewById(R.id.spSubject);
        rdTerm1 = findViewById(R.id.rdTerm1);
        rdTerm2 = findViewById(R.id.rdTerm2);


        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        mistakes = (Mistakes) intent.getSerializableExtra("mistake");
        student = (Student) intent.getSerializableExtra("student");
        List<Category> subjectList = getListSubject();

        String tgvp = mistakes.getLtvpThoiGian();
        int firstSpaceIndex = tgvp.indexOf("-");
        String subject = tgvp.substring(0, firstSpaceIndex);
        //  Toast.makeText(MistakeUpdateMistake.this, "size"+subjectList.size(),Toast.LENGTH_LONG).show();
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, subjectList);
        spSubject.setAdapter(adapterCategory);
        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  Toast.makeText(mistake_edit.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
                // subject = adapterCategory.getItem(position).getNameCategory();
                // Tìm vị trí của nienKhoa trong danh sách
                int selectedPosition = -1;
                for (int i = 0; i < subjectList.size(); i++) {
                    if (subjectList.get(i).getNameCategory().equals(subject)) {
                        selectedPosition = i;
                        break;
                    }
                }
                // Set giá trị mặc định cho Spinner
                if (selectedPosition != -1) {
                    spSubject.setSelection(selectedPosition);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setDataMistakeUpdate();
        final String[] lhNK = new String[1];

        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mistake_edit.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
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

        btnExitUpdateMistake.setOnClickListener(v -> {
            finish();
        });

        btnUpdateMistake.setOnClickListener(v -> {
            update(lhNK[0]);
        });


        date();




    }

    private  void setDataMistakeUpdate() {
        txtNamePersonl.setText(student.getHsHoTen());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference taiKhoanRef = db.collection("viPham");
        Query query = taiKhoanRef.whereEqualTo("VP_id", mistakes.getVpID());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        String viPhamP = documentSnapshot.getString("VP_TenViPham");
                        String viDiemtru = documentSnapshot.getString("VP_DiemTru");
                        diemTru = Integer.parseInt(viDiemtru);
                       txtNameMistake.setText(viPhamP);


                    } else {
                       // Toast.makeText(context, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(context, "ERRR", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String tgvp = mistakes.getLtvpThoiGian();
        int firstSpaceIndex = tgvp.indexOf("-");
        String subject = tgvp.substring(0, firstSpaceIndex);
        //setSpSubject(subject);

       // Toast.makeText(this, subject,Toast.LENGTH_LONG).show();

        String ngay = tgvp.substring(firstSpaceIndex+1);
        txtVDate.setText(ngay);


    //    Toast.makeText(this, "luot vi oham id: "+ mistakes.ltvpID, Toast.LENGTH_LONG).show();
        if(mistakes.getLtvpHK().toLowerCase().equals("học kỳ 1")) {
            rdTerm1.setChecked(true);
        } else {
            rdTerm2.setChecked(true);
        }



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
    private void setSpSubject(String sj) {

    }





    public void date(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dayOfWeekString = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
            String dateString = dayOfWeekString + " " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

            txtVDate.setText(dateString);
        };

        imgCalend.setOnClickListener(v -> {
            new DatePickerDialog(this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

// Cập nhật TextViews với ngày và thứ hiện tại
        String currentDayOfWeekString = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
        String currentDateString = currentDayOfWeekString + " " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
      //  txtVDate.setText(currentDateString);
        date = currentDateString;
    }

    public void update(String subject1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("luotViPham").whereEqualTo("LTVP_id", mistakes.getLtvpID());
        // Thực hiện query và lấy document snapshot
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {

                                String time = txtVDate.getText().toString();
                                int checkedRadioButtonId = rdGTerm.getCheckedRadioButtonId();
                                RadioButton checkedRadioButton = rdGTerm.findViewById(checkedRadioButtonId);
                                String hocky = (String) checkedRadioButton.getText().toString();


                                if(!hocky.equals(mistakes.ltvpHK.toString().toLowerCase())) {
                                    updateHK(hocky);
                                }

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("LTVP_ThoiGian", subject1+"-"+time);
                                updates.put("HK_HocKy", hocky.trim());

                                document.getReference().update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MistakeUpdateMistake.this, "sucess", Toast.LENGTH_LONG).show();
                                                Mistake_See.adapterMistakeSee.notifyDataSetChanged();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MistakeUpdateMistake.this, "fail"+e, Toast.LENGTH_LONG).show();

                                            }
                                        });
                            }
                        }
                    } else {
                        Log.w("FirestoreQuery", "Lỗi khi truy vấn: ", task.getException());
                    }
                });

    }

    private void setSpSubject() {

    }


    private void updateHK(@NonNull String hky) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(hky.toLowerCase().equals("học kỳ 1")) {

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



                            drl -= diemTru;

                            if(drl >= 90 && drl <= 100) {
                                hanhKiem = "Tốt";
                            } else if (drl >= 70 && drl <= 89) {
                                hanhKiem = "Khá";
                            }else if(drl >= 50 && drl <= 69){
                                hanhKiem = "Trung bình";
                            }else {
                                hanhKiem = "Yếu";
                            }


                            int finalDrl = drl;
                            db.collection("hanhKiem")
                                    .whereEqualTo("HKM_id", "HKI"+student.getHsID())
                                    .get()
                                    .addOnSuccessListener(querySnapshot1 -> {
                                        if (!querySnapshot1.isEmpty()) {
                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("HKM_DiemRenLuyen", finalDrl +"");
                                            updates.put("HKM_HanhKiem", hanhKiem);
                                            docRef.update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                        //    Toast.makeText(mistake_edit.this, "Cập nhật hk thành công!", Toast.LENGTH_LONG).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        //     Toast.makeText(mistake_edit.this, "Cập nhật hk thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();

                                                    });
                                        } else {
                                            //  Toast.makeText(mistake_edit.this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Lỗi khi truy vấn Firestore
                                    });
                        } else {
                            //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            CollectionReference hanhKiem2Ref = db.collection("hanhKiem");
            Query query1 = hanhKiem2Ref.whereEqualTo("HKM_id", "HKII"+student.getHsID());
            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String hkDRl = documentSnapshot.getString("HKM_DiemRenLuyen");
                            int drl = Integer.parseInt(hkDRl);
                            drl += diemTru;

                            if(drl >= 90 && drl <= 100) {
                                hanhKiem = "Tốt";
                            } else if (drl >= 70 && drl <= 89) {
                                hanhKiem = "Khá";
                            }else if(drl >= 50 && drl <= 69){
                                hanhKiem = "Trung bình";
                            }else {
                                hanhKiem = "Yếu";
                            }


                            int finalDrl = drl;
                            db.collection("hanhKiem")
                                    .whereEqualTo("HKM_id", "HKII"+student.getHsID())
                                    .get()
                                    .addOnSuccessListener(querySnapshot1 -> {
                                        if (!querySnapshot1.isEmpty()) {
                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("HKM_DiemRenLuyen", finalDrl +"");
                                            updates.put("HKM_HanhKiem", hanhKiem);
                                            docRef.update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Toast.makeText(this, "Cập nhật hk thành công!", Toast.LENGTH_LONG).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        //    Toast.makeText(this, "Cập nhật hk thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                                                    });
                                        } else {
                                            //  Toast.makeText(this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

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
        } else {
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



                            drl += diemTru;

                            if(drl >= 90 && drl <= 100) {
                                hanhKiem = "Tốt";
                            } else if (drl >= 70 && drl <= 89) {
                                hanhKiem = "Khá";
                            }else if(drl >= 50 && drl <= 69){
                                hanhKiem = "Trung bình";
                            }else {
                                hanhKiem = "Yếu";
                            }


                            int finalDrl = drl;
                            db.collection("hanhKiem")
                                    .whereEqualTo("HKM_id", "HKI"+student.getHsID())
                                    .get()
                                    .addOnSuccessListener(querySnapshot1 -> {
                                        if (!querySnapshot1.isEmpty()) {
                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("HKM_DiemRenLuyen", finalDrl +"");
                                            updates.put("HKM_HanhKiem", hanhKiem);
                                            docRef.update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                        //    Toast.makeText(mistake_edit.this, "Cập nhật hk thành công!", Toast.LENGTH_LONG).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        //     Toast.makeText(mistake_edit.this, "Cập nhật hk thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();

                                                    });
                                        } else {
                                            //  Toast.makeText(mistake_edit.this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Lỗi khi truy vấn Firestore
                                    });
                        } else {
                            //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            CollectionReference hanhKiem2Ref = db.collection("hanhKiem");
            Query query1 = hanhKiem2Ref.whereEqualTo("HKM_id", "HKII"+student.getHsID());
            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String hkDRl = documentSnapshot.getString("HKM_DiemRenLuyen");
                            int drl = Integer.parseInt(hkDRl);
                            drl -= diemTru;

                            if(drl >= 90 && drl <= 100) {
                                hanhKiem = "Tốt";
                            } else if (drl >= 70 && drl <= 89) {
                                hanhKiem = "Khá";
                            }else if(drl >= 50 && drl <= 69){
                                hanhKiem = "Trung bình";
                            }else {
                                hanhKiem = "Yếu";
                            }


                            int finalDrl = drl;
                            db.collection("hanhKiem")
                                    .whereEqualTo("HKM_id", "HKII"+student.getHsID())
                                    .get()
                                    .addOnSuccessListener(querySnapshot1 -> {
                                        if (!querySnapshot1.isEmpty()) {
                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("HKM_DiemRenLuyen", finalDrl +"");
                                            updates.put("HKM_HanhKiem", hanhKiem);
                                            docRef.update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Toast.makeText(this, "Cập nhật hk thành công!", Toast.LENGTH_LONG).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        //    Toast.makeText(this, "Cập nhật hk thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                                                    });
                                        } else {
                                            //  Toast.makeText(this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

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