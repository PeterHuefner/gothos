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

	public LinkedHashMap<String, Double> getApparatiValues() {
		return apparatiValues;
	}

	public void setApparatiValues(LinkedHashMap<String, Double> apparatiValues) {
		this.apparatiValues = apparatiValues;
	}

	public String getName() {
		return name;
	}

	/*public void addGymnast(Gymnast gymnast) {
		gymnasts.add(gymnast);
	}*/

	public ArrayList<Gymnast> getGymnasts() {
		return gymnasts;
	}

	public Team(ArrayList<Gymnast> gymnasts, LinkedHashMap<String, Double> apparatiValues, Double sum, String name) {
		this.gymnasts = gymnasts;
		this.apparatiValues = apparatiValues;
		this.setSum(sum);
		this.name = name;
	}

	/*public void calculateResult() {

	}*/
}
