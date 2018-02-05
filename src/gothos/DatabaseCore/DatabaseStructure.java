package gothos.DatabaseCore;

import gothos.Common;
import gothos.Start;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DatabaseStructure {

    public static boolean createGlobalClasses(){
        return Start.database.execute(
                "CREATE TABLE IF NOT EXISTS global_classes (" +
                        "class TEXT PRIMARY KEY NOT NULL," +
                        "calculation TEXT NOT NULL," +
                        "displayColumns TEXT NOT NULL" +
                     ");"
        );
    }

    public static boolean createSettings(){
        return Start.database.execute(
                "CREATE TABLE IF NOT EXISTS settings (" +
                        "name TEXT PRIMARY KEY NOT NULL," +
                        "value TEXT" +
                     ");"
        );
    }

    public static boolean createCompetitions(){
        return Start.database.execute(
                "CREATE TABLE IF NOT EXISTS competitions (" +
                        "name TEXT PRIMARY KEY NOT NULL," +
                        "longname TEXT," +
                        "description TEXT," +
                        "competitionDay TEXT" +
                     ");"
        );
    }
}
