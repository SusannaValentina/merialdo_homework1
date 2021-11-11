package mergeList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import fileWriter.WriterFile;
import jsonparser.Cell;

public class MergeList {

	private int TOP_K_MAX = 50;
	private int TOP_K_MIN = 2;
	private int TOP_K = 5;

	public void mergeList(Set<Cell> querySet, Directory directory) throws Exception {

		WriterFile writerFile = new WriterFile();
		IndexReader reader = DirectoryReader.open(directory); //obtain read access to the inverted indexes
		IndexSearcher searcher = new IndexSearcher(reader); 
		Map<Integer,Integer> set2count = new TreeMap<Integer,Integer>();		//chiave: documento
																				//valore: numero di volte che matcha con la query
		
		
		long startIndexing = System.currentTimeMillis();

		for(Cell cell : querySet) {
			String text = cell.getCleanedText().toLowerCase();
			Query query = new TermQuery(new Term("cella", text));
			TopDocs hits = searcher.search(query, reader.numDocs()); //search for all documents that match the query

			for (int i = 0; i < hits.scoreDocs.length; i++) { 
				ScoreDoc scoreDoc = hits.scoreDocs[i]; 								//hits.scoreDocs[i] indica il documento 
																					//hits.scoreDocs documenti che matchano con la query 
				//Document doc = searcher.doc(scoreDoc.doc); //fetch returned document		//scoreDoc.doc è la posizione del documento
				//doc è il documento
				if(set2count.containsKey(scoreDoc.doc)) {
					int count = set2count.get(scoreDoc.doc) + 1;
					set2count.put(scoreDoc.doc, count);
				}
				else { 
					set2count.put(scoreDoc.doc, 1);
				}
			}
		}		
		
		//numero di documenti con cui matcha la query
		int sizeSet2Count = set2count.size();
		
		//numero di match della query
		int matching = 0;
		for(int i : set2count.values()) {
			matching = matching + i;
		}
		
		//tempi degli esperimenti al variare di K
		List<Map.Entry<Integer, Integer>>  orderedList = sortByValue(set2count);
		long timeIndexingAndSort = System.currentTimeMillis() - startIndexing;
		
		long startTopKMin = System.currentTimeMillis();
		Map<Integer,Integer> orderedSet2count_MIN_K = selectTopK(orderedList, TOP_K_MIN);
		long timeTopKMin = System.currentTimeMillis() - startTopKMin;
		timeTopKMin = timeIndexingAndSort + timeTopKMin;
		
		long startTopKMax = System.currentTimeMillis();
		Map<Integer,Integer> orderedSet2count_MAX_K = selectTopK(orderedList, TOP_K_MAX);
		long timeTopKMax = System.currentTimeMillis() - startTopKMax;
		timeTopKMax = timeIndexingAndSort + timeTopKMax;
		
		writerFile.writeOnFile(querySet.size(), timeTopKMin, timeTopKMax, sizeSet2Count, matching);
		
		/*//selezione best K
		Map<Integer,Integer> orderedSet2count_TOP_K = selectTopK(orderedList, TOP_K);		//set2count con K=5	
		Map<Integer,Integer> orderedSet2count_BEST_K = selectTopBestK(orderedSet2count_TOP_K);		//set2count con BEST_K
		
		writerFile.writeOnFileQuery(querySet.size(), TOP_K);
		writerFile.writeOnFileMergedMap(orderedSet2count_TOP_K);
		
		Set<Integer> valuesSet = new HashSet<>(orderedSet2count_BEST_K.values());
		writerFile.writeOnFileQuery(querySet.size(), valuesSet.size());
		writerFile.writeOnFileMergedMap(orderedSet2count_BEST_K); */
	}

	public List<Map.Entry<Integer, Integer>> sortByValue(Map<Integer,Integer> set2count) {
		// Create a list from elements of HashMap
		List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(set2count.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer> >() {
			public int compare(Map.Entry<Integer, Integer> o1,
					Map.Entry<Integer, Integer> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		return list;
	}
	
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
		Map<Integer, Integer> bestSet2Count = new HashMap<Integer, Integer>();
		for(int i: orderedSet2Count.keySet()) {
			if(orderedSet2Count.get(i) < stop ) {
				break;
			}
			bestSet2Count.put(i, orderedSet2Count.get(i));
		}
	
		return bestSet2Count;
	}
}
