package jsonparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import com.fasterxml.jackson.databind.ObjectMapper;

import mergeList.InvertedIndex;

public class Parser {

	private ObjectMapper objectMapper;
	private IndexWriter writer;
	//QUERY_NUMBER: numero della colonna della query
	private static final int QUERY_NUMBER = 0;

	public Parser() {
		super();
		this.objectMapper = new ObjectMapper();
	}

	
	/* ------- parserJsonTables: effettua il parser e l'indice per ogni tabella e per tutte le tabelle ------- */
	public void parserJsonTables(Directory directory) throws Exception{

		InvertedIndex invertedIndex = new InvertedIndex(directory);
		invertedIndex.getWriter().deleteAll();

		BufferedReader br = new BufferedReader(new FileReader("tables.json"));

		String line = null;
		Table table = null;
		int count = 0;
		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 

			count = count + 1;

			// Deserialization into the `Table` class
			table = objectMapper.readValue(line, Table.class);
			table.createCells();
			writer = invertedIndex.indexing(table);

			if(count == 50000) {
				writer.commit();
				System.out.println("count: " + count);
				count = 0;
			}	
		}  
		writer.commit();
		br.close(); 
	}


	/* ------- parserJsonQuery: torna la query letta dal file json ------- */
	public Set<Cell> parserJsonQuery() throws Exception {

		try(BufferedReader br = new BufferedReader(new FileReader("tables.json"))) {

			String line = null;
			Table table = null;
			List<Cell> queryList = null;
			Set<Cell> querySet = null;

			int count = 0;

			Random rand = new Random();
			int upperBound = 500000;
			int int_random = rand.nextInt(upperBound);

			//returns true if there is another line to read  
			while((line = br.readLine()) != null) { 

				count = count + 1;

				if(count == int_random) {
					// Deserialization into the `Table` class
					table = objectMapper.readValue(line, Table.class);
					table.createCells();
					queryList = table.getMappaColonne().get(QUERY_NUMBER);
					querySet = new HashSet<>(queryList);
					return querySet;
				}	
			}  
			br.close();
			return querySet;
		}
	}
}
