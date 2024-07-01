package com.example.qldrl.Class;

import java.util.List;

public class FileClassUpdatedEvent {
    public List<ListClass> updatedClass;
    public FileClassUpdatedEvent(List<ListClass> updatedClass){
        this.updatedClass = updatedClass;
    }
}
