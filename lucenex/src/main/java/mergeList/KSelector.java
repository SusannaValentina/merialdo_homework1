package mergeList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;

public class KSelector {

	/* ------- selectTopK: seleziona i top K documenti da orderedSet2count ------- */
	public Map<Integer,Integer> selectTopK (Map<Integer,Integer> orderedSet2count, int TOP_K) {

		//chiave: id documento, valore: numero di volte che matcha con la query (solo i top K)
		Map<Integer,Integer> orderedSet2count_TOP_K = new HashMap<Integer, Integer>();

		//seleziona i top k valori
		if(orderedSet2count.size() > TOP_K) {
			
			int maxValue = orderedSet2count.get(orderedSet2count.keySet().toArray()[0]);
			for(Integer i: orderedSet2count.keySet()) {
				if(orderedSet2count.get(i) == (maxValue)) {
					orderedSet2count_TOP_K.put(i, orderedSet2count.get(i));
				}
				else {
					TOP_K --;
					if(TOP_K == 0) {
						break;
					}
					else {
						maxValue = orderedSet2count.get(i);
						orderedSet2count_TOP_K.put(i, orderedSet2count.get(i));
					}
				}
			}
		}
		else {
			orderedSet2count_TOP_K.putAll(orderedSet2count);
		}

		//ordinamento di orderedSet2count_TOP_K
		orderedSet2count_TOP_K = orderedSet2count_TOP_K.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		return orderedSet2count;
	}



	/* ------- selectTopBestK: seleziona i best K documenti da orderedSet2count ------- */
	public Map<Integer,Integer> selectTopBestK (Map<Integer,Integer> orderedSet2count_TOP_K) {

		int stop = orderedSet2count_TOP_K.get(orderedSet2count_TOP_K.keySet().toArray()[0])/3;
		
		//chiave: id documento, valore: numero di volte che matcha con la query (solo i top K)
		Map<Integer, Integer> orderedSet2count_BEST_K = new TreeMap<Integer, Integer>();
		
		for(int i: orderedSet2count_TOP_K.keySet()) {
			if(orderedSet2count_TOP_K.get(i) < stop ) {
				break;
			}
			orderedSet2count_BEST_K.put(i, orderedSet2count_TOP_K.get(i));
		}

		//ordinamento di orderedSet2count_BEST_K
		orderedSet2count_BEST_K = orderedSet2count_BEST_K.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		return orderedSet2count_BEST_K;
	}
}
