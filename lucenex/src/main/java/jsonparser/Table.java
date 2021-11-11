package jsonparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//selezione dei campi della tabella da ignorare nel file json (la tabella rappresenta una riga del file json)
@JsonIgnoreProperties({"_id", "className", "id", "beginIndex", "endIndex", "referenceContext", "type", "classe", "headersCleaned", "keyColumn"})

public class Table {

	//collezione non ordinata delle celle della tabella
	@JsonProperty("cells")
	private Collection<Cell> collectionCells;

	//massima dimensione della tabella
	@JsonProperty("maxDimensions")
	private MaxDimension maxDimension;

	//mappa delle colonne della tabella -> chiave: numero della colonna, valore: lista delle celle della colonna
	private Map<Integer, List<Cell>> mappaColonne;

	public Table() {
		this.mappaColonne = new HashMap<>();
	}
	

	/* ------- CREATECELLS: CREA LA MAPPA DI COLONNE ------- */
	
	//input: collezione di celle
	//output: mappa di colonne<int, lista di celle che formano una colonna>
	public void createCells() {
		//long startCreateCells = System.currentTimeMillis();
		
		List<Cell> temp = null;
		for(Cell c : this.collectionCells) {
			if(!(c.isHeader())) {  
				temp = this.mappaColonne.get(c.getCoordinates().getColumn());
				if(temp == null) 
					temp = new ArrayList<Cell>();
				temp.add(c);
				this.mappaColonne.put(c.getCoordinates().getColumn(), temp);
			}
		}
		
		//long elapsedTimeCreateCells = System.currentTimeMillis() - startCreateCells; //nanoTime() - start;
		//System.out.println("Tempo impiegato per il createCells: " + elapsedTimeCreateCells/(1000F) + " secondi\n");
	}
	
	
	/* ------- GETTER E SETTER ------- */

	public Collection<Cell> getCollectionCells() {
		return collectionCells;
	}

	public void setCollectionCells(Collection<Cell> collectionCells) {
		this.collectionCells = collectionCells;
	}

	public MaxDimension getMaxDimension() {
		return maxDimension;
	}

	public void setMaxDimension(MaxDimension maxDimension) {
		this.maxDimension = maxDimension;
	}

	public Map<Integer, List<Cell>> getMappaColonne() {
		return mappaColonne;
	}

	public void setMappaColonne(Map<Integer, List<Cell>> mappaColonne) {
		this.mappaColonne = mappaColonne;
	}
	
	
	/* ------- TO STRING ------- */

	@Override
	public String toString() {
		return "Table [collectionCells=" + collectionCells + ", maxDimension=" + maxDimension + ", mappaColonne="
				+ mappaColonne + "]";
	}

}
