package handy.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import handy.common.array.ArrayHelper;

public class ColumnParser {

	public static boolean getTagInfo(ColumnTagInfo cti, List<String> lineDelims, List<String> columnDelims,
			String text) {
		String[] lines = {};
		String selectedDelim = null;
		for (String delim : lineDelims) {
			lines = text.split(delim);
			if (lines.length > 2) {
				selectedDelim = delim;
				break;
			}
		}

		if (selectedDelim == null) {
			return false;
		}

		String[] headers = {};
		String selectedHeaderDelim = null;
		for (String cDelim : columnDelims) {
			headers = lines[0].split(cDelim);
			if (headers.length > 2) {
				selectedHeaderDelim = cDelim;
				break;
			}
		}

		if (selectedHeaderDelim == null) {
			return false;
		}

		for (String label : cti.getAllTags()) {
			int idx = -1;
			LabelToColumnTag mapper = cti.getMapper(label);
			for (String str : mapper.getColumnTags()) {
				int checked = getIndexOfHeader(str, headers);
				if (checked != -1) {
					idx = checked;
					cti.setIndex(label, idx);
					break;
				}
			}
			if (idx == -1 && mapper.mandatory) {
				return false;
			}
		}

		return process(lines, cti, headers.length, selectedHeaderDelim, true);
	}

	public static boolean attemptBruteForce(ColumnTagInfo cti, List<String> lineDelims, List<String> columnDelims,
			String text) {
		String[] lines = {};
		String selectedDelim = null;
		for (String delim : lineDelims) {
			lines = text.split(delim);
			if (lines.length > 2) {
				selectedDelim = delim;
				break;
			}
		}

		if (selectedDelim == null) {
			return false;
		}

		String chosenDelim = null;
		int countOfCols = -1;
		outerloop: for (String delim : columnDelims) {
			for (String line : lines) {
				String[] elems = line.split(delim);
				if (elems.length > 4) {
					chosenDelim = delim;
					countOfCols = elems.length;
					break outerloop;
				}
			}
		}

		if (chosenDelim == null || countOfCols == -1) {
			return false;
		}
		
		Map<String, int[]> probabilisticMapper = new HashMap<>();
		for (String label : cti.getAllTags()) {
			LabelToColumnTag mapper = cti.getMapper(label);
			if(mapper.isProbabisticContains()) {
				probabilisticMapper.put(label, new int[countOfCols]);
			}
		}

		for (String line : lines) {
			String elems[] = line.split(chosenDelim);
			if (elems.length == countOfCols) {
				for (int idx = 0; idx < elems.length; idx++) {

					for (String label : cti.getAllTags()) {
						LabelToColumnTag mapper = cti.getMapper(label);
						if (cti.getIndex(label) == -1) {
							if (mapper.getPossibleRegex() != null) {
								for (Pattern pattern : mapper.getPossibleRegex()) {
									if (pattern.matcher(elems[idx]).find() &&
											(mapper.getExactLen() == -1 || mapper.getExactLen() == elems[idx].length())) {
										System.out.println("PIdx: " + label + " : " + idx);
										cti.setIndex(label, idx);
									}
								}
							}else if(mapper.getPossibleValues() != null && !mapper.isProbabisticContains()) {
								if(contains(elems[idx], mapper.getPossibleValues()) &&
										(mapper.getExactLen() == -1 || mapper.getExactLen() == elems[idx].length())) {
									System.out.println("CIdx: " + label + " : " + idx);
									cti.setIndex(label, idx);
								}
							}else if(mapper.getPossibleValues() != null && mapper.isProbabisticContains()){
								if(contains(elems[idx], mapper.getPossibleValues())) {
									probabilisticMapper.get(label)[idx]++;
								}
								//TODO: Need to account for first, last, full names with the 
								//probabilstic locator
							}
						}
					}

				}
			}
		}
		
		for (String label : cti.getAllTags()) {
			LabelToColumnTag mapper = cti.getMapper(label);
			if(mapper.isProbabisticContains()) {
				cti.setIndex(label, ArrayHelper.getIndexOfHighestAboveZero(probabilisticMapper.get(label)));
				System.out.println("ProbIdx: " + label + " : " + cti.getIndex(label));
			}
		}
		
		for(String label : cti.getAllTags()) {
			LabelToColumnTag mapper = cti.getMapper(label);
			if(mapper.mandatory && cti.getIndex(label) == -1) {
				return false;
			}
		}

		return process(lines, cti, countOfCols, chosenDelim, false);
	}

	public static boolean process(String[] lines, ColumnTagInfo cti, int targetColumnSize, String delim, boolean omitHeaderRow) {
		int idx = 0;
		if(omitHeaderRow) {
			idx = 1;
		}
		for (; idx < lines.length; idx++) {
			String[] lineBreaks = lines[idx].split(delim);
			if (lineBreaks.length == targetColumnSize) {
				Map<String, String> valueMap = new HashMap<>();
				for (String label : cti.getAllTags()) {
					int colIdx = cti.getIndex(label);
					if (colIdx != -1) {
						valueMap.put(label, lineBreaks[colIdx]);
					} else {
						// If an element does not have its own tag (above), check to see
						// if its a union field, and we can build it from composite.
						List<String> composite = cti.getComposite(label);
						if (composite != null) {
							String union = "";
							Set<Integer> mappedIdx = new HashSet<>();
							for (String elem : composite) {
								colIdx = cti.getIndex(elem);
								//Make sure that if there is a column referenced twice, we don't build it twice into the union
								if (colIdx != -1 && !mappedIdx.contains(colIdx)) {
									union += lineBreaks[colIdx];
									union += " ";
									mappedIdx.add(colIdx);
								}
							}
							if (union.length() != 0) {
								valueMap.put(label, union.trim());
							} else {
								// Stash whole thing if we can't find the composite members individually
								valueMap.put(label, lines[idx]);
							}
						}
					}
				}
				cti.addValueMap(valueMap);
			}

		}
		return true;
	}

	private static int getIndexOfHeader(String tag, String[] headers) {
		for (int idx = 0; idx < headers.length; idx++) {
			if (headers[idx].equalsIgnoreCase(tag)) {
				return idx;
			}
		}
		return -1;
	}
	
	private static boolean contains(String examine, Collection<String> strs) {
		for(String str : strs) {
			if(examine.contains(str)) {
				return true;
			}
		}
		return false;
	}
}
