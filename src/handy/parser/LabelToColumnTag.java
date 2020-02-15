package handy.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LabelToColumnTag {
	public final String label;
	private List<String> columnTags = new ArrayList<>();
	private List<String> columnPossibleValues;
	private List<Pattern> columnPossibleRegex; 
	private int exactLen = -1;
	private boolean probabisticContains = false;
	public final boolean mandatory;
	
	public boolean isProbabisticContains() {
		return probabisticContains;
	}

	public void setProbabisticContains(boolean probabisticContains) {
		this.probabisticContains = probabisticContains;
	}

	public int getExactLen() {
		return exactLen;
	}
	
	public void setExactLen(int minLen) {
		this.exactLen = minLen;
	}
	
	public List<String> getPossibleValues(){
		return columnPossibleValues;
	}
	
	public List<Pattern> getPossibleRegex(){
		return columnPossibleRegex;
	}
	
	public void addPossibleRegex(String regex) {
		if(columnPossibleRegex == null) {
			columnPossibleRegex = new ArrayList<>();
		}
		columnPossibleRegex.add(Pattern.compile(regex));
	}
	
	public void addPossibleValues(List<String> newVals) {
		if(columnPossibleValues == null) {
			columnPossibleValues = new ArrayList<>();
		}
		columnPossibleValues.addAll(newVals);
	}
	
	public LabelToColumnTag(String label, boolean mandatory) {
		this.label = label;
		this.mandatory = mandatory;
	}
	
	public void addTag(String tag) {
		columnTags.add(tag);
	}
	
	public List<String> getColumnTags() {
		return columnTags;
	}
}
