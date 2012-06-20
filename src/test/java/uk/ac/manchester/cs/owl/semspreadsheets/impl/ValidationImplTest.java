package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class ValidationImplTest {

	private WorkbookManager manager;
	private Sheet sheet;
	
	@Before
	public void setup() {
		manager = new WorkbookManager();
		sheet = manager.getWorkbook().getSheet(0);
	}
	
	@Test
	public void testIsDataValidation() {
		Validation v = new ValidationImpl("wksowlv0",sheet,1,1,1,1);
		assertTrue(v.isDataValidation());
		
		
		v = new ValidationImpl("property:wksowlv0:<http://www.mygrid.org.uk/ontology/JERMOntology#hasCharacteristics>:DATA_OBJECT",sheet,1,1,1,1);
		assertFalse(v.isDataValidation());
	}

}