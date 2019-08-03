package gothos.DatabaseCore;

import gothos.Application;
import gothos.Common;
import gothos.competitionMainForm.Gymnast;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TransferDataForFinalCompetition {

	String          competition;
	String          referenceCompetition;
	CompetitionData competitionData;

	Integer finalistsCount = 6;
	Integer replacersCount = 2;

	public enum CalculationMethod {
		MORE_MEMBERS_ON_EQUAL_VALUE,
		BETTER_RESULT_ON_EQUAL_VALUE,
		BETTER_RESULT_ONLY_FOR_LAST_ON_EQUAL_VALUE
	}

	public CalculationMethod finalistCalculationMethod = CalculationMethod.MORE_MEMBERS_ON_EQUAL_VALUE;

	public void setFinalistsCount (Integer finalistsCount) {
		this.finalistsCount = finalistsCount;
	}

	public void setReplacersCount (Integer replacersCount) {
		this.replacersCount = replacersCount;
	}

	public void setFinalistCalculationMethod (CalculationMethod finalistCalculationMethod) {
		this.finalistCalculationMethod = finalistCalculationMethod;
	}

	public TransferDataForFinalCompetition (String competition, String referenceCompetition) {
		this.competition = competition;
		this.referenceCompetition = referenceCompetition;

		competitionData = new CompetitionData();
		competitionData.setCompetition(referenceCompetition);
	}

	public void transfer () {

		transferApparaties();
		transferCertificates();
		transferClasses();

		transferGymnasts();
	}

	void transferApparaties () {
		ArrayList<String> referenceApparaties = DatabaseAnalyse.listApparatiInCompetition(referenceCompetition);
		ArrayList<String> apparaties          = DatabaseAnalyse.listApparatiInCompetition(competition);

		for (String apparatus : referenceApparaties) {

			if (apparaties.indexOf(apparatus) == -1) {
				DatabaseStructure.addApparatiToCompetition(competition, apparatus);
			}

		}
	}

	void transferClasses () {
		String sql = "INSERT INTO " + competition + "_classes SELECT * FROM " + referenceCompetition + "_classes;";
		Application.database.execute(sql);
	}

	void transferCertificates () {
		String sql = "INSERT INTO " + competition + "_certificates SELECT * FROM " + referenceCompetition + "_certificates;";
		Application.database.execute(sql);
	}

	void transferGymnasts () {

		ArrayList<String> apparaties = DatabaseAnalyse.listApparatiInCompetition(competition);
		ArrayList<String> classes    = competitionData.listClasses();

		for (String apparatus : apparaties) {
			for (String className : classes) {
				ArrayList<ArrayList<Integer>> gymnasts = getBestGymnastsForClassAndApparatus(apparatus, className);


			}
		}

	}

	ArrayList<ArrayList<Integer>> getBestGymnastsForClassAndApparatus (String apparatus, String className) {
		String                       sql        = "SELECT gymnast, " + apparatus + " FROM competition_" + referenceCompetition + "_apparati_" + apparatus + " JOIN competition_" + referenceCompetition + " ON competition_" + referenceCompetition + ".rowid = competition_" + referenceCompetition + "_apparati_" + apparatus + ".gymnast WHERE class = ? ORDER BY " + apparatus + " DESC;";
		ArrayList<DatabaseParameter> parameters = new ArrayList<>();
		parameters.add(new DatabaseParameter(className));

		LinkedHashMap<Integer, Double> gymnastValues = new LinkedHashMap<>();
		ArrayList<Integer>             finalists     = new ArrayList<>();
		ArrayList<Integer>             replacers     = new ArrayList<>();

		competitionData.setClassName(className);
		ArrayList<Gymnast> classResult = competitionData.calculateClassResult();

		try {
			ResultSet result = Application.database.query(sql, parameters);

			while (result.next()) {
				gymnastValues.put(
						result.getInt("gymnast"),
						result.getDouble(apparatus)
				);
			}

			result.close();
		} catch (Exception e) {
			Common.showError(e.getMessage());
			Common.showMessage("Fehler beim Kopieren der Daten");
		}

		Double  lastValue           = 0.0;
		Integer loopCounter         = 0;
		Integer localFinalistCount  = finalistsCount;
		Integer localReplacersCount = replacersCount;
		for (Map.Entry<Integer, Double> gymnastValue : gymnastValues.entrySet()) {
			//@todo Regeln für das Ermitteln der Finalisten klären

			switch (finalistCalculationMethod) {
				case MORE_MEMBERS_ON_EQUAL_VALUE:

					if (gymnastValue.getValue().equals(lastValue)) {
						if (loopCounter < localFinalistCount) {
							localFinalistCount++;
						} else if (loopCounter < (localFinalistCount + localReplacersCount)) {
							localReplacersCount++;
						}
					}

					break;
				case BETTER_RESULT_ON_EQUAL_VALUE:

					if (gymnastValue.getValue().equals(lastValue)) {

					}

					break;
				case BETTER_RESULT_ONLY_FOR_LAST_ON_EQUAL_VALUE:

					break;
			}

			if (loopCounter < localFinalistCount) {
				finalists.add(gymnastValue.getKey());
			} else if (loopCounter < (localFinalistCount + localReplacersCount)) {
				replacers.add(gymnastValue.getKey());
			}


			lastValue = gymnastValue.getValue();
			loopCounter++;
		}

		ArrayList<ArrayList<Integer>> returnList = new ArrayList<>(2);
		returnList.add(finalists);
		returnList.add(replacers);

		return returnList;
	}

	void insertGymnast (Integer gymnastId) {

	}

	void transferGymnastsForApparatus (String apparatus, String className) {

	}
}
