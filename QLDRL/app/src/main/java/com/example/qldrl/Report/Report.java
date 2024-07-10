package com.example.qldrl.Report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Report extends AppCompatActivity {

    private Workbook workbook;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        CardView cardListAcc = findViewById(R.id.cardListAcc);
        CardView cardListMistakeAll = findViewById(R.id.cardListMistakeAll);
        CardView cardListMistakeClass = findViewById(R.id.cardListMistakeClass);

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
                                openDinalogDownLoadListMistakeClass(lhId);
                            }
                        } else {
                            Log.d("GiaoVien", "Error getting documents: ", task.getException());
                        }
                    });


        });

        cardListMistakeAll.setOnClickListener(v -> openDinalogDownLoadListMistakeAll());

        cardListAcc.setOnClickListener(v -> openDinalogDownLoadListAcc());

    }

    private void openDinalogDownLoadListAcc() {
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
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        Button btnCancel = dialog.findViewById(R.id.btnExitDownLoad);
        Button btnDownLoad = dialog.findViewById(R.id.btnDownloadFile);
        TextView txtNameFile = dialog.findViewById(R.id.txtNameFileDown);
        Button btnExportFile = dialog.findViewById(R.id.btnExportFile);
        RadioButton rdStud = dialog.findViewById(R.id.rdStud);
        RadioButton rdTeach = dialog.findViewById(R.id.rdTeach);
        RadioButton rdBoard = dialog.findViewById(R.id.rdBoard);
        RadioGroup rdGTypeAcc = dialog.findViewById(R.id.rdGTypeAcc);
        LinearLayout layoutErrorType = dialog.findViewById(R.id.layoutErrorType);





        btnExportFile.setOnClickListener( v -> {
            if(!rdBoard.isChecked() && !rdStud.isChecked() && !rdTeach.isChecked()) {
                layoutErrorType.setVisibility(View.VISIBLE);
                rdGTypeAcc.setOnCheckedChangeListener((group, checkedId) -> layoutErrorType.setVisibility(View.GONE));
            } else {
                if(rdStud.isChecked()) {
                    studentAccs(txtNameFile, btnDownLoad, dialog);
                } else if(rdTeach.isChecked()) {
                    TeachAccs(txtNameFile, btnDownLoad, dialog);
                } else {
                    boardAccs(txtNameFile,btnDownLoad, dialog);
                }
            }
        });



        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    public void studentAccs(TextView txtNameFile, Button btnDownLoad, Dialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hocSinh")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, List<Map<String, Object>>> dataByClass = new HashMap<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String HS_id = document.getString("HS_id");
                        String HS_HoTen = document.getString("HS_HoTen");
                        String HS_GioiTinh = document.getString("HS_GioiTinh");
                        String HS_NgaySinh = document.getString("HS_NgaySinh");
                        String LH_id = document.getString("LH_id");
                        String TK_id = document.getString("TK_id");

                        db.collection("taiKhoan")
                                .whereEqualTo("TK_id", TK_id)
                                .get()
                                .addOnSuccessListener(taiKhoanSnapshot -> {
                                    for (QueryDocumentSnapshot taiKhoanDoc : taiKhoanSnapshot) {
                                        String TK_TenTaiKhoan = taiKhoanDoc.getString("TK_TenTaiKhoan");
                                        String TK_MatKhau = taiKhoanDoc.getString("TK_MatKhau");

                                        Map<String, Object> studentData = new HashMap<>();
                                        studentData.put("HS_id", HS_id);
                                        studentData.put("HS_HoTen", HS_HoTen);
                                        studentData.put("HS_GioiTinh", HS_GioiTinh);
                                        studentData.put("HS_NgaySinh", HS_NgaySinh);
                                        studentData.put("TK_TenTaiKhoan", TK_TenTaiKhoan);
                                        studentData.put("TK_MatKhau", TK_MatKhau);

                                        if (!dataByClass.containsKey(LH_id)) {
                                            dataByClass.put(LH_id, new ArrayList<>());
                                        }
                                        Objects.requireNonNull(dataByClass.get(LH_id)).add(studentData);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle error
                                });
                    }

                    String fileName = "Danh_sach_tai_khoan_HS.xlsx";
                  //  createExcelFileListAcc(fileName, dataByClass);
                    txtNameFile.setText(fileName);

                    btnDownLoad.setOnClickListener(v -> {
                        saveExcelFileToDownloadsClass(fileName, dataByClass);
                        dialog.dismiss();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    public void TeachAccs(TextView txtNameFile, Button btnDownLoad, Dialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("giaoVien")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                   List< Map<String, Object>> data = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String GV_id = document.getString("GV_id");
                        String GV_HoTen = document.getString("GV_HoTen");
                        String GV_GioiTinh = document.getString("GV_GioiTinh");
                        String GV_NgaySinh = document.getString("GV_NgaySinh");
                        String TK_id = document.getString("TK_id");

                        db.collection("taiKhoan")
                                .whereEqualTo("TK_id", TK_id)
                                .get()
                                .addOnSuccessListener(taiKhoanSnapshot -> {
                                    for (QueryDocumentSnapshot taiKhoanDoc : taiKhoanSnapshot) {
                                        String TK_TenTaiKhoan = taiKhoanDoc.getString("TK_TenTaiKhoan");
                                        String TK_MatKhau = taiKhoanDoc.getString("TK_MatKhau");
                                        Map<String, Object> dataGV = new HashMap<>();


                                        dataGV.put("GV_id", GV_id);
                                        dataGV.put("GV_HoTen", GV_HoTen);
                                        dataGV.put("GV_GioiTinh", GV_GioiTinh);
                                        dataGV.put("GV_NgaySinh", GV_NgaySinh);
                                        dataGV.put("TK_TenTaiKhoan", TK_TenTaiKhoan);
                                        dataGV.put("TK_MatKhau", TK_MatKhau);

                                        data.add(dataGV);


                                    }
                                    String fileName = "Danh_sach_tai_khoan_GV.xlsx";
                                      createExcelFileListAccTeach(fileName, data);
                                    txtNameFile.setText(fileName);

                                    btnDownLoad.setOnClickListener(v -> {
                                        saveExcelFileToDownloads(fileName, data);
                                        dialog.dismiss();
                                    });

                                })
                                .addOnFailureListener(e -> {
                                    // Handle error
                                });
                    }


                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
    public void boardAccs(TextView txtNameFile, Button btnDownLoad, Dialog dialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("taiKhoan").whereEqualTo("TK_ChucVu", "Ban giám hiệu")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List< Map<String, Object>> data = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Map<String, Object> dataBGH = new HashMap<>();
                        String TK_id = document.getString("TK_id");
                        String TK_HoTen = document.getString("TK_HoTen");
                        String TK_NgaySinh = document.getString("TK_NgaySinh");
                        String TK_TenTaiKhoan = document.getString("TK_TenTaiKhoan");
                        String TK_MatKhau = document.getString("TK_MatKhau");



                        dataBGH.put("TK_id", TK_id);
                        dataBGH.put("TK_HoTen", TK_HoTen);
                        dataBGH.put("TK_NgaySinh", TK_NgaySinh);
                        dataBGH.put("TK_TenTaiKhoan", TK_TenTaiKhoan);
                        dataBGH.put("TK_MatKhau", TK_MatKhau);


                        data.add(dataBGH);

                    }
                    String fileName = "Danh_sach_tai_khoan_BGH(Admin).xlsx";
                    createExcelFileListAccBoard(fileName, data);
                    txtNameFile.setText(fileName);

                    btnDownLoad.setOnClickListener(v -> {
                        saveExcelFileToDownloads(fileName, data);
                        dialog.dismiss();
                    });


                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void createExcelFileListAccBoard(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ban Giam Hieu (Admin)");

        // Tạo tiêu đề
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Danh sách tài khoản Admin (Ban giám hiệu)");
        headerCell.setCellStyle(getBoldCenteredCellStyle(workbook));

        // Gộp các cột từ 0 đến 5 trong dòng 0 (dòng tiêu đề)
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 5);
        sheet.addMergedRegion(mergedRegion);

        // Tạo tiêu đề các cột
        Row dataRow = sheet.createRow(3);
        dataRow.createCell(0).setCellValue("Mã BGH");
        dataRow.createCell(1).setCellValue("Họ Tên Chủ Tài Khoản");
        dataRow.createCell(2).setCellValue("Ngày Sinh Chủ Tài Khoản");
        dataRow.createCell(3).setCellValue("Tên Tài Khoản (Tên đăng nhập)");
        dataRow.createCell(4).setCellValue("Mật khẩu");

        // Thêm dữ liệu vào bảng
        int rowNum = 4;
        for (Map<String, Object> studentInfo : data) {
            Row row = sheet.createRow(rowNum++);
            Cell cell;
            cell = row.createCell(0);
            cell.setCellValue((String) studentInfo.get("TK_id"));
            cell = row.createCell(1);
            cell.setCellValue((String) studentInfo.get("TK_HoTen"));
            cell = row.createCell(2);
            cell.setCellValue((String) studentInfo.get("TK_NgaySinh"));
            cell = row.createCell(3);
            cell.setCellValue((String) studentInfo.get("TK_TenTaiKhoan"));
            cell = row.createCell(4);
            cell.setCellValue((String) studentInfo.get("TK_MatKhau"));
        }

        // Điều chỉnh độ rộng các cột
        sheet.setColumnWidth(0, 5000); // Mã Tài Khoản
        sheet.setColumnWidth(1, 5000); // Họ Tên Chủ Tài Khoản
        sheet.setColumnWidth(2, 5000); // Ngày Sinh Chủ Tài Khoản
        sheet.setColumnWidth(3, 5000); // Tên Tài Khoản (Tên đăng nhập)
        sheet.setColumnWidth(4, 5000); // Mật khẩu

        // Vẽ đường viền ô
        setBorders(sheet, 0, rowNum - 1, 0, 4);

        // Trả về workbook đã tạo, không lưu vào file ở đây
        this.workbook = workbook;
    }




    private void createExcelFileListAccTeach(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Giao Vien");

        // Tạo tiêu đề
        Row headerRow1 = sheet.createRow(0);
        Cell headerCell = headerRow1.createCell(0);
        headerCell.setCellValue("Danh sách tài khoản Giáp Viên");
        headerCell.setCellStyle(getBoldCenteredCellStyle(workbook));

        // Gộp các cột từ 0 đến 5 trong dòng 0 (dòng tiêu đề)
        CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(mergedRegion);


        Row headerRow = sheet.createRow(3);
        headerRow.createCell(0).setCellValue("Mã GV");
        headerRow.createCell(1).setCellValue("Họ và Tên");
        headerRow.createCell(2).setCellValue("Giới Tính");
        headerRow.createCell(3).setCellValue("Ngày Sinh");
        headerRow.createCell(4).setCellValue("Tên Tài Khoản (Tên đăng nhập)");
        headerRow.createCell(5).setCellValue("Mật Khẩu");


        // Populate the data rows
        int rowNum = 4;
        for (Map<String, Object> studentInfo : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) studentInfo.get("GV_id"));
            row.createCell(1).setCellValue((String) studentInfo.get("GV_HoTen"));
            row.createCell(2).setCellValue((String) studentInfo.get("GV_GioiTinh"));
            row.createCell(3).setCellValue((String) studentInfo.get("GV_NgaySinh"));
            row.createCell(4).setCellValue((String) studentInfo.get("TK_TenTaiKhoan"));
            row.createCell(5).setCellValue((String) studentInfo.get("TK_MatKhau"));


        }

        //  Auto-size the columns
        sheet.setColumnWidth(0, 5000); // HS_id
        sheet.setColumnWidth(1, 5000); // HS_HoTen
        sheet.setColumnWidth(2, 5000); // HS_GioiTinh
        sheet.setColumnWidth(3, 5000); // HS_NgaySinh
        sheet.setColumnWidth(4, 5000); // violationCount
        sheet.setColumnWidth(5, 5000); // violationNames


        setBorders(sheet, 0, rowNum - 1, 0, 5);
        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }

    private void openDinalogDownLoadListMistakeAll() {
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
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

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

        btnExport.setOnClickListener( v -> {
            if(!rdT1.isChecked() && !rdT2.isChecked()) {
                layoutErrorTermReport.setVisibility(View.VISIBLE);
                rdGTermReport.setOnCheckedChangeListener((group, checkedId) -> layoutErrorTermReport.setVisibility(View.GONE));
            } else {
               // MsAll(rdT1,txtNameFile,btnDownLoad);
                // count hanh kiem
                if(rdT1.isChecked()) {
                    MistakeAllClass(txtNameFile,btnDownLoad,"Học kỳ 1");
                }
                else {
                    MistakeAllClass(txtNameFile,btnDownLoad,"Học kỳ 2");
                }

            }
        });


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void openDinalogDownLoadListMistakeClass(String LHid) {
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
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);


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


        btnCancel.setOnClickListener(v -> dialog.dismiss());


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
        Sheet sheet = workbook.createSheet("Vi Phạm");


        // Tạo tiêu đề
        Row headerRow1 = sheet.createRow(0);
        Cell headerCell = headerRow1.createCell(0);
        headerCell.setCellValue("Danh sách điểm rèn luyện");
        headerCell.setCellStyle(getBoldCenteredCellStyle(workbook));

        Row headerRow = sheet.createRow(3);
        headerRow.createCell(0).setCellValue("Mã HS");
        headerRow.createCell(1).setCellValue("Họ và Tên");
        headerRow.createCell(2).setCellValue("Giới Tính");
        headerRow.createCell(3).setCellValue("Ngày Sinh");
        headerRow.createCell(4).setCellValue("Điểm Rèn Luyện");
        headerRow.createCell(5).setCellValue("Hạnh Kiểm");
        headerRow.createCell(6).setCellValue("Số Lần Vi Phạm");
        headerRow.createCell(7).setCellValue("Tên Vi Phạm");


        // Populate the data rows
        int rowNum = 4;
        for (Map<String, Object> studentInfo : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) studentInfo.get("HS_id"));
            row.createCell(1).setCellValue((String) studentInfo.get("HS_HoTen"));
            row.createCell(2).setCellValue((String) studentInfo.get("HS_GioiTinh"));
            row.createCell(3).setCellValue((String) studentInfo.get("HS_NgaySinh"));
            row.createCell(4).setCellValue((String) studentInfo.get("HKM_DiemRenLuyen"));
            row.createCell(5).setCellValue((String) studentInfo.get("HKM_HanhKiem"));
            row.createCell(6).setCellValue((int) studentInfo.get("violationCount"));
            row.createCell(7).setCellValue(String.join(", ", (List<String>) studentInfo.get("violationNames")));

        }

        //  Auto-size the columns
        sheet.setColumnWidth(0, 5000); // HS_id
        sheet.setColumnWidth(1, 6000); // HS_HoTen
        sheet.setColumnWidth(2, 6000); // HS_GioiTinh
        sheet.setColumnWidth(3, 6000); // HS_NgaySinh
        sheet.setColumnWidth(4, 6000); // violationCount
        sheet.setColumnWidth(5, 6000); // violationNames
        sheet.setColumnWidth(6, 6000); // HKM_DiemRenLuyen
        sheet.setColumnWidth(7, 6000); // HKM_HanhKiem
        setBorders(sheet, 0, rowNum - 1, 0, 7);
        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }
    private void createExcelFileAllClass(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lớp");

        // Tạo tiêu đề
        Row headerRow1 = sheet.createRow(0);
        Cell headerCell = headerRow1.createCell(0);
        headerCell.setCellValue("Danh sách Thông kê hạnh kiểm các lớp");
        headerCell.setCellStyle(getBoldCenteredCellStyle(workbook));


        // Tạo header
        Row headerRow = sheet.createRow(3);
        headerRow.createCell(0).setCellValue("Mã Lớp");
        headerRow.createCell(1).setCellValue("Tên Lớp");
        headerRow.createCell(2).setCellValue("Niên Khóa");
        headerRow.createCell(3).setCellValue("Sĩ Số");
        headerRow.createCell(4).setCellValue("Số Lượt Vi Phạm");
        headerRow.createCell(5).setCellValue("SL Hạnh Kiểm Tốt");
        headerRow.createCell(6).setCellValue("SL Hạnh Kiểm Khá");
        headerRow.createCell(7).setCellValue("SL Hạnh Kiểm TB");
        headerRow.createCell(8).setCellValue("SL Hạnh Kiểm Yếu");

        // Thêm dữ liệu vào sheet
        int rowNum = 4;
        for (Map<String, Object> row : data) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue((String) row.get("LH_id"));
            dataRow.createCell(1).setCellValue((String) row.get("LH_TenLop"));
            dataRow.createCell(2).setCellValue((String) row.get("NK_NienKhoa"));
            dataRow.createCell(3).setCellValue((String) row.get("SoLuongHS"));
            dataRow.createCell(4).setCellValue((String) row.get("SoLuotViPham"));
            dataRow.createCell(5).setCellValue((String) row.get("SoHKMTot"));
            dataRow.createCell(6).setCellValue((String) row.get("SoHKMKha"));
            dataRow.createCell(7).setCellValue((String) row.get("SoHKMTrungBinh"));
            dataRow.createCell(8).setCellValue((String) row.get("SoHKMYeu"));
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

        setBorders(sheet, 0, rowNum - 1, 0, 8);
        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }

    @SuppressLint("SetTextI18n")
    public  void MistakeAllClass(TextView txtNameFile, Button btnDownLoad, String HK) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve data from the "lop" collection
        db.collection("lop")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> dataFromFirestore = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("LH_id", document.getString("LH_id"));
                        data.put("LH_TenLop", document.getString("LH_TenLop"));
                        data.put("NK_NienKhoa", document.getString("NK_NienKhoa"));

                        // Store the data temporarily
                        dataFromFirestore.add(data);
                    }

                    // Calculate the number of students and number of violations
                    for (Map<String, Object> classData : dataFromFirestore) {
                        String LH_id = (String) classData.get("LH_id");
                        AtomicInteger soHKMTot = new AtomicInteger();
                        AtomicInteger soHKMKha = new AtomicInteger();
                        AtomicInteger soHKMTrungBinh = new AtomicInteger();
                        AtomicInteger soHKMYeu = new AtomicInteger();
                        // Count the number of students in the class
                        db.collection("hocSinh")
                                .whereEqualTo("LH_id", LH_id)
                                .get()
                                .addOnSuccessListener(querySnapshotHS -> {
                                    classData.put("SoLuongHS", String.valueOf(querySnapshotHS.size()));
                                    List<String> idHSList = new ArrayList<>();
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshotHS) {
                                        idHSList.add(queryDocumentSnapshot.getString("HS_id"));
                                    }

                                    if (!idHSList.isEmpty()) {
                                        db.collection("luotViPham")
                                                .whereEqualTo("HK_HocKy", HK)
                                                .whereIn("HS_id", idHSList)
                                                .get()
                                                .addOnSuccessListener(querySnapshot2 -> {
                                                    int totalViolations = querySnapshot2.size();
                                                    classData.put("SoLuotViPham", String.valueOf(totalViolations));
                                                   // Toast.makeText(Report.this, "" + totalViolations, Toast.LENGTH_LONG).show();

                                                    for (String HS_id : idHSList) {
                                                        db.collection("hanhKiem")
                                                                .whereEqualTo("HS_id", HS_id)
                                                                .whereEqualTo("HK_HocKy", HK)
                                                                .get()
                                                                .addOnSuccessListener(querySnapshotHK -> {

                                                                    for (QueryDocumentSnapshot documentHK : querySnapshotHK) {
                                                                        String hanhKiem = documentHK.getString("HKM_HanhKiem");
                                                                        switch (hanhKiem) {
                                                                            case "Tốt":
                                                                                soHKMTot.getAndIncrement();
                                                                                break;
                                                                            case "Khá":
                                                                                soHKMKha.getAndIncrement();
                                                                                break;
                                                                            case "Trung bình":
                                                                                soHKMTrungBinh.getAndIncrement();
                                                                                break;
                                                                            case "Yếu":
                                                                                soHKMYeu.getAndIncrement();
                                                                                break;
                                                                        }
                                                                    }

                                                                    if (idHSList.size() == soHKMTot.get() + soHKMKha.get() + soHKMTrungBinh.get() + soHKMYeu.get()) {
                                                                        classData.put("SoHKMTot", soHKMTot.get() + "");
                                                                        Toast.makeText(Report.this, "sl t" +soHKMTot,Toast.LENGTH_LONG).show();
                                                                        classData.put("SoHKMKha", soHKMKha.get() + "");
                                                                        classData.put("SoHKMTrungBinh", soHKMTrungBinh.get() + "");
                                                                        classData.put("SoHKMYeu", soHKMYeu.get() + "");
                                                                        String fileName = "Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx";
                                                                        txtNameFile.setText(fileName);
                                                                        createExcelFileAllClass(fileName, dataFromFirestore);
                                                                        btnDownLoad.setOnClickListener(v1 -> {
                                                                            saveExcelFileToDownloads(fileName, dataFromFirestore);
                                                                        });
                                                                    }
                                                                });
                                                    }






                                                    // Create an Excel file and save it to Downloads

                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle error
                                                });
                                    } else {
                                        classData.put("SoLuotViPham", "0");
                                        classData.put("SoHKMTot", soHKMTot.get() + "");
                                        classData.put("SoHKMKha", soHKMKha.get() + "");
                                        classData.put("SoHKMTrungBinh", soHKMTrungBinh.get() + "");
                                        classData.put("SoHKMYeu", soHKMYeu.get() + "");
                                        String fileName = "Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx";
                                        txtNameFile.setText(fileName);
                                        createExcelFileAllClass(fileName, dataFromFirestore);
                                        btnDownLoad.setOnClickListener(v1 -> {
                                            saveExcelFileToDownloads(fileName, dataFromFirestore);
                                        });


                                        // Create an Excel file and save it to Downloads

                                    }


                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
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

    public  void saveExcelFileToDownloadsClass(String fileName, Map<String, List<Map<String, Object>>> dataByClass) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            Workbook workbook = new XSSFWorkbook();

            // Create a sheet for each class
            for (Map.Entry<String, List<Map<String, Object>>> entry : dataByClass.entrySet()) {
                String className = entry.getKey();
                List<Map<String, Object>> students = entry.getValue();

                Sheet sheet = workbook.createSheet(className);
                // Tạo tiêu đề
                Row headerRow1= sheet.createRow(0);
                Cell headerCell = headerRow1.createCell(0);
                headerCell.setCellValue("Danh sách tài khoản Học Sinh");
                headerCell.setCellStyle(getBoldCenteredCellStyle(workbook));
                // Create header row
                Row headerRow = sheet.createRow(3);
                headerRow.createCell(0).setCellValue("Mã HS");
                headerRow.createCell(1).setCellValue("Họ và tên");
                headerRow.createCell(2).setCellValue("Giới tính");
                headerRow.createCell(3).setCellValue("Ngày sinh");
                headerRow.createCell(4).setCellValue("Tên đăng nhập");
                headerRow.createCell(5).setCellValue("Mật khẩu");

                // Add student data to the sheet
                int rowIndex = 4;
                for (Map<String, Object> student : students) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(student.get("HS_id").toString());
                    row.createCell(1).setCellValue(student.get("HS_HoTen").toString());
                    row.createCell(2).setCellValue(student.get("HS_GioiTinh").toString());
                    row.createCell(3).setCellValue(student.get("HS_NgaySinh").toString());
                    row.createCell(4).setCellValue(student.get("TK_TenTaiKhoan").toString());
                    row.createCell(5).setCellValue(student.get("TK_MatKhau").toString());
                }
                // Điều chỉnh độ rộng các cột
                sheet.setColumnWidth(0, 5000); // Mã Tài Khoản
                sheet.setColumnWidth(1, 5000); // Họ Tên Chủ Tài Khoản
                sheet.setColumnWidth(2, 5000); // Ngày Sinh Chủ Tài Khoản
                sheet.setColumnWidth(3, 5000); // Tên Tài Khoản (Tên đăng nhập)
                sheet.setColumnWidth(4, 5000); // Mật khẩu
                setBorders(sheet, 0, rowIndex - 1, 0, 5);
            }

            workbook.write(outputStream);
            outputStream.close();
            workbook.close();

            // Update the UI with the file path
            // ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private CellStyle getBoldCenteredCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private void setBorders(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = startCol; j <= endCol; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellStyle(getBorderedCellStyle(sheet.getWorkbook()));
                    }
                }
            }
        }
    }

    private CellStyle getBorderedCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }
}