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

import com.example.qldrl.Class.ListStudentOfClass;
import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AdapterListStudentOfConduct extends RecyclerView.Adapter<AdapterListStudentOfConduct.MyViewHolder> {
    private List<ListStudentOfClass> listStudentOfClass;
    private List<ListStudentOfConduct> listStudentOfConduct;
    private ListStudentOfConduct listOConduct;
    private Account account;
    private Context context;
    private String semester;

    public AdapterListStudentOfConduct(List<ListStudentOfClass> listStudentOfClass,Context context, Account account, String semester){
        this.context = context;
        this.listStudentOfClass = listStudentOfClass;
        this.account = account;
        this.semester = semester;
    }

    @NonNull
    @Override
    public AdapterListStudentOfConduct.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.liststudent_of_conduct_information, parent,false);
        return new AdapterListStudentOfConduct.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListStudentOfConduct.MyViewHolder holder, int position) {
        ListStudentOfClass listOClass = this.listStudentOfClass.get(position);

        String StudentTrainningPoint = new String();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("hanhKiem").whereEqualTo("HS_id", listOClass.getId()).whereEqualTo("HK_HocKy",semester);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        String idConduct = (String) queryDocumentSnapshot.getString("HKM_id");
                        String idStudent = (String) queryDocumentSnapshot.getString("HS_id");
                        String trainingPoint = (String) queryDocumentSnapshot.getString("HKM_DiemRenLuyen");
                        String conduct = (String) queryDocumentSnapshot.getString("HKM_HanhKiem");
                        String term = (String) queryDocumentSnapshot.getString("HK_HocKi");
                        listOConduct = new ListStudentOfConduct(idConduct, idStudent, trainingPoint, conduct, term);


                        holder.txtTrainingPointStudent.setText(trainingPoint);
                        holder.txtConductStudent.setText(conduct);
                    }
                }
            }
        });
        holder.txtIdStudent.setText(listOClass.getId());
        holder.txtNameStudent.setText(listOClass.getName());


        holder.btnConductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConductInformation.class);
                intent.putExtra("StudentName", listOClass.getName());
                intent.putExtra("StudentId", listOClass.getId());
                intent.putExtra("StudentTrainningPoint", listOConduct.getTrainingPoint());
                intent.putExtra("StudentConduct",listOConduct.getConduct());
                intent.putExtra("semester",semester);
                intent.putExtra("account", account);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listStudentOfClass.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtIdStudent, txtNameStudent, txtTrainingPointStudent, txtConductStudent;
        private ImageView btnConductDetail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtIdStudent = itemView.findViewById(R.id.txtIdStudent);
            txtNameStudent = itemView.findViewById(R.id.txtNameStudent);
            txtTrainingPointStudent = itemView.findViewById(R.id.txtTrainingPointStudent);
            txtConductStudent = itemView.findViewById(R.id.txtConductStudent);
            btnConductDetail = itemView.findViewById(R.id.btnConductDetail);
        }
    }
}
