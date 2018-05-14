package gothos.DatabaseCore;

import gothos.Common;
import gothos.Application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseAnalyse {

	public void checkDatabase(){

		/*boolean st0 = Application.database.execute("INSERT INTO test(col1, col2) values ('one', 'two');");
		long id = Application.database.getLastInsertId();
		boolean st1 = Application.database.execute("INSERT OR IGNORE INTO test(ROWID,col1)values(10,\"asd\");");

		long id2 = Application.database.getLastInsertId();*/

		ArrayList<String> tables = this.listTables();

		//Check if settings table exists
		if(!tables.contains("global_classes")){
			if(!DatabaseStructure.createGlobalClasses()){
				Common.printError("Fehler beim Anlegen der global_classes");
			}
		}

		if(!tables.contains("settings")){
			if(!DatabaseStructure.createSettings()){
				Common.printError("Fehler beim Anlegen der settings");
			}
		}

		if(!tables.contains("competitions")){
			if(!DatabaseStructure.createCompetitions()){
				Common.printError("Fehler beim Anlegen der competitions");
			}
		}

	}

	public void checkCompetition(String competition){

	}

	public ArrayList<String> listTables(){
		return this.listTables("");
	}

	public ArrayList<String> listTables(String matchRegex){
		ArrayList<String> tables = new ArrayList<String>();

		ResultSet rs = Application.database.query("SELECT name FROM sqlite_master WHERE type = 'table';");

		try{
			while (rs.next()){
				if(matchRegex.isEmpty()){
					tables.add(rs.getString("name"));
				}else if(rs.getString("name").matches(matchRegex)){
					tables.add(rs.getString("name"));
				}
			}
			rs.close();
		}catch (SQLException e){
			Common.printError(e);
		}

		return  tables;
	}

	public ArrayList<String> listApparatiInCompetition(){
		return this.listApparatiInCompetition(Application.selectedCompetition);
	}

	public ArrayList<String> listApparatiInCompetition(String competition){
		ArrayList<String> tables = this.listTables("^competition_" + competition + "_apparati_.+$");
		ArrayList<String> apparati = new ArrayList<String>();

		for(String table: tables){
			Matcher matcher = Pattern.compile("^.+_apparati_(.+)$").matcher(table);
			if(matcher.find()){
				apparati.add(matcher.group(1));
			}
		}

		return  apparati;
	}

	public String baseCompetitionSelect(){
		return baseCompetitionSelect(false, new ArrayList<String>(), true);
	}

	public String baseCompetitionSelect(Boolean readableColumns, ArrayList<String> apparaties, Boolean addAppartiesToCols){
		String colums = "competition_" + Application.selectedCompetition + ".ROWID, ID, name, birthdate, class, club, squad, team";
		String joins = "";
		String where = "WHERE active = 1";

		String sql;

		if(readableColumns){
			colums = "competition_" + Application.selectedCompetition + ".ROWID, ID, Name, birthdate AS Geburtsdatum, class AS Altersklasse, club AS Verein, squad AS Riege, team AS Mannschaft";
		}

		if(apparaties.size() > 0){
			for(String apparti: apparaties){

				joins += " LEFT JOIN competition_" + Application.selectedCompetition + "_apparati_" + apparti + " ON competition_" + Application.selectedCompetition + ".ROWID = competition_" + Application.selectedCompetition + "_apparati_" + apparti + ".gymnast";

				if(addAppartiesToCols){
					colums += ", " + apparti;
				}
			}
		}

		sql = "SELECT " + colums + " FROM competition_" + Application.selectedCompetition + " " + joins + " " + where;

		return sql;
	}
}
