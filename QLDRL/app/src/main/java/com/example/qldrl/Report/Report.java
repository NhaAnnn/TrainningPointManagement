package com.example.qldrl.Report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report extends AppCompatActivity {

    private Workbook workbook;
    private CardView cardListAcc, cardListMistakeAll, cardListMistakeClass;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        cardListAcc = findViewById(R.id.cardListAcc);
        cardListMistakeAll = findViewById(R.id.cardListMistakeAll);
        cardListMistakeClass = findViewById(R.id.cardListMistakeClass);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");

        cardListMistakeClass.setOnClickListener(v -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("giaoVien")
                    .whereEqualTo("TK_id", account.getTkID())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String lhId = document.getString("LH_id");
                                // Xử lý lhId ở đây
                                openDinalogDownLoadListMistakeClass(Gravity.CENTER, lhId);
                            }
                        } else {
                            Log.d("GiaoVien", "Error getting documents: ", task.getException());
                        }
                    });


        });

        cardListMistakeAll.setOnClickListener( v -> {
            openDinalogDownLoadListMistakeAll(Gravity.CENTER);
        });

    }

    private void openDinalogDownLoadListAcc(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_down_load_list_acc);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        Button btnCancel = dialog.findViewById(R.id.btnExitDownLoad);
        Button btnDownLoad = dialog.findViewById(R.id.btnDownloadFile);
        TextView txtNameFile = dialog.findViewById(R.id.txtNameFileDown);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hocSinh")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> dataFromFirestore = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("HS_id", document.getString("HS_id"));
                        data.put("HS_HoTen", document.getString("HS_HoTen"));
                        data.put("HS_NgaySinh", document.getString("HS_NgaySinh"));
                        data.put("HS_GioiTinh", document.getString("HS_GioiTinh"));
                        dataFromFirestore.add(data);
                    }

                    String fileName = "Danh_sach_tai_khoan.xlsx";
                    createExcelFile(fileName, dataFromFirestore);
                    txtNameFile.setText(fileName);

                    btnDownLoad.setOnClickListener(v -> {
                        saveExcelFileToDownloads(fileName, dataFromFirestore);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void openDinalogDownLoadListMistakeAll(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_down_load_list_acc);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        Button btnCancel = dialog.findViewById(R.id.btnExitDownLoad);
        Button btnDownLoad = dialog.findViewById(R.id.btnDownloadFile);
        TextView txtNameFile = dialog.findViewById(R.id.txtNameFileDown);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lop")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> dataFromFirestore = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("LH_id", document.getString("LH_id"));
                        data.put("LH_TenLop", document.getString("LH_TenLop"));
                        data.put("NK_NienKhoa", document.getString("NK_NienKhoa"));

                        // Count number of students in the class
                        db.collection("hocSinh")
                                .whereEqualTo("LH_id", document.getString("LH_id"))
                                .get()
                                .addOnSuccessListener(studentQuerySnapshot -> {
                                    data.put("soLuongHocSinh", studentQuerySnapshot.size());

                                    // Count violations for each student in the class
                                    int totalViolations = 0;
                                    int totalGoodBehaviors = 0;
                                    int totalAverageBehaviors = 0;
                                    int totalPoorBehaviors = 0;
                                    for (QueryDocumentSnapshot studentDocument : studentQuerySnapshot) {
                                        totalViolations += studentDocument.getLong("luotViPham").intValue();
                                        String behavior = studentDocument.getString("HKM_HanhKiem");
                                        if (behavior.equals("Tốt")) {
                                            totalGoodBehaviors++;
                                        } else if (behavior.equals("Khá")) {
                                            totalAverageBehaviors++;
                                        } else if (behavior.equals("Trung bình")) {
                                            totalAverageBehaviors++;
                                        } else if (behavior.equals("Yếu")) {
                                            totalPoorBehaviors++;
                                        }
                                    }
                                    data.put("soLuongLuotViPham", totalViolations);
                                    data.put("soLuongHKMTot", totalGoodBehaviors);
                                    data.put("soLuongHKMKha", totalAverageBehaviors);
                                    data.put("soLuongHKMTrungBinh", totalAverageBehaviors);
                                    data.put("soLuongHKMYeu", totalPoorBehaviors);

                                    dataFromFirestore.add(data);
                                });
                    }

                    String fileName = "lop.xlsx";
                    createExcelFileAllClass(fileName, dataFromFirestore);
                    txtNameFile.setText(fileName);

                    btnDownLoad.setOnClickListener(v -> {
                        saveExcelFileToDownloads(fileName, dataFromFirestore);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });



        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void openDinalogDownLoadListMistakeClass(int gravity, String LHid) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_down_load_file);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        String HocKy ;

        Button btnCancel = dialog.findViewById(R.id.btnExitDownLoad);
        Button btnDownLoad = dialog.findViewById(R.id.btnDownloadFile);
        TextView txtNameFile = dialog.findViewById(R.id.txtNameFileDown);
        Button btnExport = dialog.findViewById(R.id.btnExportFile);
        RadioGroup rdGTermReport = dialog.findViewById(R.id.rdGTermReport);
        // Lấy giá trị của radio button
        // Lấy button được chọn trong RadioGroup
        RadioButton rdT1 = dialog.findViewById(R.id.rdT1);
        RadioButton rdT2 = dialog.findViewById(R.id.rdT2);
        LinearLayout layoutErrorTermReport = dialog.findViewById(R.id.layoutErrorTermReport);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("hocSinh")
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    List<Map<String, Object>> dataFromFirestore = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        Map<String, Object> data = new HashMap<>();
//                        data.put("HS_id", document.getString("HS_id"));
//                        data.put("HS_HoTen", document.getString("HS_HoTen"));
//                        data.put("HS_NgaySinh", document.getString("HS_NgaySinh"));
//                        data.put("HS_GioiTinh", document.getString("HS_GioiTinh"));
//                        dataFromFirestore.add(data);
//                    }
//
//                    String fileName = "Danh_sach_tai_khoan.xlsx";
//                    createExcelFile(fileName, dataFromFirestore);
//                    txtNameFile.setText(fileName);
//
//                    btnDownLoad.setOnClickListener(v -> {
//                        saveExcelFileToDownloads(fileName, dataFromFirestore);
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    // Handle error
//                });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        btnExport.setOnClickListener( v -> {
            if(rdT1.isChecked() == false && rdT2.isChecked() == false) {
                layoutErrorTermReport.setVisibility(View.VISIBLE);
                rdGTermReport.setOnCheckedChangeListener((group, checkedId) -> {
                    layoutErrorTermReport.setVisibility(View.GONE);

                });
            } else {
                Ms(rdT1,txtNameFile,btnDownLoad,LHid);
            }
        });

        // Lấy danh sách học sinh trong lớp



        dialog.show();
    }

    private void createExcelFile(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Học Sinh");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("HS_id");
        headerRow.createCell(1).setCellValue("HS_HoTen");
        headerRow.createCell(2).setCellValue("HS_GioiTinh");
        headerRow.createCell(3).setCellValue("HS_NgaySinh");
        headerRow.createCell(4).setCellValue("violationCount");
        headerRow.createCell(5).setCellValue("violationNames");
        headerRow.createCell(6).setCellValue("HKM_DiemRenLuyen");
        headerRow.createCell(7).setCellValue("HKM_HanhKiem");

        // Populate the data rows
        int rowNum = 1;
        for (Map<String, Object> studentInfo : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) studentInfo.get("HS_id"));
            row.createCell(1).setCellValue((String) studentInfo.get("HS_HoTen"));
            row.createCell(2).setCellValue((String) studentInfo.get("HS_GioiTinh"));
            row.createCell(3).setCellValue((String) studentInfo.get("HS_NgaySinh"));
            row.createCell(4).setCellValue((int) studentInfo.get("violationCount"));
            row.createCell(5).setCellValue(String.join(", ", (List<String>) studentInfo.get("violationNames")));
            row.createCell(6).setCellValue((String) studentInfo.get("HKM_DiemRenLuyen"));
            row.createCell(7).setCellValue((String) studentInfo.get("HKM_HanhKiem"));
        }

        //  Auto-size the columns
        sheet.setColumnWidth(0, 5000); // HS_id
        sheet.setColumnWidth(1, 10000); // HS_HoTen
        sheet.setColumnWidth(2, 5000); // HS_GioiTinh
        sheet.setColumnWidth(3, 10000); // HS_NgaySinh
        sheet.setColumnWidth(4, 5000); // violationCount
        sheet.setColumnWidth(5, 10000); // violationNames
        sheet.setColumnWidth(6, 5000); // HKM_DiemRenLuyen
        sheet.setColumnWidth(7, 5000); // HKM_HanhKiem
        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }
    private void createExcelFileAllClass(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Học Sinh");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("LH_id");
        headerRow.createCell(1).setCellValue("LH_TenLop");
        headerRow.createCell(2).setCellValue("NK_NienKhoa");
        headerRow.createCell(3).setCellValue("SoLuongHocSinh");
        headerRow.createCell(4).setCellValue("SoLuongLuotViPham");
        headerRow.createCell(5).setCellValue("soLuongHKMTot");
        headerRow.createCell(6).setCellValue("soLuongHKMKha");
        headerRow.createCell(7).setCellValue("soLuongHKMTrungBinh");
        headerRow.createCell(8).setCellValue("soLuongHKMYeu");

        // Populate the data rows
        int rowNum = 1;
        for (Map<String, Object> studentInfo : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) studentInfo.get("LH_id"));
            row.createCell(1).setCellValue((String) studentInfo.get("LH_TenLop"));
            row.createCell(2).setCellValue((String) studentInfo.get("NK_NienKhoa"));
            row.createCell(3).setCellValue((String) studentInfo.get("SoLuongHocSinh"));
            row.createCell(4).setCellValue((int) studentInfo.get("SoLuongLuotViPham"));
            row.createCell(5).setCellValue((String)studentInfo.get("SoLuongHanhKiemTot"));
            row.createCell(6).setCellValue((String) studentInfo.get("SoLuongHanhKiemKha"));
            row.createCell(7).setCellValue((String) studentInfo.get("SoLuongHanhKiemTrungBinh"));
            row.createCell(8).setCellValue((String) studentInfo.get("SoLuongHanhKiemYeu"));
        }

        //  Auto-size the columns
        sheet.setColumnWidth(0, 5000); // HS_id
        sheet.setColumnWidth(1, 10000); // HS_HoTen
        sheet.setColumnWidth(2, 5000); // HS_GioiTinh
        sheet.setColumnWidth(3, 10000); // HS_NgaySinh
        sheet.setColumnWidth(4, 5000); // violationCount
        sheet.setColumnWidth(5, 10000); // violationNames
        sheet.setColumnWidth(6, 5000); // HKM_DiemRenLuyen
        sheet.setColumnWidth(7, 5000); // HKM_HanhKiem
        sheet.setColumnWidth(8, 5000); // HKM_HanhKiem
        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }
    public void Ms(RadioButton rdT1, TextView txtNameFile, Button btnDownLoad,String LHid) {
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        db.collection("hocSinh")
                .whereEqualTo("LH_id", LHid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> studentData = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String HS_id = document.getString("HS_id");
                        String HS_HoTen = document.getString("HS_HoTen");
                        String HS_GioiTinh = document.getString("HS_GioiTinh");
                        String HS_NgaySinh = document.getString("HS_NgaySinh");
                        if(rdT1.isChecked() == true) {
                            final int[] violationCount = {0};
                            db.collection("luotViPham")
                                    .whereEqualTo("HS_id", HS_id)
                                    .whereEqualTo("HK_HocKy", "Học kỳ 1")
                                    .get()
                                    .addOnSuccessListener(luotViPhamQuerySnapshot -> {
                                        violationCount[0] = luotViPhamQuerySnapshot.size();

                                        // Lấy tên các vi phạm của học sinh
                                        List<String> violationNames = new ArrayList<>();
                                        for (QueryDocumentSnapshot luotViPhamDocument : luotViPhamQuerySnapshot) {
                                            String VP_id = luotViPhamDocument.getString("VP_id");
                                            db.collection("viPham")
                                                    .whereEqualTo("VP_id", VP_id)
                                                    .get()
                                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                                        for(QueryDocumentSnapshot viPham: queryDocumentSnapshots) {
                                                            String tenViPham = viPham.getString("VP_TenViPham");
                                                            violationNames.add(tenViPham);
                                                        }
                                                    });
                                        }

                                        // Lấy điểm rèn luyện và hạnh kiểm của học sinh

                                        db.collection("hanhKiem")
                                                .whereEqualTo("HS_id", HS_id).whereEqualTo("HK_HocKy","Học kỳ 1")
                                                .get()
                                                .addOnSuccessListener(hanhKiemQuerySnapshot -> {
                                                    for (QueryDocumentSnapshot hanhKiemDocument : hanhKiemQuerySnapshot) {
                                                        String HKM_DiemRenLuyen = hanhKiemDocument.getString("HKM_DiemRenLuyen");
                                                        String HKM_HanhKiem = hanhKiemDocument.getString("HKM_HanhKiem");

                                                        Map<String, Object> studentInfo = new HashMap<>();
                                                        studentInfo.put("HS_id", HS_id);
                                                        studentInfo.put("HS_HoTen", HS_HoTen);
                                                        studentInfo.put("HS_GioiTinh", HS_GioiTinh);
                                                        studentInfo.put("HS_NgaySinh", HS_NgaySinh);
                                                        studentInfo.put("violationCount", violationCount[0]);
                                                        studentInfo.put("violationNames", violationNames);
                                                        studentInfo.put("HKM_DiemRenLuyen", HKM_DiemRenLuyen);
                                                        studentInfo.put("HKM_HanhKiem", HKM_HanhKiem);
                                                        studentData.add(studentInfo);
                                                    }

                                                    // Xuất dữ liệu ra file Excel
                                                    String fileName = "Danh_sach_vi_pham_HK1_lop_" + LHid + ".xlsx";
                                                    createExcelFile(fileName, studentData);
                                                    txtNameFile.setText(fileName);

                                                    btnDownLoad.setOnClickListener(v1 -> {
                                                        if(txtNameFile.getText().toString().isEmpty()) {
                                                            txtNameFile.setError("Vui lòng xuất file");
                                                            Toast.makeText(Report.this, "Bạn chưa xuất file",Toast.LENGTH_LONG).show();
                                                        } else  {
                                                            saveExcelFileToDownloads(fileName, studentData);
                                                            txtNameFile.setError(null);

                                                        }
                                                    });
                                                });



                                    });
                        }
                        else {
                            final int[] violationCount = {0};
                            db.collection("luotViPham")
                                    .whereEqualTo("HS_id", HS_id)
                                    .whereEqualTo("HK_HocKy", "Học kỳ 2")
                                    .get()
                                    .addOnSuccessListener(luotViPhamQuerySnapshot -> {
                                        violationCount[0] = luotViPhamQuerySnapshot.size();

                                        // Lấy tên các vi phạm của học sinh
                                        List<String> violationNames = new ArrayList<>();
                                        for (QueryDocumentSnapshot luotViPhamDocument : luotViPhamQuerySnapshot) {
                                            String VP_id = luotViPhamDocument.getString("VP_id");
                                            db.collection("viPham")
                                                    .whereEqualTo("VP_id", VP_id)
                                                    .get()
                                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                                        for(QueryDocumentSnapshot viPham: queryDocumentSnapshots) {
                                                            String tenViPham = viPham.getString("VP_TenViPham");
                                                            violationNames.add(tenViPham);
                                                        }
                                                    });
                                        }

                                        // Lấy điểm rèn luyện và hạnh kiểm của học sinh

                                        db.collection("hanhKiem")
                                                .whereEqualTo("HS_id", HS_id).whereEqualTo("HK_HocKy","Học kỳ 2")
                                                .get()
                                                .addOnSuccessListener(hanhKiemQuerySnapshot -> {
                                                    for (QueryDocumentSnapshot hanhKiemDocument : hanhKiemQuerySnapshot) {
                                                        String HKM_DiemRenLuyen = hanhKiemDocument.getString("HKM_DiemRenLuyen");
                                                        String HKM_HanhKiem = hanhKiemDocument.getString("HKM_HanhKiem");

                                                        Map<String, Object> studentInfo = new HashMap<>();
                                                        studentInfo.put("HS_id", HS_id);
                                                        studentInfo.put("HS_HoTen", HS_HoTen);
                                                        studentInfo.put("HS_GioiTinh", HS_GioiTinh);
                                                        studentInfo.put("HS_NgaySinh", HS_NgaySinh);
                                                        studentInfo.put("violationCount", violationCount[0]);
                                                        studentInfo.put("violationNames", violationNames);
                                                        studentInfo.put("HKM_DiemRenLuyen", HKM_DiemRenLuyen);
                                                        studentInfo.put("HKM_HanhKiem", HKM_HanhKiem);
                                                        studentData.add(studentInfo);
                                                    }

                                                    // Xuất dữ liệu ra file Excel
                                                    String fileName = "Danh_sach_vi_pham_HK2_lop_" + LHid + ".xlsx";
                                                    createExcelFile(fileName, studentData);
                                                    txtNameFile.setText(fileName);

                                                    btnDownLoad.setOnClickListener(v1 -> {
                                                        saveExcelFileToDownloads(fileName, studentData);
                                                    });
                                                });



                                    });
                        }
                        // Lấy số lượt vi phạm của học sinh

                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }
    private void createExcelFile1(String fileName, List<Map<String, Object>> data) {

            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();

            // Create a new sheet
            Sheet sheet = workbook.createSheet("Student Data");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("HS_id");
            headerRow.createCell(1).setCellValue("HS_HoTen");
            headerRow.createCell(2).setCellValue("HS_GioiTinh");
            headerRow.createCell(3).setCellValue("HS_NgaySinh");
            headerRow.createCell(4).setCellValue("violationCount");
            headerRow.createCell(5).setCellValue("violationNames");
            headerRow.createCell(6).setCellValue("HKM_DiemRenLuyen");
            headerRow.createCell(7).setCellValue("HKM_HanhKiem");

            // Populate the data rows
            int rowNum = 1;
            for (Map<String, Object> studentInfo : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue((String) studentInfo.get("HS_id"));
                row.createCell(1).setCellValue((String) studentInfo.get("HS_HoTen"));
                row.createCell(2).setCellValue((String) studentInfo.get("HS_GioiTinh"));
                row.createCell(3).setCellValue((String) studentInfo.get("HS_NgaySinh"));
                row.createCell(4).setCellValue((int) studentInfo.get("violationCount"));
                row.createCell(5).setCellValue(String.join(", ", (List<String>) studentInfo.get("violationNames")));
                row.createCell(6).setCellValue((String) studentInfo.get("HKM_DiemRenLuyen"));
                row.createCell(7).setCellValue((String) studentInfo.get("HKM_HanhKiem"));
            }

            // Auto-size the columns
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            this.workbook = workbook;

    }

    private void saveExcelFileToDownloads(String fileName, List<Map<String, Object>> data) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.flush();
            out.close();
            Toast.makeText(this, "File Excel đã được lưu thành công vào thư mục Downloads!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}