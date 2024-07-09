package com.example.qldrl.Class;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;

public class StudentInformation extends AppCompatActivity {
    private ListStudentOfClass listStudentOfClass;
    private String newPosition;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);

        Intent intent = getIntent();
        ListStudentOfClass student = (ListStudentOfClass) intent.getSerializableExtra("Student");
        String nameClass = intent.getStringExtra("Class");
        index = (Integer) intent.getIntExtra("Position",-1);
        

        TextView txtPage = findViewById(R.id.pageTitle);
        TextView txtIdStudent = findViewById(R.id.idStudent);
        TextView txtNameClass = findViewById(R.id.nameClass);
        TextView txtNameStudent = findViewById(R.id.nameStudent);
        TextView txtGenderStudent = findViewById(R.id.genderStudent);
        TextView txtBirthStudent = findViewById(R.id.birtdayStudent);
        RadioGroup position = findViewById(R.id.radioGroup);
        RadioButton po1 = findViewById(R.id.radioPo1);
        RadioButton po2 = findViewById(R.id.radioPo2);
        Button saveButton = findViewById(R.id.saveButton);

        getNewPosition(student.getPosition());
        String bcs = "Ban cán sự";
        if(student.getPosition().equals(bcs)){
            po2.setChecked(true);
        }else{
            po1.setChecked(true);
        }

        txtPage.setText(student.getName());
        txtIdStudent.setText(student.getId());
        txtNameClass.setText(nameClass);
        txtNameStudent.setText(student.getName());
        txtGenderStudent.setText(student.getGender());
        txtBirthStudent.setText(student.getDate());

        position.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Xử lý sự kiện khi một RadioButton được chọn
                if (checkedId == R.id.radioPo1) {
                    // Xử lý khi RadioButton 1 được chọn
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updatePosition(student, po1.getText().toString());
                        }
                    });
                } else if (checkedId == R.id.radioPo2) {
                    // Xử lý khi RadioButton 2 được chọn
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updatePosition(student, po2.getText().toString());
                        }
                    });
                }
            }
        });

        ImageView btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> {
            EventBus.getDefault().post(new StudentInformationUpdatedEvent(index, new ListStudentOfClass(student.getId(), student.getIdTK(), student.getIdLH(), student.getName(), student.getDate(), student.getGender(), newPosition)));
            onBackPressed();
        });
    }
    public void updatePosition(ListStudentOfClass student, String position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hocSinh").whereEqualTo("HS_id",student.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Cập nhật giá trị cho document
                            document.getReference().update("HS_ChucVu", position);
                            String idtk = document.getString("TK_id");
                            db.collection("taiKhoan").whereEqualTo("TK_id",idtk)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                        documentSnapshot.getReference().update("TK_ChucVu", position);
                                    }
                                }
                            });
                            getNewPosition(position);
                            Toast.makeText(StudentInformation.this, "Cập nhật chức vụ thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void getNewPosition (String po){
        newPosition = po;
    }

}