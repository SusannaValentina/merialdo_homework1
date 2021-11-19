package jsonparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//selezione dei campi della tabella da ignorare nel file json (la tabella rappresenta una riga del file json)
@JsonIgnoreProperties({"className", "id", "beginIndex", "endIndex", "referenceContext", "type", "classe", "headersCleaned", "keyColumn"})

public class Table {

	@JsonProperty("_id")
	private IdTable idTable;

	//collezione non ordinata delle celle della tabella
	@JsonProperty("cells")
	private Collection<Cell> collectionCells;

	//massima dimensione della tabella
	@JsonProperty("maxDimensions")
	private MaxDimension maxDimension;

	//mappa delle colonne della tabella -> chiave: numero della colonna, valore: lista delle celle della colonna
	private Map<Integer, List<Cell>> columnsMap;

	public Table() {
		this.columnsMap = new HashMap<>();
	}


	/* ------- CREATECELLS: CREA LA MAPPA DI COLONNE ------- */

	//input: collezione di celle
	//output: mappa di colonne<int, lista di celle che formano una colonna>
	public void createCells() {

		List<Cell> temp = null;
		for(Cell c : this.collectionCells) {
			if(!(c.isHeader())) {  
				temp = this.columnsMap.get(c.getCoordinates().getColumn());
				if(temp == null) 
					temp = new ArrayList<Cell>();
				temp.add(c);
				c.setIdTable(idTable.getId());
				this.columnsMap.put(c.getCoordinates().getColumn(), temp);
			}
		}
	}

	//input: collezione di celle
	//output: mappa di colonne<int, lista di celle che formano una colonna> considerando anche le celle header
	public void createCellsWithHeader() {

		List<Cell> temp = null;
		for(Cell c : this.collectionCells) {
			temp = this.columnsMap.get(c.getCoordinates().getColumn());
			if(temp == null) 
				temp = new ArrayList<Cell>();
			temp.add(c);
			c.setIdTable(idTable.getId());
			this.columnsMap.put(c.getCoordinates().getColumn(), temp);

		}
	}


	/* ------- GETTER E SETTER ------- */

	public IdTable getId() {
		return idTable;
	}

	public void setId(IdTable idTable) {
		this.idTable = idTable;
	}

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

	public Map<Integer, List<Cell>> getColumnsMap() {
		return columnsMap;
	}

	public void setColumnsMap(Map<Integer, List<Cell>> mappaColonne) {
		this.columnsMap = mappaColonne;
	}


	/* ------- TO STRING ------- */

	@Override
	public String toString() {
		return "Table [collectionCells=" + collectionCells + ", maxDimension=" + maxDimension + ", mappaColonne="
				+ columnsMap + "]";
	}

}

