package com.example.qldrl.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.qldrl.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialogCreateFileClass#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogCreateFileClass extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DialogCreateFileClass() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialogCreateFileClass.
     */
    // TODO: Rename and change types and number of parameters
    public static DialogCreateFileClass newInstance(String param1, String param2) {
        DialogCreateFileClass fragment = new DialogCreateFileClass();
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
    private ActivityResultLauncher<Intent> openDocumentLauncher;
    private Uri selectedFileUri;
    private TextView srcFile;
    private Button btnChooseFile, saveButton, cancelButton;
    private List<ListClass> listClasses;
    private View createFileClass;

    public void initFileChooserLauncher(){
        openDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        String fileName = getFileName(fileUri);
                        srcFile.setText( fileName);
                        //     Toast.makeText(getContext(), getFilePath(getContext(), fileUri), Toast.LENGTH_LONG).show();
                        selectedFileUri = fileUri;
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        createFileClass =  inflater.inflate(R.layout.fragment_dialog_create_file_class, container, false);

        srcFile = createFileClass.findViewById(R.id.srcFile);
        saveButton = createFileClass.findViewById(R.id.saveButton);
        btnChooseFile = createFileClass.findViewById(R.id.btnChooseFile);
        cancelButton = createFileClass.findViewById(R.id.cancelButton);



        btnChooseFile.setOnClickListener(v -> {
            openFileChooser();
        });
        initFileChooserLauncher();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadExcelDataToFirestore(selectedFileUri);
                if (ListClass.dialog != null) {
                    EventBus.getDefault().post(new FileClassUpdatedEvent(listClasses));
                    ListClass.dialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(v -> {
            if (ListClass.dialog != null) {
                ListClass.dialog.dismiss();
            }
        });

        return  createFileClass;
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        openDocumentLauncher.launch(intent);
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

    public void uploadExcelDataToFirestore(Uri selectedFileUri){
        if (selectedFileUri != null) {
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedFileUri);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
                listClasses = new ArrayList<>();
                // Duyệt qua các hàng trong sheet và đẩy dữ liệu lên Firebase
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        // Lấy dữ liệu từ các ô trong hàng
                        Map<String, Object> rowData = new HashMap<>();


                        String className = "LH_TenLop";
                        Object classNameValue = row.getCell(1).getStringCellValue(); // Tên lớp
                        String classNameVal = row.getCell(1).getStringCellValue();
                        rowData.put(className, classNameValue);

                        String teacherName = "LH_GVCN";
                        Object teacherNameValue = row.getCell(2).getStringCellValue(); // Tên GVCN
                        String teacherNameVal = row.getCell(2).getStringCellValue();
                        rowData.put(teacherName, teacherNameValue);

                        String year = "NK_NienKhoa";
                        Object yearValue = row.getCell(3).getStringCellValue(); // Niên khóa
                        String yearVal = row.getCell(3).getStringCellValue();
                        rowData.put(year, yearValue);

                        String id = "LH_id";
                        String idVal = (classNameValue.toString().toLowerCase() + yearValue).trim();
                        Object idValue = (classNameValue.toString().toLowerCase() + yearValue).trim();
                        rowData.put(id,idValue);

                        ListClass aClass = new ListClass(idVal, classNameVal,teacherNameVal, yearVal);
                        listClasses.add(aClass);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("lop").document(idVal).set(rowData);
                    }
                }

                inputStream.close();
                workbook.close();

                // Hiển thị thông báo thành công
                Toast.makeText(getContext(), "Dữ liệu đã được tải lên Firebase thành công!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                // Hiển thị thông báo lỗi
                Toast.makeText(getContext(), "Có lỗi xảy ra khi tải dữ liệu lên Firebase.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn file Excel trước khi tải dữ liệu lên Firebase.", Toast.LENGTH_SHORT).show();
        }
    }

}