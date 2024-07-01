package com.example.qldrl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qldrl.Login.Login;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_FILE = 1;
    private FirebaseFirestore db;
    private TextView txtVName;
    Button btnPick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish(); // Kết thúc MainActivity
            }
        }, 40000); // Delay 2 giây





//        db = FirebaseFirestore.getInstance();
//        txtVName = findViewById(R.id.txtVName);
//
//        btnPick = findViewById(R.id.btnPick);
//        btnPick.setOnClickListener(v -> chooseFile());
    }





//
//    private void chooseFile() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_CHOOSE_FILE && resultCode == RESULT_OK && data != null) {
//            Uri fileUri = data.getData();
//            new ReadAndUploadDataTask().execute(fileUri);
//        } else {
//            txtVName.setText("No file selected");
//        }
//    }
//
//    private class ReadAndUploadDataTask extends AsyncTask<Uri, Void, Boolean> {
//        @Override
//        protected Boolean doInBackground(Uri... params) {
//            Uri fileUri = params[0];
//            try {
//                List<Student> students = readDataFromExcel(fileUri);
//                uploadDataToFirestore(students);
//                return true;
//            } catch (IOException e) {
//                txtVName.setText("Error reading data from Excel file: " + e.getMessage());
//                return false;
//            } catch (Exception e) {
//                txtVName.setText("Error: " + e.getMessage());
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            super.onPostExecute(success);
//            if (success) {
//                // Quay về màn hình ban đầu
//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        }
//    }
//
//    private List<Student> readDataFromExcel(Uri fileUri) throws IOException {
//        List<Student> students = new ArrayList<>();
//
//        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
//            if (inputStream != null) {
//                Workbook workbook = new XSSFWorkbook(inputStream);
//                ((XSSFWorkbook) workbook).setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                Sheet sheet = workbook.getSheetAt(0);
//
//                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                    Row row = sheet.getRow(i);
//                    int stt = (int) row.getCell(0).getNumericCellValue();
//                    String name = row.getCell(1).getStringCellValue();
//                    String email = row.getCell(2).getStringCellValue();
//                    String chucvu = row.getCell(3).getStringCellValue();
//                    students.add(new Student(stt, name, email, chucvu));
//                }
//            }
//        }
//
//        return students;
//    }
//
//    private void uploadDataToFirestore(List<Student> students) {
//        for (Student student : students) {
//            db.collection("hocSinh")
//                    .add(student)
//                    .addOnSuccessListener(documentReference -> {
//                        student.setId(documentReference.getId());
//                        txtVName.setText("Data uploaded successfully");
//                    })
//                    .addOnFailureListener(e -> {
//                        txtVName.setText("Error uploading data: " + e.getMessage());
//                    });
//        }
//    }
}