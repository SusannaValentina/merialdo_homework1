package jsonparser;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TableTest {

	private Coordinates coord1 = new Coordinates(1,1);
	private Coordinates coord2 = new Coordinates(2,1);
	private Coordinates coord3 = new Coordinates(1,2);
	private Coordinates coord4 = new Coordinates(2,3);
	private Cell c1 = new Cell(false, coord1, "ciao1");
	private Cell c2 = new Cell(true, coord2, "ciao2");
	private Cell c3 = new Cell(false, coord3, "ciao3");
	private Cell c4 = new Cell(false, coord4, "ciao4");

	private Collection<Cell> collections = new ArrayList<Cell>();
	private Map<Integer, List<Cell>> mappaColonne = new HashMap<>();

	@Test
	public void test() throws Exception {

		collections.add(c3);
		collections.add(c2);
		collections.add(c4);
		collections.add(c1);

		Table table = new Table();
		table.setCollectionCells(collections);
		table.setMappaColonne(mappaColonne);
		table.createCells();
		System.out.println(collections);
		System.out.println(mappaColonne);
	}
}
