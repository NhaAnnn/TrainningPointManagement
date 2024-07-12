package com.example.qldrl.Mistake;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.General.Student;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterMistakeED extends RecyclerView.Adapter<AdapterMistakeED.mistakeViewHoder> {

    public AdapterMistakeED(List<Mistake> listMistake, Context context, String namePersonl, Account account, Student student) {
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
        return new AdapterMistakeED.mistakeViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mistakeViewHoder holder, int position) {
        try {
            Mistake mistake = listMistake.get(position); // sử dụng listID chẵn hạn
            holder.txtVMistake.setText(mistake.getNameMistake());
            holder.txtVMistake.setOnClickListener(v -> {
                openDinalogEDMistake(Gravity.CENTER, account,position, mistake);
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



    private void openDinalogEDMistake(int gravity, Account account, int position, Mistake mistake) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_mistake);

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

        TextView txtNameDialogMistake = dialog.findViewById(R.id.txtNameDialogMistake);
        txtNameDialogMistake.setText("Chỉnh sửa vi phạm");

        Spinner spTypeMistake = dialog.findViewById(R.id.spTypeMistake);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("loaiViPham")
                .whereEqualTo("LVP_id", mistake.getLvpid())
                .get()
                .addOnCompleteListener(task -> {
                   for(QueryDocumentSnapshot doc : task.getResult()) {
                       String nameVP = doc.getString("LVP_TenLoaiViPham");
                       getListSemester(spTypeMistake,nameVP);
                   }
                });


        final String[] lhNK = new String[1];

        spTypeMistake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = (String) parent.getItemAtPosition(position);
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                // Sử dụng selectedCategory object thay vì cast nó thành String
                String categoryName = selectedCategory.getNameCategory();
                //  Toast.makeText(context, "g"+categoryName, Toast.LENGTH_LONG).show();
                lhNK[0] = categoryName;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        EditText editNameMistake = dialog.findViewById(R.id.editNameMistake);
        editNameMistake.setText(mistake.getNameMistake());

        EditText editPointMistake = dialog.findViewById(R.id.editPointMistake);
        editPointMistake.setText(mistake.getVpDiemtru());

        LinearLayout layoutDelete = dialog.findViewById(R.id.layoutDelete);
        layoutDelete.setVisibility(View.VISIBLE);

        Button btnAddMistake = dialog.findViewById(R.id.btnAddMistake);
        Button btnExitAddMistake = dialog.findViewById(R.id.btnExitAddMistake);
        Button btnDeleteMis = dialog.findViewById(R.id.btnDeleteMis);

        btnAddMistake.setText("Chỉnh sửa");

        btnExitAddMistake.setOnClickListener(v -> dialog.dismiss());

        btnDeleteMis.setOnClickListener(v -> {
            openDinalogDelete(Gravity.CENTER, mistake, position);
        });

        btnAddMistake.setOnClickListener( v -> {
           DocumentReference docRef =  db.collection("viPham")
                   .document(mistake.getVpID());

            Map<String, Object> updates = new HashMap<>();
            updates.put("VP_TenViPham",editNameMistake.getText().toString().trim());
            updates.put("VP_DiemTru", editPointMistake.getText().toString().trim());



            db.collection("loaiViPham")
                    .whereEqualTo("LVP_TenLoaiViPham", lhNK[0])
                    .get()
                    .addOnCompleteListener(task -> {
                        for(QueryDocumentSnapshot doc : task.getResult()) {
                            String nameIDVP = doc.getString("LVP_id");
                            updates.put("LVP_id", nameIDVP);
                            docRef.update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Chỉnh sửa vi phạm thành công!", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText(context, "Chỉnh sửa vi phạm thất bại! "+e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
//                        mistake_detail_edit.recycDetail.notifyAll();
                    });



           // dialog.dismiss();
        });
        dialog.show();
    }


    private void getListSemester(Spinner spinner, String tenLVP) {
        // Lấy tham chiếu đến collection "hocKy"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hocKyRef = db.collection("loaiViPham");

        // Tạo một danh sách để lưu trữ các trường HK_HocKy
        List<MistakeType> hocKyList = new ArrayList<>();
        List<Category> tenVPs = new ArrayList<>();

        // Lấy dữ liệu từ collection "hocKy"
        hocKyRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua các tài liệu trong collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy giá trị của trường "NK_NienKhoa"
                            String lvpId = document.getString("LVP_id");
                            String tenLVP = document.getString("LVP_TenLoaiViPham");

                            // Thêm giá trị vào danh sách
                            hocKyList.add(new MistakeType(lvpId,tenLVP));
                            tenVPs.add(new Category(tenLVP));
                        }

                        // Bây giờ bạn có thể sử dụng danh sách hocKyList ở đâu tùy ý
                        // Ví dụ: hiển thị nó trong một Spinner
                        AdapterCategory adapterCategory = new AdapterCategory(context, R.layout.layout_item_selected, tenVPs);
                        spinner.setAdapter(adapterCategory);

                        // Tìm vị trí của nienKhoa trong danh sách
                        int selectedPosition = -1;
                        for (int i = 0; i < tenVPs.size(); i++) {
                            if (tenVPs.get(i).getNameCategory().equals(tenLVP)) {
                                selectedPosition = i;
                                break;
                            }
                        }

                        // Set giá trị mặc định cho Spinner
                        if (selectedPosition != -1) {
                            spinner.setSelection(selectedPosition);
                        }

                        //    Toast.makeText(context, selectedPosition+"", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi, ví dụ: hiển thị thông báo lỗi
                        Log.e("FirestoreError", "Lỗi khi lấy dữ liệu từ Firestore: " + e.getMessage());
                    }
                });
    }

    private void openDinalogDelete(int gravity, Mistake mistake, int position) {
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
        txtDialogXoa.setText("Bạn có chắc muốn  vi phạm?");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(v -> {
            //  Toast.makeText(context, "helllo", Toast.LENGTH_LONG).show();
            //    Toast.makeText(context, "helllo", Toast.LENGTH_LONG).show();
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

            // Tạo truy vấn để tìm document có TK_id = "111"
            Query query = db1.collection("viPham")
                    .whereEqualTo("VP_id", mistake.getVpID());

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
                        Toast.makeText(context, "Xóa tài vi phạm thành công!", Toast.LENGTH_SHORT).show();

                    } else {
                        // System.out.println("Không tìm thấy document nào có TK_id = 111.");
                        Toast.makeText(context, "Lỗi không tìm thấy vi phạm!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(context, "Lỗi truy vấn!", Toast.LENGTH_SHORT).show();

                }
            });


            listMistake.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listMistake.size());
            dialog.dismiss();

        });
        dialog.show();
    }
}
