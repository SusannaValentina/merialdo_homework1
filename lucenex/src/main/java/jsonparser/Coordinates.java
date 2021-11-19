package jsonparser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinates {

	@JsonProperty("row")
	private int row;
	
	@JsonProperty("column")
	private int column;
	
	public Coordinates() {	
	}
	
	public Coordinates(int row, int column) {
		this.row = row;
		this.column = column;		
	}

	/* ------- GETTER E SETTER ------- */
	
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	
	/* ------- TO STRING ------- */
	
	@Override
	public String toString() {
		return "Coordinates [row=" + row + ", column=" + column + "]";
	}

}
