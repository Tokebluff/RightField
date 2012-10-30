package uk.ac.manchester.cs.owl.semspreadsheets.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.PropertyValidationForumlaDefinition;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Validation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;

public abstract class GeneralSheetTests {
	
	@Test
	public void testGetTestSheet() throws Exception {
		//check that fetching of the sheet used in these tests is working correctly
		Sheet sheet = getTestSheet();
		assertNotNull(sheet);
		assertEquals("Sheet0",sheet.getName());
	}
	
	@Test
	public void testMaxColumns() throws Exception {
		Sheet sheet = getBlankSheet();
		assertEquals(256,sheet.getMaxColumns());
	}
	
	@Test
	public void testClearDataValidationsOnBlankSheet() throws Exception {
		Sheet sheet = getBlankSheet();
		assertEquals(0,sheet.getValidations().size());
		sheet.clearValidationData();
		assertEquals(0,sheet.getValidations().size());
		sheet.clearValidationData();
	}
	
	@Test
	public void testClearDataValidations() throws Exception {
		Sheet sheet = getTestSheet();				
		assertEquals(1,sheet.getValidations().size());
		sheet.clearValidationData();		
		assertEquals(0,sheet.getValidations().size());	
		sheet.clearValidationData();
		assertEquals(0,sheet.getValidations().size());
	}
	
	@Test
	public void testClearDataValidationsInSheetWithProperties() throws Exception {
		Sheet sheet = getTestSheetWithProperties();
		assertEquals(2,sheet.getValidations().size());
		sheet.clearValidationData();
		assertEquals(0,sheet.getValidations().size());
		sheet.clearValidationData();
		assertEquals(0,sheet.getValidations().size());
	}
	
	@Test
	public void testIndex() throws Exception {
		Workbook wb = getBlankWorkbook();
		Sheet sheet = wb.getSheet(0); 
		assertEquals(0,sheet.getIndex());
		sheet = wb.addSheet();
		assertEquals(1,sheet.getIndex());
		
		wb = getTestWorkbook();
		sheet = wb.getSheet(2);
		assertEquals(2,sheet.getIndex());
		sheet = wb.addSheet();
		assertEquals(3,sheet.getIndex());
	}
	
	@Test
	public void testAddPropertyValidation() throws Exception {		
		Sheet sheet = getTestWorkbook().addSheet();
		assertEquals(0,sheet.getValidations().size());
		sheet.addLiteralValidation("wksowlv0", 2, 3,3, 4);
		assertEquals(1,sheet.getValidations().size());
		Validation validation = sheet.getValidations().iterator().next();
		assertTrue(validation.isLiteralValidation());
		assertFalse(validation.isDataValidation());
		assertTrue("propliteral^wksowlv0",validation.getFormula().contains("propliteral^wksowlv0"));
		assertEquals("wksowlv0",PropertyValidationForumlaDefinition.decode(validation.getFormula()));
		assertEquals(new Range(sheet,2,3,3,4),validation.getRange());
	}
	
	@Test
	public void testGetColumnWidth() throws Exception {
		Sheet sheet = getBlankSheet();
		assertEquals(48,sheet.getColumnWidth(0));
		assertEquals(48,sheet.getColumnWidth(1));
		assertEquals(48,sheet.getColumnWidth(255));
	}

	@Test
	public void testGetValidations() throws Exception {
		Sheet sheet = getTestSheet();
		Collection<Validation> validations = sheet.getValidations();
		List<Validation> list = new ArrayList<Validation>(validations);
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals(4,val.getFirstColumn());
		assertEquals(4,val.getLastColumn());
		assertEquals(11,val.getFirstRow());
		assertEquals(11,val.getLastRow());
		assertEquals(sheet,val.getSheet());
		assertEquals("Sheet0!E12:E12",val.getRange().toString());
	}
	
	
	
	@Test
	public void testGetContainingValidations() throws Exception {
		Sheet sheet = getTestSheet();
		
		Range r = new Range(sheet,4,11,4,11);
		Collection<Validation> validations = sheet.getContainingValidations(r);		
		List<Validation> list = new ArrayList<Validation>(validations);
		
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals(sheet,val.getSheet());
		assertEquals("Sheet0!E12:E12",val.getRange().toString());		
		
		r = new Range(sheet,3,3,3,3);
		validations = sheet.getContainingValidations(r);		
		list = new ArrayList<Validation>(validations);
		
		assertEquals(0,list.size());		
	}
	
	@Test
	public void testGetIntersectingValidations() throws Exception {
		Sheet sheet = getTestSheet();
		
		Range r = new Range(sheet,3,10,5,12);
		Collection<Validation> validations = sheet.getIntersectingValidations(r);
		List<Validation> list = new ArrayList<Validation>(validations);
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals(sheet,val.getSheet());
		assertEquals("Sheet0!E12:E12",val.getRange().toString());
		
		r = new Range(sheet,5,12,7,15);
		validations = sheet.getContainingValidations(r);		
		list = new ArrayList<Validation>(validations);
		
		assertEquals(0,list.size());	
	}
	
	@Test
	public void testAddValidation() throws Exception {
		Sheet sheet = getTestWorkbook().addSheet();
		
		Collection<Validation> validations = sheet.getValidations();
		List<Validation> list = new ArrayList<Validation>(validations);
		assertEquals(0,list.size());
		sheet.addValidation("wksowlv0", 1, 1, 2, 2);
		validations = sheet.getValidations();	
		list = new ArrayList<Validation>(validations);		
		assertEquals(1,list.size());
		Validation val = list.get(0);
		assertEquals("wksowlv0",val.getFormula());
		assertEquals("Sheet1!B2:C3",val.getRange().toString());
	}
	
	@Test
	public void testHiddenSheets() throws Exception {
		Workbook book = getTestWorkbook();
		assertEquals(3,book.getSheets().size());
		Sheet sheet = book.getSheet(1);
		assertEquals("HiddenSheet",sheet.getName());
		assertTrue(sheet.isHidden());
		assertFalse(sheet.isVeryHidden());
	}
	
	@Test
	public void testSetHiddenSheet() throws Exception {
		Sheet sheet = getTestSheet();
		assertFalse(sheet.isHidden());
		sheet.setHidden(true);
		assertTrue(sheet.isHidden());
	}
	
	@Test
	public void testSetVisible() throws Exception {
		Sheet sheet = getTestWorkbook().getSheet(1);
		assertTrue(sheet.isHidden());
		sheet.setHidden(false);
		assertFalse(sheet.isHidden());
	}
	
	@Test
	public void testSetVeryHiddden() throws Exception {
		Sheet sheet = getTestSheet();
		assertFalse(sheet.isVeryHidden());
		assertFalse(sheet.isHidden());
		sheet.setVeryHidden(true);
		assertTrue(sheet.isVeryHidden());
		assertFalse(sheet.isHidden());
	}
	
	@Test
	public void testVeryHiddenSheets() throws Exception {
		Workbook book = getTestWorkbook();
		Sheet sheet = book.getSheet(2);
		assertEquals("wksowlv0",sheet.getName());
		assertFalse(sheet.isHidden());
		assertTrue(sheet.isVeryHidden());		
	}
	
	@Test
	public void testVisibleSheets() throws Exception {
		Workbook book = getTestWorkbook();
		Sheet sheet = book.getSheet(0);
		assertEquals("Sheet0",sheet.getName());
		assertFalse(sheet.isHidden());
		assertFalse(sheet.isVeryHidden());		
	}	
	
	protected abstract Workbook getTestWorkbook() throws Exception;
	protected abstract Sheet getTestSheet() throws Exception;
	protected abstract Sheet getBlankSheet() throws Exception;
	protected abstract Workbook getBlankWorkbook() throws Exception;
	protected abstract Sheet getTestSheetWithProperties() throws Exception;
	

}
