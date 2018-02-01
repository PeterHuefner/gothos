package gothos.DatabaseCore;

import gothos.Common;
import gothos.Start;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DatabaseStructure {

    public void checkDatabase(){

        //Check if settings table exists
        if(this.listTables("^global_classes$").size() == 0){
            if(!this.createGlobalClasses()){
                Common.printError("Fehler beim Anlegen der global_classes");
            }
        }

    }

    public ArrayList<String> listTables(){
        return this.listTables("");
    }

    public ArrayList<String> listTables(String matchRegex){
        ArrayList<String> tables = new ArrayList<String>();

        ResultSet rs = Start.database.query("SELECT name FROM sqlite_master WHERE type = 'table';");

        try{
            while (rs.next()){
                if(matchRegex.isEmpty()){
                    tables.add(rs.getString("name"));
                }else if(rs.getString("name").matches(matchRegex)){
                    tables.add(rs.getString("name"));
                }
            }
        }catch (SQLException e){
            Common.printError(e);
        }

        return  tables;
    }

    public ArrayList<String> listApparatiInCompetition(){
        ArrayList<String> tables = this.listTables("^" + Start.selectedCompetition + "_apparati_.+$");
        ArrayList<String> apparati = new ArrayList<String>();

        for(String table: tables){
            Matcher matcher = Pattern.compile("^.+_apparati_(.+)$").matcher(table);
            if(matcher.find()){
                apparati.add(matcher.group());
            }
        }

        return  apparati;
    }

    public boolean createGlobalClasses(){
        return Start.database.execute(
                "CREATE TABLE global_classes (" +
                        "class TEXT PRIMARY KEY NOT NULL," +
                        "calculation TEXT NOT NULL," +
                        "displayColumns TEXT NOT NULL" +
                     ");"
        );
    }
}
