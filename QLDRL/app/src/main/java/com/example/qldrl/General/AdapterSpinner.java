package com.example.qldrl.General;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.qldrl.Homes.MainHome;

import java.util.ArrayList;
import java.util.List;

public class AdapterSpinner {
    private Spinner spinnerGrade, spinnerSemester, spinnerYear;
    private List<String> list = new ArrayList<>();
    private String[] yearOptions = {"Năm học"};

    public AdapterSpinner(Spinner spinnerSemester) {
        this.spinnerSemester = spinnerSemester;
    }

    public AdapterSpinner(Spinner spinnerGrade, Spinner spinnerSemester, Spinner spinnerYear) {
        this.spinnerGrade = spinnerGrade;
        this.spinnerSemester = spinnerSemester;
        this.spinnerYear = spinnerYear;
    }

    public AdapterSpinner(Spinner spinnerGrade, Spinner spinnerYear) {
        this.spinnerGrade = spinnerGrade;
        this.spinnerYear = spinnerYear;
    }

    public void setupSpinnerGrade(Context context) {
        // Tạo danh sách các lựa chọn cho Spinner
        String[] gradeOptions = {"Khối", "Khối 10", "Khối 11", "Khối 12"};

        // Tạo ArrayAdapter với các lựa chọn
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, gradeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        spinnerGrade.setAdapter(adapter);
        spinnerGrade.setSelection(0);

    }
    public void setupSpinnerSemester(Context context) {
        // Tạo danh sách các lựa chọn cho Spinner
        String[] gradeOptions = {"Học kỳ 1", "Học kỳ 2"};

        // Tạo ArrayAdapter với các lựa chọn
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, gradeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        spinnerSemester.setAdapter(adapter);
        spinnerSemester.setSelection(0);

    }
    public void setupSpinnerYear(Context context) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, MainHome.yearOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gán adapter cho Spinner
        spinnerYear.setAdapter(adapter);
        spinnerYear.setSelection(0);

    }


}
