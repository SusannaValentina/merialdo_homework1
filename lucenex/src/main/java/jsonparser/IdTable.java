package jsonparser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdTable {

	@JsonProperty("$oid")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
