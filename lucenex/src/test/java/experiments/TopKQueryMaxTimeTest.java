package experiments;


import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fileWriter.WriterFile;
import jsonparser.Cell;
import jsonparser.Table;
import mergeList.KSelector;

public class TopKQueryMaxTimeTest {

	private int TOP_K_MAX = 50;
	private int TOP_K_MIN = 2;

	@Test
	/* ------- main: parser di tables.json e selezione della colonna con il numero maggiore di righe come query ------- */
	public void main() throws Exception {

		//apertura path e directory
		Path path = Paths.get("lucene-index");
		Directory directory = FSDirectory.open(path);

		BufferedReader br = new BufferedReader(new FileReader("tables.json"));

		ObjectMapper objectMapper = new ObjectMapper();
		String line = null;
		Table table = null;

		Set<Cell> querySetNotNull = new HashSet<Cell>();
		Set<Cell> setMax = new HashSet<Cell>();
		int max = 0;

		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 

			table = objectMapper.readValue(line, Table.class);
			table.createCells();

			for(int i : table.getColumnsMap().keySet()) {
				querySetNotNull = delDupl(table.getColumnsMap().get(i));
				if(querySetNotNull.size() > max) {
					setMax = querySetNotNull;
					max = querySetNotNull.size();
				}
			}
		}  
		br.close();
		this.mergeList(setMax, directory);
		System.out.println("Fine");	
	}


	
	/* ------- delDupl: elimina duplicati e valori nulli dalla query ------- */
	public Set<Cell> delDupl(List<Cell> queryList) throws Exception { 

		Set<Cell> querySetNotNull = new HashSet<Cell>();
		Boolean b = true;

		//elimina duplicati e valori nulli dalla query
		for(Cell c : queryList) {
			b = true;
			if(!(c.getCleanedText().equals("")) && !(c.getCleanedText().equals("-"))) { //nulli
				for(Cell cell : querySetNotNull) {		//duplicati
					if(cell.getCleanedText().equals(c.getCleanedText())) {
						b = false;
						break;
					}
				}
				if(b)
					querySetNotNull.add(c);
			}
		}
		return querySetNotNull;
	}



	/* ------- mergeList: effettua il merge ------- */
	public void mergeList(Set<Cell> querySet, Directory directory) throws Exception {

		WriterFile writerFile = new WriterFile();
		IndexReader reader = DirectoryReader.open(directory); 
		IndexSearcher searcher = new IndexSearcher(reader); 
		
		KSelector kSelector = new KSelector();
		
		//chiave: documento, valore: numero di volte che matcha con la query
		Map<Integer,Integer> set2count = new TreeMap<Integer,Integer>();		

		long startIndexing = System.currentTimeMillis();

		for(Cell cell : querySet) {
			String text = cell.getCleanedText().toLowerCase();
			Query query = new TermQuery(new Term("cella", text));
			TopDocs hits = searcher.search(query, reader.numDocs()); //search for all documents that match the query

			for (int i = 0; i < hits.scoreDocs.length; i++) { 
				
				ScoreDoc scoreDoc = hits.scoreDocs[i]; 								 
												
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


		//ordinamento di set2count
		Map<Integer,Integer> orderedSet2count = set2count.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		long timeIndexingAndSort = System.currentTimeMillis() - startIndexing;		


		//tempi degli esperimenti al variare di K
		long startTopKMin = System.currentTimeMillis();
		Map<Integer,Integer> orderedSet2count_MIN_K = kSelector.selectTopK(orderedSet2count, TOP_K_MIN);
		long timeTopKMin = System.currentTimeMillis() - startTopKMin;
		timeTopKMin = timeIndexingAndSort + timeTopKMin;

		long startTopKMax = System.currentTimeMillis();
		Map<Integer,Integer> orderedSet2count_MAX_K = kSelector.selectTopK(orderedSet2count, TOP_K_MAX);
		long timeTopKMax = System.currentTimeMillis() - startTopKMax;
		timeTopKMax = timeIndexingAndSort + timeTopKMax;

		//scrittura su file
		writerFile.writeOnFile(querySet.size(), timeTopKMin, timeTopKMax, sizeSet2Count, matching);
	}	
}
