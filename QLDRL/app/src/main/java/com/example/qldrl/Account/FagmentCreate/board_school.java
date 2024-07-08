package com.example.qldrl.Account.FagmentCreate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qldrl.Account.CreateManyAccountCallback;
import com.example.qldrl.Account.listAcc;
import com.example.qldrl.General.Account;
import com.example.qldrl.General.AdapterCategory;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link board_school#newInstance} factory method to
 * create an instance of this fragment.
 */
public class board_school extends Fragment {
    private CreateManyAccountCallback callback;
    private EditText editNameBoard, editCodeBoard, editPassBoard,editPassAgianBoard;
    private Button btnCreateBoard, btnExitBoard;
    private TextView txtNameAccBoard, txtDatePickBoard;
    private RadioGroup rdGPositionBoard, rdGGenderBoard;
    private View myBoardView;
    AdapterCategory adapterCategory;
    private ImageView imgBtnPickBoard;
    private DatePickerDialog datePickerDialog;
    String gioiTinh, chucVu, makhau,ngaySinh, hoTen, maBGH;

    private LinearLayout layoutErrorGenderBoard;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public board_school() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment board_school.
     */
    // TODO: Rename and change types and number of parameters
    public static board_school newInstance(String param1, String param2) {
        board_school fragment = new board_school();
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
    private void onCreateAccount(List<Account> newAccounts) {

        callback.onManyAccountCreated(newAccounts);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myBoardView = inflater.inflate(R.layout.fragment_board_school, container, false);

        editCodeBoard = myBoardView.findViewById(R.id.editCodeBoard);
        editNameBoard = myBoardView.findViewById(R.id.editNameBoard);
        editPassBoard = myBoardView.findViewById(R.id.editPassBoard);
        editPassAgianBoard = myBoardView.findViewById(R.id.editPassAgianBoard);
        imgBtnPickBoard = myBoardView.findViewById(R.id.imgBtnPickBoard);
        txtDatePickBoard = myBoardView.findViewById(R.id.txtDatePickBoard);
        txtNameAccBoard = myBoardView.findViewById(R.id.txtNameAccBoard);
        rdGGenderBoard = myBoardView.findViewById(R.id.rdGGenderBoard);
        rdGPositionBoard = myBoardView.findViewById(R.id.rdGPositionBoard);
        btnCreateBoard = myBoardView.findViewById(R.id.btnCreateBoard);
        btnExitBoard = myBoardView.findViewById(R.id.btnExitBoard);
        layoutErrorGenderBoard = myBoardView.findViewById(R.id.layoutErrorGenderBoard);

        txtDatePickBoard.setText(getTodayDate());

        editCodeBoard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nameAcc = s.toString();
                txtNameAccBoard.setText(nameAcc);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCreateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference giaoVienRef = db.collection("taiKhoan");

                Query query = giaoVienRef.whereEqualTo("TK_TenTaiKhoan", editCodeBoard.getText().toString());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                editCodeBoard.setError("Đã tồn tại tài khoản!");
                            } else {
                                // Toast.makeText(Login.this, "Sai tên tài khoản!", Toast.LENGTH_SHORT).show();
                                editCodeBoard.setError(null);
                                boolean isValid = true;



                                // Kiểm tra trường "editNameTeacher"
                                if (editNameBoard.getText().toString().isEmpty()) {
                                    editNameBoard.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editNameBoard.setError(null);
                                }

                                // Kiểm tra trường "editCodeTeacher"
                                if (editCodeBoard.getText().toString().isEmpty()) {
                                    editCodeBoard.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editCodeBoard.setError(null);
                                }

                                // Kiểm tra trường "editPassAgianAccTeacher"
                                if (editPassBoard.getText().toString().isEmpty()) {
                                    editPassBoard.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editPassBoard.setError(null);
                                }
                                // kiem tra truong editPass
                                if (editPassAgianBoard.getText().toString().isEmpty()) {
                                    editPassAgianBoard.setError("Không thể bỏ trống!");
                                    isValid = false;
                                } else {
                                    editPassAgianBoard.setError(null);
                                }

                                // Kiểm tra radio button "rdGenderTeacher"
                                if (rdGGenderBoard.getCheckedRadioButtonId() == -1) {
                                    layoutErrorGenderBoard.setVisibility(View.VISIBLE);
                                    rdGGenderBoard.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            layoutErrorGenderBoard.setVisibility(View.GONE);
                                        }
                                    });
                                    isValid = false;
                                } else {
                                    layoutErrorGenderBoard.setVisibility(View.GONE);
                                }


                                if(!editPassAgianBoard.getText().toString().equals(editPassBoard.getText().toString())) {
                                    editPassAgianBoard.setError("Mật khẩu không khớp");
                                    isValid = false;
                                } else {
                                    editPassAgianBoard.setError(null);
                                }

                                // Nếu tất cả điều kiện hợp lệ, gọi hàm saveTeacher()
                                if (isValid) {
                                    saveBoard();
                                    listAcc.currentDialog.dismiss();
                                }

                            }
                        } else {
                            //Toast.makeText(Login.this, "ERRR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        imgBtnPickBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        initDataPicker();

        btnExitBoard.setOnClickListener(v -> {
            if (listAcc.currentDialog != null) {
                listAcc.currentDialog.dismiss();
            }
        });
        return myBoardView;
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month,year);

    }

    private void saveBoard() {

        // Kết nối với Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        int selectedRadioButtonId = rdGGenderBoard.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            // Một RadioButton đã được chọn
            RadioButton selectedRadioButton = myBoardView.findViewById(selectedRadioButtonId);
            gioiTinh = selectedRadioButton.getText().toString();
        }

        int selectPosition = rdGPositionBoard.getCheckedRadioButtonId();

        if (selectPosition != -1) {
            // Một RadioButton đã được chọn
            RadioButton selectedRadioButton = myBoardView.findViewById(selectPosition);
            chucVu = selectedRadioButton.getText().toString();
        }

        hoTen = editNameBoard.getText().toString();
        maBGH = editCodeBoard.getText().toString();
        ngaySinh = txtDatePickBoard.getText().toString();
        makhau = editPassBoard.getText().toString();






        CollectionReference luotViPhamRef = db.collection("taiKhoan");

        AtomicInteger count = new AtomicInteger(0);

        luotViPhamRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    count.set(queryDocumentSnapshots.size());
                    int slTK = count.get();
                    DocumentReference docRef = db.collection("taiKhoan").document(maBGH);

// Tạo một Map để lưu trữ dữ liệu
                    Map<String, Object> data = new HashMap<>();
                    data.put("TK_ChucVu", chucVu);
                    data.put("TK_id", maBGH);
                    data.put("TK_HoTen", hoTen);
                    data.put("TK_MatKhau", makhau);
                    data.put("TK_NgaySinh", ngaySinh);
                    data.put("TK_TenTaiKhoan", maBGH);

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



                });

        Account account = new Account(maBGH, maBGH,ngaySinh,makhau,hoTen,chucVu);
        List<Account> listTest = new ArrayList<>();
        listTest.add(account);
        onCreateAccount(listTest);

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
                txtDatePickBoard.setText(date);
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