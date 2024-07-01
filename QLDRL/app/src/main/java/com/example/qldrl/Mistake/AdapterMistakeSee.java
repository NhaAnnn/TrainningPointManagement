package com.example.qldrl.Mistake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.List;

public class AdapterMistakeSee  extends RecyclerView.Adapter<AdapterMistakeSee.myViewHolder> {
    int clickCount = 0;
    private List<Mistakes> listMistakes;
    private List<Mistakes> listOldMistakes;
    Account account;

    public AdapterMistakeSee(List<Mistakes> listMistakes,  Context context) {
        this.listMistakes = listMistakes;

        this.context = context;
    }

    //test show click
    private Context context;

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_see_mistake,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Mistakes mistakes = listMistakes.get(position);
        holder.txtNoMistake.setText((position+1)+"");
        getNameMistake(mistakes.getVpID(), holder);
        getPersonalEdit(mistakes.getTkID(), holder);
        holder.txtTimeEdit.setText(mistakes.ltvpThoiGian);

        holder.imgBtnSeeDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (clickCount == 0) {
                        holder.layoutDetail.setVisibility(View.VISIBLE);
                        holder.imgBtnSeeDetail.setRotation(180f);
                        clickCount++;
                    } else {
                        holder.layoutDetail.setVisibility(View.GONE);
                        holder.imgBtnSeeDetail.setRotation(0f);
                        clickCount = 0;
                    }
                    return true;
                }
                return false;
            }
        });

        holder.imgBtnSeeDetail.setOnClickListener(v -> {
            holder.layoutDetail.setVisibility(View.VISIBLE);
        });
    }

    private void getPersonalEdit(String tkID, myViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference taiKhoanRef = db.collection("taiKhoan");

        Query query = taiKhoanRef.whereEqualTo("TK_id", tkID);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        String hoTenP = documentSnapshot.getString("TK_HoTen");
                        holder.txtPersonalEdit.setText(hoTenP);


                    } else {
                        Toast.makeText(context, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "ERRR", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getNameMistake(String vpID, myViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference taiKhoanRef = db.collection("viPham");

        Query query = taiKhoanRef.whereEqualTo("VP_id", vpID);
        final String[] viPham = {""};
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        String viPhamP = documentSnapshot.getString("VP_TenViPham");
                       holder.txtNameMistakeSee.setText(viPhamP);


                    } else {
                        Toast.makeText(context, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "ERRR", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return listMistakes.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNameMistakeSee, txtNoMistake, txtTimeEdit, txtPersonalEdit;
        private ImageView imgBtnSeeDetail;
        private Button btnEditMistakeSee;
        private LinearLayout layoutDetail;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);


            txtNoMistake = itemView.findViewById(R.id.txtNoMistake);
            txtNameMistakeSee = itemView.findViewById(R.id.txtNameMistakeSee);
            txtPersonalEdit = itemView.findViewById(R.id.txtPersonalEdit);
            txtTimeEdit = itemView.findViewById(R.id.txtTimeEdit);
            imgBtnSeeDetail = itemView.findViewById(R.id.imgBtnSeeDetail);
            btnEditMistakeSee = itemView.findViewById(R.id.btnEditMistakeSee);
            layoutDetail = itemView.findViewById(R.id.layoutDetail);

        }
    }
}
