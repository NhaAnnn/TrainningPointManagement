package com.example.qldrl.Account.FagmentCreate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qldrl.Account.listAcc;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link uploadListBoard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class uploadListBoard extends Fragment {
    private static final int REQUEST_CODE = 123;
    private TextView txtNameFile;
    private Button btnExitAcc,btnUpload, btnChoiceFile;
    private View myListBoard;
    private ActivityResultLauncher<Intent> fileChooserLauncher;
    Uri uri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public uploadListBoard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment uploadList.
     */
    // TODO: Rename and change types and number of parameters
    public static uploadListBoard newInstance(String param1, String param2) {
        uploadListBoard fragment = new uploadListBoard();
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
        // Inflate the layout for this fragment


        myListBoard =  inflater.inflate(R.layout.fragment_upload_list_board, container, false);

        txtNameFile = myListBoard.findViewById(R.id.txtNameFileBoard);
        btnUpload = myListBoard.findViewById(R.id.btnUploadBoard);
        btnChoiceFile = myListBoard.findViewById(R.id.btnChoiceFileBoard);
        btnExitAcc = myListBoard.findViewById(R.id.btnExitBoard);



        btnChoiceFile.setOnClickListener(v -> {
            openFileChooser();
        });
        initFileChooserLauncher();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadExcelDataToFirestore(uri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(getContext(), uri.getPath()+"", Toast.LENGTH_LONG).show();

            }
        });

        btnExitAcc.setOnClickListener(v -> {
            if (listAcc.currentDialog != null) {
                listAcc.currentDialog.dismiss();
            }
        });

        return  myListBoard;
    }



    private void initFileChooserLauncher() {
        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();

                        String fileName = getFileName(fileUri);

                        txtNameFile.setText( fileName);

                        //     Toast.makeText(getContext(), getFilePath(getContext(), fileUri), Toast.LENGTH_LONG).show();

                        uri = fileUri;
                    }
                }
        );
    }


    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileChooserLauncher.launch(intent);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void uploadExcelDataToFirestore(Uri fileUri) throws IOException {

        Log.d("hhhhhhh", "jhhhhhhhhhhh");

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri)) {
            if (inputStream != null) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                ((XSSFWorkbook) workbook).setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Sheet sheet = workbook.getSheetAt(0);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // sheet.getLastRowNum()
                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bắt đầu từ hàng thứ hai (giả sử hàng đầu tiên là tiêu đề)
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        String hsId = getCellValueAsString(row.getCell(0));
                        String hsHoTen = getCellValueAsString(row.getCell(1));
                        String hsNgaySinh = getCellValueAsString(row.getCell(2));
                        //String hsGioiTinh = getCellValueAsString(row.getCell(3));
                       // String hsTenLop = getCellValueAsString(row.getCell(4));
                        String hsChucVu = getCellValueAsString(row.getCell(3));
                       // String hsNienKhoa = getCellValueAsString(row.getCell(6));
                        String hsMatKhau = getCellValueAsString(row.getCell(4));

                        // Lưu dữ liệu học sinh vào Firestore
//                        Map<String, Object> hsData = new HashMap<>();
//                        hsData.put("GV_id", hsId);
//                        hsData.put("GV_HoTen", hsHoTen);
//                        hsData.put("GV_NgaySinh", hsNgaySinh);
//                        hsData.put("GV_GioiTinh", hsGioiTinh);
//                        hsData.put("GV_ChucVu", hsChucVu);
//                        hsData.put("LH_id", hsTenLop+hsNienKhoa);
//                        hsData.put("TK_id", hsId);

                        //    Lưu dữ liệu tài khoản vào Firestore
                        Map<String, Object> tkData = new HashMap<>();
                        tkData.put("TK_id", hsId);
                        tkData.put("TK_HoTen", hsHoTen);
                        tkData.put("TK_TenTaiKhoan", hsId);
                        tkData.put("TK_NgaySinh", hsNgaySinh);
                        tkData.put("TK_ChucVu", hsChucVu);
                        tkData.put("TK_MatKhau", hsMatKhau);

                        // Lưu dữ liệu học sinh vào collection "hocSinh"
//                        DocumentReference hsRef = db.collection("giaoVien").document(hsId);
//                        Task<Void> hsWriteResult = hsRef.set(hsData);
//                        hsWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(getContext(),"ess",Toast.LENGTH_LONG).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Xử lý lỗi khi lưu dữ liệu học sinh
//                                Toast.makeText(getContext(),"effffss",Toast.LENGTH_LONG).show();
//                            }
//                        });

                        DocumentReference tkRef = db.collection("taiKhoan").document(hsId);
                        Task<Void> tkWriteResult = tkRef.set(tkData);
                        tkWriteResult.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Lưu dữ liệu tài khoản thành công
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi khi lưu dữ liệu tài khoản
                            }
                        });
                    }
                }
            }
        }
    }


    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // Nếu ô có kiểu dữ liệu là Date
                Date cellDate = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(cellDate);
            } else {
                // Nếu ô có kiểu dữ liệu là số
                return String.valueOf(cell.getNumericCellValue());
            }
        } else if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return "";
        }
    }

}