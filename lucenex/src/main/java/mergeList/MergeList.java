package mergeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

	/* ------- mergeList: effettua il mergeList tra la query e tables.json ------- */
	/**
	 * 
	 * @param querySet: colonna scelta come query
	 * @param directory
	 * @return bestDoc2table: mappa dei migliori top k documenti che matchano con la query
	 * @throws Exception
	 */
	public Map<Integer,String> mergeList(Set<Cell> querySet, Directory directory) throws Exception {

		WriterFile writerFile = new WriterFile();
		IndexReader reader = DirectoryReader.open(directory); //obtain read access to the inverted indexes
		IndexSearcher searcher = new IndexSearcher(reader); 

		KSelector kSelector = new KSelector();

		//chiave: documento, valore: numero di volte che matcha con la query
		Map<Integer,Integer> set2count = new TreeMap<Integer,Integer>();		

		//chiave: id documento, valore: id tabella che contiene il documento
		Map<Integer,String> doc2table = new HashMap<Integer,String>();			

		//scorre la colonna di query
		for(Cell cell : querySet) {
			String text = cell.getCleanedText().toLowerCase();
			Query query = new TermQuery(new Term("cella", text));
			TopDocs hits = searcher.search(query, reader.numDocs()); //search for all documents that match the query

			//scorre tutti i documenti che matchano con la query e crea set2count
			for (int i = 0; i < hits.scoreDocs.length; i++) { 		//hits.scoreDocs documenti che matchano con la query
				
				ScoreDoc scoreDoc = hits.scoreDocs[i]; 		//hits.scoreDocs[i] indica il documento 
				Document doc = searcher.doc(scoreDoc.doc); 		//scoreDoc.doc è la posizione del documento e doc il documento

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

		//ordinamento di set2count per valore
		//chiave: id documento, valore: numero di volte che matcha con la query
		Map<Integer,Integer> orderedSet2count = set2count.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		//selezione top K da orderedSet2count
		Map<Integer,Integer> orderedSet2count_TOP_K = kSelector.selectTopK(orderedSet2count, TOP_K);		//set2count con K=5	

		//selezione best K da orderedSet2count_TOP_K
		Map<Integer,Integer> orderedSet2count_BEST_K = kSelector.selectTopBestK(orderedSet2count_TOP_K);		//set2count con BEST_K


		//scrittura di top K su file
		writerFile.writeOnFileQuery(querySet.size(), TOP_K);
		writerFile.writeOnFileMergedMap(orderedSet2count_TOP_K);

		//scrittura di best K su file
		Set<Integer> valuesSet = new HashSet<>(orderedSet2count_BEST_K.values());		//valuesSet: indica il nuovo valore di k
		writerFile.writeOnFileQuery(querySet.size(), valuesSet.size());
		writerFile.writeOnFileMergedMap(orderedSet2count_BEST_K); 

		//selezione da doc2table dei documenti presenti in orderedSet2count_BEST_K
		Map<Integer,String> bestDoc2table = selectDoc2table(orderedSet2count_BEST_K, doc2table);

		return bestDoc2table;
	}	


	/* ------- selectDoc2table: seleziona dalla mappa di tutti i documenti solo i migliori del match ------- */
	/**
	 * 
	 * @param orderedSet2count_BEST_K: risultato del mergeList -> mappa ordinata dei best k documenti
	 * @param doc2table: mappa (documento -> tabella a cui appartiene)
	 * @return bestDoc2table: doc2table con solo le chiavi presenti in orderedSet2count_BEST_K
	 */
	public Map<Integer,String> selectDoc2table(Map<Integer,Integer> orderedSet2count_BEST_K, Map<Integer,String> doc2table) {

		//chiave: id documento, valore: id tabella
		Map<Integer,String> bestDoc2table = new HashMap<Integer, String>();
		
		for(Integer i : orderedSet2count_BEST_K.keySet()) {
			bestDoc2table.put(i, doc2table.get(i));
		}

		//ordinamento per valore 
		/*bestDoc2table = bestDoc2table.entrySet().stream().sorted(Map.Entry.<Integer, String>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));*/

		return bestDoc2table;
	}
}
