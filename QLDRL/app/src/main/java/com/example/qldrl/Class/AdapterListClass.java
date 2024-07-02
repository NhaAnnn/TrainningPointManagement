package com.example.qldrl.Class;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterListClass extends RecyclerView.Adapter<AdapterListClass.MyViewHolder> {
    private Context context;
    private List<ListClass> listClass;
    private int clickCount = 0;

    public AdapterListClass(List<ListClass> listClass, Context context) {
        this.context = context;
        this.listClass = listClass;
        EventBus.getDefault().register(this); // Đăng ký lắng nghe EventBus
    }


    @NonNull
    @Override
    public AdapterListClass.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.listclass_information, parent,false);
        return new AdapterListClass.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListClass.MyViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListClass list = this.listClass.get(position);
        int p = position;

        holder.txtNameClass.setText(list.getName());
        holder.txtYear.setText(list.getYear());
        Query query = db.collection("hocSinh").whereEqualTo("LH_id", list.getId());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                holder.txtMount.setText(task.getResult().size()+"");
            } else {
                holder.txtMount.setText("0");
            }
        });
        Log.d(TAG,"111"+ list.getName());
        holder.txtNameTearcher.setText(list.getNameTeacher());
        holder.btnClassroom.setVisibility(View.GONE);


        holder.btnClassDetail.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (clickCount == 0) {
                    holder.btnExpand.setRotation(180f);
                    holder.btnClassroom.setVisibility(View.VISIBLE);
                    clickCount++;
                } else {
                    holder.btnExpand.setRotation(0f);
                    holder.btnClassroom.setVisibility(View.GONE);
                    clickCount = 0;
                }
                return true;
            }
            return false;
        });

        holder.btnViewClass.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListStudentOfClass.class);
            intent.putExtra("className",""+ list.getName());
            intent.putExtra("classId",""+ list.getId());
            context.startActivity(intent);
        });

        holder.btnUpdateClass.setOnClickListener(v -> updateClass(list, p));
        holder.btnDeleteClass.setOnClickListener(v -> deleteClass(list, p));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileClassUpdated(FileClassUpdatedEvent event) {
        // Cập nhật dữ liệu trong listClass
        listClass.addAll(event.updatedClass);
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        EventBus.getDefault().unregister(this); // Hủy đăng ký lắng nghe EventBus
    }
    @Override
    public int getItemCount() {
        return listClass.size();
    }

    public void updateClass(ListClass list, int position){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialog_update_class);

        Log.d(TAG, "updateClass: "+list.getId());
        //Tìm id
        EditText diaClassName = dialog.findViewById(R.id.diaClassName);
        diaClassName.setText(list.getName());
        EditText diaTeacherName = dialog.findViewById(R.id.diaTeacherName);
        diaTeacherName.setText(list.getNameTeacher());
        EditText diaYear = dialog.findViewById(R.id.diaYear);
        diaYear.setText( list.getYear());

        // Làm mờ khu vực xung quanh
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_backgroud));
        dialog.getWindow().setDimAmount(0.5f);

        // Xử lý sự kiện đóng dialog
        Button btnClose = dialog.findViewById(R.id.cancelButton);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        Button btnSave = dialog.findViewById(R.id.saveButton);
        btnSave.setOnClickListener(v -> {
            String cName = diaClassName.getText().toString();
            String tName = diaTeacherName.getText().toString();
            String y = diaYear.getText().toString();
            String id = (cName.toLowerCase() + y).trim();

            Map<String, Object> newData = new HashMap<>();
            newData.put("LH_id",id);
            newData.put("LH_TenLop", cName);
            newData.put("LH_GVCN", tName);
            newData.put("NK_NienKhoa",y);
            ListClass newClass = new ListClass(id, cName, tName, y);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("lop").whereEqualTo("LH_id",id)
                    .get().addOnCompleteListener(task -> {
                boolean isValid = false;
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty() && !id.equals(list.getId())){
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
                            db.collection("lop").whereEqualTo("LH_id", list.getId())
                                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    // Cập nhật giá trị cho document
                                    document.getReference().update(newData);
                                    Toast.makeText(context, "Đã chỉnh sửa thông tin lớp thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    listClass.set(position,newClass);
                                    notifyItemChanged(position);

                                }
                            }).addOnFailureListener(e -> Toast.makeText(context, "Chỉnh sửa lớp thất bại", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            });
        });
        // Hiện dialog
        dialog.show();
    }

    public void deleteClass(ListClass list, int position){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialog_delete_class);

        // Làm mờ khu vực xung quanh
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_backgroud));
        dialog.getWindow().setDimAmount(0.5f);


        // Xử lý sự kiện đóng dialog
        Button btnClose = dialog.findViewById(R.id.cancelButton);
        btnClose.setOnClickListener(view -> dialog.dismiss());

        Button btnDelete = dialog.findViewById(R.id.deleteButton);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("lop").whereEqualTo("LH_id",list.getId())
                        .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Cập nhật giá trị cho document
                        document.getReference().delete();
                        Toast.makeText(context, "Xóa lớp học thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        listClass.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listClass.size());
                    }
                });
            }
        });

        // Hiện dialog
        dialog.show();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout btnClassroom,btnViewClass,btnUpdateClass,btnDeleteClass;
        private TextView txtNameClass, txtYear, txtMount, txtNameTearcher;
        private ImageView btnExpand;
        private ConstraintLayout btnClassDetail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameClass = itemView.findViewById(R.id.txtNameClass);
            txtMount = itemView.findViewById(R.id.txtMount);
            txtNameTearcher = itemView.findViewById(R.id.txtNameTeacher);
            btnClassDetail = itemView.findViewById(R.id.btnClassDetail);
            btnViewClass = itemView.findViewById(R.id.btnViewClass);
            btnUpdateClass = itemView.findViewById(R.id.btnUpdateClass);
            btnDeleteClass = itemView.findViewById(R.id.btnDeleteClass);
            btnClassroom = itemView.findViewById(R.id.btnClassroom);
            txtYear = itemView.findViewById(R.id.txtYear);
            btnExpand = itemView.findViewById(R.id.btnExpand);
        }
    }
}
