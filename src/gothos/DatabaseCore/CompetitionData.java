package gothos.DatabaseCore;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import gothos.Application;
import gothos.Common;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CompetitionData {

	protected String competition;

	protected ArrayList<DatabaseParameter> parameters;
	protected String[]                     colums;
	protected String                       className;
	protected String                       team;
	protected Integer                      gymnast;
	protected String                       squad;
	protected String                       club;

	protected Boolean           readableCols     = false;
	protected Boolean           allApparaties    = false;
	protected ArrayList<String> apparaties       = new ArrayList<>();
	protected Boolean           apparatiesAsCols = false;
	protected String            groupBy;
	//protected Boolean           calculateSum;
	//protected Boolean           calculateTeamSum;

	public void setCompetition(String competition) {
		this.competition = competition;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public void setGymnast(Integer gymnast) {
		this.gymnast = gymnast;
	}

	public void setSquad(String squad) {
		this.squad = squad;
	}

	public void setClub(String club) {
		this.club = club;
	}

	public void setReadableCols(Boolean readableCols) {
		this.readableCols = readableCols;
	}

	public void setAllApparaties(Boolean allApparaties) {
		this.allApparaties = allApparaties;
	}

	public void setApparaties(ArrayList<String> apparaties) {
		this.apparaties = apparaties;
	}

	public void setApparatiesAsCols(Boolean apparatiesAsCols) {
		this.apparatiesAsCols = apparatiesAsCols;
	}

	public void setColums(String[] colums) {
		this.colums = colums;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	/*public void setCalculateSum(Boolean calculateSum) {
		this.calculateSum = calculateSum;
	}

	public void setCalculateTeamSum(Boolean calculateTeamSum) {
		this.calculateTeamSum = calculateTeamSum;
	}*/

	public CompetitionData() {
		competition = Application.selectedCompetition;
	}

	public String getSql() {
		DatabaseAnalyse analyse = new DatabaseAnalyse();
		String          sql;
		StringBuilder   cols    = new StringBuilder();
		StringBuilder   where   = new StringBuilder();
		StringBuilder   joins   = new StringBuilder();
		String          order   = "";
		String          group   = "";

		parameters = new ArrayList<>();

		if (readableCols) {
			cols.append("competition_" + competition + ".ROWID, ID, Name, birthdate AS Geburtsdatum, class AS Altersklasse, club AS Verein, squad AS Riege, team AS Mannschaft");
		} else if (colums == null || colums.length == 0) {
			cols.append("competition_" + competition + ".ROWID, ID, birthdate, class, club, squad, team");
		}

		if (allApparaties) {
			apparaties = analyse.listApparatiInCompetition(competition);
		}

		if (apparaties != null && apparaties.size() > 0) {
			for (String apparti : apparaties) {

				joins.append(" LEFT JOIN competition_" + competition + "_apparati_" + apparti + " ON competition_" + competition + ".ROWID = competition_" + competition + "_apparati_" + apparti + ".gymnast");

				if (apparatiesAsCols) {
					cols.append(", " + apparti);
				}
			}
		} else if (colums != null && colums.length > 0) {
			String comma = "";
			for (String column : colums) {
				cols.append(comma + column);
				comma = ", ";
			}
		}

		if (!Common.emptyString(squad)) {
			where.append(
					" AND squad = ?"
			);
			parameters.add(new DatabaseParameter(squad));
		}

		if (!Common.emptyString(team)) {
			where.append(
					" AND team = ?"
			);
			parameters.add(new DatabaseParameter(team));
		}

		if (!Common.emptyString(className)) {
			where.append(
					" AND class = ?"
			);
			parameters.add(new DatabaseParameter(className));
		}

		if (!Common.emptyString(club)) {
			where.append(
					" AND club = ?"
			);
			parameters.add(new DatabaseParameter(club));
		}

		if (gymnast != null && gymnast != 0) {
			where.append(
					" AND competition_" + competition + ".ROWID = ?"
			);
			parameters.add(new DatabaseParameter(gymnast));
		}

		if (!Common.emptyString(groupBy)) {
			group = " GROUP BY " + groupBy;
		}

		sql = "SELECT " + cols.toString() + " FROM competition_" + competition + " " + joins.toString() + " WHERE active = 1 " + where + " " + order + group + ";";

		return sql;
	}

	public ArrayList<DatabaseParameter> getParameters() {
		return parameters;
	}

	protected ArrayList<String> list(String column) {
		ArrayList<String> values = new ArrayList<>();
		CompetitionData data = new CompetitionData();

		data.setGroupBy(column);
		data.setColums((new String[]{column}));

		ResultSet result = Application.database.query(data.getSql());

		try {

			while (result.next()) {
				values.add(
						result.getString(column)
				);
			}

			result.close();

		} catch (SQLException e) {
			Common.printError(e);
		}

		return values;
	}

	public ArrayList<String> listClasses() {
		return list("class");
	}

	public ArrayList<String> listClubs() {
		return list("club");
	}

	public ArrayList<String> listSquads() {
		return list("squad");
	}

	public ArrayList<String> listTeams() {
		return list("team");
	}
}
