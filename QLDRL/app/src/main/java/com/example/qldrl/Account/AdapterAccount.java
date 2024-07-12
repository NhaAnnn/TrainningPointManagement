package com.example.qldrl.Account;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.General.Student;
import com.example.qldrl.General.Teacher;
import com.example.qldrl.Mistake.ClassRom;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterAccount extends RecyclerView.Adapter<AdapterAccount.MyViewHoder> implements Filterable {
    private Context context;
    private  List<Account> accountList;
    private  List<Account> oldAccountList;

    private DatePickerDialog datePickerDialog;
    public AdapterAccount(Context context, List<Account> accountList) {

        this.context = context;
        this.accountList = accountList;
        this.oldAccountList = accountList;
    }

    @NonNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_acc,parent,false);
        return new AdapterAccount.MyViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
        try {
                Account account = accountList.get(position);
                holder.txtNameAcc.setText(account.getTkTenTK());
                holder.txtAccPass.setText(account.getTkMatKhau());
                holder.txtAccType.setText(account.getTkChucVu());
                holder.txtAccNameClass.setText("class name");

                holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "ban lick thanh caong", Toast.LENGTH_LONG).show();
                        openDinalogDelete(Gravity.CENTER, account, holder.getAdapterPosition());

                    }
                });
                holder.imgBtnEditAcc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDinalogEdit(Gravity.CENTER, account);
                    }
                });

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Lấy thông tin giáo viên
            CollectionReference giaoVienRef = db.collection("giaoVien");
            Query giaoVienQuery = giaoVienRef.whereEqualTo("TK_id", account.getTkID());
            giaoVienQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Tìm thấy giáo viên, không cần lọc qua hocSinh
                            for (DocumentSnapshot document : querySnapshot) {
                                // Lấy thông tin giáo viên và làm gì đó với chúng

                                String lhID = document.getString("LH_id");



                                CollectionReference lopRef = db.collection("lop");
                                Query lopQuery = lopRef.whereEqualTo("LH_id", lhID);
                                lopQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if (!querySnapshot.isEmpty()) {
                                                // Tìm thấy lớp học
                                                for (DocumentSnapshot document : querySnapshot) {
                                                    // Lấy LH_TenLop
                                                    String LH_TenLop = document.getString("LH_TenLop");
                                                    holder.txtAccNameClass.setText(LH_TenLop);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            // Không tìm thấy giáo viên, lọc qua hocSinh
                            CollectionReference hocSinhRef = db.collection("hocSinh");
                            Query hocSinhQuery = hocSinhRef.whereEqualTo("TK_id", account.getTkID());
                            hocSinhQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            // Tìm thấy học sinh
                                            for (DocumentSnapshot document : querySnapshot) {
                                                // Lấy thông tin học sinh và làm gì đó với chúng
                                                String lhid = document.getString("LH_id");

                                                CollectionReference lopRef = db.collection("lop");
                                                Query lopQuery = lopRef.whereEqualTo("LH_id", lhid);
                                                lopQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            if (!querySnapshot.isEmpty()) {
                                                                // Tìm thấy lớp học
                                                                for (DocumentSnapshot document : querySnapshot) {
                                                                    // Lấy LH_TenLop
                                                                    String LH_TenLop = document.getString("LH_TenLop");
                                                                    holder.txtAccNameClass.setText(LH_TenLop);
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            // Không tìm thấy cả giáo viên và học sinh
                                            // Xử lý trường hợp đặc biệt này
                                            holder.txtAccNameClass.setText("");
                                        }
                                    } else {
                                        // Xử lý lỗi
                                    }
                                }
                            });
                        }
                    } else {
                        // Xử lý lỗi
                    }
                }
            });

                Log.d("RecyclerViewAdapter", "Đã gán dữ liệu thành công cho vị trí " + position);
        }catch (Exception e) {
                Log.e("RecyclerViewAdapter", "Lỗi khi gán dữ liệu cho vị trí " + position, e);
            }
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()) {
                    accountList = oldAccountList;
                } else {
                    List<Account>  list = new ArrayList<>();
                    for ( Account account : oldAccountList) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        if(account.getTkChucVu().toLowerCase().contains(strSearch.toLowerCase())
                                || account.getTkTenTK().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(account);
                        }
                    }
                    accountList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values   = accountList;

                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                accountList = (List<Account>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHoder extends RecyclerView.ViewHolder {
        private TextView txtNameAcc, txtAccPass, txtAccNameClass, txtAccType;
        private CardView cardVItemAcc;
        private ImageView imgBtnDelete, imgBtnEditAcc;
        public MyViewHoder(@NonNull View itemView) {
            super(itemView);

            txtNameAcc = itemView.findViewById(R.id.txtNameAcc);
            txtAccPass = itemView.findViewById(R.id.txtAccPass);
            txtAccNameClass = itemView.findViewById(R.id.txtAccNameClass);
            txtAccType = itemView.findViewById(R.id.txtAccType);
            cardVItemAcc = itemView.findViewById(R.id.cardVItemAcc);
            imgBtnDelete = itemView.findViewById(R.id.imgBtnDelete);
            imgBtnEditAcc = itemView.findViewById(R.id.imgBtnEditAcc);

        }
    }

    private void openDinalogDelete(int gravity, Account account, int position) {
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
                Query query = db.collection("taiKhoan")
                        .whereEqualTo("TK_id", account.getTkID());

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

                if(account.getTkChucVu().toLowerCase().equals("giáo viên")) {
                    Query query1 = db.collection("giaoVien")
                            .whereEqualTo("TK_id", account.getTkID());

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

                } else if(account.getTkChucVu().toLowerCase().equals("học sinh") || account.getTkChucVu().toLowerCase().equals("ban cán sự") ) {
                    Query query1 = db.collection("hocSinh")
                            .whereEqualTo("TK_id", account.getTkID());

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


                //    Toast.makeText(context, "Xóa p thành công!", Toast.LENGTH_SHORT).show();

                    Query query2 = db.collection("hanhKiem")
                            .whereEqualTo("HS_id", account.getTkID());

                    // Thực hiện truy vấn và lấy snapshot
                    query2.get().addOnCompleteListener(task -> {
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

                }

                Toast.makeText(context, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
                accountList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, accountList.size());
                dialog.dismiss();

            }

        });
        dialog.show();
    }

    private void openDinalogEdit(int gravity, Account account) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_edit_acc);

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

        EditText editClassED, editNameUserED, editCodeUserED, editPassED, editPassAccAgainED;
        TextView txtDateED, txtNameAccED;
        Spinner spYearED;
        ImageView btnDatePickED;
        RadioGroup rdGoupGender, rdPosition;
        Button btnUpdateAcc, btnExitED;
        RadioButton rdNormal, rdMonitor,rdBoard, rdTeacher,rdFemale,rdMale;

        LinearLayout layoutClass, layoutSpiner, layoutGender, layoutPosition,layoutTitle, layoutEDIT;

        rdBoard = dialog.findViewById(R.id.rdBoard);
        rdNormal = dialog.findViewById(R.id.rdNormal);
        rdTeacher = dialog.findViewById(R.id.rdTeacher);
        rdFemale = dialog.findViewById(R.id.rdFemale);
        rdMale = dialog.findViewById(R.id.rdMale);

        editClassED = dialog.findViewById(R.id.editClassED);
        editNameUserED = dialog.findViewById(R.id.editNameUserED);
        editCodeUserED = dialog.findViewById(R.id.editCodeUserED);
        editPassED = dialog.findViewById(R.id.editPassED);
        editPassAccAgainED = dialog.findViewById(R.id.editPassAgianAccED);
        txtDateED = dialog.findViewById(R.id.txtDateED);
        txtNameAccED = dialog.findViewById(R.id.txtNameAccED);
        spYearED = dialog.findViewById(R.id.spYearED);
        btnDatePickED = dialog.findViewById(R.id.btnDateED);
        rdPosition = dialog.findViewById(R.id.rdPositionED);
        rdGoupGender = dialog.findViewById(R.id.rdGenderED);
        btnExitED = dialog.findViewById(R.id.btnExitED);
        btnUpdateAcc = dialog.findViewById(R.id.btnUpdateAcc);
        layoutGender = dialog.findViewById(R.id.layoutGender);
        layoutClass = dialog.findViewById(R.id.layoutClass);
        layoutPosition = dialog.findViewById(R.id.layoutPosition);
        layoutSpiner = dialog.findViewById(R.id.layoutSpiner);
        layoutTitle = dialog.findViewById(R.id.layoutTitle);
        layoutEDIT = dialog.findViewById(R.id.layoutEDIT);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        rdPosition.setEnabled(false);

        if(account.getTkChucVu().equals("Giáo viên")) {
            CollectionReference taiKhoanRef = db.collection("giaoVien");

            Query query = taiKhoanRef.whereEqualTo("TK_id", account.getTkID());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String id = documentSnapshot.getString("GV_id");
                            String ten = documentSnapshot.getString("GV_HoTen");
                            String ngaySinh = documentSnapshot.getString("GV_NgaySinh");
                            String gioiTinh = documentSnapshot.getString("GV_GioiTinh");
                            String lhid = documentSnapshot.getString("LH_id");
                            String tkid = documentSnapshot.getString("TK_id");
                            Teacher teacher = new Teacher(ten, id, ngaySinh, gioiTinh, lhid, tkid);

                            String nienKhoa = lhid.substring(lhid.length()-9);
                            String nameClass = lhid.substring(0, lhid.length() - 9);
                            editClassED.setText(nameClass);
                            editNameUserED.setText(ten);
                            editCodeUserED.setText(account.getTkTenTK());
                            txtDateED.setText(ngaySinh);
                            txtNameAccED.setText(account.getTkTenTK());
                            editPassED.setText(account.getTkMatKhau());
                            editPassAccAgainED.setText(account.getTkMatKhau());



                            if(gioiTinh.toLowerCase().equals("nam")) {
                                rdMale.setChecked(true);

                            } else {
                                rdGoupGender.check(rdGoupGender.getChildAt(1).getId());
                            }

                            if(account.getTkChucVu().toLowerCase().equals("ban giám hiệu")) {
                                rdPosition.check(rdPosition.getChildAt(0).getId());

                            } else if(account.getTkChucVu().toLowerCase().equals("giáo viên") ){
                                rdPosition.check(rdPosition.getChildAt(1).getId());

                            } else if(account.getTkChucVu().toLowerCase().equals("ban cán sự") || account.getTkChucVu().toLowerCase().equals("học sinh")  ){
                                rdPosition.check(rdPosition.getChildAt(2).getId());

                            } else {
                                rdPosition.check(rdPosition.getChildAt(3).getId());

                            }

                            getListSemester(spYearED, nienKhoa);


                        } else {
                           // Toast.makeText(Login.this, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                       // Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else if(account.getTkChucVu().equals("Học sinh") || account.getTkChucVu().equals("Ban cán sự")){
            CollectionReference taiKhoanRef = db.collection("hocSinh");

            Query query = taiKhoanRef.whereEqualTo("TK_id", account.getTkID());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String id = documentSnapshot.getString("HS_id");
                            String ten = documentSnapshot.getString("HS_HoTen");
                            String ngaySinh = documentSnapshot.getString("HS_NgaySinh");
                            String gioiTinh = documentSnapshot.getString("HS_GioiTinh");
                            String chucVu = documentSnapshot.getString("HS_ChucVu");
                            String lhid = documentSnapshot.getString("LH_id");
                            String tkid = documentSnapshot.getString("TK_id");
                            Student student = new Student(chucVu, ten, ngaySinh, gioiTinh,id, lhid, tkid);


                            String nienKhoa = lhid.substring(lhid.length()-9);
                            String nameClass = lhid.substring(0, lhid.length() - 9);

                            editClassED.setText(nameClass);
                            editNameUserED.setText(ten);
                            editCodeUserED.setText(account.getTkTenTK());
                            txtDateED.setText(ngaySinh);
                            txtNameAccED.setText(account.getTkTenTK());
                            editPassED.setText(account.getTkMatKhau());
                            editPassAccAgainED.setText(account.getTkMatKhau());

                            if(gioiTinh.equals("Nam")) {
                                rdMale.setChecked(true);

                            } else {
                                rdGoupGender.check(rdGoupGender.getChildAt(1).getId());
                            }

                            if(account.getTkChucVu().equals("Ban giám hiệu")) {
                                rdPosition.check(rdPosition.getChildAt(0).getId());

                            } else if(account.getTkChucVu().equals("Giáo viên") ){
                                rdPosition.check(rdPosition.getChildAt(1).getId());

                            } else {
                                rdPosition.check(rdPosition.getChildAt(2).getId());

                            }

                            getListSemester(spYearED, nienKhoa);

                        } else {
                            // Toast.makeText(Login.this, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            editClassED.setText("");
            editNameUserED.setText(account.getTkHoTen());
            editCodeUserED.setText(account.getTkTenTK());
            txtDateED.setText(account.getTkNgaySinh());
            txtNameAccED.setText(account.getTkTenTK());
            editPassED.setText(account.getTkMatKhau());
            editPassAccAgainED.setText(account.getTkMatKhau());

            if(account.getTkChucVu().equals("Ban giám hiệu")) {
                rdPosition.check(rdPosition.getChildAt(0).getId());

            } else if(account.getTkChucVu().equals("Giáo viên") ){
                rdPosition.check(rdPosition.getChildAt(1).getId());

            } else if(account.getTkChucVu().equals("Ban cán sự") ){
                rdPosition.check(rdPosition.getChildAt(2).getId());

            } else {
                rdPosition.check(rdPosition.getChildAt(3).getId());

            }
          //  layoutPosition.setVisibility(View.GONE);
            layoutSpiner.setVisibility(View.GONE);
            layoutGender.setVisibility(View.GONE);
            layoutClass.setVisibility(View.GONE);

            layoutTitle.layout(10,20,10,10);

            ViewGroup.LayoutParams layoutParams = layoutTitle.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;

                // Thiết lập margin
                marginLayoutParams.setMargins(10, 20, 10, 10);

                // Cập nhật lại layoutParams cho layoutTitle
                layoutTitle.setLayoutParams(marginLayoutParams);
            }


            ViewGroup.LayoutParams layoutParams1 = layoutEDIT.getLayoutParams();
            if (layoutParams1 instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams1;

                // Thiết lập margin
                marginLayoutParams.setMargins(10, 20, 10, 10);

                // Cập nhật lại layoutParams cho layoutTitle
                layoutEDIT.setLayoutParams(marginLayoutParams);
            }

        }

        final String[] lhNK = new String[1];

        spYearED.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        initDataPicker(txtDateED);
        btnDatePickED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });



        btnExitED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        btnUpdateAcc.setOnClickListener(v -> {

            if( rdNormal.isChecked() == true)
            {

                boolean isValid = true;

                // Kiểm tra trường "editClassTeacher"
                if (editClassED.getText().toString().isEmpty()) {
                    editClassED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editClassED.setError(null);
                }

                // Kiểm tra trường "editNameTeacher"
                if (editNameUserED.getText().toString().isEmpty()) {
                    editNameUserED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editNameUserED.setError(null);
                }

                // Kiểm tra trường "editCodeTeacher"
                if (editCodeUserED.getText().toString().isEmpty()) {
                    editCodeUserED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editCodeUserED.setError(null);
                }

                // Kiểm tra trường "editPassAgianAccTeacher"
                if (editPassED.getText().toString().isEmpty()) {
                    editPassED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editPassED.setError(null);
                }
                // kiem tra truong editPass
                if (editPassAccAgainED.getText().toString().isEmpty()) {
                    editPassAccAgainED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editPassAccAgainED.setError(null);
                }

                // Kiểm tra radio button "rdGenderTeacher"


                if (!editPassAccAgainED.getText().toString().equals(editPassED.getText().toString())) {
                    editPassAccAgainED.setError("Mật khẩu không khớp");
                    isValid = false;
                } else {
                    editPassAccAgainED.setError(null);
                }
//                    Toast.makeText(context, "eeeee"+isValid, Toast.LENGTH_LONG).show();

                // Nếu tất cả điều kiện hợp lệ, gọi hàm saveTeacher()
                if (isValid) {
                    //saveStudent();
//                  oast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();
                    db.collection("hocSinh")
                            .whereEqualTo("TK_id",account.getTkID())
                            .get()
                            .addOnSuccessListener(querySnapshot1 -> {
                                if (!querySnapshot1.isEmpty()) {
                                    DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("HS_HoTen", editNameUserED.getText().toString().trim());
                                    updates.put("HS_NgaySinh", txtDateED.getText().toString().trim());

                                    int selectedPosi = rdPosition.getCheckedRadioButtonId();
                                    if(selectedPosi != -1) {
                                        RadioButton selecRadio = dialog.findViewById(selectedPosi);

                                        updates.put("HS_ChucVu",selecRadio.getText().toString() );

                                    }

                                    int selectedGender = rdGoupGender.getCheckedRadioButtonId();
                                    if(selectedGender != -1) {
                                        RadioButton selecRadio = dialog.findViewById(selectedGender);

                                        updates.put("HS_GioiTinh",selecRadio.getText().toString() );

                                    }

                                    updates.put("LH_id",editClassED.getText().toString()+lhNK[0] );

                                    docRef.update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, "Cập nhật tài khoản thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();

                                            });
                                } else {
                                    Toast.makeText(context, "Không tìm thấy tài khoản để cập nhật", Toast.LENGTH_LONG).show();

                                }
                            })
                            .addOnFailureListener(e -> {
                                // Lỗi khi truy vấn Firestore
                            });

                    db.collection("taiKhoan")
                            .whereEqualTo("TK_TenTaiKhoan", account.getTkTenTK())
                            .get()
                            .addOnSuccessListener(querySnapshot1 -> {
                                if (!querySnapshot1.isEmpty()) {
                                    DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                    Map<String, Object> updates = new HashMap<>();
                                    //  updates.put("TK_id", editCodeUserED.getText().toString().trim());
                                    updates.put("TK_HoTen", editNameUserED.getText().toString().trim());
                                    updates.put("TK_NgaySinh", txtDateED.getText().toString().trim());

                                    int selectedPosi = rdPosition.getCheckedRadioButtonId();
                                    if(selectedPosi != -1) {
                                        RadioButton selecRadio = dialog.findViewById(selectedPosi);

                                        updates.put("TK_ChucVu",selecRadio.getText().toString() );

                                    }
                                    updates.put("TK_TenTaiKhoan",editCodeUserED.getText().toString().trim());
                                    updates.put("TK_MatKhau", editPassAccAgainED.getText().toString().trim());
                                    docRef.update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, "Cập nhật tài khoản thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();

                                            });
                                } else {
                                    Toast.makeText(context, "Không tìm thấy tài khoản để cập nhật", Toast.LENGTH_LONG).show();

                                }
                            })
                            .addOnFailureListener(e -> {
                                // Lỗi khi truy vấn Firestore
                            });


                }
                dialog.dismiss();

            }
            else if(rdTeacher.isChecked() == true)
            {

                CollectionReference teachersRef = db.collection("giaoVien");

                teachersRef.whereEqualTo("LH_id", editClassED.getText().toString()+lhNK[0])
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                String tkid;
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    tkid = document.getString("TK_id");
                                    // Xử lý giáo viên được tìm thấy
                                    if(!tkid.equals(account.getTkID())) {
                                        //  Toast.makeText(context,"heeko co", Toast.LENGTH_LONG).show();
                                        editClassED.setError("Lớp này đã có GVCN");
                                    }
                                    else
                                    {
                                        editClassED.setError(null);

                                        boolean isValid = true;

                                        // Kiểm tra trường "editClassTeacher"
                                        if (editClassED.getText().toString().isEmpty()) {
                                            editClassED.setError("Không thể bỏ trống!");
                                            isValid = false;
                                        } else {
                                            editClassED.setError(null);
                                        }

                                        // Kiểm tra trường "editNameTeacher"
                                        if (editNameUserED.getText().toString().isEmpty()) {
                                            editNameUserED.setError("Không thể bỏ trống!");
                                            isValid = false;
                                        } else {
                                            editNameUserED.setError(null);
                                        }

                                        // Kiểm tra trường "editCodeTeacher"
                                        if (editCodeUserED.getText().toString().isEmpty()) {
                                            editCodeUserED.setError("Không thể bỏ trống!");
                                            isValid = false;
                                        } else {
                                            editCodeUserED.setError(null);
                                        }

                                        // Kiểm tra trường "editPassAgianAccTeacher"
                                        if (editPassED.getText().toString().isEmpty()) {
                                            editPassED.setError("Không thể bỏ trống!");
                                            isValid = false;
                                        } else {
                                            editPassED.setError(null);
                                        }
                                        // kiem tra truong editPass
                                        if (editPassAccAgainED.getText().toString().isEmpty()) {
                                            editPassAccAgainED.setError("Không thể bỏ trống!");
                                            isValid = false;
                                        } else {
                                            editPassAccAgainED.setError(null);
                                        }

                                        // Kiểm tra radio button "rdGenderTeacher"


                                        if (!editPassAccAgainED.getText().toString().equals(editPassED.getText().toString())) {
                                            editPassAccAgainED.setError("Mật khẩu không khớp");
                                            isValid = false;
                                        } else {
                                            editPassAccAgainED.setError(null);
                                        }

                                        // Toast.makeText(context, "eeeee"+isValid, Toast.LENGTH_LONG).show();

                                        // Nếu tất cả điều kiện hợp lệ, gọi hàm saveTeacher()
                                        if (isValid) {
                                            db.collection("giaoVien")
                                                    .whereEqualTo("TK_id", account.getTkID())
                                                    .get()
                                                    .addOnSuccessListener(querySnapshot1 -> {
                                                        if (!querySnapshot1.isEmpty()) {
                                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("GV_HoTen", editNameUserED.getText().toString().trim());
                                                            updates.put("GV_NgaySinh", txtDateED.getText().toString().trim());

                                                            int selectedPosi = rdPosition.getCheckedRadioButtonId();
                                                            if(selectedPosi != -1) {
                                                                RadioButton seRadio = dialog.findViewById(selectedPosi);

                                                                updates.put("GV_ChucVu",seRadio.getText().toString() );
                                                                //  Toast.makeText(context, "hellcv "+seRadio.getText().toString(), Toast.LENGTH_LONG).show();

                                                            }

                                                            int selectedGender = rdGoupGender.getCheckedRadioButtonId();
                                                            if(selectedGender != -1) {
                                                                RadioButton selecRadio = dialog.findViewById(selectedGender);
                                                                // Toast.makeText(context, "gttu "+selecRadio.getText().toString(), Toast.LENGTH_LONG).show();

                                                                updates.put("GV_GioiTinh",selecRadio.getText().toString() );

                                                            }

                                                            updates.put("LH_id",editClassED.getText().toString()+lhNK[0] );



                                                            docRef.update(updates)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(context, "Cập nhật tài khoản thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                                                                    });
                                                        } else {
                                                            Toast.makeText(context, "Không tìm thấy tài khoản để cập nhật", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Lỗi khi truy vấn Firestore
                                                    });

                                            db.collection("taiKhoan")
                                                    .whereEqualTo("TK_TenTaiKhoan", account.getTkTenTK())
                                                    .get()
                                                    .addOnSuccessListener(querySnapshot1 -> {
                                                        if (!querySnapshot1.isEmpty()) {
                                                            DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("TK_HoTen", editNameUserED.getText().toString().trim());
                                                            updates.put("TK_NgaySinh", txtDateED.getText().toString().trim());
                                                            updates.put("TK_MatKhau", editPassAccAgainED.getText().toString().trim());
                                                            docRef.update(updates)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(context, "Cập nhật tài khoản thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                                                                    });
                                                        } else {
                                                            Toast.makeText(context, "Không tìm thấy tài khoản để cập nhật", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Lỗi khi truy vấn Firestore
                                                    });
                                            dialog.dismiss();
                                        }


                                    }
                                }


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Lỗi khi truy vấn
                                Log.e("Teacher", "Error finding teacher: " + e.getMessage());
                            }
                        });
            }
            else
            {
                boolean isValid = true;

                // Kiểm tra trường "editClassTeacher"
//                                    if (editClassED.getText().toString().isEmpty()) {
//                                        editClassED.setError("Không thể bỏ trống!");
//                                        isValid = false;
//                                    } else {
//                                        editClassED.setError(null);
//                                    }

                // Kiểm tra trường "editNameTeacher"
                if (editNameUserED.getText().toString().isEmpty()) {
                    editNameUserED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editNameUserED.setError(null);
                }

                // Kiểm tra trường "editCodeTeacher"
                if (editCodeUserED.getText().toString().isEmpty()) {
                    editCodeUserED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editCodeUserED.setError(null);
                }

                // Kiểm tra trường "editPassAgianAccTeacher"
                if (editPassED.getText().toString().isEmpty()) {
                    editPassED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editPassED.setError(null);
                }
                // kiem tra truong editPass
                if (editPassAccAgainED.getText().toString().isEmpty()) {
                    editPassAccAgainED.setError("Không thể bỏ trống!");
                    isValid = false;
                } else {
                    editPassAccAgainED.setError(null);
                }

                // Kiểm tra radio button "rdGenderTeacher"


                if (!editPassAccAgainED.getText().toString().equals(editPassED.getText().toString())) {
                    editPassAccAgainED.setError("Mật khẩu không khớp");
                    isValid = false;
                } else {
                    editPassAccAgainED.setError(null);
                }

                // Nếu tất cả điều kiện hợp lệ, gọi hàm saveTeacher()
                if (isValid) {
                    //saveStudent();
//                        Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();


                    db.collection("taiKhoan")
                            .whereEqualTo("TK_TenTaiKhoan", account.getTkTenTK())
                            .get()
                            .addOnSuccessListener(querySnapshot1 -> {
                                if (!querySnapshot1.isEmpty()) {
                                    DocumentReference docRef = querySnapshot1.getDocuments().get(0).getReference();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("TK_id", editCodeUserED.getText().toString().trim());
                                    updates.put("TK_HoTen", editNameUserED.getText().toString().trim());
                                    updates.put("TK_NgaySinh", txtDateED.getText().toString().trim());
                                    updates.put("TK_TenTaiKhoan",editCodeUserED.getText().toString().trim());
                                    updates.put("TK_MatKhau", editPassAccAgainED.getText().toString().trim());
                                    docRef.update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, "Cập nhật tài khoản thất bại, Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();

                                            });
                                } else {
                                    Toast.makeText(context, "Không tìm thấy tài khoản để cập nhật", Toast.LENGTH_LONG).show();

                                }
                            })
                            .addOnFailureListener(e -> {
                                // Lỗi khi truy vấn Firestore
                            });

                    editClassED.setError(null);

                }
            }



//            int selectedPosi = rdPosition.getCheckedRadioButtonId();
//            if(selectedPosi != -1) {
//                RadioButton seRadio = dialog.findViewById(selectedPosi);
//
//               // updates.put("GV_ChucVu",seRadio.getText().toString() );
//                Toast.makeText(context, "hellcv "+seRadio.getText().toString(), Toast.LENGTH_LONG).show();
//
//            }


          //  FirebaseFirestore db = FirebaseFirestore.getInstance();



        });




        dialog.show();
    }




    private void getListSemester(Spinner spinner, String nienKhoa) {
        // Lấy tham chiếu đến collection "hocKy"
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hocKyRef = db.collection("nienKhoa");

        // Tạo một danh sách để lưu trữ các trường HK_HocKy
        List<Category> hocKyList = new ArrayList<>();

        // Lấy dữ liệu từ collection "hocKy"
        hocKyRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua các tài liệu trong collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy giá trị của trường "NK_NienKhoa"
                            String hocKy = document.getString("NK_NienKhoa");

                            // Thêm giá trị vào danh sách
                            hocKyList.add(new Category(hocKy));
                        }

                        // Bây giờ bạn có thể sử dụng danh sách hocKyList ở đâu tùy ý
                        // Ví dụ: hiển thị nó trong một Spinner
                        AdapterCategory adapterCategory = new AdapterCategory(context, R.layout.layout_item_selected, hocKyList);
                        spinner.setAdapter(adapterCategory);

                        // Tìm vị trí của nienKhoa trong danh sách
                        int selectedPosition = -1;
                        for (int i = 0; i < hocKyList.size(); i++) {
                            if (hocKyList.get(i).getNameCategory().equals(nienKhoa)) {
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



    private void openDatePicker() {
        datePickerDialog.show();
    }

    private void initDataPicker(TextView txtDatePick) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month,  year);
                txtDatePick.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.R.style.Theme_Material_Dialog_Alert;
        datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return day + "/" + month +"/"+year;
    }

}
