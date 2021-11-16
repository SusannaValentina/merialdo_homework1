package main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import jsonparser.Cell;
import jsonparser.Parser;
import mergeList.MergeList;

public class Main {


	public static void main(String args[]) throws Exception {

		//apertura path e directory
		Path path = Paths.get("lucene-index");
		Directory directory = FSDirectory.open(path);

		Parser parser = new Parser();
		Set<Cell> query = null;
		MergeList mergeList = new MergeList();

		int count = 0;
		int tot = 1;

		//tot esperimenti
		while(count < tot) {
			//query senza duplicati
			query = parser.parserJsonQuery(count);

			//mergeList sulla query scelta
			mergeList.mergeList(query , directory);
			count ++;
		}

		System.out.println("Fine");
	}	
}
