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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
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

public class AdapterClassRom extends RecyclerView.Adapter<AdapterClassRom.MyViewHoder> implements Filterable {
    //solution 2 lam viec Adapter tái sử dụng
    //tạo từng cái list ví dụ List<Integer> listImgIds; List <String> listTxt;
    //tao contructor
    public AdapterClassRom(List<ClassRom> listClass, Context context, Account account) {
        this.context = context;
        this.listClass = listClass;
        this.listClassOld = listClass;
        this.account = account;
    }


    private List<ClassRom> listClass;
    private List<ClassRom> listClassOld;
    Account account;

    //test show click
    private Context context;

    private int mountMistake = 0;


    @NonNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_class,parent,false);
        return new MyViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {


        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            ClassRom classRom = listClass.get(position); // sử dụng listID chẵn hạn
            holder.txtNameClass.setText(classRom.getLhTen());
            holder.txtNKClass.setText(classRom.getNkNienKhoa());
            //Lay du lieu ve si so
            CollectionReference collectionRef = db.collection("hocSinh");
            // Tạo query để lọc theo trường lopHoc = "10a1"
            Query query = collectionRef.whereEqualTo("LH_id", classRom.getLhID());
            // Đếm số document thỏa mãn query
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                     //   System.out.println("Tổng số luong ncccccguoi: " + querySnapshot.size());

                        if(!querySnapshot.isEmpty()) {
                             int  count = querySnapshot.size() ;
                             holder.txtMount.setText(count + "");
                        } else {
                            holder.txtMount.setText("0");
                        }
                    } else {
                    }
                }
            });


            //Lay du lieu ve si so
            CollectionReference collectionRef1 = db.collection("hocSinh");
            // Tạo query để lọc theo trường lopHoc = "10a1"
            Query query1 = collectionRef1.whereEqualTo("LH_id", classRom.getLhID());
            // Đếm số document thỏa mãn query
            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                  //      System.out.println("Tổng số luong nguoisss: " +querySnapshot.size());

                        List<String> hsIdList = new ArrayList<>();
                       for(DocumentSnapshot documentSnapshot: querySnapshot) {
                           hsIdList.add(documentSnapshot.getString("HS_id"));
                       }
                      //  System.out.println("Tổng số luong nguoi: " + hsIdList.size());
                        if (!hsIdList.isEmpty()) {
                            CollectionReference luotViPhamRef = db.collection("luotViPham");
                            luotViPhamRef.whereIn("HS_id", hsIdList)
                                    .get()
                                    .addOnSuccessListener(querySnapshot2 -> {
                                        int tongSoLuotViPham = querySnapshot2.size();
                                        holder.txtMistake.setText(tongSoLuotViPham+"");
                                  //      System.out.println("Tổng số lượt vi phạm của lớp: " + tongSoLuotViPham);
                                    })
                                    .addOnFailureListener(e -> {
                                        System.err.println("Lỗi: " + e.getMessage());
                                    });
                        } else {
                            System.out.println("Không có học sinh trong lớp.");
                        }

                    } else {
                    }
                }
            });




            holder.txtNameTearcher.setText(classRom.getLhGVCN());
            holder.txtMistake.setText("0");



            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String className = classRom.getLhTen();
                    String classID = classRom.getLhID();
                    Intent intent = new Intent(context, Mistake_Personal.class);
                    intent.putExtra("className", className);
                    intent.putExtra("classID", classID);
                    intent.putExtra("account", account);
                    context.startActivity(intent);
                   // Toast.makeText(context, classRom.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("RecyclerViewAdapter", "Đã gán dữ liệu thành công cho vị trí " + position);
        } catch (Exception e) {
            Log.e("RecyclerViewAdapter", "Lỗi khi gán dữ liệu cho vị trí " + position, e);

        }
    }

    @Override
    public int getItemCount() {
        return listClass.size();
    } // dung 1 trongmaayay cái list kia lấy size

    @Override
    public  Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()) {
                    listClass = listClassOld;
                } else {
                    List<ClassRom>  list = new ArrayList<>();
                    for ( ClassRom classRom : listClassOld) {
                        if(classRom.getLhGVCN().toLowerCase().contains(strSearch.toLowerCase())
                                || classRom.getLhTen().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(classRom);
                        }
                    }
                    listClass = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values   = listClass;

                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listClass = (List<ClassRom>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class MyViewHoder extends RecyclerView.ViewHolder {
        private TextView txtNameClass, txtMount, txtNameTearcher, txtMistake, txtNKClass;
        private CardView cardVItemClass;
        private ImageView btnDetail;
        public MyViewHoder(@NonNull View itemView) {
            super(itemView);

            txtNameClass = itemView.findViewById(R.id.txtNameClass);
            txtMount = itemView.findViewById(R.id.txtMount);
            txtNameTearcher = itemView.findViewById(R.id.txtNameTeacher);
            txtMistake = itemView.findViewById(R.id.txtMistake);
            cardVItemClass = itemView.findViewById(R.id.cardVItemClass);
            btnDetail = itemView.findViewById(R.id.imgBtnDetail);
            txtNKClass = itemView.findViewById(R.id.txtNKClass);

        }
    }


}
