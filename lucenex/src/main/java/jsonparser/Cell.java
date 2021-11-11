package jsonparser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"className", "Rows", "innerHTML", "type"})

public class Cell {

	@JsonProperty("isHeader")
	private boolean isHeader;
	
	@JsonProperty("Coordinates")
	private Coordinates coordinates;
	
	@JsonProperty("cleanedText")
	private String cleanedText;
	
	public Cell() {
	}
	
	public Cell(boolean isHeader, Coordinates coordinates, String text) {
		super();
		this.isHeader = isHeader;
		this.coordinates = coordinates;
		this.cleanedText = text;
	}
	
	
	/* ------- GETTER E SETTER ------- */

	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public String getCleanedText() {
		return cleanedText;
	}

	public void setCleanedText(String cleanedText) {
		this.cleanedText = cleanedText;
	}

	
	/* ------- TO STRING ------- */
	
	@Override
	public String toString() {
		return "Cell [isHeader=" + isHeader + ", coordinates=" + coordinates + ", cleanedText=" + cleanedText + "] " + "\n";
	}
	
	
}
