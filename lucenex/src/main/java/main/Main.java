package main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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
		
		//chiave: id documento (solo i documenti migliori del match), valore: id tabella	
		Map<Integer,String> bestDoc2table = new TreeMap<Integer,String>();

		Random rand = new Random();
		int upperBound = 550271;		//numero di tabelle del dataset
		int int_random = rand.nextInt(upperBound);
		
		query = parser.parserJsonQuery(int_random);
		
		bestDoc2table = mergeList.mergeList(query, directory);
		
		//rilettura delle tabelle di join e scrittura su file
		parser.parserJsonIdTable(bestDoc2table);
		
		System.out.println("Fine");		
	}	
}
