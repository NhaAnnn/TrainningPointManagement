package com.example.qldrl.Mistake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdapterMistakeDetail extends RecyclerView.Adapter<AdapterMistakeDetail.mistakeViewHoder> {

    public AdapterMistakeDetail(List<MistakeType> listMistake, Context context, String namePersonl, Account account, Student student) {
        this.context = context;
        this.listMistake = listMistake;
        this.namePersonl = namePersonl;
        this.account = account;
        this.student = student;

    }
    private List<MistakeType> listMistake;
    //test show click
    private Context context;
    private  String namePersonl;
    private Account account;
    private Student student;
    private  int clickCount =0;

    @NonNull
    @Override
    public mistakeViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_mistaked,parent,false);
        return new AdapterMistakeDetail.mistakeViewHoder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull mistakeViewHoder holder, int position) {
        try {
            MistakeType mistake = listMistake.get(position); // sử dụng listID chẵn hạn
            if(position < 10) {
                holder.txtNameMistakeDetail.setText("0"+(position+1)+"_"+mistake.getLvpTen());

            } else {
                holder.txtNameMistakeDetail.setText((position+1)+"_"+mistake.getLvpTen());
            }

           holder.layoutDetailss.setOnTouchListener((v, event) -> {
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   if (clickCount == 0) {
                       holder.recycMistakeDetail.setVisibility(View.VISIBLE);
                       holder.imgMistakeDetail.setRotation(180f);
                       clickCount++;
                   } else {
                       holder.recycMistakeDetail.setVisibility(View.GONE);
                       holder.imgMistakeDetail.setRotation(0f);
                       clickCount = 0;
                   }
                   return true;
               }
               return false;
           });


               FirebaseFirestore db = FirebaseFirestore.getInstance();
               CollectionReference collectionRef = db.collection("viPham");
               List<Mistake> vpTenViPhams = new ArrayList<>();

               collectionRef.whereEqualTo("LVP_id", mistake.lvpID)
                       .get()
                       .addOnSuccessListener(queryDocumentSnapshots -> {
                           for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                               String vpTenViPham = document.getString("VP_TenViPham");
                               String vpID = document.getString("VP_id");
                               String lvpid = document.getString("LVP_id");
                               String vpdiemtru = document.getString("VP_DiemTru");
                               Mistake mistake1 = new Mistake(vpTenViPham, lvpid,vpID,vpdiemtru);

                                   vpTenViPhams.add(mistake1);

                           }

                           // Xử lý vpTenViPhams ở đây
                       //    Log.d("TAG", String.valueOf(vpTenViPhams.size()));

                           AdapterMistake adapterMistake = new AdapterMistake(vpTenViPhams, context, namePersonl, account, student);
                           holder.recycMistakeDetail.setAdapter(adapterMistake);
                           holder.recycMistakeDetail.setLayoutManager(new GridLayoutManager(context, 1));
                       })
                       .addOnFailureListener(e -> {
                           Toast.makeText(context, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                       });



//            holder.txtVMistake.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, mistake_edit.class);
//                    intent.putExtra("mistakeName", mistake.getNameMistake());
//                    intent.putExtra("namePersonl", namePersonl);
//                    intent.putExtra("account", account);
//                    intent.putExtra("student", student);
//                    context.startActivity(intent);
//                    Toast.makeText(context,namePersonl, Toast.LENGTH_SHORT).show();
//                }
//            });

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
        private TextView txtNameMistakeDetail;
        private ImageView imgMistakeDetail;
        private RecyclerView recycMistakeDetail;
        private LinearLayout layoutDetailss;
        public mistakeViewHoder(@NonNull View itemView) {
            super(itemView);
            txtNameMistakeDetail = itemView.findViewById(R.id.txtNameMistakeDetail);
            imgMistakeDetail = itemView.findViewById(R.id.imgMistakeDetail);
            recycMistakeDetail = itemView.findViewById(R.id.recycMistakeDetail);
            layoutDetailss = itemView.findViewById(R.id.layoutDetailss);

        }
    }
}
