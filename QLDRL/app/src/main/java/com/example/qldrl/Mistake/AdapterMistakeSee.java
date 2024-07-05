package com.example.qldrl.Mistake;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterMistakeSee  extends RecyclerView.Adapter<AdapterMistakeSee.myViewHolder> {
    int clickCount = 0;
    private List<Mistakes> listMistakes;
    private List<Mistakes> listOldMistakes;
    Account account;
    Student student;
    String hanhKiem = "";
    int Diem = 0, drlhk;

    public AdapterMistakeSee(List<Mistakes> listMistakes,  Context context, Account account, Student student) {
        this.listMistakes = listMistakes;

        this.context = context;
        this.student  = student;
        this.account = account;
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

        holder.relativeDetail.setOnTouchListener(new View.OnTouchListener() {
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



        holder.btnEditMistakeSee.setOnClickListener(v -> {
            Intent intent = new Intent(context, MistakeUpdateMistake.class);
            intent.putExtra("account", account);
            intent.putExtra("student", student);
            intent.putExtra("mistake", mistakes);
            context.startActivity(intent);
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference hanhKiemRef = db.collection("viPham");
        Query query = hanhKiemRef.whereEqualTo("VP_id", mistakes.getVpID());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String diem = documentSnapshot.getString("VP_DiemTru");
                        int diemTRu = Integer.parseInt(diem);
                        getDT(diemTRu);
                    } else {
                        //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                }
            }


        });



        holder.btnDeleteMistakeSee.setOnClickListener(v -> {
            openDinalogDelete(Gravity.CENTER, mistakes, position);
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
        private Button btnEditMistakeSee, btnDeleteMistakeSee;
        private LinearLayout layoutDetail;
        private RelativeLayout relativeDetail;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);


            txtNoMistake = itemView.findViewById(R.id.txtNoMistake);
            txtNameMistakeSee = itemView.findViewById(R.id.txtNameMistakeSee);
            txtPersonalEdit = itemView.findViewById(R.id.txtPersonalEdit);
            txtTimeEdit = itemView.findViewById(R.id.txtTimeEdit);
            imgBtnSeeDetail = itemView.findViewById(R.id.imgBtnSeeDetail);
            btnEditMistakeSee = itemView.findViewById(R.id.btnEditMistakeSee);
            layoutDetail = itemView.findViewById(R.id.layoutDetail);
            btnDeleteMistakeSee = itemView.findViewById(R.id.btnDeleteMistakeSee);
            relativeDetail = itemView.findViewById(R.id.relativeDetail);
        }
    }

    private void getDT(int dt) {
            Diem = dt;
    }
    private void getDRL(int drl) {
        drlhk = drl;
    }
    private void updateHK(@NonNull String hky) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(hky.toLowerCase().equals("học kỳ 1")) {

            CollectionReference hanhKiemRef = db.collection("hanhKiem");
            Query query = hanhKiemRef.whereEqualTo("HKM_id", "HKI"+student.getHsID());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String hkDRl = documentSnapshot.getString("HKM_DiemRenLuyen");
                            int drl = Integer.parseInt(hkDRl);

                            getDRL(drl);

                            drlhk += Diem;

                            if(drlhk >= 90 && drlhk <= 100) {
                                hanhKiem = "Tốt";
                            } else if (drlhk >= 70 && drlhk <= 89) {
                                hanhKiem = "Khá";
                            }else if(drlhk >= 50 && drlhk <= 69){
                                hanhKiem = "Trung bình";
                            }else {
                                hanhKiem = "Yếu";
                            }


                            db.collection("hanhKiem")
                                    .whereEqualTo("HKM_id", "HKI"+student.getHsID())
                                    .get()
                                    .addOnSuccessListener(querySnapshot1 -> {
                                        if (!querySnapshot1.isEmpty()) {
                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("HKM_DiemRenLuyen", drlhk+"");
                                            updates.put("HKM_HanhKiem", hanhKiem);
                                            docRef.update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                    //    Toast.makeText(mistake_edit.this, "Cập nhật hk thành công!", Toast.LENGTH_LONG).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                   //     Toast.makeText(mistake_edit.this, "Cập nhật hk thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();

                                                    });
                                        } else {
                                          //  Toast.makeText(mistake_edit.this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Lỗi khi truy vấn Firestore
                                    });
                        } else {
                            //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else
        {
            CollectionReference hanhKiemRef = db.collection("hanhKiem");
            Query query = hanhKiemRef.whereEqualTo("HKM_id", "HKII"+student.getHsID());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String hkDRl = documentSnapshot.getString("HKM_DiemRenLuyen");
                            int drl = Integer.parseInt(hkDRl);
                            getDRL(drl);
                        } else {
                            //  Toast.makeText(Login.this, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            drlhk += Diem;

            if(drlhk >= 90 && drlhk <= 100) {
                hanhKiem = "Tốt";
            } else if (drlhk >= 70 && drlhk <= 89) {
                hanhKiem = "Khá";
            }else if(drlhk >= 50 && drlhk <= 69){
                hanhKiem = "Trung bình";
            }else {
                hanhKiem = "Yếu";
            }


            db.collection("hanhKiem")
                    .whereEqualTo("HKM_id", "HKII"+student.getHsID())
                    .get()
                    .addOnSuccessListener(querySnapshot1 -> {
                        if (!querySnapshot1.isEmpty()) {
                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("HKM_DiemRenLuyen", drlhk+"");
                            updates.put("HKM_HanhKiem", hanhKiem);
                            docRef.update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                       // Toast.makeText(this, "Cập nhật hk thành công!", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                    //    Toast.makeText(this, "Cập nhật hk thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                                    });
                        } else {
                          //  Toast.makeText(this, "Không tìm thấy  để cập nhật", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(e -> {
                        // Lỗi khi truy vấn Firestore
                    });
        }


    }



    private void openDinalogDelete(int gravity, Mistakes mistakes, int position) {
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
        txtNameDialog.setText("Xóa Vi Phạm");
        txtDialogXoa.setText("Bạn có chắc muốn xóa lượt vi phạm này?");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Query query12 = db.collection("luotViPham")
                    .whereEqualTo("LTVP_id", mistakes.getLtvpID());

            // Thực hiện truy vấn và lấy snapshot
            query12.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (!snapshot.isEmpty()) {
                        // Xóa document
                        for (DocumentSnapshot document : snapshot) {
                            String hocky = document.getString("HK_HocKy");
                            updateHK(hocky);
                        }
                        // System.out.println("Đã xóa document có TK_id = 111 thành công!");
                    } else {
                        // System.out.println("Không tìm thấy document nào có TK_id = 111.");
                    }
                } else {
                    // System.out.println("Lỗi khi thực hiện truy vấn: " + task.getException());
                }
            });



            // Tạo truy vấn để tìm document có TK_id = "111"
            Query query1 = db.collection("luotViPham")
                    .whereEqualTo("LTVP_id", mistakes.getLtvpID());

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


            listMistakes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listMistakes.size());
            Mistake_Board.adapterClassRom.notifyDataSetChanged();
            Mistake_Personal.adaperPersonal.notifyDataSetChanged();
            dialog.dismiss();

        });
        dialog.show();
    }
}
