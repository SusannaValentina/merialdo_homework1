package mergeList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class KSelector {

	public Map<Integer,Integer> selectTopK (List<Map.Entry<Integer, Integer>> list, int TOP_K) {

		//select top k value
		if(list.size() < TOP_K)
			list = list.subList(0, list.size());
		else {
			int count = -1;
			int maxValue = list.get(0).getValue(); 
			for(Map.Entry<Integer, Integer> i: list) {
				if(i.getValue() == maxValue) {
					count++;
				}
				else {
					TOP_K --;
					if(TOP_K == 0) {
						break;
					}
					else {
						maxValue = i.getValue();
						count++;
					}
				}
			}
			list = list.subList(0, count);
		}

		// put data from sorted list to hashmap
		HashMap<Integer, Integer> orderedSet2count = new LinkedHashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> element : list) {
			orderedSet2count.put(element.getKey(), element.getValue());
		}
		return orderedSet2count;
	}



	public Map<Integer,Integer> selectTopBestK (Map<Integer,Integer> orderedSet2Count) {

		int stop = orderedSet2Count.get(0)/3;
		Map<Integer, Integer> bestSet2Count = new TreeMap<Integer, Integer>();
		//bestSet2Count.entrySet().stream().sorted(Entry.comparingByKey());
		for(int i: orderedSet2Count.keySet()) {
			if(orderedSet2Count.get(i) < stop ) {
				break;
			}
			bestSet2Count.put(i, orderedSet2Count.get(i));
		}
		return bestSet2Count;
	}
}
