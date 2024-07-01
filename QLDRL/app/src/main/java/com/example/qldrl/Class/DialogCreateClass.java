package com.example.qldrl.Class;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DialogCreateClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_create_class);
    }

    public void showDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialog_create_class);

        // Làm mờ khu vực xung quanh
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_backgroud));
        dialog.getWindow().setDimAmount(0.5f);


        // Xử lý sự kiện đóng dialog
        Button btnClose = dialog.findViewById(R.id.cancelButton);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        Button btnSave = dialog.findViewById(R.id.saveClass);
        btnSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               EditText diaClassName = dialog.findViewById(R.id.diaCreateClassName);
               EditText diaTeacherName = dialog.findViewById(R.id.diaCreateTeacherName);
               EditText diaYear = dialog.findViewById(R.id.diaCreateYear);
               String cName = diaClassName.getText().toString();
               String tName = diaTeacherName.getText().toString();
               String y = diaYear.getText().toString();
               String id = (cName.toLowerCase()+y).trim();
               Map<String, Object> newData = new HashMap<>();
               newData.put("LH_id",id);
               newData.put("LH_TenLop", cName);
               newData.put("LH_GVCN", tName);
               newData.put("NK_NienKhoa",y);
               FirebaseFirestore db = FirebaseFirestore.getInstance();
               db.collection("lop").whereEqualTo("LH_id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       boolean isValid = false;
                       if(task.isSuccessful()){
                           if(!task.getResult().isEmpty()){
                               diaClassName.setError("Lớp đã tồn tại!");
                           }
                           else {
                               diaClassName.setError(null);
                               isValid = true;

                               // Kiểm tra trường "className"
                               if (diaClassName.getText().toString().isEmpty()) {
                                   diaClassName.setError("Không thể bỏ trống!");
                                   isValid = false;
                               } else {
                                   diaClassName.setError(null);
                               }

                               // Kiểm tra trường "diaTeacherName"
                               if (diaTeacherName.getText().toString().isEmpty()) {
                                   diaTeacherName.setError("Không thể bỏ trống!");
                                   isValid = false;
                               } else {
                                   diaTeacherName.setError(null);
                               }

                               // Kiểm tra trường "year"
                               if (diaYear.getText().toString().isEmpty()) {
                                   diaYear.setError("Không thể bỏ trống!");
                                   isValid = false;
                               } else {
                                   diaYear.setError(null);
                               }
                               if(isValid) {
                                   db.collection("lop").add(newData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                       @Override
                                       public void onSuccess(DocumentReference documentReference) {

                                           Toast.makeText(context, "Thêm lớp học mới thành công", Toast.LENGTH_SHORT).show();
                                           Intent intent = new Intent(context, ListClass.class);
                                           context.startActivity(intent);
                                           finish();
                                       }
                                   })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(context, "Thêm lớp học mới thất bại", Toast.LENGTH_SHORT).show();
                                       }
                                   });
                               }
                           }
                       }
                   }
               });

           }
       });

        // Hiện dialog
        dialog.show();
    }
}