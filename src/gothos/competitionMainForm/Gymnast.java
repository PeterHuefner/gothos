package gothos.competitionMainForm;

import gothos.Common;
import gothos.DatabaseCore.DatabaseAnalyse;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;

public class Gymnast {

	protected Integer                                              ROWID                    = 0;
	protected LinkedHashMap<String, String>                        metaData                 = new LinkedHashMap<>();
	protected LinkedHashMap<String, Double>                        apparatiValues           = new LinkedHashMap<>();
	protected Double                                               sum;
	protected Integer                                              ranking;
	protected LinkedHashMap<String, LinkedHashMap<String, Double>> additionalApparatiValues = new LinkedHashMap<>();

	public Gymnast() {

	}

	public Gymnast(ResultSet databaseRow, boolean nullForAbsenceApparati) {
		loadResultSet(databaseRow, nullForAbsenceApparati);
	}

	public Gymnast(ResultSet databaseRow) {
		loadResultSet(databaseRow, false);
	}

	public Integer getROWID() {
		return ROWID;
	}

	public void setROWID(Integer ROWID) {
		this.ROWID = ROWID;
	}

	public LinkedHashMap<String, String> getMetaData() {
		return metaData;
	}

	public void setMetaData(LinkedHashMap<String, String> metaData) {
		this.metaData = metaData;
	}

	public LinkedHashMap<String, Double> getApparatiValues() {
		return apparatiValues;
	}

	public void setApparatiValues(LinkedHashMap<String, Double> apparatiValues) {
		this.apparatiValues = apparatiValues;
	}

	public LinkedHashMap<String, LinkedHashMap<String, Double>> getAdditionalApparatiValues() {
		return additionalApparatiValues;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		sum = Common.round(sum, 3);
		this.sum = sum;
	}

	public Integer getRanking() {
		return ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

	private void loadResultSet(ResultSet databaseRow, boolean nullForAbsenceApparati) {
		ArrayList<String> apparati = DatabaseAnalyse.listApparatiInCompetition();

		try {
			ResultSetMetaData resultMetaData = databaseRow.getMetaData();

			for (Integer index = 0; index < resultMetaData.getColumnCount(); index++) {
				String columnName = resultMetaData.getColumnLabel(index + 1);
				if (Common.emptyString(columnName)) {
					columnName = resultMetaData.getColumnName(index + 1);
				}

				String columnValue = databaseRow.getString(index + 1);

				if (columnName.equalsIgnoreCase("Gesamt")) {
					if (columnValue == null || columnValue.isEmpty() || columnValue.equalsIgnoreCase("null")) {
						columnValue = "0";
					}
					this.setSum(Double.parseDouble(columnValue));
				} else if (apparati.contains(columnName)) {
					if (columnValue == null || columnValue.isEmpty() || columnValue.equalsIgnoreCase("null")) {
						if (nullForAbsenceApparati) {
							apparatiValues.put(columnName, null);
						} else {
							apparatiValues.put(columnName, 0.0);
						}
					} else {
						apparatiValues.put(columnName, Double.parseDouble(columnValue));
					}

				} else if (columnName.equalsIgnoreCase("ROWID")) {
					ROWID = Integer.parseInt(columnValue);
				} else {
					Matcher match = Common.regex("(.+)_(d_value|e_value|additional_value_one|additional_value_two|additional_value_three|additional_value_four)", columnName);

					if (match.matches()) {
						if (!additionalApparatiValues.containsKey(match.group(1))) {
							additionalApparatiValues.put(match.group(1), new LinkedHashMap<String, Double>());
						}

						if (columnValue == null || columnValue.isEmpty() || columnValue.equalsIgnoreCase("null")) {
							if (nullForAbsenceApparati) {
								additionalApparatiValues.get(match.group(1)).put(match.group(2), null);
							} else {
								additionalApparatiValues.get(match.group(1)).put(match.group(2), 0.0);
							}
						} else {
							additionalApparatiValues.get(match.group(1)).put(match.group(2), Double.parseDouble(columnValue));
						}
					} else {
						metaData.put(columnName, columnValue);
					}
				}
			}

		} catch (Exception e) {
			Common.printError(e);
		}
	}

	public Double getApparatusValue(String apparatus) {
		Double value = 0.0;

		if (apparatiValues.get(apparatus) != null) {
			value = apparatiValues.get(apparatus);
		}

		return value;
	}

	public String getMetaData(String column) {
		String value = "";

		if (metaData.get(column) != null) {
			value = metaData.get(column);
		}

		return value;
	}
}
