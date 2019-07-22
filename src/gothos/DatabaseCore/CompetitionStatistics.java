package gothos.DatabaseCore;

import gothos.Application;
import gothos.Common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompetitionStatistics {

    protected CompetitionData competitionData;
    protected Integer gymnastCountInCurrentQuery = 0;

    public CompetitionStatistics(CompetitionData competitionData) {
        this.competitionData = competitionData;
    }

    public Integer getGymnastCountInCurrentQuery() {
        return gymnastCountInCurrentQuery;
    }

    public LinkedHashMap<String, Integer> getCountsForCols(String[] columns) {
        return this.calcCountsForCols(columns, this.getStatsForCols(columns));
    }

    public LinkedHashMap<String, Integer> getCountsForCols(String[] columns, String sql, ArrayList<DatabaseParameter> parameters) {
        return this.calcCountsForCols(columns, this.getStatsForColsFromQuery(columns, sql, parameters));
    }

    public LinkedHashMap<String, Integer> calcCountsForCols(String[] columns, LinkedHashMap<String, LinkedHashMap<String, Integer>> stats) {
        LinkedHashMap<String, Integer> counts                       = new LinkedHashMap<>(columns.length);


        for (int i = 0; i < columns.length; i++) {
            counts.put(columns[i], 0);
        }

        for (Map.Entry<String, LinkedHashMap<String, Integer>> statEntry : stats.entrySet()) {
            counts.put(statEntry.getKey(), statEntry.getValue().size());
        }

        return counts;
    }

    public LinkedHashMap<String, LinkedHashMap<String, Integer>> getStatsForCols(String[] columns) {
        return this.getStatsForColsFromQuery(columns, this.competitionData.getSql(), this.competitionData.getParameters());
    }

    public LinkedHashMap<String, LinkedHashMap<String, Integer>> getStatsForColsFromQuery(String[] columns, String sql, ArrayList<DatabaseParameter> parameters) {
        this.gymnastCountInCurrentQuery = 0;

        LinkedHashMap<String, LinkedHashMap<String, Integer>> stats = new LinkedHashMap<>(columns.length);
        LinkedHashMap<Integer, LinkedHashMap<String, String>> gymnasts = Application.database.fetchAll(sql, parameters);

        for (int i = 0; i < columns.length; i++) {
            stats.put(columns[i], new LinkedHashMap<>());
        }

        for (Map.Entry<Integer, LinkedHashMap<String, String>> gymnastEntry : gymnasts.entrySet()) {
            LinkedHashMap<String, String> gymnast = gymnastEntry.getValue();
            this.gymnastCountInCurrentQuery++;

            for (int i = 0; i < columns.length; i++) {
                if (gymnast.containsKey(columns[i])) {
                    String columnValue = gymnast.get(columns[i]);
                    LinkedHashMap<String, Integer> columnStat = stats.get(columns[i]);

                    if (!columnStat.containsKey(columnValue)) {
                        if (!Common.emptyString(columnValue)) {
                            columnStat.put(columnValue, 1);
                        }
                    } else {
                        Integer columnTypeCount = columnStat.get(columnValue);
                        columnTypeCount++;
                        columnStat.put(columnValue, columnTypeCount);
                    }

                    stats.put(columns[i], columnStat);
                }
            }
        }

        return stats;
    }
}
