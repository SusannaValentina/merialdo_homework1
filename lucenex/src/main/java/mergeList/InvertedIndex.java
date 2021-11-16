package mergeList;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import jsonparser.Table;
import jsonparser.Cell;

public class InvertedIndex {

	private IndexWriter writer;
	private IndexWriterConfig config;

	public InvertedIndex(Directory directory) {

		try {
			this.config = new IndexWriterConfig(); 

			/*SimpleTextCodec codec = new SimpleTextCodec();
			config.setCodec(codec);
			if (codec != null) {
				config.setCodec(codec);
			}*/
			
			this.writer = new IndexWriter(directory, config);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.writer = null;
		}
	}


	public IndexWriter indexing(Table table) throws Exception{
		
		//long startIndexing = System.currentTimeMillis();

		for(List<Cell> column : table.getMappaColonne().values()) {
			Document doc = new Document();
			for(Cell c : column) {
				doc.add(new StringField("cella", c.getCleanedText().toLowerCase(), Field.Store.NO));
				doc.add(new StringField("idTable", c.getIdTable(), Field.Store.YES));
			}
			writer.addDocument(doc);  /* add Documents to be indexed */
		}
	
		return writer;
	}
	

	public IndexWriter getWriter() {
		return writer;
	}

	public void setWriter(IndexWriter writer) {
		this.writer = writer;
	}

	public IndexWriterConfig getConfig() {
		return config;
	}


	public void setConfig(IndexWriterConfig config) {
		this.config = config;
	}


}
