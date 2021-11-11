package testStatistics;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsonparser.Cell;
import jsonparser.Coordinates;
import jsonparser.MaxDimension;
import jsonparser.Table;
import mergeList.InvertedIndex;

public class Prova {
	
	private Coordinates coord1 = new Coordinates(0,0);
	private Coordinates coord2 = new Coordinates(1,0);
	private Coordinates coord3 = new Coordinates(0,1);
	private Coordinates coord4 = new Coordinates(1,1);
	
	private Cell c1 = new Cell(false, coord1, "");
	private Cell c2 = new Cell(false, coord2, "");
	private Cell c3 = new Cell(false, coord3, "ciao3");
	private Cell c4 = new Cell(false, coord4, "");
	
	private Collection<Cell> collections = new ArrayList<Cell>();
	private Map<Integer, List<Cell>> mappaColonne = new HashMap<>();
	
	@Test
	public void parserJsonTablesStatistics() throws Exception{

		collections.add(c3);
		collections.add(c2);
		collections.add(c4);
		collections.add(c1);
		
		Table table = new Table();
		table.setCollectionCells(collections);
		table.setMappaColonne(mappaColonne);
		table.createCells();
		
		//ObjectMapper objectMapper = new ObjectMapper();
		Path path = Paths.get("lucene-index");
		Directory directory = FSDirectory.open(path); 
		InvertedIndex invertedIndex = new InvertedIndex(directory);
		invertedIndex.getWriter().deleteAll();
		
		int totTables = 0;
		int numColumns = 0;
		int totColumns = 0;
		int avgColumns = 0;
		
		int numRows = 0;
		int totRows = 0;
		int avgRows = 0;
		
		int numNullValues = 0;
		int totNullValues = 0;
		int avgNullValues = 0;
		
		float perc = 0;
		int totValues4Table;
		int totCelle = 0;
		
		Map<Integer,Integer> distrNumColumns = new HashMap<Integer,Integer>(); 	//chiave: numero di colonne
																				//valore: quante tabelle hanno quel numero di colonne
		Map<Integer,Integer> distrNumRows = new HashMap<Integer,Integer>();		//chiave: numero di righe
																				//valore: quante tabelle hanno quel numero di righe
		
		Map<Integer,Integer> distrDistValues4Columns = new HashMap<Integer,Integer>();
		Map<Integer,Integer> distrDistValues4Tables = new HashMap<Integer,Integer>();
		Map<Integer,Integer> distrPercNullValues4Table = new HashMap<Integer,Integer>();
		Map<Integer,Integer> distrNullValuesCol = new HashMap<Integer,Integer>(); 

	
		
		//returns true if there is another line to read  
		totTables = totTables + 1;  					//conto il numero di tabelle totali
		

		//COLONNE
		numColumns = table.getMappaColonne().size();				//numero di colonne di una tabella
		totColumns = totColumns + numColumns;						//numero di colonne totali

		if(distrNumColumns.containsKey((Integer)numColumns)) {		//distribuzione del numero di colonne 
			distrNumColumns.put((Integer)numColumns, distrNumColumns.get((Integer)numColumns) + 1);
		}
		else {
			distrNumColumns.put((Integer)numColumns, 1);
		}

			
		//RIGHE
		numRows = table.getMappaColonne().get(0).size();		//conto il numero di righe di una tabella
		totRows = totRows + numRows;							//conto il numero di righe totali

		if(distrNumRows.containsKey((Integer)numRows)) {		//distribuzione del numero di righe 
			distrNumRows.put((Integer)numRows, distrNumRows.get((Integer)numRows) + 1);
		}
		else {
			distrNumRows.put((Integer)numRows, 1);
		}
		
			
		//VALORI NULLI E DISTINTI
		Set<String> distValues4TableSet = new HashSet<>();
		numNullValues = 0;
		for(int i : table.getMappaColonne().keySet()) {
			Set<String> distValues4ColumnSet = new HashSet<>(); 
			List<Cell> column = table.getMappaColonne().get(i);
			for(Cell c : column) {
				totCelle = totCelle + 1;
				if(c.getCleanedText().equals("")) {
					numNullValues = numNullValues + 1;		//numero di valori nulli in ogni tabella
				}
				else {										//valori distinti
					distValues4ColumnSet.add(c.getCleanedText());
					distValues4TableSet.add(c.getCleanedText());
				}
			}
			//System.out.println(distValues4Column);

			//distribuzione valori nulli per colonna
			if(distrNullValuesCol.containsKey((Integer)numNullValues)) {			//distribuzione del numero di valori distinti per colonna 
				distrNullValuesCol.put((Integer)numNullValues, distrNullValuesCol.get((Integer)numNullValues) + 1);
			}
			else {
				distrNullValuesCol.put((Integer)numNullValues, 1);
			} 
			
			int num = distValues4ColumnSet.size();  						//chiave di distrDistValues4Columns
			if(distrDistValues4Columns.containsKey((Integer)num)) {			//distribuzione del numero di valori distinti per colonna 
				distrDistValues4Columns.put((Integer)num, distrDistValues4Columns.get((Integer)num) + 1);
			}
			else {
				distrDistValues4Columns.put((Integer)num, 1);
			} 
		}

		totNullValues = totNullValues + numNullValues;		//numero di valori nulli in tutte le tabelle


		int num1 = distValues4TableSet.size();  						//chiave di distrDistValues4Tables
		System.out.println(distValues4TableSet);
		System.out.println(num1);
		if(distrDistValues4Tables.containsKey((Integer)num1)) {		//distribuzione del numero di valori distinti per colonna 
			distrDistValues4Tables.put((Integer)num1, distrDistValues4Tables.get((Integer)num1) + 1);
		}
		else {
			distrDistValues4Tables.put((Integer)num1, 1);
		} 
		
		
		//distribuzione in percentuale del numero di valori nulli per tabella
		perc = ((float)numNullValues/(float)totCelle) * 100;
		System.out.println();
		if(distrPercNullValues4Table.containsKey((int)perc)) {		
			distrPercNullValues4Table.put((int)perc, distrPercNullValues4Table.get((int)perc) + 1);
		}
		else {
			distrPercNullValues4Table.put((int)perc, 1);
		} 
		
		//medie
		avgColumns = totColumns/totTables;  		//numero medio di colonne
		avgRows = totRows/totTables;				//numero medio di righe
		avgNullValues = totNullValues/totTables;	//numero medio di valori nulli
	
		for (Integer i : distrDistValues4Columns.keySet()) {
			System.out.println(i + " -> " + distrDistValues4Columns.get(i));
		}
		System.out.println("size: " + distrDistValues4Columns.size() + "\n");
		
		
		for (Integer i : distrDistValues4Tables.keySet()) {
			System.out.println(i + " -> " + distrDistValues4Tables.get(i));
		}
		System.out.println("size: " + distrDistValues4Tables.size() + "\n");
		
		
		for (int i : distrPercNullValues4Table.keySet()) {
			System.out.println(i + " -> " + distrPercNullValues4Table.get(i));
		}
		System.out.println("size: " + distrPercNullValues4Table.size() + "\n");
		
		System.out.println("valori nulli per colonna");
		for (int i : distrNullValuesCol.keySet()) {
			System.out.println(i + " -> " + distrNullValuesCol.get(i));
		}
		System.out.println("size: " + distrNullValuesCol.size() + "\n");
	}
}
