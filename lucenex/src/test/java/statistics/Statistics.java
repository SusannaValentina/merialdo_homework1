package statistics;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsonparser.Cell;
import jsonparser.Table;

public class Statistics {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private int totTables = 0;
	
	private int numColumns = 0;
	private int totColumns = 0;
	private int avgColumns = 0;
	
	private int numRows = 0;
	private int totRows = 0;
	private int avgRows = 0;
	
	private int numNullValues = 0;
	private int totNullValues = 0;
	private int avgNullValues = 0;
	private int numNullValuesCol = 0;
	private int totCells4Col = 0;
	private float percCol = 0;
	
	private int totCells = 0;
	private float perc = 0;
	
	private int max = 0;
	private int weightedAvg = 0;
	
	private Map<Integer,Integer> distrNumColumns = new HashMap<Integer,Integer>(); 	//chiave: numero di colonne
																					//valore: quante tabelle hanno quel numero di colonne
	private Map<Integer,Integer> distrNumRows = new HashMap<Integer,Integer>();		//chiave: numero di righe
																					//valore: quante tabelle hanno quel numero di righe
	private Map<Integer,Integer> distrNullValues = new HashMap<Integer,Integer>(); 	//chiave: numero di valori nulli
																					//valore: quante tabelle hanno quel numero di valori nulli
	private Map<Integer,Integer> distrNullValuesCol = new HashMap<Integer,Integer>(); 	//chiave: numero di valori nulli
																						//valore: quante colonne hanno quel numero di valori nulli
	private Map<Integer,Integer> distrDistValues4Columns = new HashMap<Integer,Integer>();  //chiave: numero valori distinti
																							//valore: quante colonne hanno quel numero di valori distinti
	private Map<Integer,Integer> distrDistValues4Tables = new HashMap<Integer,Integer>();	//chiave: numero valori distinti
																							//valore: quante tabelle hanno quel numero di valori distinti
	private Map<Integer,Integer> distrPercNullValues4Table = new HashMap<Integer,Integer>();	//chiave: percentuali valori nulli
																							//valore: quante tabelle hanno quella percentuale di valori nulli
	private Map<Integer,Integer> distrPercNullValues4Col = new HashMap<Integer,Integer>();	//chiave: percentuali valori nulli
																								//valore: quante colonne hanno quella percentuale di valori nulli

	
	
	@Test
	public void parserJsonTablesStatistics() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader("tables.json"));
		String line;
		Table table;
		int count = 0;
		
		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 
			
			totCells = 0;
			count = count + 1;
			
			totTables = totTables + 1;  					//numero di tabelle totali
			
			// Deserialization into the `Table` class
			table = objectMapper.readValue(line, Table.class);
			table.createCells();
			
			//COLONNE
			numColumns = table.getColumnsMap().size();				//numero di colonne di una tabella
			totColumns = totColumns + numColumns;						//numero di colonne totali
			
			//distribuzione colonne
			if(distrNumColumns.containsKey((Integer)numColumns)) {		//distribuzione del numero di colonne 
				distrNumColumns.put((Integer)numColumns, distrNumColumns.get((Integer)numColumns) + 1);
			}
			else {
				distrNumColumns.put((Integer)numColumns, 1);
			}
			
			
			//RIGHE
			numRows = table.getColumnsMap().get(0).size();		//conto il numero di righe di una tabella
			totRows = totRows + numRows;							//conto il numero di righe totali
			
			//distribuzione righe
			if(distrNumRows.containsKey((Integer)numRows)) {		//distribuzione del numero di righe 
				distrNumRows.put((Integer)numRows, distrNumRows.get((Integer)numRows) + 1);
			}
			else {
				distrNumRows.put((Integer)numRows, 1);
			}
			
			
			//VALORI NULLI E DISTINTI
			Set<String> distValues4TableSet = new HashSet<>();
			numNullValues = 0;
			for(int i : table.getColumnsMap().keySet()) {
				totCells = totCells + 1;
				Set<String> distValues4ColumnSet = new HashSet<>(); 
				List<Cell> column = table.getColumnsMap().get(i);
				
				numNullValuesCol = 0;
				totCells4Col = column.size();
				for(Cell c : column) {
					if(c.getCleanedText().equals("")) {
						numNullValuesCol = numNullValuesCol + 1; ////numero di valori nulli in ogni colonna
						numNullValues = numNullValues + 1;		//numero di valori nulli in ogni tabella
					}
					else {										//valori distinti
						distValues4ColumnSet.add(c.getCleanedText());
						distValues4TableSet.add(c.getCleanedText());
					}
				}
	
				//distribuzione valori nulli per colonna
				if(distrNullValuesCol.containsKey((Integer)numNullValuesCol)) {			//distribuzione del numero di valori distinti per colonna 
					distrNullValuesCol.put((Integer)numNullValuesCol, distrNullValuesCol.get((Integer)numNullValuesCol) + 1);
				}
				else {
					distrNullValuesCol.put((Integer)numNullValuesCol, 1);
				} 
				
				//distribuzione in percentuale del numero di valori nulli per tabella
				percCol = ((float)numNullValuesCol/(float)totCells4Col) * 100;
				if(distrPercNullValues4Col.containsKey((int)percCol)) {		
					distrPercNullValues4Col.put((int)percCol, distrPercNullValues4Col.get((int)percCol) + 1);
				}
				else {
					distrPercNullValues4Col.put((int)percCol, 1);
				} 
				
				//distribuzione valori distinti per colonna
				int num = distValues4ColumnSet.size();  						//chiave di distrDistValues4Columns
				if(distrDistValues4Columns.containsKey((Integer)num)) {			//distribuzione del numero di valori distinti per colonna 
					distrDistValues4Columns.put((Integer)num, distrDistValues4Columns.get((Integer)num) + 1);
				}
				else {
					distrDistValues4Columns.put((Integer)num, 1);
				} 
				
			}
			
			totNullValues = totNullValues + numNullValues;		//numero di valori nulli in tutte le tabelle
			
			//distribuzione di valori nulli per tabella
			if(distrNullValues.containsKey(numNullValues)) {			
				distrNullValues.put((Integer)numNullValues, distrNullValues.get(numNullValues) + 1);
			}
			else {
				distrNullValues.put((Integer)numNullValues, 1);
			}
			
			//distribuzione del numero di valori distinti per tabella 
			int num1 = distValues4TableSet.size();  						//chiave di distrDistValues4Tables
			if(distrDistValues4Tables.containsKey((Integer)num1)) {		
				distrDistValues4Tables.put((Integer)num1, distrDistValues4Tables.get((Integer)num1) + 1);
			}
			else {
				distrDistValues4Tables.put((Integer)num1, 1);
			} 
			
			//distribuzione in percentuale del numero di valori nulli per tabella
			perc = ((float)numNullValues/(float)totCells) * 100;
			if(distrPercNullValues4Table.containsKey((int)perc)) {		
				distrPercNullValues4Table.put((int)perc, distrPercNullValues4Table.get((int)perc) + 1);
			}
			else {
				distrPercNullValues4Table.put((int)perc, 1);
			} 
		}
		
		//numero massimo e medio delle percentuali dei valori nulli
		max = 0;
		weightedAvg = 0;
		for(int i : distrPercNullValues4Table.keySet()) {
			if(i > max)
				max = i;
			weightedAvg = weightedAvg + i * distrPercNullValues4Table.get(i);
		}
		weightedAvg = weightedAvg/totTables;
		
		br.close(); 
		
		
		//medie
		avgColumns = totColumns/totTables;  		//numero medio di colonne
		avgRows = totRows/totTables;				//numero medio di righe
		avgNullValues = totNullValues/totTables;	//numero medio di valori nulli
	
		/*for (Integer i : distrDistValues4Columns.keySet()) {
			System.out.println(i + " -> " + distrDistValues4Columns.get(i));
		}
		System.out.println("size: " + distrDistValues4Columns.size() + "\n");
		
		
		for (Integer i : distrDistValues4Tables.keySet()) {
			System.out.println(i + " -> " + distrDistValues4Tables.get(i));
		}
		System.out.println("size: " + distrDistValues4Tables.size() + "\n");
		*/
		writeOnFile();
	}
	
	public void writeOnFile() {
		try {
			FileWriter myWriter = new FileWriter("statistics.txt");
			myWriter.write("Numero di tabelle: " + totTables + "\n\n");
			myWriter.write("Numero totale di colonne: " + totColumns + "\n");
			myWriter.write("Numero medio di colonne: " + avgColumns + "\n\n");
			myWriter.write("Numero totale di righe: " + totRows + "\n");
			myWriter.write("Numero medio di righe: " + avgRows + "\n\n");
			myWriter.write("Numero totale di valori nulli per tabella: " + totNullValues + "\n");
			myWriter.write("Numero medio di valori nulli per tabella: " + avgNullValues + "\n\n");
			
			//per scrivere mappe su file
			myWriter.write("Distribuzione numero di colonne (quante tabelle hanno 1, 2, 3, 4, etc. colonne) " + "\n");
			myWriter.write("[Chiave: numero di colonne, Valore: quante tabelle hanno quel numero di colonne] " + "\n");
			for (Integer i : distrNumColumns.keySet()) {
				myWriter.write(i + " ; " + distrNumColumns.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Distribuzione numero di righe (quante tabelle hanno 1, 2, 3, 4, etc. righe) " + "\n");
			myWriter.write("[Chiave: numero di righe, Valore: quante tabelle hanno quel numero di righe] " + "\n");
			for (Integer i : distrNumRows.keySet()) {
				myWriter.write(i + " ; " + distrNumRows.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Distribuzione di valori nulli (quante tabelle hanno 1, 2, 3, 4, etc. valori nulli) " + "\n");
			myWriter.write("[Chiave: numero di valori nulli, Valore: quante tabelle hanno quel numero di valori nulli]" + "\n");
			for (Integer i : distrNullValues.keySet()) {
				myWriter.write(i + " ; " + distrNullValues.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Distribuzione di valori distinti (quante colonne hanno 1, 2, 3, 4, etc. valori distinti) " + "\n");
			myWriter.write("[Chiave: numero di valori distinti, Valore: quante colonne hanno quel numero di valori distinti su tutte le tabelle]" + "\n");
			for (Integer i : distrDistValues4Columns.keySet()) {
				myWriter.write(i + " ; " + distrDistValues4Columns.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Distribuzione di valori distinti (quante tabelle hanno 1, 2, 3, 4, etc. valori distinti) " + "\n");
			myWriter.write("[Chiave: numero di valori distinti, Valore: quante tabelle hanno quel numero di valori distinti]" + "\n");
			for (Integer i : distrDistValues4Tables.keySet()) {
				myWriter.write(i + " ; " + distrDistValues4Tables.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Distribuzione in percentuale di valori nulli (quante tabelle hanno il 10%, 20%, etc. valori nulli) " + "\n");
			myWriter.write("[Chiave: percentuale di valori nulli, Valore: quante tabelle hanno quella percentuale di valori nulli]" + "\n");
			for (int i : distrPercNullValues4Table.keySet()) {
				myWriter.write(i + " ; " + distrPercNullValues4Table.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Numero massimo percentuale di valori nulli tra tutte le tabelle: " + max + "\n\n");
			myWriter.write("Numero medio percentuale valori nulli (media ponderata): " + weightedAvg + "\n\n");
			
			myWriter.write("Distribuzione di valori nulli per colonna (quante colonne hanno 1, 2, 3 etc. valori nulli) " + "\n");
			myWriter.write("[Chiave: numero di valori nulli, Valore: quante colonne hanno quel numero di valori nulli]" + "\n");
			for (int i : distrNullValuesCol.keySet()) {
				myWriter.write(i + " ; " + distrNullValuesCol.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.write("Distribuzione in percentuale di valori nulli per colonne (quante colonne hanno il 10%, 20%, etc. valori nulli) " + "\n");
			myWriter.write("[Chiave: percentuale di valori nulli, Valore: quante colonne hanno quella percentuale di valori nulli]" + "\n");
			for (int i : distrPercNullValues4Col.keySet()) {
				myWriter.write(i + " ; " + distrPercNullValues4Col.get(i) + "\n");
			}
			myWriter.write("\n");
			
			myWriter.close();
			//System.out.println("Successfully wrote to the file.");
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
