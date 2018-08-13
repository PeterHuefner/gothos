package gothos.DatabaseCore;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import gothos.Application;
import gothos.Common;
import gothos.competitionMainForm.Gymnast;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class CompetitionData {

	protected String competition;

	protected ArrayList<DatabaseParameter> parameters;
	protected String[]                     colums;
	protected String                       className;
	protected String                       team;
	protected Integer                      gymnast;
	protected String                       squad;
	protected String                       club;
	protected String                       calculation = "";

	protected Boolean           readableCols     = false;
	protected Boolean           allApparaties    = false;
	protected ArrayList<String> apparaties       = new ArrayList<>();
	protected Boolean           apparatiesAsCols = false;
	protected String            groupBy;

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

	public CompetitionData() {
		competition = Application.selectedCompetition;
	}

	public String getSql() {
		String        sql;
		StringBuilder cols     = new StringBuilder();
		StringBuilder where    = new StringBuilder();
		StringBuilder joins    = new StringBuilder();
		String        order    = "";
		String        group    = "";
		String        classSql = "IFNULL((SELECT displayName FROM competition_" + competition + "_classes WHERE class = competition_" + competition + ".class), class)";

		parameters = new ArrayList<>();

		if (readableCols) {
			cols.append("competition_" + competition + ".ROWID, ID, Name, birthdate AS Geburtsdatum, " + classSql + " AS Altersklasse, club AS Verein, squad AS Riege, team AS Mannschaft");
		} else if (colums == null || colums.length == 0) {
			cols.append("competition_" + competition + ".ROWID, ID, name, birthdate, " + classSql + " AS class, club, squad, team");
		}

		if (allApparaties) {
			apparaties = DatabaseAnalyse.listApparatiInCompetition(competition);
		}

		if (apparaties != null && apparaties.size() > 0) {
			for (String apparti : apparaties) {

				joins.append(" LEFT JOIN competition_" + competition + "_apparati_" + apparti + " ON competition_" + competition + ".ROWID = competition_" + competition + "_apparati_" + apparti + ".gymnast");

				if (apparatiesAsCols) {
					cols.append(", " + apparti);
				}
			}
		}

		if (colums != null && colums.length > 0) {
			String comma = "";
			for (String column : colums) {
				cols.append(comma + column);
				comma = ", ";
			}
		}

		if (!calculation.isEmpty()) {
			for (String apparati : DatabaseAnalyse.listApparatiInCompetition()) {
				calculation = calculation.replaceAll(apparati, "IFNULL(" + apparati + ", 0)");
			}

			cols.append(", " + calculation + " AS Gesamt");
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

	public ArrayList<Gymnast> calculateClassResult() {
		String mode = "sumAll";
		calculation = "";
		ArrayList<Gymnast>            result      = new ArrayList<>();
		LinkedHashMap<String, String> classConfig = this.getClassConfig(className);

		if (!Common.emptyString(classConfig.get("displayColumns"))) {
			String[]          apparati     = classConfig.get("displayColumns").split("\\s*,\\s*");
			ArrayList<String> apparatiList = new ArrayList<>(apparati.length);

			for (String apparatus : apparati) {
				apparatiList.add(
						apparatus.replaceAll("AS .+", "")
				);
			}

			setApparaties(apparatiList);
			String[] addedCols = colums.clone();
			colums = new String[addedCols.length + 1];

			int i = 0;
			for (String col : addedCols) {
				colums[i] = addedCols[i];
				i++;
			}
			colums[i] = classConfig.get("displayColumns");
		}


		if (!Common.emptyString(classConfig.get("minApparati"))) {
			mode = "minApparati";
		} else if (classConfig.get("sumAll").equals("1")) {
			mode = "sumAll";
		} else if (!Common.emptyString(classConfig.get("calculation"))) {
			calculation = classConfig.get("calculation");
			mode = "calculation";
		}

		ResultSet classResult = Application.database.query(getSql(), getParameters());

		try {

			while (classResult.next()) {
				result.add(
						new Gymnast(classResult)
				);
			}

			classResult.close();

		} catch (Exception e) {
			Common.printError(e);
		}

		if (!mode.equals("calculation")) {

			for (Gymnast gymnast : result) {

				if (mode.equals("sumAll")) {

					Double sum = 0.0;
					for (Map.Entry<String, Double> apparatus : gymnast.getApparatiValues().entrySet()) {
						sum += apparatus.getValue();
					}
					gymnast.setSum(sum);

				} else if (mode.equals("minApparati")) {
					ArrayList<Double> valueList = new ArrayList<Double>(gymnast.getApparatiValues().values());
					Double            sum       = 0.0;

					Collections.sort(valueList, new Comparator<Double>() {
						@Override
						public int compare(Double o1, Double o2) {
							return Double.compare(o1, o2) * -1;
							//return (o1 > o2 ? 1 : (o1 < o2 ? -1 : 0));
						}
					});

					Integer minApparati    = Integer.parseInt(classConfig.get("minApparati"));
					Integer summedApparati = 0;
					for (Double apparatiValue : valueList) {
						if (summedApparati < minApparati) {
							sum += apparatiValue;
							summedApparati++;
						}
					}

					gymnast.setSum(sum);
				}

			}

		}

		//Nach sum sortieren
		Collections.sort(result, new Comparator<Gymnast>() {
			@Override
			public int compare(Gymnast o1, Gymnast o2) {
				return Double.compare(o1.getSum(), o2.getSum()) * -1;
			}
		});

		//Platzierung berechnen
		Double  lastSum  = 0.0;
		Integer lastRank = 1;
		Integer rank     = 1;
		for (Gymnast gymnast : result) {

			if (gymnast.getSum().equals(lastSum)) {
				gymnast.setRanking(lastRank);
			} else {
				gymnast.setRanking(rank);
			}

			lastRank = gymnast.getRanking();
			lastSum = gymnast.getSum();

			rank++;
		}

		return result;
	}

	public ArrayList<DatabaseParameter> getParameters() {
		return parameters;
	}

	public LinkedHashMap<String, String> getClassConfig(String className) {
		LinkedHashMap<String, String> config = new LinkedHashMap<>();

		String                       sql   = "SELECT * FROM competition_" + competition + "_classes WHERE class = ?;";
		ArrayList<DatabaseParameter> param = new ArrayList<>();

		param.add(new DatabaseParameter(className));

		ResultSet result = Application.database.query(sql, param);

		try {

			if (result.next()) {
				config = Application.database.convertResultRowToLinkedHasmap(result);
			}

			result.close();
		} catch (SQLException e) {
			Common.printError(e);
		}

		if (config.size() == 0) {

			if (Pattern.compile("(w|weiblich)$").matcher(className).find()) {
				config.put("displayColumns", "Sprung, Stufenbarren, Balken, Boden");
			} else {
				config.put("displayColumns", "Boden, Pauschenpferd, Ringe, Sprung, Barren, Reck");
			}

		}

		return config;
	}

	protected ArrayList<String> list(String column) {
		ArrayList<String> values = new ArrayList<>();
		CompetitionData   data   = new CompetitionData();

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
