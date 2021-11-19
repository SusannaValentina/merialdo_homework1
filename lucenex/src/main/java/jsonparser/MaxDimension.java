package jsonparser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaxDimension {

	@JsonProperty("row")
	private int row;
	
	@JsonProperty("column")
	private int column;

	public MaxDimension() {
		
	}

	
	/* ------- GETTER E SETTER ------- */
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	
	/* ------- TO STRING ------- */
	
	@Override
	public String toString() {
		return "MaxDimension [row=" + row + ", column=" + column + "]";
	}
}
