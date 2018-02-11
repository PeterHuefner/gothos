package gothos.DatabaseCore;

import gothos.Application;

public class DatabaseStructure {

    public static boolean createGlobalClasses(){
        return Application.database.execute(
                "CREATE TABLE IF NOT EXISTS global_classes (" +
                        "class TEXT PRIMARY KEY," +
                        "displayName TEXT," +
                        "calculation TEXT," +
                        "displayColumns TEXT" +
                     ");"
        );
    }

    public static boolean createSettings(){
        return Application.database.execute(
                "CREATE TABLE IF NOT EXISTS settings (" +
                        "name TEXT PRIMARY KEY NOT NULL," +
                        "value TEXT" +
                     ");"
        );
    }

    public static boolean createCompetitions(){
        return Application.database.execute(
                "CREATE TABLE IF NOT EXISTS competitions (" +
                        "name TEXT PRIMARY KEY NOT NULL," +
                        "longname TEXT," +
                        "description TEXT," +
                        "competitionDay TEXT" +
                     ");"
        );
    }
}
