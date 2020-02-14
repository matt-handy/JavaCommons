package handy.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TagParseSet {
	private List<Pattern> patterns = new ArrayList<>();
	private String tag;
	
	public TagParseSet(String tag) {
		this.tag = tag;
	}
	
	public void addPattern(Pattern pattern) {
		patterns.add(pattern);
	}

	public List<Pattern> getPatterns() {
		return patterns;
	}

	public String getTag() {
		return tag;
	}
	
	
}
