package mergeList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

import fileWriter.WriterFile;
import jsonparser.Cell;

public class MergeList {
	
	private int TOP_K = 5; 

	public void mergeList(Set<Cell> querySet, Directory directory) throws Exception {

		WriterFile writerFile = new WriterFile();
		IndexReader reader = DirectoryReader.open(directory); //obtain read access to the inverted indexes
		IndexSearcher searcher = new IndexSearcher(reader); 
		Map<Integer,Integer> set2count = new TreeMap<Integer,Integer>();		//chiave: documento
																				//valore: numero di volte che matcha con la query
		Map<Integer,String> doc2table = new TreeMap<Integer,String>();			//chiave: id documento
																				//valore: id tabella che contiene il doc
		KSelector kSelector = new KSelector();
		
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
		
		List<Map.Entry<Integer, Integer>> orderedList = sortByValue(set2count);
		
		//selezione best K
		Map<Integer,Integer> orderedSet2count_TOP_K = kSelector.selectTopK(orderedList, TOP_K);		//set2count con K=5	
		Map<Integer,Integer> orderedSet2count_BEST_K = kSelector.selectTopBestK(orderedSet2count_TOP_K);		//set2count con BEST_K
		
		writerFile.writeOnFileQuery(querySet.size(), TOP_K);
		writerFile.writeOnFileMergedMap(orderedSet2count_TOP_K);
		
		//valuesSet: indica il nuovo valore di k
		Set<Integer> valuesSet = new HashSet<>(orderedSet2count_BEST_K.values());
		writerFile.writeOnFileQuery(querySet.size(), valuesSet.size());
		writerFile.writeOnFileMergedMap(orderedSet2count_BEST_K); 
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
