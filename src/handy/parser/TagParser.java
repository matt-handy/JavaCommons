package handy.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagParser {
	private Set<TagParseSet> simpleOptionalSets = new HashSet<>();
	private Set<TagParseSet> simpleMandatorySets = new HashSet<>();
	private Map<TagParseSet, List<TagParseSet>> unionOrComposition = new HashMap<>(); 
	
	public void addOptionalSimpleTag(String id, Pattern pattern) {
		TagParseSet tps = new TagParseSet(id);
		tps.addPattern(pattern);
		simpleOptionalSets.add(tps);
	}
	
	public void addMandatorySimpleTag(String id, Pattern pattern) {
		TagParseSet tps = new TagParseSet(id);
		tps.addPattern(pattern);
		simpleMandatorySets.add(tps);
	}
	
	public void addOptionalTag(TagParseSet newSet) {
		simpleOptionalSets.add(newSet);
	}
	
	public void addMandatoryTag(TagParseSet newSet) {
		simpleMandatorySets.add(newSet);
	}
	
	//This supports the program first checking to see if a dataset is availabile in 
	//a file that represents the union of discrete data members or individual tags
	//for component elements. For example, "Full Name" would be a union tag, while
	//"First", "Middle", and "Last" could be individually tagged child elements.
	public void addUnionOrCompositionCompoundFilter(TagParseSet union, List<TagParseSet> componsition) {
		unionOrComposition.put(union, componsition);
	}

	public Set<TagParseSet> getSimpleOptionalSets() {
		return simpleOptionalSets;
	}

	public Set<TagParseSet> getSimpleMandatorySets() {
		return simpleMandatorySets;
	}
	
	public Map<String, List<String>> parse(String text){
		Map<String, List<String>> dataMap = new HashMap<>();
		
		//TODO: Separate tag from the data member
		for(TagParseSet set : simpleMandatorySets) {
			if(!parseTag(set, text, true, dataMap)) {
				return null;
			}
		}
		
		for(TagParseSet set : simpleOptionalSets) {
			parseTag(set, text, false, dataMap);
		}
		
		for(TagParseSet union : unionOrComposition.keySet()) {
			List<TagParseSet> composition = unionOrComposition.get(union);
			parseUnionOrCompoundTag(union, composition, text, dataMap);
		}
		
		
		return dataMap;
	}
	
	public boolean parseTag(TagParseSet set, String text, boolean mandatory, Map<String, List<String>> dataMap) {
		Matcher matcher;
		List<Matcher> matchers = new ArrayList<>();
		for(Pattern pattern : set.getPatterns()) {
			matchers.add(pattern.matcher(text));
		}
		matcher = findValidMatcher(matchers);

		if (matcher == null) {
			dataMap.put(set.getTag(), new ArrayList<>());
			return false;
		}
		
		List<String> taggedData = new ArrayList<>();
		while (matcher.find()) {
			taggedData.add(matcher.group());
		}
		
		dataMap.put(set.getTag(), taggedData);
		return true;
	}
	
	public boolean parseUnionOrCompoundTag(TagParseSet union, List<TagParseSet> compound, String text, Map<String, List<String>> dataMap) {
		Matcher unionMatcher = null;
		List<Matcher> matchers = new ArrayList<>();
		for(Pattern pattern : union.getPatterns()) {
			matchers.add(pattern.matcher(text));
		}
		unionMatcher = findValidMatcher(matchers);
		
		List<List<Matcher>> matcherList = new ArrayList<>();
		for(TagParseSet set : compound) {
			matchers = new ArrayList<>();
			for(Pattern pattern : set.getPatterns()) {
				matchers.add(pattern.matcher(text));
			}
			matcherList.add(matchers);
		}
		List<Matcher> culledMatchers = findValidMatchers(matcherList);
		
		Map<String, List<String>> dataHarvest = new HashMap<>();
		dataHarvest.put(union.getTag(), new ArrayList<>());
		for(TagParseSet element : compound) {
			dataHarvest.put(element.getTag(), new ArrayList<>());
		}
		
		if(culledMatchers != null && culledMatchers.size() != 0) {
			boolean nextMatch = true; // Assume true b/c findValidMatchers confirmed at least one valid
			for(Matcher m : culledMatchers) {
				m.reset();
				m.find();
			}
			while(nextMatch) {
				String unionStr = "";
				for(int idx = 0; idx < culledMatchers.size(); idx++) {
					String element = culledMatchers.get(idx).group();
					nextMatch = culledMatchers.get(idx).find() && nextMatch;
					
					dataHarvest.get(compound.get(idx).getTag()).add(element);
					unionStr += element;
					if(idx != culledMatchers.size() - 1) {
						unionStr += " ";
					}
				}
				dataHarvest.get(union.getTag()).add(unionStr);
			}
			dataMap.putAll(dataHarvest);
		}else if(unionMatcher != null) {
			dataMap.putAll(dataHarvest);
			parseTag(union, text, false, dataMap);
		}else {
			return false;
		}
		
		return true;
	}
	
	public static Matcher findValidMatcher(List<Matcher> matchers) {
		for (Matcher matcher : matchers) {
			if (matcher.find()) {
				matcher.reset();
				return matcher;
			}
		}
		return null;
	}
	
	public static List<Matcher> findValidMatchers(List<List<Matcher>> matcherList){
		List<Matcher> matchers = new ArrayList<>();
		for(List<Matcher> list : matcherList) {
			Matcher match = findValidMatcher(list);
			if(match == null) {
				return null;
			}
			matchers.add(match);
		}
		return matchers;
	}
}
