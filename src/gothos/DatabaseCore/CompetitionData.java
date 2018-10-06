package gothos.DatabaseCore;

import com.sun.xml.internal.xsom.impl.scd.Iterators;
import gothos.Application;
import gothos.Common;
import gothos.competitionMainForm.Gymnast;
import gothos.competitionMainForm.Team;
import org.apache.pdfbox.debugger.ui.MapEntry;

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

	protected Boolean           readableCols         = false;
	protected Boolean           allApparaties        = false;
	protected ArrayList<String> apparaties           = new ArrayList<>();
	protected Boolean           apparatiesAsCols     = false;
	protected Boolean           onlyTeamMemberValues = false;
	protected String            groupBy;
	protected String            classSql;
	protected String            orderBy;


	public void setCompetition(String competition) {
		this.competition = competition;
		classSql = "IFNULL((SELECT displayName FROM competition_" + competition + "_classes WHERE class = competition_" + competition + ".class), class)";
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

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}

	public void setOnlyTeamMemberValues(Boolean onlyTeamMemberValues) {
		this.onlyTeamMemberValues = onlyTeamMemberValues;
	}

	public CompetitionData() {
		competition = Application.selectedCompetition;
		classSql = "IFNULL((SELECT displayName FROM competition_" + competition + "_classes WHERE class = competition_" + competition + ".class), class)";
	}

	public String getSql() {
		String        sql;
		StringBuilder cols  = new StringBuilder();
		StringBuilder where = new StringBuilder();
		StringBuilder joins = new StringBuilder();
		String        order = "";
		String        group = "";

		parameters = new ArrayList<>();

		if (readableCols) {
			cols.append("competition_" + competition + ".ROWID, ID, Name, birthdate AS Geburtsdatum, " + classSql + " AS Altersklasse, club AS Verein, squad AS Riege, team AS Mannschaft");
		} else if (colums == null || colums.length == 0) {
			useInternalColumns();
			//cols.append("competition_" + competition + ".ROWID, ID, name, birthdate, " + classSql + " AS class, club, squad, team");
		}

		if (allApparaties) {
			apparaties = DatabaseAnalyse.listApparatiInCompetition(competition);
		}

		if (apparaties != null && apparaties.size() > 0) {
			String comma = "";
			for (String apparti : apparaties) {

				String additionalJoin = "";
				if (onlyTeamMemberValues) {
					additionalJoin = " AND competition_" + competition + "_apparati_" + apparti + ".isTeamMember = 1";
				}

				joins.append(" LEFT JOIN competition_" + competition + "_apparati_" + apparti + " ON competition_" + competition + ".ROWID = competition_" + competition + "_apparati_" + apparti + ".gymnast" + additionalJoin);

				if (apparatiesAsCols) {
					if (cols.length() > 0) {
						comma = ", ";
					}

					cols.append(comma + apparti);
				}
			}
		}

		if (colums != null && colums.length > 0) {
			String comma = "";
			if (cols.length() > 0) {
				comma = ", ";
			}
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

		if (!Common.emptyString(orderBy)) {
			order = " ORDER BY " + orderBy;
		}

		if (!Common.emptyString(groupBy)) {
			group = " GROUP BY " + groupBy;
		}

		sql = "SELECT " + cols.toString() + " FROM competition_" + competition + " " + joins.toString() + " WHERE active = 1 " + where + " " + order + group + ";";
		//Common.systemoutprintln(sql);
		return sql;
	}

	public void useInternalColumns() {
		colums = new String[]{"competition_" + competition + ".ROWID, ID, name, birthdate, " + classSql + " AS class, club, squad, team"};
	}

	public ArrayList<Gymnast> calculateClassResult() {
		String mode = "sumAll";
		calculation = "";
		ArrayList<Gymnast>            result      = new ArrayList<>();
		LinkedHashMap<String, String> classConfig = this.getClassConfig(className);

		if (colums == null || colums.length == 0) {
			useInternalColumns();
		}

		if (!Common.emptyString(classConfig.get("displayColumns"))) {
			String[]          apparati     = classConfig.get("displayColumns").split("\\s*,\\s*");
			ArrayList<String> apparatiList = new ArrayList<>(apparati.length);

			for (String apparatus : apparati) {
				apparatiList.add(
						apparatus.replaceAll("AS .+", "")
				);
			}

			this.setApparaties(apparatiList);
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
		} else if (!Common.emptyString(classConfig.get("sumAll")) && classConfig.get("sumAll").equals("1")) {
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

				this.calculateGymnastSum(gymnast, mode, classConfig);

				/*if (mode.equals("sumAll")) {

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
				}*/

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

	public ArrayList<Team> calculateTeamResult() {
		ArrayList<Team>                                      result            = new ArrayList<>();
		ArrayList<String>                                    teams             = (new CompetitionData()).listTeams();
		LinkedHashMap<String, LinkedHashMap<String, String>> classConfigs      = new LinkedHashMap<>();
		LinkedHashMap<String, String>                        competitionConfig = this.getCompetitionData();
		LinkedHashMap<String, Integer>                       valuedApparati    = new LinkedHashMap<>();

		Integer calculationMode = Integer.parseInt(competitionConfig.getOrDefault("teamCalculationMode", "0"));
		Integer maxTeamMembers  = Integer.parseInt(competitionConfig.getOrDefault("numberOfMaxTeamMembers", "0"));

		this.setOnlyTeamMemberValues(true);
		this.setAllApparaties(true);
		this.setApparatiesAsCols(true);

		for (String teamName : teams) {

			Double                        teamSum      = 0.0;
			LinkedHashMap<String, Double> teamApparati = new LinkedHashMap<>();
			ArrayList<Gymnast>            gymnasts     = new ArrayList<>();
			this.setTeam(teamName);
			ResultSet teamResult = Application.database.query(getSql(), getParameters());

			try {

				while (teamResult.next()) {
					gymnasts.add(
							new Gymnast(teamResult)
					);
				}

				teamResult.close();

			} catch (Exception e) {

			}

			// alle Geräte addieren, aber nur bis max
			if (calculationMode == 0) {

				LinkedHashMap<String, ArrayList<Double>> teamMemberApparatiValues = new LinkedHashMap<>();

				for (Gymnast gymnast : gymnasts) {
					calculateGymnastSum(gymnast);

					// Von jedem Turner die Wertungen für jedes Gerät speichern
					for (Map.Entry<String, Double> apparatus : gymnast.getApparatiValues().entrySet()) {
						ArrayList<Double> apparatiValues = teamMemberApparatiValues.getOrDefault(apparatus.getKey(), new ArrayList<>());
						apparatiValues.add(apparatus.getValue());
						teamMemberApparatiValues.put(apparatus.getKey(), apparatiValues);
					}
				}

				//Nach sum sortieren
				Collections.sort(gymnasts, new Comparator<Gymnast>() {
					@Override
					public int compare(Gymnast o1, Gymnast o2) {
						return Double.compare(o1.getSum(), o2.getSum()) * -1;
					}
				});

				// Für jedes Gerät die Wertungen sortieren und nur die Anzahl der besten Wertungen behalten die erlaubt sind
				for (Map.Entry<String, ArrayList<Double>> apparatus : teamMemberApparatiValues.entrySet()) {
					ArrayList<Double> values = apparatus.getValue();

					if (values.size() > maxTeamMembers && maxTeamMembers > 0) {
						Collections.sort(values, new Comparator<Double>() {
							@Override
							public int compare(Double o1, Double o2) {
								return Double.compare(o1, o2) * -1;
							}
						});

						ArrayList<Double> realValues = new ArrayList<>();

						for (Double value : values) {
							if (realValues.size() <= maxTeamMembers) {
								realValues.add(value);
							}
						}

						teamMemberApparatiValues.put(apparatus.getKey(), realValues);
					}
				}

				for (Map.Entry<String, ArrayList<Double>> apparatus : teamMemberApparatiValues.entrySet()) {
					Double apparatiSum = 0.0;

					for (Double value : apparatus.getValue()) {
						apparatiSum += value;
					}

					if (apparatiSum > 0) {
						teamApparati.put(apparatus.getKey(), apparatiSum);
						teamSum += apparatiSum;
						valuedApparati.put(apparatus.getKey(), 1);
					}
				}

			} else {
				for (Gymnast gymnast : gymnasts) {
					//@TODO: Muss überabritet werden, dass Einzelergebnis des Turners nach AK kann nicht auf die Mannschaft addiert werden? Oder doch?
					calculateGymnastSum(gymnast);

					for (Map.Entry<String, Double> apparatus : gymnast.getApparatiValues().entrySet()) {
						teamApparati.putIfAbsent(apparatus.getKey(), 0.0);
						teamApparati.put(
								apparatus.getKey(),
								teamApparati.getOrDefault(apparatus.getKey(), 0.0) + apparatus.getValue()
						);
					}

					teamSum += gymnast.getSum();
				}
			}


			Team team = new Team(
					gymnasts,
					teamApparati,
					teamSum,
					teamName
			);

			result.add(team);
		}

		//Nach sum sortieren
		Collections.sort(result, new Comparator<Team>() {
			@Override
			public int compare(Team o1, Team o2) {
				return Double.compare(o1.getSum(), o2.getSum()) * -1;
			}
		});

		Comparator<Map.Entry<String, Double>> maleComparator = new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> a1, Map.Entry<String, Double> a2) {
				int compare = 0;

				LinkedHashMap<String, Integer> apparatiOrder = new LinkedHashMap<>();
				apparatiOrder.put("Boden", 1);
				apparatiOrder.put("Pauschenpferd", 2);
				apparatiOrder.put("Ringe", 3);
				apparatiOrder.put("Sprung", 4);
				apparatiOrder.put("Barren", 5);
				apparatiOrder.put("Reck", 6);

				compare = apparatiOrder.getOrDefault(a1.getKey(), 0) - apparatiOrder.getOrDefault(a2.getKey(), 0);

				return compare;
			}
		};

		Comparator<Map.Entry<String, Double>> femaleComparator = new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> a1, Map.Entry<String, Double> a2) {
				int compare = 0;

				LinkedHashMap<String, Integer> apparatiOrder = new LinkedHashMap<>();
				apparatiOrder.put("Sprung", 1);
				apparatiOrder.put("Stufenbarren", 2);
				apparatiOrder.put("Balken", 3);
				apparatiOrder.put("Boden", 4);

				compare = apparatiOrder.getOrDefault(a1.getKey(), 0) - apparatiOrder.getOrDefault(a2.getKey(), 0);

				return compare;
			}
		};

		Comparator<Map.Entry<String, Double>> comparator = maleComparator;

		if (result.size() > 0 && (result.get(0).getApparatiValues().get("Balken") != null || result.get(0).getApparatiValues().get("Stufenbarren") != null)) {
			comparator = femaleComparator;
		}

		//Platzierung berechnen
		Double  lastSum  = 0.0;
		Integer lastRank = 1;
		Integer rank     = 1;
		for (Team thisteam : result) {

			if (thisteam.getSum().equals(lastSum)) {
				thisteam.setRanking(lastRank);
			} else {
				thisteam.setRanking(rank);
			}

			lastRank = thisteam.getRanking();
			lastSum = thisteam.getSum();

			rank++;

			for (Map.Entry<String, Integer> apparatus : valuedApparati.entrySet()) {
				thisteam.getApparatiValues().putIfAbsent(apparatus.getKey(), 0.0);
			}

			LinkedHashMap<String, Double> newApparatiList = new LinkedHashMap<>();
			thisteam.getApparatiValues().entrySet().stream()
					.sorted(comparator)
					.forEach(stringDoubleEntry -> {
						newApparatiList.put(stringDoubleEntry.getKey(), stringDoubleEntry.getValue());
					});
			thisteam.setApparatiValues(newApparatiList);
		}

		return result;
	}

	protected Gymnast calculateGymnastSum(Gymnast gymnast, String mode, LinkedHashMap<String, String> classConfig) {

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

			//LinkedHashMap<String, String> classConfig = this.getClassConfig(gymnast.getMetaData("class"));

			Integer minApparati    = Integer.parseInt(classConfig.get("minApparati"));
			Integer summedApparati = 0;
			for (Double apparatiValue : valueList) {
				if (summedApparati < minApparati) {
					sum += apparatiValue;
					summedApparati++;
				}
			}

			gymnast.setSum(sum);
		} else if (mode.equals("calculation")) {
			//LinkedHashMap<String, String> classConfig = this.getClassConfig(gymnast.getMetaData("class"));

			if (!Common.emptyString(classConfig.get("calculation"))) {

				CompetitionData competitionData = new CompetitionData();
				competitionData.setCalculation(classConfig.get("calculation"));
				competitionData.setGymnast(gymnast.getROWID());
				ResultSet gymnastResult = Application.database.query(competitionData.getSql(), competitionData.getParameters());

				try {
					if (gymnastResult.next()) {
						Double sum = Double.parseDouble(gymnastResult.getString("Gesamt"));
						gymnast.setSum(sum);
					}
					gymnastResult.close();
				} catch (Exception e) {
					Common.printError(e);
				}
			}
		}

		return gymnast;
	}

	protected Gymnast calculateGymnastSum(Gymnast gymnast) {
		LinkedHashMap<String, String> classConfig = this.getClassConfig(gymnast.getMetaData("class"));
		String                        mode        = "sumAll";

		if (!Common.emptyString(classConfig.get("minApparati"))) {
			mode = "minApparati";
		} else if (!Common.emptyString(classConfig.get("sumAll")) && classConfig.get("sumAll").equals("1")) {
			mode = "sumAll";
		} else if (!Common.emptyString(classConfig.get("calculation"))) {
			//calculation = classConfig.get("calculation");
			mode = "calculation";
		}

		return this.calculateGymnastSum(gymnast, mode, classConfig);
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

		if (config.size() == 0 || Common.emptyString(config.get("displayColumns"))) {

			if (Pattern.compile("(w|weiblich)$").matcher(className).find()) {
				config.put("displayColumns", "Sprung, Stufenbarren, Balken, Boden");
			} else {
				config.put("displayColumns", "Boden, Pauschenpferd, Ringe, Sprung, Barren, Reck");
			}

		}

		return config;
	}

	public LinkedHashMap<String, String> getCompetitionData(String competition) {
		LinkedHashMap<String, String> competitionData = new LinkedHashMap<>();
		String                        sql             = "SELECT * FROM competitions WHERE name = ?";
		ArrayList<DatabaseParameter>  parameters      = new ArrayList<>();

		parameters.add(new DatabaseParameter(competition));

		ResultSet result = Application.database.query(sql, parameters);

		try {
			if (result.next()) {
				competitionData = Application.database.convertResultRowToLinkedHasmap(result);
			}

			result.close();
		} catch (Exception e) {
			Common.printError(e);
		}

		return competitionData;
	}

	public LinkedHashMap<String, String> getCompetitionData() {
		return this.getCompetitionData(competition);
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
