package org.example.studentmanagement.utill;

import java.util.UUID;

public class GenerateId {

    public static String generateId(){
        return UUID.randomUUID().toString();
    }
}
