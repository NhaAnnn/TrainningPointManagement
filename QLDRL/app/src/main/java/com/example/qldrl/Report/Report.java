package com.example.qldrl.Report;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
import java.util.concurrent.atomic.AtomicInteger;

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

        cardListAcc.setOnClickListener(v -> {
            openDinalogDownLoadListAcc(Gravity.CENTER);
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
        Button btnExportFile = dialog.findViewById(R.id.btnExportFile);
        RadioButton rdStud = dialog.findViewById(R.id.rdStud);
        RadioButton rdTeach = dialog.findViewById(R.id.rdTeach);
        RadioButton rdBoard = dialog.findViewById(R.id.rdBoard);
        RadioGroup rdGTypeAcc = dialog.findViewById(R.id.rdGTypeAcc);
        LinearLayout layoutErrorType = dialog.findViewById(R.id.layoutErrorType);





        btnExportFile.setOnClickListener( v -> {
            if(!rdBoard.isChecked() && !rdStud.isChecked() && !rdTeach.isChecked()) {
                layoutErrorType.setVisibility(View.VISIBLE);
                rdGTypeAcc.setOnCheckedChangeListener((group, checkedId) -> {
                    layoutErrorType.setVisibility(View.GONE);

                });
            } else {
                if(rdStud.isChecked()) {
                    studentAccs(txtNameFile, btnDownLoad);
                } else if(rdTeach.isChecked()) {
                    TeachAccs(txtNameFile, btnDownLoad);
                } else {
                    boardAccs(txtNameFile,btnDownLoad);
                }
            }
        });



        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void studentAccs(TextView txtNameFile, Button btnDownLoad) {
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
                                        dataByClass.get(LH_id).add(studentData);
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
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    public void TeachAccs(TextView txtNameFile, Button btnDownLoad) {
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
                        String LH_id = document.getString("LH_id");
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
    public void boardAccs(TextView txtNameFile, Button btnDownLoad) {
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
                    });


                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void createExcelFileListAccBoard(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ban Giam Hieu (Admin)");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("TK_id");
        headerRow.createCell(1).setCellValue("TK_HoTen");
        headerRow.createCell(2).setCellValue("TK_NgaySinh");
        headerRow.createCell(3).setCellValue("TK_TenTaiKhoan");
        headerRow.createCell(4).setCellValue("TK_MatKhau");


        // Populate the data rows
        int rowNum = 1;
        for (Map<String, Object> studentInfo : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) studentInfo.get("TK_id"));
            row.createCell(1).setCellValue((String) studentInfo.get("TK_HoTen"));
            row.createCell(2).setCellValue((String) studentInfo.get("TK_NgaySinh"));
            row.createCell(3).setCellValue((String) studentInfo.get("TK_TenTaiKhoan"));
            row.createCell(4).setCellValue((String) studentInfo.get("TK_MatKhau"));


        }

        //  Auto-size the columns
        sheet.setColumnWidth(0, 5000); // HS_id
        sheet.setColumnWidth(1, 10000); // HS_HoTen
        sheet.setColumnWidth(2, 5000); // HS_GioiTinh
        sheet.setColumnWidth(3, 10000); // HS_NgaySinh
        sheet.setColumnWidth(4, 5000); // violationCount


        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }

    private void createExcelFileListAccTeach(String fileName, List<Map<String, Object>> data) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Giao Vien");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("GV_id");
        headerRow.createCell(1).setCellValue("GV_HoTen");
        headerRow.createCell(2).setCellValue("GV_GioiTinh");
        headerRow.createCell(3).setCellValue("GV_NgaySinh");
        headerRow.createCell(4).setCellValue("TK_TenTaiKhoan");
        headerRow.createCell(5).setCellValue("TK_MatKhau");


        // Populate the data rows
        int rowNum = 1;
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
        sheet.setColumnWidth(1, 10000); // HS_HoTen
        sheet.setColumnWidth(2, 5000); // HS_GioiTinh
        sheet.setColumnWidth(3, 10000); // HS_NgaySinh
        sheet.setColumnWidth(4, 5000); // violationCount
        sheet.setColumnWidth(5, 10000); // violationNames

        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }

    private void openDinalogDownLoadListMistakeAll(int gravity) {
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
            if(rdT1.isChecked() == false && rdT2.isChecked() == false) {
                layoutErrorTermReport.setVisibility(View.VISIBLE);
                rdGTermReport.setOnCheckedChangeListener((group, checkedId) -> {
                    layoutErrorTermReport.setVisibility(View.GONE);

                });
            } else {
               // MsAll(rdT1,txtNameFile,btnDownLoad);
                // count hanh kiem
                if(rdT1.isChecked() == true) {
                    MistakeAllClass(txtNameFile,btnDownLoad,"Học kỳ 1");
                }
                else {
                    MistakeAllClass(txtNameFile,btnDownLoad,"Học kỳ 2");
                }

            }
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
        Sheet sheet = workbook.createSheet("Lớp");

        // Tạo header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("LH_id");
        headerRow.createCell(1).setCellValue("LH_TenLop");
        headerRow.createCell(2).setCellValue("NK_NienKhoa");
        headerRow.createCell(3).setCellValue("SoLuongHS");
        headerRow.createCell(4).setCellValue("SoLuotViPham");
        headerRow.createCell(5).setCellValue("SoHKMTot");
        headerRow.createCell(6).setCellValue("SoHKMKha");
        headerRow.createCell(7).setCellValue("SoHKMTrungBinh");
        headerRow.createCell(8).setCellValue("SoHKMYeu");

        // Thêm dữ liệu vào sheet
        int rowNum = 1;
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
        // Return the created workbook, don't save it here
        this.workbook = workbook;
    }

    public  void MistakeAllClass( TextView txtNameFile, Button btnDownLoad, String HK) {
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

                                                                        txtNameFile.setText("Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx");
                                                                        createExcelFileAllClass("Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx", dataFromFirestore);
                                                                        btnDownLoad.setOnClickListener(v1 -> {
                                                                            saveExcelFileToDownloads("Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx", dataFromFirestore);
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

                                        txtNameFile.setText("Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx");
                                        createExcelFileAllClass("Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx", dataFromFirestore);
                                        btnDownLoad.setOnClickListener(v1 -> {
                                            saveExcelFileToDownloads("Danh_sach_thong_ke_HK"+HK.substring(HK.length()-1)+".xlsx", dataFromFirestore);
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



    public static void createExcelFileListAcc12(String fileName, Map<String, List<Map<String, Object>>> dataByClass) {
        try {
            Workbook workbook = new XSSFWorkbook();

            // Create a sheet for each class
            for (Map.Entry<String, List<Map<String, Object>>> entry : dataByClass.entrySet()) {
                String className = entry.getKey();
                List<Map<String, Object>> students = entry.getValue();

                Sheet sheet = workbook.createSheet(className);

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Tên đăng nhập");
                headerRow.createCell(1).setCellValue("Mật khẩu");
                headerRow.createCell(2).setCellValue("Họ và tên");
                headerRow.createCell(3).setCellValue("Giới tính");
                headerRow.createCell(4).setCellValue("Ngày sinh");

                // Add student data to the sheet
                int rowIndex = 1;
                for (Map<String, Object> student : students) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(student.get("TK_TenTaiKhoan").toString());
                    row.createCell(1).setCellValue(student.get("TK_MatKhau").toString());
                    row.createCell(2).setCellValue(student.get("HS_HoTen").toString());
                    row.createCell(3).setCellValue(student.get("HS_GioiTinh").toString());
                    row.createCell(4).setCellValue(student.get("HS_NgaySinh").toString());
                }
            }

            // Save the workbook to a file
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();

            // Update the UI with the file name
            // ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveExcelFileToDownloadsClass(String fileName, Map<String, List<Map<String, Object>>> dataByClass) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            Workbook workbook = new XSSFWorkbook();

            // Create a sheet for each class
            for (Map.Entry<String, List<Map<String, Object>>> entry : dataByClass.entrySet()) {
                String className = entry.getKey();
                List<Map<String, Object>> students = entry.getValue();

                Sheet sheet = workbook.createSheet(className);

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Tên đăng nhập");
                headerRow.createCell(1).setCellValue("Mật khẩu");
                headerRow.createCell(2).setCellValue("Họ và tên");
                headerRow.createCell(3).setCellValue("Giới tính");
                headerRow.createCell(4).setCellValue("Ngày sinh");

                // Add student data to the sheet
                int rowIndex = 1;
                for (Map<String, Object> student : students) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(student.get("TK_TenTaiKhoan").toString());
                    row.createCell(1).setCellValue(student.get("TK_MatKhau").toString());
                    row.createCell(2).setCellValue(student.get("HS_HoTen").toString());
                    row.createCell(3).setCellValue(student.get("HS_GioiTinh").toString());
                    row.createCell(4).setCellValue(student.get("HS_NgaySinh").toString());
                }
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

}