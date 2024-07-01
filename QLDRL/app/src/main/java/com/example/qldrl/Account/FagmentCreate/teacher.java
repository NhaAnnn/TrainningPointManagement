package com.example.qldrl.Account.FagmentCreate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qldrl.Account.listAcc;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.General.Category;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link teacher#newInstance} factory method to
 * create an instance of this fragment.
 */
public class teacher extends Fragment {
    private EditText editNameTeacher, editCodeTeacher, editPassTeacher,editPassAgianAccTeacher, editClassTeacher;
    private Button btnCreateTeacher, btnExitFragTeacher;
    private TextView txtNameAccTeacher, txtDatePick;
    private RadioGroup rdPositionTeacher, rdGenderTeacher;
    private View myView;
    AdapterCategory adapterCategory;
    private ImageView btnDatePick;
    private DatePickerDialog datePickerDialog;
    String gioiTinh, chucVu, makhau,ngaySinh, hoTen, maGV;
    private Spinner spYearTeacher;
    private LinearLayout layoutErrorGender;

    String lhid ;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public teacher() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment teacher.
     */
    // TODO: Rename and change types and number of parameters
    public static teacher newInstance(String param1, String param2) {
        teacher fragment = new teacher();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_teacher, container, false);
        // Inflate the layout for this fragment

        btnExitFragTeacher = myView.findViewById(R.id.btnExitFragTeacher);
        btnCreateTeacher = myView.findViewById(R.id.btnCreateTeacher);
        editPassAgianAccTeacher = myView.findViewById(R.id.editPassAgianAccTeacher);
        editPassTeacher = myView.findViewById(R.id.editPassTeacher);
        txtNameAccTeacher = myView.findViewById(R.id.txtNameAccTeacher);
        rdPositionTeacher = myView.findViewById(R.id.rdPositionTeacher);
        rdGenderTeacher = myView.findViewById(R.id.rdGenderTeacher);
        editCodeTeacher = myView.findViewById(R.id.editCodeTeacher);
        editNameTeacher = myView.findViewById(R.id.editNameTeacher);
        txtDatePick = myView.findViewById(R.id.txtDatePick);
        txtDatePick.setText(getTodayDate());
        layoutErrorGender = myView.findViewById(R.id.layoutErrorGender);
        spYearTeacher = myView.findViewById(R.id.spYearTeacher);
        editClassTeacher = myView.findViewById(R.id.editClassTeacher);

        getListYear(spYearTeacher);


        btnExitFragTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                // Xóa dữ liệu từ collection "taiKhoan"
//                CollectionReference taiKhoanRef = FirebaseFirestore.getInstance().collection("taiKhoan");
//                taiKhoanRef.get().addOnSuccessListener(querySnapshot -> {
//                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
//                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                        batch.delete(document.getReference());
//                    }
//                    batch.commit().addOnSuccessListener(aVoid -> {
//                        Log.d("FirebaseDelete", "Xóa dữ liệu từ collection 'taiKhoan' thành công!");
//                    }).addOnFailureListener(e -> {
//                        Log.e("FirebaseDelete", "Lỗi khi xóa dữ liệu từ collection 'taiKhoan': " + e.getMessage());
//                    });
//                }).addOnFailureListener(e -> {
//                    Log.e("FirebaseDelete", "Lỗi khi lấy dữ liệu từ collection 'taiKhoan': " + e.getMessage());
//                });
//
//                // Xóa dữ liệu từ collection "giaoVien"
//                CollectionReference giaoVienRef = FirebaseFirestore.getInstance().collection("giaoVien");
//                giaoVienRef.get().addOnSuccessListener(querySnapshot -> {
//                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
//                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                        batch.delete(document.getReference());
//                    }
//                    batch.commit().addOnSuccessListener(aVoid -> {
//                        Log.d("FirebaseDelete", "Xóa dữ liệu từ collection 'giaoVien' thành công!");
//                    }).addOnFailureListener(e -> {
//                        Log.e("FirebaseDelete", "Lỗi khi xóa dữ liệu từ collection 'giaoVien': " + e.getMessage());
//                    });
//                }).addOnFailureListener(e -> {
//                    Log.e("FirebaseDelete", "Lỗi khi lấy dữ liệu từ collection 'giaoVien': " + e.getMessage());
//                });
            }
        });


//        editPassAgianAccTeacher.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // Không cần xử lý
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Không cần xử lý
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // Kiểm tra xem mật khẩu và nhập lại mật khẩu có khớp nhau không
//                String password = editPassTeacher.getText().toString();
//                String confirmPassword = editPassAgianAccTeacher.getText().toString();
//                if (!password.equals(confirmPassword)) {
//                    editPassAgianAccTeacher.setError("Mật khẩu không khớp");
//                } else {
//                    editPassAgianAccTeacher.setError(null);
//                }
//            }
//        });



        editCodeTeacher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameAcc = s.toString();
                txtNameAccTeacher.setText(nameAcc);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCreateTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference giaoVienRef = db.collection("giaoVien");

                Query query = giaoVienRef.whereEqualTo("GV_id", editCodeTeacher.getText().toString());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                editCodeTeacher.setError("Đã tồn tại tài khoản!");
                            } else {
                                // Toast.makeText(Login.this, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                                editCodeTeacher.setError(null);
                                boolean isValid = true;

                                // Kiểm tra trường "editClassTeacher"
                                if (editClassTeacher.getText().toString().isEmpty()) {
                                    editClassTeacher.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editClassTeacher.setError(null);
                                }

                                // Kiểm tra trường "editNameTeacher"
                                if (editNameTeacher.getText().toString().isEmpty()) {
                                    editNameTeacher.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editNameTeacher.setError(null);
                                }

                                // Kiểm tra trường "editCodeTeacher"
                                if (editCodeTeacher.getText().toString().isEmpty()) {
                                    editCodeTeacher.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editCodeTeacher.setError(null);
                                }

                                // Kiểm tra trường "editPassAgianAccTeacher"
                                if (editPassAgianAccTeacher.getText().toString().isEmpty()) {
                                    editPassAgianAccTeacher.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editPassAgianAccTeacher.setError(null);
                                }
                                // kiem tra truong editPass
                                if (editPassTeacher.getText().toString().isEmpty()) {
                                    editPassTeacher.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editPassTeacher.setError(null);
                                }

                                // Kiểm tra radio button "rdGenderTeacher"
                                if (rdGenderTeacher.getCheckedRadioButtonId() == -1) {
                                    layoutErrorGender.setVisibility(View.VISIBLE);
                                    rdGenderTeacher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            layoutErrorGender.setVisibility(View.GONE);
                                        }
                                    });
                                    isValid = false;
                                } else {
                                    layoutErrorGender.setVisibility(View.GONE);
                                }


                                if(!editPassAgianAccTeacher.getText().toString().equals(editPassTeacher.getText().toString())) {
                                    editPassAgianAccTeacher.setError("Mật khẩu không khớp");
                                    isValid = false;
                                } else {
                                    editPassAgianAccTeacher.setError(null);
                                }

                                // Nếu tất cả điều kiện hợp lệ, gọi hàm saveTeacher()
                                if (isValid) {
                                    saveTeacher();
                                }

                            }
                        } else {
                            //Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btnDatePick = myView.findViewById(R.id.btnDateTeacher);
        btnDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        initDataPicker();
        btnExitFragTeacher.setOnClickListener(v -> {
            if (listAcc.currentDialog != null) {
                listAcc.currentDialog.dismiss();
            }
        });

        return myView;

    }


    private void getListYear(Spinner spinner){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Lấy tham chiếu đến collection "hocKy"
        CollectionReference hocKyRef = db.collection("nienKhoa");

        // Tạo một danh sách để lưu trữ các trường HK_HocKy
        List<Category> nienKhoaList = new ArrayList<>();

        // Lấy dữ liệu từ collection "hocKy"
        hocKyRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Duyệt qua các tài liệu trong collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy giá trị của trường "HK_HocKy"
                            String nienKhoa = document.getString("NK_NienKhoa");

                            // Thêm giá trị vào danh sách
                            nienKhoaList.add(new Category(nienKhoa));
                        }
                        Collections.reverse(nienKhoaList);

                        // Bây giờ bạn có thể sử dụng danh sách hocKyList ở đâu tùy ý
                        // Ví dụ: hiển thị nó trong một Spinner
                        adapterCategory = new AdapterCategory(getContext(), R.layout.layout_item_selected, nienKhoaList);
                        spinner.setAdapter(adapterCategory);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getContext(), adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
                                lhid =  adapterCategory.getItem(position).getNameCategory().toString() ;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
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

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month,year);

    }

    private void saveTeacher() {

        // Kết nối với Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        int selectedRadioButtonId = rdGenderTeacher.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            // Một RadioButton đã được chọn
            RadioButton selectedRadioButton = myView.findViewById(selectedRadioButtonId);
            gioiTinh = selectedRadioButton.getText().toString();
        }

        int selectPosition = rdPositionTeacher.getCheckedRadioButtonId();

        if (selectPosition != -1) {
            // Một RadioButton đã được chọn
            RadioButton selectedRadioButton = myView.findViewById(selectPosition);
            chucVu = selectedRadioButton.getText().toString();
        }

         hoTen = editNameTeacher.getText().toString();
         maGV = editCodeTeacher.getText().toString();
         ngaySinh = txtDatePick.getText().toString();
         makhau = editPassTeacher.getText().toString();






        CollectionReference luotViPhamRef = db.collection("taiKhoan");

        AtomicInteger count = new AtomicInteger(0);

        luotViPhamRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    count.set(queryDocumentSnapshots.size());
                    int slTK = count.get();
                    DocumentReference docRef = db.collection("taiKhoan").document();

// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> data = new HashMap<>();
                    data.put("TK_ChucVu", chucVu);
                    data.put("TK_id", maGV);
                    data.put("TK_HoTen", hoTen);
                    data.put("TK_MatKhau", makhau);
                    data.put("TK_NgaySinh", ngaySinh);
                    data.put("TK_TenTaiKhoan", maGV);

// Lưu dữ liệu vào Firestore
                    docRef.set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   Toast.makeText(getContext(), "success tk", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "g tk"+e, Toast.LENGTH_LONG).show();

                                }
                            });


                    DocumentReference docgvRef = db.collection("giaoVien").document();

// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> dataGV = new HashMap<>();
                    dataGV.put("GV_GioiTinh", gioiTinh);
                    dataGV.put("GV_HoTen", hoTen);
                    dataGV.put("GV_NgaySinh", ngaySinh);
                    dataGV.put("GV_id", maGV);
                    dataGV.put("LH_id",editClassTeacher.getText().toString().trim()+ lhid);
                    dataGV.put("TK_id", maGV);

// Lưu dữ liệu vào Firestore
                    docgvRef.set(dataGV)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "success tk"+slTK +lhid, Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "gg tk"+e, Toast.LENGTH_LONG).show();

                                }
                            });

                });




    }

    private void openDatePicker() {
        datePickerDialog.show();
    }

    private void initDataPicker() {
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
        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return day + "/" + month +"/"+year;
    }
}