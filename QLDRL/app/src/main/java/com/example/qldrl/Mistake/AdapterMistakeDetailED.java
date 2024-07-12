package com.example.qldrl.Mistake;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdapterMistakeDetailED extends RecyclerView.Adapter<AdapterMistakeDetailED.mistakeViewHoder> {

    public AdapterMistakeDetailED(List<MistakeType> listMistake, Context context, String namePersonl, Account account, Student student) {
        this.context = context;
        this.listMistake = listMistake;
        this.namePersonl = namePersonl;
        this.account = account;
        this.student = student;

    }
    @SuppressLint("StaticFieldLeak")
    public static AdapterMistakeED adapterMistakeED;
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
        return new AdapterMistakeDetailED.mistakeViewHoder(view);
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
            holder.imgDeleteType.setVisibility(View.VISIBLE);

            holder.imgDeleteType.setOnClickListener(v -> {
                openDinalogDelete(Gravity.CENTER, mistake, position);
            });

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

                            adapterMistakeED = new AdapterMistakeED(vpTenViPhams, context, namePersonl, account, student);
                           holder.recycMistakeDetail.setAdapter(adapterMistakeED);
                           holder.recycMistakeDetail.setLayoutManager(new GridLayoutManager(context, 1));
                       })
                       .addOnFailureListener(e -> {
                           Toast.makeText(context, "Error getting documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        private TextView txtNameMistakeDetail;
        private ImageView imgMistakeDetail, imgDeleteType;
        private RecyclerView recycMistakeDetail;
        private LinearLayout layoutDetailss;
        public mistakeViewHoder(@NonNull View itemView) {
            super(itemView);
            txtNameMistakeDetail = itemView.findViewById(R.id.txtNameMistakeDetail);
            imgMistakeDetail = itemView.findViewById(R.id.imgMistakeDetail);
            recycMistakeDetail = itemView.findViewById(R.id.recycMistakeDetail);
            layoutDetailss = itemView.findViewById(R.id.layoutDetailss);
            imgDeleteType = itemView.findViewById(R.id.imgDeleteType);
        }
    }

    @SuppressLint("SetTextI18n")
    private void openDinalogDelete(int gravity, MistakeType mistake, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_account_dinalog_delete);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        TextView txtDialogXoa = dialog.findViewById(R.id.txtDialogXoa);
        TextView txtNameDialog = dialog.findViewById(R.id.txtNameDialog);
        txtNameDialog.setText("Xóa Loại Vi Phạm");
        txtDialogXoa.setText("Bạn có chắc muốn Loại vi phạm? Lưu ý: Toàn bộ vi phạm thuộc loại này " +
                "cũng sẽ bị xóa");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(context, "helllo", Toast.LENGTH_LONG).show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Tạo truy vấn để tìm document có TK_id = "111"
                Query query = db.collection("loaiViPham")
                        .whereEqualTo("LVP_id", mistake.getLvpID());

                // Thực hiện truy vấn và lấy snapshot
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (!snapshot.isEmpty()) {
                            // Xóa document
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                document.getReference().delete();
                            }
                            // System.out.println("Đã xóa document có TK_id = 111 thành công!");
                        } else {
                            // System.out.println("Không tìm thấy document nào có TK_id = 111.");
                        }
                    } else {
                        // System.out.println("Lỗi khi thực hiện truy vấn: " + task.getException());
                    }
                });


                    Query query1 = db.collection("viPham")
                            .whereEqualTo("LVP_id", mistake.getLvpID());

                    // Thực hiện truy vấn và lấy snapshot
                    query1.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (!snapshot.isEmpty()) {
                                // Xóa document
                                for (DocumentSnapshot document : snapshot.getDocuments()) {
                                    document.getReference().delete();
                                }
                                // System.out.println("Đã xóa document có TK_id = 111 thành công!");
                            } else {
                                // System.out.println("Không tìm thấy document nào có TK_id = 111.");
                            }
                        } else {
                            // System.out.println("Lỗi khi thực hiện truy vấn: " + task.getException());
                        }
                    });
                    //     Toast.makeText(context, "Xóa p thành công!", Toast.LENGTH_SHORT).show();


                Toast.makeText(context, "Xóa Loại Vi Phạm thành công!", Toast.LENGTH_SHORT).show();
                listMistake.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listMistake.size());
                dialog.dismiss();

            }

        });
        dialog.show();
    }
}
