package experiments;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsonparser.Cell;
import jsonparser.Table;

public class Rows100Test {

	@Test
	/* ------- main: parsa tables.json e scrive su file solo le colonne di lunghezza 100 ------- */
	public void main() throws Exception {

		BufferedReader br = new BufferedReader(new FileReader("tables.json"));

		ObjectMapper objectMapper = new ObjectMapper();
		String line = null;
		Table table = null;

		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 

			table = objectMapper.readValue(line, Table.class);
			table.createCells();

			if(table.getColumnsMap().get(0).size() == 100) {
				writeHeaderTable(table.getColumnsMap().get(0));
			}
			else {
				FileWriter myWriter = new FileWriter("VerificaPicco.txt", true);
				myWriter.write("\n\n");
				myWriter.close();
			}

		}
		br.close();
	}  


	
	/* ------- writeHeaderTable: scrittura su file ------- */
	public void writeHeaderTable(List<Cell> lista) {
		try {
			FileWriter myWriter = new FileWriter("VerificaPicco.txt", true);
			for (Cell c : lista) {
				myWriter.write(c.getCleanedText() + ";");
			}
			myWriter.write("\n");
			myWriter.close();
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}
}