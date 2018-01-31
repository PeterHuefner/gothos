package gothos.DatabaseCore;

import gothos.Start;

import java.sql.ResultSet;

public class DatabaseStructure {

    public void checkDatabase(){

        ResultSet rs = Start.database.query("SELECT * FROM test;");

        try{
            while (rs.next()){
                System.out.println(rs.getString("col1"));
                System.out.println(rs.getString("col2"));
            }
        }catch (Exception e){

        }
    }

}
