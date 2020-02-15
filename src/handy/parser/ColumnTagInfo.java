package handy.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColumnTagInfo {

	private Map<String, LabelToColumnTag> labelToTag = new HashMap<>();
	private Map<String, Integer> tagToIndex = new HashMap<>();
	private Set<Map<String, String>> tagToValue = new HashSet<>();
	private Map<String, List<String>> compositeMapper = new HashMap<>();
	
	public void addCompositeMapper(String union, String member) {
		List<String> existingList = compositeMapper.get(union);
		if(existingList != null) {
			existingList.add(member);
		}else {
			List<String> memberList = new ArrayList<>();
			memberList.add(member);
			compositeMapper.put(union, memberList);
		}
	}
	
	public List<String> getComposite(String union){
		return compositeMapper.get(union);
	}
	
	public void addSimpleLabel(String label, String tag, boolean mandatory) {
		LabelToColumnTag mapper = new LabelToColumnTag(label, mandatory);
		mapper.addTag(tag);
		labelToTag.put(label, mapper);
	}
	
	public void setIndex(String tag, int index) {
		tagToIndex.put(tag, index);
	}
	
	public int getIndex(String tag) {
		if(tagToIndex.get(tag) == null) {
			return -1;
		}
		return tagToIndex.get(tag);
	}
	
	public void addValueMap(Map<String, String> newMap) {
		tagToValue.add(newMap);
	}
	
	public Set<Map<String, String>> getValues(){
		return tagToValue;
	}
	
	public void addLabel(String tag, LabelToColumnTag mapper) {
		labelToTag.put(tag, mapper);
	}
	
	public Set<String> getAllTags(){
		return labelToTag.keySet();
	}
	
	public LabelToColumnTag getMapper(String tag) {
		return labelToTag.get(tag);
	}
}
