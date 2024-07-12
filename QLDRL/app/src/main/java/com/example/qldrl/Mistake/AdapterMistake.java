package com.example.qldrl.Mistake;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;

import java.util.List;

public class AdapterMistake extends RecyclerView.Adapter<AdapterMistake.mistakeViewHoder> {

    public AdapterMistake(List<Mistake> listMistake, Context context, String namePersonl, Account account, Student student) {
        this.context = context;
        this.listMistake = listMistake;
        this.namePersonl = namePersonl;
        this.account = account;
        this.student = student;

    }
    private List<Mistake> listMistake;
    //test show click
    private Context context;
    private  String namePersonl;
    private Account account;
    private Student student;

    @NonNull
    @Override
    public mistakeViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_mistake,parent,false);
        return new AdapterMistake.mistakeViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mistakeViewHoder holder, int position) {
        try {
            Mistake mistake = listMistake.get(position); // sử dụng listID chẵn hạn
            holder.txtVMistake.setText(mistake.getNameMistake());
            holder.txtVMistake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, mistake_edit.class);
                    intent.putExtra("mistakeName", mistake.getNameMistake());
                    intent.putExtra("mistake", mistake);
                    intent.putExtra("namePersonl", namePersonl);
                    intent.putExtra("account", account);
                    intent.putExtra("student", student);
                    context.startActivity(intent);
                 //   Toast.makeText(context,namePersonl, Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("RecyclerViewAdapter", "Đã gán dữ liệu thành công cho vị trí " + position);
        } catch (Exception e) {
            Log.e("RecyclerViewAdapter", "Lỗi khi gán dữ liệu cho vị trí " + position, e);

        }
    }

    @Override
    public int getItemCount() {
        return listMistake.size();
    }

    class mistakeViewHoder extends RecyclerView.ViewHolder {
        private TextView txtVMistake;

        public mistakeViewHoder(@NonNull View itemView) {
            super(itemView);
            txtVMistake = itemView.findViewById(R.id.txtVMistake);

        }
    }
}
