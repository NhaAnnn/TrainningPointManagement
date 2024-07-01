package com.example.qldrl.Mistake;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MistakeUpdateMistake extends AppCompatActivity {
    private TextView txtVDate, txtNameMistake, txtNamePersonl;
    private AdapterCategory adapterCategory;
    private RadioGroup rdGTerm;
    private Button btnExitUpdateMistake, btnUpdateMistake;
    private ImageView imgCalend;
    private Spinner spSubject;
    Account account;
    Mistakes mistakes;
    Student student;
    String date;
    RadioButton rdTerm2, rdTerm1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake_update_mistake);


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

        setDataMistakeUpdate();


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
        int firstSpaceIndex = tgvp.indexOf(" ");
        String subject = tgvp.substring(0, firstSpaceIndex);
        setSpSubject(subject);


        String ngay = tgvp.substring(firstSpaceIndex+1);
        txtVDate.setText(ngay);


        Toast.makeText(this, "luot vi oham id: "+ mistakes.ltvpID, Toast.LENGTH_LONG).show();

        CollectionReference luotVPRef = db.collection("luotViPham");
        Query query1 = luotVPRef.whereEqualTo("LTVP_id", mistakes.getLtvpID());
        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        String hkHocKy = documentSnapshot.getString("HK_HocKy");
                        if(hkHocKy.toLowerCase().equals("học kỳ 1")) {
                            rdTerm1.setChecked(true);
                        } else {
                            rdTerm2.setChecked(true);
                        }


                    } else {
                        // Toast.makeText(context, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(context, "ERRR", Toast.LENGTH_SHORT).show();
                }
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
    private void setSpSubject(String sj) {
        adapterCategory = new AdapterCategory(this, R.layout.layout_item_selected, getListSubject());
        spSubject.setAdapter(adapterCategory);
        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(mistake_edit.this, adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
               // subject = adapterCategory.getItem(position).getNameCategory();
                // Tìm vị trí của nienKhoa trong danh sách
                int selectedPosition = -1;
                for (int i = 0; i < getListSubject().size(); i++) {
                    if (getListSubject().get(i).getNameCategory().equals(sj)) {
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
    }

    public void date(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dayOfWeekString = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
            String dateString = dayOfWeekString + ", " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

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
        String currentDateString = currentDayOfWeekString + ", " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
        txtVDate.setText(currentDateString);
        date = currentDateString;
    }
}