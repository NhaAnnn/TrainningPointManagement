package com.example.qldrl.Conduct;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.Class.ListClass;
import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdapterListConduct extends RecyclerView.Adapter<AdapterListConduct.MyViewHolder> {
    private Context context;
    private Account account;
    private List<ListClass> listClass;
    private String semester;
    private int goodcnt = 0;
    private int quitecnt = 0;
    private int avegecnt = 0;
    private int weakcnt = 0;
    public AdapterListConduct(List<ListClass> listClass , Context context, Account account, String semester) {
        this.context = context;
        this.listClass = listClass;
        this.account = account;
        this.semester = semester;
    }

    @NonNull
    @Override
    public AdapterListConduct.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.listconduct_information, parent,false);
        return new AdapterListConduct.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListClass list = this.listClass.get(position);
        String good = "Tốt";
        String quite = "Khá";
        String avege = "Trung bình";
        String weak = "Yếu";

        Log.d(TAG, "onBindViewHolder: "+list.getName());
        holder.txtNameClass.setText(list.getName());
        holder.txtNameTeacher.setText(list.getNameTeacher());
        holder.txtYear.setText(list.getYear());
        holder.txtSemester.setText(semester);

        db.collection("hocSinh").whereEqualTo("LH_id", list.getId())
            .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    holder.txtMount.setText(task.getResult().size()+"");

                    List<String> hsIdList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        String id = (String) documentSnapshot.getString("HS_id");
                        hsIdList.add(documentSnapshot.getString("HS_id"));
                        Log.d(TAG, "onComplete: ID   " + id + " " + list.getName());
                    }
                    if(task.getResult().size() != 0){
                        db.collection("hanhKiem").whereIn("HS_id", hsIdList)
                                .whereEqualTo("HK_HocKy", semester).whereEqualTo("HKM_HanhKiem",good)
                                .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    holder.txtConductGood.setText(""+queryDocumentSnapshots1.size());
                                });
                        db.collection("hanhKiem").whereIn("HS_id", hsIdList)
                                .whereEqualTo("HK_HocKy", semester).whereEqualTo("HKM_HanhKiem",quite)
                                .get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                                    holder.txtConductQuite.setText(""+queryDocumentSnapshots2.size());
                                });
                        db.collection("hanhKiem").whereIn("HS_id", hsIdList)
                                .whereEqualTo("HK_HocKy", semester).whereEqualTo("HKM_HanhKiem",avege)
                                .get().addOnSuccessListener(queryDocumentSnapshots3 -> {
                                    holder.txtConductAvage.setText(""+queryDocumentSnapshots3.size());
                                });
                        db.collection("hanhKiem").whereIn("HS_id", hsIdList)
                                .whereEqualTo("HK_HocKy", semester).whereEqualTo("HKM_HanhKiem",weak)
                                .get().addOnSuccessListener(queryDocumentSnapshots4 -> {
                                    holder.txtConductWeak.setText(""+queryDocumentSnapshots4.size());
                                });
                    }else{
                        holder.txtConductGood.setText("0");
                        holder.txtConductQuite.setText("0");
                        holder.txtConductAvage.setText("0");
                        holder.txtConductWeak.setText("0");
                    }

                    Log.d(TAG, "onComplete: ReSETTTT" + list.getName());
                } else {

                    holder.txtMount.setText("0");
                }
                Log.d(TAG, "onComplete: ENDD "+list.getName());
            });


        holder.btnConductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListStudentOfConduct.class);
                intent.putExtra("className", list.getName());
                intent.putExtra("teacherName", list.getNameTeacher());
                intent.putExtra("classId", list.getId());
                intent.putExtra("account", account);
                intent.putExtra("semester",semester);
//                intent.putExtra("listStudentOfClass", (Serializable) listStudentOfClass);
//                intent.putExtra("listStudentOfConduct", (Serializable) listStudentOfConduct);
                context.startActivity(intent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return listClass.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView btnConductDetail;
        private TextView txtNameClass, txtMount, txtNameTeacher, txtYear, txtSemester, txtConductGood, txtConductQuite, txtConductAvage, txtConductWeak;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameClass = itemView.findViewById(R.id.txtNameClass);
            txtMount = itemView.findViewById(R.id.txtMount);
            txtNameTeacher = itemView.findViewById(R.id.txtNameTeacher);
            txtConductGood = itemView.findViewById(R.id.txtConductGood);
            txtConductQuite = itemView.findViewById(R.id.txtConductQuite);
            txtConductAvage = itemView.findViewById(R.id.txtConductAvege);
            txtConductWeak = itemView.findViewById(R.id.txtConductWeak);
            btnConductDetail = itemView.findViewById(R.id.btnConductDetail);
            txtYear = itemView.findViewById(R.id.txtYear);
            txtSemester = itemView.findViewById(R.id.txtSemester);
        }
    }
}
