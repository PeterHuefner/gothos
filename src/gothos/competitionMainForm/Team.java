package gothos.competitionMainForm;

import gothos.Common;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Team {

	protected String                        name;
	protected LinkedHashMap<String, Double> apparatiValues;
	protected ArrayList<Gymnast>            gymnasts;
	protected Double                        sum;
	protected Integer                       ranking;

	public void setSum(Double sum) {
		sum = Common.round(sum, 3);
		this.sum = sum;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

	public Double getSum() {
		return sum;
	}

	public Integer getRanking() {
		return ranking;
	}

	public void addGymnast(Gymnast gymnast) {
		gymnasts.add(gymnast);
	}

	public ArrayList<Gymnast> getGymnasts() {
		return gymnasts;
	}

	public Team() {

	}

	public void calculateResult() {

	}
}
