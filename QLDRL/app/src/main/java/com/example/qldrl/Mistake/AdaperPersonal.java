package com.example.qldrl.Mistake;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AdaperPersonal extends RecyclerView.Adapter<AdaperPersonal.MyViewHoder> implements Filterable {
    //solution 2 lam viec Adapter tái sử dụng
    //tạo từng cái list ví dụ List<Integer> listImgIds; List <String> listTxt;
    //tao contructor
    public AdaperPersonal(List<Student> listPersonal, Context context, Account account) {
        this.account = account;
        this.context = context;
        this.listPersonal = listPersonal;
        this.listPersonalOld = listPersonal;
    }
    private List<Student> listPersonal;
    private List<Student> listPersonalOld;
    Account account;
    Student student;

    //test show click
    private Context context;

    @NonNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_personal,parent,false);
        return new MyViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {


        try {



            Student student1 = listPersonal.get(position); // sử dụng listID chẵn hạn


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference taiKhoanRef = db.collection("hocSinh");
            Query query = taiKhoanRef.whereEqualTo("HS_id", student1.getHsID());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                            String id = documentSnapshot.getString("HS_id");
                            String hoTen = documentSnapshot.getString("HS_HoTen");
                            String chucVu = documentSnapshot.getString("HS_ChucVu");
                            String gioiTinh = documentSnapshot.getString("HS_GioiTinh");
                            String ngaySinh = documentSnapshot.getString("HS_NgaySinh");
                            String lhID = documentSnapshot.getString("LH_id");
                            String tkID = documentSnapshot.getString("TK_id");

                            student = new Student(chucVu,hoTen,ngaySinh, gioiTinh, id,lhID,tkID);

                        } else {
                            // Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();

                    }
                }
            });



                CollectionReference luotViPhamRef = db.collection("luotViPham");

                AtomicInteger count = new AtomicInteger(0);

                luotViPhamRef.whereEqualTo("HS_id", student1.getHsID())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            count.set(queryDocumentSnapshots.size());
                            holder.txtLastName.setText(count.get()+"");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });





            holder.txtId.setText(student1.getHsID());
            holder.txtFirstName.setText(student1.getHsHoTen() + "");

            holder.imgEditMistake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, mistake_detail.class);
                    intent.putExtra("mistakePersonl", student1.getHsHoTen() );
                    intent.putExtra("account", account);
                    intent.putExtra("student", student1);

                    context.startActivity(intent);
           //         Toast.makeText(context, student1.getHsHoTen(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.imgViewMistake.setOnClickListener(v -> {
                Intent intent = new Intent(context, Mistake_See.class);
                intent.putExtra("student", student1);
                intent.putExtra("account", account);
                context.startActivity(intent);

            });
            Log.d("RecyclerViewAdapter", "Đã gán dữ liệu thành công cho vị trí " + position);
        } catch (Exception e) {
            Log.e("RecyclerViewAdapter", "Lỗi khi gán dữ liệu cho vị trí " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return listPersonal.size();
    } // dung 1 trongmaayay cái list kia lấy size

    //Loc du lieu
    @Override
    public  Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()) {
                    listPersonal = listPersonalOld;
                } else {
                    List<Student>  list = new ArrayList<>();
                    for ( Student personal : listPersonalOld) {
                        if(personal.getHsID().toLowerCase().contains(strSearch.toLowerCase())
                        || personal.getHsHoTen().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(personal);
                        }
                    }
                    listPersonal = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values   = listPersonal;

                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listPersonal = (List<Student>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHoder extends RecyclerView.ViewHolder {
        private TextView txtId, txtFirstName, txtLastName;
        private CardView cardVItemPersonal;
        private ImageView imgViewMistake, imgEditMistake;
        public MyViewHoder(@NonNull View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.txtId);
            txtFirstName = itemView.findViewById(R.id.txtFirstName);
            txtLastName = itemView.findViewById(R.id.txtLastName);
            imgViewMistake = itemView.findViewById(R.id.imgViewMistake);
            imgEditMistake = itemView.findViewById(R.id.imgEditMistake);
        }
    }


}
