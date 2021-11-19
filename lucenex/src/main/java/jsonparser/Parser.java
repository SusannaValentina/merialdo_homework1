package jsonparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fileWriter.WriterFile;
import invertedIndex.InvertedIndex;

public class Parser {

	private ObjectMapper objectMapper;
	private IndexWriter writer;
	//QUERY_NUMBER: numero della colonna della query
	private static final int QUERY_NUMBER = 0;

	public Parser() {
		super();
		this.objectMapper = new ObjectMapper();
	}


	/* ------- parserJsonTables: effettua il parser e crea l'indice per ogni tabella ------- */
	public void parserJsonTables(Directory directory) throws Exception{

		InvertedIndex invertedIndex = new InvertedIndex(directory);
		invertedIndex.getWriter().deleteAll();

		BufferedReader br = new BufferedReader(new FileReader("tables.json"));

		String line = null;
		Table table = null;
		int count4commit = 0;
		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 

			count4commit = count4commit + 1;

			// Deserialization into the `Table` class
			table = objectMapper.readValue(line, Table.class);
			table.createCells();
			writer = invertedIndex.indexing(table);

			if(count4commit == 50000) {
				writer.commit();
				System.out.println("count: " + count4commit);
				count4commit = 0;
			}	
		}  
		writer.commit();
		br.close(); 
	}


	/* ------- parserJsonQuery: restituisce una query letta dal file json ------- */
	/**
	 * 
	 * @param int_random: intero random per selezionare una tabella di tables.json
	 * @return querySet: la colonna numero QUERY_NUMBER della tabella selezionata
	 * @throws Exception
	 */
	public Set<Cell> parserJsonQuery(int int_random) throws Exception {

		try (BufferedReader br = new BufferedReader(new FileReader("tables.json"))) {
			String line = null;
			Table table = null;
			List<Cell> queryList = null;
			Set<Cell> querySet = null;
			WriterFile writerFile = new WriterFile();

			int count = 0;

			//returns true if there is another line to read  
			while((line = br.readLine()) != null) { 

				if(count == int_random) {		
					// Deserialization into the `Table` class
					table = objectMapper.readValue(line, Table.class);
					table.createCells();
					writerFile.writeOnFileQueryTable(table);
					queryList = table.getColumnsMap().get(QUERY_NUMBER);
					querySet = new HashSet<>();

					//elimina i valori null (""), "-" e i duplicati 
					Boolean b = true;
					for(Cell c : queryList) {
						b = true;
						if(!(c.getCleanedText().equals("")) || !(c.getCleanedText().equals("-"))) {
							for(Cell cell : querySet) {
								if(cell.getCleanedText().equals(c.getCleanedText())) {
									b = false;
									break;
								}
							}
							if(b)
								querySet.add(c);
						}
					}
					return querySet;
				}	
				count = count + 1;
			}  
			br.close();
			return querySet;
		}
	}


	/* ------- parserJsonIdTable: rilegge le tabelle di join e le scrive su file ------- */
	/**
	 * 
	 * @param selectDoc2table: mappa che associa il documento alla tabella a cui appartiene
	 * @throws Exception
	 */
	public void parserJsonIdTable(Map<Integer,String> bestDoc2table) throws Exception{

		BufferedReader br = new BufferedReader(new FileReader("tables.json"));

		String line = null;
		Table table = null;

		WriterFile writerFile = new WriterFile();

		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 

			// Deserialization into the `Table` class
			table = objectMapper.readValue(line, Table.class);

			if(bestDoc2table.containsValue(table.getId().getId())) {
				table.createCellsWithHeader();
				for(Integer i : bestDoc2table.keySet()) {
					if(bestDoc2table.get(i).equals(table.getId().getId())) {
						writerFile.writeOnFileTable(table, i);
					}
				}
			}
		}  
		br.close(); 
	}
}
