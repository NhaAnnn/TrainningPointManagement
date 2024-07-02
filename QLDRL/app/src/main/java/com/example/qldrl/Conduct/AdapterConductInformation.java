package com.example.qldrl.Conduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AdapterConductInformation extends RecyclerView.Adapter<AdapterConductInformation.MyViewHolder> {
    private List<ConductInformation> conductInformationList;
    private Account account;
    private Context context;
    private int clickCount = 0;

    public AdapterConductInformation(Context context, List<ConductInformation> conductInformationList, Account account) {
        this.context = context;
        this.conductInformationList = conductInformationList;
        this.account = account;
    }


    @NonNull
    @Override
    public AdapterConductInformation.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.listmistake_of_student, parent,false);
        return new AdapterConductInformation.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterConductInformation.MyViewHolder holder, int position) {
        ConductInformation list = conductInformationList.get(position);
        int cnt = position +1;
        holder.textSTT.setText(""+cnt);
        holder.txtTime.setText(list.getTimeMistake());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("viPham").whereEqualTo("VP_id",list.getIdMistake())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            String nameM = (String) queryDocumentSnapshot.getString("VP_TenViPham");
                            holder.txtNameMistake.setText(nameM);
                        }
                    }
                });
        db.collection("taiKhoan").whereEqualTo("TK_id",list.getIdAccount())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            String nameA = (String) queryDocumentSnapshot.getString("TK_HoTen");
                            holder.txtNameUpdate.setText(nameA);
                        }
                    }
                });

        holder.mistakeView.setVisibility(View.GONE);


        holder.btnMistakeDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (clickCount == 0) {
                        holder.btnExpand.setRotation(180f);
                        holder.mistakeView.setVisibility(View.VISIBLE);
                        clickCount++;
                    } else {
                        holder.btnExpand.setRotation(0f);
                        holder.mistakeView.setVisibility(View.GONE);
                        clickCount = 0;
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return conductInformationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textSTT, txtNameMistake, txtNameUpdate, txtTime;
        private ImageView btnExpand;
        private LinearLayout mistakeView, btnMistakeDetail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textSTT = itemView.findViewById(R.id.textSTT);
            txtNameMistake = itemView.findViewById(R.id.txtNameMistake);
            txtNameUpdate = itemView.findViewById(R.id.txtNameUpdate);
            txtTime = itemView.findViewById(R.id.txtTime);
            btnMistakeDetail = itemView.findViewById(R.id.btnMistakeDetail);
            mistakeView = itemView.findViewById(R.id.mistakeView);
            btnExpand = itemView.findViewById(R.id.btnExpand);
        }
    }
}
