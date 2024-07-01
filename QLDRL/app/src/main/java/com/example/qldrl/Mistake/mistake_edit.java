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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private LinearLayout layoutTest;
    private RecyclerView recyctest;
    private Account account;
    private Student student;
    String mistakeName, date, subject, LVPid, VPid;
    Button btnSaveMistake, btnExitEditMistake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_edit);
        imgCalen = findViewById(R.id.imgCalend);

        txtDate = findViewById(R.id.txtVDate);
        date();

        spSubject = findViewById(R.id.spSubject);
        setSpSubject();

        txtNameMistake = findViewById(R.id.txtNameMistake);
        txtNamePersonl = findViewById(R.id.txtNamePersonl);

        btnExitEditMistake = findViewById(R.id.btnExitEditMistake);

        btnExitEditMistake.setOnClickListener(v -> {
//            Intent intent1 = new Intent(this, Mistake_Personal.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent1);
            finish();
        });


        Intent intent = getIntent();
         mistakeName = intent.getStringExtra("mistakeName");
        String namePersonl = intent.getStringExtra("namePersonl");
        account = (Account) intent.getSerializableExtra("account");
        student = (Student) intent.getSerializableExtra("student");

        Toast.makeText(this, account.getTkID()+"111111", Toast.LENGTH_SHORT).show();

        txtNameMistake.setText(mistakeName);
        txtNamePersonl.setText(namePersonl);


        getIDLVPVP();

        btnSaveMistake = findViewById(R.id.btnSavaMistake);


        btnSaveMistake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMistake();
            }
        });

        List <ClassRom> classList = new ArrayList<ClassRom>();

//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a2", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a3", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a4", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));
//        classList.add(new ClassRom("10a1", 30, "Tran thi xuan mai", 20));



        // List <Integer> listbearimgids = new arrayList<> ();
        // List <String> listbearNames = new arrayList<> ();
        //for(Bear bear : bearList) {listbearimgids.add(bear.getImgid())
        //listbearNames.add(bear.getName() }


   //     layoutTest.addView();
//        Calendar calendar = Calendar.getInstance();
//        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.MONTH, month);
//            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//            String dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
//            WindowDecorActionBar.TabImpl dateTextView;
//            txtDate.setText(dateString);
//        };
//
//        imgCalen.setOnClickListener(v -> {
//            new DatePickerDialog(this, dateSetListener,
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH)).show();
//        });
//
//        String currentDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
//        txtDate.setText(currentDateString);
    }
    public void date(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dayOfWeekString = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
            String dateString = dayOfWeekString + ", " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

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
        String currentDateString = currentDayOfWeekString + ", " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
        txtDate.setText(currentDateString);
        date = currentDateString;
    }

    private void setSpSubject() {
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, getListSubject());
        spSubject.setAdapter(adapterCategory);
        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mistake_edit.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
                subject = adapterCategory.getItem(position).getNameCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void test() {


        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, getListSubject());
        spSubject.setAdapter(adapterCategory);
        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mistake_edit.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<Category> getListSubject() {
        List <Category> listSubject = new ArrayList<>();
        listSubject.add(new Category(""));
        listSubject.add(new Category("Toán"));
        listSubject.add(new Category("Lý"));
        listSubject.add(new Category("Hóa"));
        listSubject.add(new Category("Sinh"));
        listSubject.add(new Category("Tin"));
        return listSubject;

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
                    DocumentReference docRef = db.collection("luotViPham").document();

// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> data = new HashMap<>();
                    data.put("HS_id", student.getHsID());
                    data.put("VP_id", VPid);
                    data.put("TK_id", account.getTkID());
                    data.put("LTVP_ThoiGian", subject+"  " +date);
                    data.put("LTVP_id", "LTVP"+luotViPham);

// Lưu dữ liệu vào Firestore
                    docRef.set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firebase", "Dữ liệu đã được lưu thành công!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firebase", "Lỗi khi lưu dữ liệu: ", e);
                                }
                            });

                });



// Tạo một DocumentReference để lưu dữ liệu vào collection "luotViPham"

        finish();
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
                       Toast.makeText(this, LVPid, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}