package com.example.qldrl.Class;

public class StudentInformationUpdatedEvent {
    public ListStudentOfClass updatedStudent;
    public int position;

    public StudentInformationUpdatedEvent(int position, ListStudentOfClass updatedStudent) {
        this.position = position;
        this.updatedStudent = updatedStudent;
    }
}
