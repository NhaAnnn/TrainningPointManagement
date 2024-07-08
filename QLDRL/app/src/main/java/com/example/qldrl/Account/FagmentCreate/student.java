package com.example.qldrl.Account.FagmentCreate;

import static com.example.qldrl.Account.listAcc.currentDialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.qldrl.Account.CreateManyAccountCallback;
import com.example.qldrl.General.Account;
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
 * Use the {@link student#newInstance} factory method to
 * create an instance of this fragment.
 */
public class student extends Fragment {

    private CreateManyAccountCallback callback;
    Activity activity;
    private EditText editNameClassStudent, editNameStudent, editCodeStudent,editPassStudent, editPassAgainStudent;
    private ImageView imgBtnPickDate;
    private TextView txtDateStudent, txtCodeAccStudent;
    private Spinner spYearStudent;
    private Button btnExitStudent, btnCreatStudent;
    private RadioGroup rdGPositionStudent, rdGGender;

    private View myStudentView;
    private AdapterCategory adapterCategory;

    String gioiTinh, chucVu, makhau,ngaySinh, hoTen, maHS, lhid;
    LinearLayout layoutErrorGenderStudent, layoutErrorPositionStudent;


    private DatePickerDialog datePickerDialog;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public student() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment student.
     */
    // TODO: Rename and change types and number of parameters
    public static student newInstance(String param1, String param2) {
        student fragment = new student();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateManyAccountCallback) {
            callback = (CreateManyAccountCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CreateAccountCallback");
        }
    }

    // ...

    private void onCreateAccount(List<Account> newAccounts) {
        // Xử lý tại Fragment khi tài khoản mới được tạo
        // Ví dụ: cập nhật dữ liệu, hiển thị thông báo, v.v.

        // Sau đó gọi callback để thông báo cho Activity
        callback.onManyAccountCreated(newAccounts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myStudentView = inflater.inflate(R.layout.fragment_student, container, false);

        btnExitStudent = myStudentView.findViewById(R.id.btnExitStudent);
        btnCreatStudent = myStudentView.findViewById(R.id.btnCreatStudent);
        imgBtnPickDate = myStudentView.findViewById(R.id.imgBtnPickDate);
        txtCodeAccStudent = myStudentView.findViewById(R.id.txtCodeAccStudent);
        txtDateStudent = myStudentView.findViewById(R.id.txtDateStudent);
        editCodeStudent = myStudentView.findViewById(R.id.editCodeStudent);
        editNameStudent = myStudentView.findViewById(R.id.editNameStudent);
        editNameClassStudent = myStudentView.findViewById(R.id.editNameClassStudent);
        editPassStudent = myStudentView.findViewById(R.id.editPassStudent);
        editPassAgainStudent = myStudentView.findViewById(R.id.editPassAgainStudent);
        rdGGender = myStudentView.findViewById(R.id.rdGGender);
        rdGPositionStudent = myStudentView.findViewById(R.id.rdGPositionStudent);
        spYearStudent = myStudentView.findViewById(R.id.spYearStudent);
        layoutErrorGenderStudent = myStudentView.findViewById(R.id.layoutErrorGenderStudent);
        layoutErrorPositionStudent = myStudentView.findViewById(R.id.layoutErrorPositionStudent);


        txtDateStudent.setText(getTodayDate());

        getListYear(spYearStudent);
        //setText nameAcc
        editCodeStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameAcc = s.toString();
                txtCodeAccStudent.setText(nameAcc);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //create acc student
        btnCreatStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference giaoVienRef = db.collection("hocSinh");

                Query query = giaoVienRef.whereEqualTo("HS_id", editCodeStudent.getText().toString());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                editCodeStudent.setError("Đã tồn tại tài khoản!");
                            } else {
                                // Toast.makeText(Login.this, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                                editCodeStudent.setError(null);
                                boolean isValid = true;

                                // Kiểm tra trường "editClassTeacher"
                                if (editNameClassStudent.getText().toString().isEmpty()) {
                                    editNameClassStudent.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editNameClassStudent.setError(null);
                                }

                                // Kiểm tra trường "editNameTeacher"
                                if (editNameStudent.getText().toString().isEmpty()) {
                                    editNameStudent.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editNameStudent.setError(null);
                                }

                                // Kiểm tra trường "editCodeTeacher"
                                if (editCodeStudent.getText().toString().isEmpty()) {
                                    editCodeStudent.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editCodeStudent.setError(null);
                                }

                                // Kiểm tra trường "editPassAgianAccTeacher"
                                if (editPassStudent.getText().toString().isEmpty()) {
                                    editPassStudent.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editPassStudent.setError(null);
                                }
                                // kiem tra truong editPass
                                if (editPassAgainStudent.getText().toString().isEmpty()) {
                                    editPassAgainStudent.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editPassAgainStudent.setError(null);
                                }

                                // Kiểm tra radio button "rdGenderTeacher"
                                if (rdGGender.getCheckedRadioButtonId() == -1) {
                                    layoutErrorGenderStudent.setVisibility(View.VISIBLE);
                                    rdGGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            layoutErrorGenderStudent.setVisibility(View.GONE);
                                        }
                                    });
                                    isValid = false;
                                } else {
                                    layoutErrorGenderStudent.setVisibility(View.GONE);
                                }

                                if (rdGPositionStudent.getCheckedRadioButtonId() == -1) {
                                    layoutErrorPositionStudent.setVisibility(View.VISIBLE);
                                    rdGPositionStudent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            layoutErrorPositionStudent.setVisibility(View.GONE);
                                        }
                                    });
                                    isValid = false;
                                } else {
                                    layoutErrorPositionStudent.setVisibility(View.GONE);
                                }


                                if(!editPassAgainStudent.getText().toString().equals(editPassStudent.getText().toString())) {
                                    editPassAgainStudent.setError("Mật khẩu không khớp");
                                    isValid = false;
                                } else {
                                    editPassAgainStudent.setError(null);
                                }

                                // Nếu tất cả điều kiện hợp lệ, gọi hàm saveTeacher()
                                if (isValid) {
                                    saveStudent();
                                    closeDialog();
                                }

                            }
                        } else {
                            //Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }


        });



        imgBtnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        initDataPicker();

        btnExitStudent.setOnClickListener(v -> {
            if (currentDialog != null) {
                currentDialog.dismiss();
            }
        });

        return myStudentView;
    }
    private void closeDialog() {

        if (currentDialog != null) {
            currentDialog.dismiss();
        }

}
    private void dismissDialog() {
        // Lấy tham chiếu đến dialog cha (có thể là Activity hoặc Fragment)
        DialogFragment parentDialog = (DialogFragment) getParentFragment();

        // Đóng dialog
        if (parentDialog != null) {
            parentDialog.dismiss();
        }
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
                              //  Toast.makeText(getContext(), adapterCategory.getItem(position).getNameCategory(), Toast.LENGTH_SHORT).show();
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
    private void openDatePicker() {
        datePickerDialog.show();
    }

    private void initDataPicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month,  year);
                txtDateStudent.setText(date);
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

    private void saveStudent() {

        // Kết nối với Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        int selectedRadioButtonId = rdGGender.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            // Một RadioButton đã được chọn
            RadioButton selectedRadioButton = myStudentView.findViewById(selectedRadioButtonId);
            gioiTinh = selectedRadioButton.getText().toString();
        }

        int selectPosition = rdGPositionStudent.getCheckedRadioButtonId();

        if (selectPosition != -1) {
            // Một RadioButton đã được chọn
            RadioButton selectedRadioButton = myStudentView.findViewById(selectPosition);
            chucVu = selectedRadioButton.getText().toString();
        }

        hoTen = editNameStudent.getText().toString();
        maHS = editCodeStudent.getText().toString();
        ngaySinh = txtDateStudent.getText().toString();
        makhau = editPassStudent.getText().toString();






        CollectionReference luotViPhamRef = db.collection("taiKhoan");

        AtomicInteger count = new AtomicInteger(0);

        luotViPhamRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    count.set(queryDocumentSnapshots.size());
                    int slTK = count.get();
                    DocumentReference docRef = db.collection("taiKhoan").document(maHS);

// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> data = new HashMap<>();
                    data.put("TK_ChucVu", chucVu);
                    data.put("TK_id", maHS);
                    data.put("TK_HoTen", hoTen);
                    data.put("TK_MatKhau", makhau);
                    data.put("TK_NgaySinh", ngaySinh);
                    data.put("TK_TenTaiKhoan", maHS);

// Lưu dữ liệu vào Firestore
                    docRef.set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Tạo tài khoản thành công!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Lỗi "+e, Toast.LENGTH_LONG).show();

                                }
                            });


                    DocumentReference docHSRef = db.collection("hocSinh").document(maHS);

// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> dataHS = new HashMap<>();
                    dataHS.put("HS_GioiTinh", gioiTinh);
                    dataHS.put("HS_ChucVu", chucVu);
                    dataHS.put("HS_HoTen", hoTen);
                    dataHS.put("HS_NgaySinh", ngaySinh);
                    dataHS.put("HS_id", maHS);
                    dataHS.put("LH_id",editNameClassStudent.getText().toString().trim()+ lhid);
                    dataHS.put("TK_id", maHS);

// Lưu dữ liệu vào Firestore
                    docHSRef.set(dataHS)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   // Toast.makeText(getContext(), "success tk"+slTK +lhid, Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                 //  Toast.makeText(getContext(), "gg tk"+e, Toast.LENGTH_LONG).show();
                                }
                            });


                    DocumentReference HKiemHSRef = db.collection("hanhKiem").document("HKI"+maHS);
                    Map<String, Object> HK1data = new HashMap<>();
                    HK1data.put("HKM_DiemRenLuyen", "100");
                    HK1data.put("HKM_id", "HKI"+maHS);
                    HK1data.put("HKM_HanhKiem", "Tốt");
                    HK1data.put("HK_HocKy", "Học kỳ 1");
                    HK1data.put("HS_id", maHS);

                    HKiemHSRef.set(HK1data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "success HK", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "gg HK"+e, Toast.LENGTH_LONG).show();
                                }
                            });


                    DocumentReference HKiem2HSRef = db.collection("hanhKiem").document("HKII"+maHS);

                    Map<String, Object> HK2data = new HashMap<>();
                    HK2data.put("HKM_DiemRenLuyen", "100");
                    HK2data.put("HKM_id", "HKII"+maHS);
                    HK2data.put("HKM_HanhKiem", "Tốt");
                    HK2data.put("HK_HocKy", "Học kỳ 2");
                    HK2data.put("HS_id", maHS);

                    HKiem2HSRef.set(HK2data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                  //  Toast.makeText(getContext(), "success HK", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                   // Toast.makeText(getContext(), "gg HK"+e, Toast.LENGTH_LONG).show();
                                }
                            });
                });


            Account account = new Account(maHS, maHS,ngaySinh,makhau,hoTen,chucVu);
            List<Account> listTest = new ArrayList<>();
            listTest.add(account);
            onCreateAccount(listTest);



    }
}