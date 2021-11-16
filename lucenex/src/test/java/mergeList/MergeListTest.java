package mergeList;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import fileWriter.WriterFile;
import jsonparser.Cell;

public class MergeListTest {

	private int TOP_K_MAX = 50;
	private int TOP_K_MIN = 2;
	
	@Test
	public void mergeList(Set<Cell> querySet, Directory directory) throws Exception {

		WriterFile writerFile = new WriterFile();
		IndexReader reader = DirectoryReader.open(directory); //obtain read access to the inverted indexes
		IndexSearcher searcher = new IndexSearcher(reader); 
		Map<Integer,Integer> set2count = new TreeMap<Integer,Integer>();		//chiave: documento
																				//valore: numero di volte che matcha con la query
		Map<Integer,String> doc2table = new TreeMap<Integer,String>();			//chiave: id documento
																				//valore: id tabella che contiene il doc
		KSelector kSelector = new KSelector();
		
		long startIndexing = System.currentTimeMillis();

		for(Cell cell : querySet) {
			String text = cell.getCleanedText().toLowerCase();
			Query query = new TermQuery(new Term("cella", text));
			TopDocs hits = searcher.search(query, reader.numDocs()); //search for all documents that match the query
			
			for (int i = 0; i < hits.scoreDocs.length; i++) { 
				ScoreDoc scoreDoc = hits.scoreDocs[i]; 								//hits.scoreDocs[i] indica il documento 
																					//hits.scoreDocs documenti che matchano con la query
				Document doc = searcher.doc(scoreDoc.doc); //fetch returned document		//scoreDoc.doc è la posizione del documento
																							//doc è il documento
				System.out.println(doc.get("idTable"));
								
				if(set2count.containsKey(scoreDoc.doc)) {
					int count = set2count.get(scoreDoc.doc) + 1;
					set2count.put(scoreDoc.doc, count);
				}
				else { 
					set2count.put(scoreDoc.doc, 1);
					doc2table.put(scoreDoc.doc, doc.get("idTable"));
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
		
		
		//ordino la mappa
		List<Map.Entry<Integer, Integer>> orderedList = sortByValue(set2count);
		long timeIndexingAndSort = System.currentTimeMillis() - startIndexing;		
		
		
		//tempi degli esperimenti al variare di K
		long startTopKMin = System.currentTimeMillis();
		Map<Integer,Integer> orderedSet2count_MIN_K = kSelector.selectTopK(orderedList, TOP_K_MIN);
		long timeTopKMin = System.currentTimeMillis() - startTopKMin;
		timeTopKMin = timeIndexingAndSort + timeTopKMin;

		long startTopKMax = System.currentTimeMillis();
		Map<Integer,Integer> orderedSet2count_MAX_K = kSelector.selectTopK(orderedList, TOP_K_MAX);
		long timeTopKMax = System.currentTimeMillis() - startTopKMax;
		timeTopKMax = timeIndexingAndSort + timeTopKMax;

		writerFile.writeOnFile(querySet.size(), timeTopKMin, timeTopKMax, sizeSet2Count, matching);
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
	

}
