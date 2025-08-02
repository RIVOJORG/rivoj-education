package com.example.leedcode2.Problems;

public class DataBase {

    public void LeedCode2356(){
        String mySqlQuery = """
        SELECT
        teacher_id,
                COUNT(DISTINCT subject_id) AS cnt
        FROM
                Teacher
        GROUP BY teacher_id""";
    }
}
