package com.example.qldrl.Class;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AdapterListStudentOfClass extends RecyclerView.Adapter<AdapterListStudentOfClass.MyViewHolder> {

    private Context context;
    private String className;
    private int p;
    public List<ListStudentOfClass> listStudentOfClass;

    public AdapterListStudentOfClass(List<ListStudentOfClass> listStudentOfClass, Context context, String className) {
        this.context = context;
        this.listStudentOfClass = listStudentOfClass;
        this.className = className;
        EventBus.getDefault().register(this); // Đăng ký lắng nghe EventBus
    }

    @NonNull
    @Override
    public AdapterListStudentOfClass.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.liststudent_of_class_information, parent,false);
        return new AdapterListStudentOfClass.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListStudentOfClass.MyViewHolder holder, int position) {
        ListStudentOfClass list = this.listStudentOfClass.get(position);
        holder.txtIdStudent.setText(list.getId());
        holder.txtNameStudent.setText(list.getName());
        holder.txtPositionStudent.setText(list.getPosition());
        holder.po =  holder.getAdapterPosition();
        holder.btnStudentDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentInformation.class);
                intent.putExtra("Student", list);
                intent.putExtra("Class",className);
                intent.putExtra("Position", holder.po);
                context.startActivity(intent);
            }
        });
    }
    // Phương thức để cập nhật dữ liệu khi có thay đổi
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStudentInformationUpdated(StudentInformationUpdatedEvent event) {
        // Cập nhật dữ liệu trong listStudentOfClass
        listStudentOfClass.set(event.position, event.updatedStudent);
        notifyItemChanged(event.position);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        EventBus.getDefault().unregister(this); // Hủy đăng ký lắng nghe EventBus
    }
    @Override
    public int getItemCount() {
        return listStudentOfClass.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView btnStudentDetail;
        private TextView txtIdStudent, txtNameStudent, txtPositionStudent;
        private int po;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtIdStudent = itemView.findViewById(R.id.txtIdStudent);
            txtNameStudent = itemView.findViewById(R.id.txtNameStudent);
            txtPositionStudent = itemView.findViewById(R.id.txtPositionStudent);
            btnStudentDetail = itemView.findViewById(R.id.btnStudentDetail);
        }
    }
}
