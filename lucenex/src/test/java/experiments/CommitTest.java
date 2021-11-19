package experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import invertedIndex.InvertedIndex;
import jsonparser.Table;

public class CommitTest {

	private final static int NUM_COMMIT = 550271;
	private IndexWriter writer;
	
	@Test
	/* ------- test: parsa tables.json e indicizza -> commit ogni NUM_COMMIT ------- */
	public void test() throws Exception {

		Path path = Paths.get("lucene-index");
		Directory directory = FSDirectory.open(path);

		InvertedIndex invertedIndex = new InvertedIndex(directory);
		invertedIndex.getWriter().deleteAll();

		ObjectMapper objectMapper = new ObjectMapper();
		
		BufferedReader br = new BufferedReader(new FileReader("tables.json"));

		String line = null;
		Table table = null;
		int count4commit = 0;

		long startIndexing = System.currentTimeMillis();
		
		//returns true if there is another line to read  
		while((line = br.readLine()) != null) { 

			count4commit = count4commit + 1;

			table = objectMapper.readValue(line, Table.class);
			table.createCells();
			writer = invertedIndex.indexing(table);

			if(count4commit == NUM_COMMIT) {
				writer.commit();
				System.out.println("count: " + count4commit);
				count4commit = 0;
			}	
		}  
		writer.commit();
		
		long timeIndexing = System.currentTimeMillis() - startIndexing;	
		
		writeOnFileTime(count4commit, timeIndexing);
		
		br.close(); 	
	}

	
	/* ------- writeOnFileTime: scrittura su file del tempo impiegato ------- */
	public void writeOnFileTime(int count4commit, long elapsedTime) {
		try {
			FileWriter myWriter = new FileWriter("EsperimentiTempiCommit.txt", true);
			myWriter.write("Tempo impiegato con commit ogni " + NUM_COMMIT + " tabelle: " + elapsedTime/(1000F) + " secondi\n\n");
			myWriter.close();
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
