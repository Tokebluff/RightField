/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.DocumentsCatalogue;
import uk.ac.manchester.cs.owl.semspreadsheets.change.WorkbookChangeListener;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OWLPropertyType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidationDescriptor;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Sheet;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Workbook;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class WorkbookManagerTest {
	
	WorkbookManager manager;
	DummyWorkbookManagerListener testListener;
	DummyWorkbookChangeListener testChangeListener;
	
	
	@Before
	public void setUp() {
		manager = new WorkbookManager();		
		testListener=new DummyWorkbookManagerListener();
		testChangeListener=new DummyWorkbookChangeListener();
		manager.addListener(testListener);
		manager.getWorkbook().addChangeListener(testChangeListener);		
	}
	
	@Test
	public void testCreateNewWorkbook() throws Exception {		
		Workbook book=manager.getWorkbook();
		assertNull(manager.getWorkbookURI());
		assertNotNull(book);
		manager.getWorkbookState().changesUnsaved();
		Workbook book2=manager.createNewWorkbook();
		assertTrue(manager.getWorkbookState().isChangesSaved());
		assertTrue(testListener.isWorkbookChangedFired());
		assertNotSame(book2, book);
		assertSame(book2, manager.getWorkbook());
		assertNull(manager.getWorkbookURI());
		
		URI uri = DocumentsCatalogue.simpleAnnotatedworkbookURI();
		manager.loadWorkbook(uri);
		manager.createNewWorkbook();
		assertNull(manager.getWorkbookURI());
	}
	
	@Test
	public void testLoadWorkbook() throws Exception {
		URI uri = DocumentsCatalogue.simpleAnnotatedworkbookURI();
		manager.getWorkbookState().changesUnsaved();
		Workbook book = manager.loadWorkbook(uri);
		assertNotNull(book);
		assertTrue(manager.getWorkbookState().isChangesSaved());
		assertSame(book, manager.getWorkbook());
		assertNotNull(manager.getWorkbookURI());
		assertEquals(uri,manager.getWorkbookURI());
		assertTrue(testListener.isWorkbookLoadedFired());
	}	
	
	@Test
	public void testInsertSheet() throws Exception {
		Sheet sheet = manager.addSheet();
		assertNotNull(sheet);
		assertEquals("Sheet1",sheet.getName());
		assertTrue(testChangeListener.isSheetAddedFired());
		//FIXME: workbookChanged should also probably be fired		
		assertFalse(testChangeListener.isWorkbookChangedFired());
		assertFalse(testChangeListener.isSheetRemovedFired());
		assertFalse(testChangeListener.isSheetRenamedFired());
		assertEquals(2,manager.getWorkbook().getSheets().size());
	}
	
	@Test
	public void testRemoveSheet() throws Exception {
		manager.deleteSheet("Sheet0");		
		assertTrue(testChangeListener.isSheetRemovedFired());
		//FIXME: workbookChanged should also probably be fired
		assertFalse(testChangeListener.isWorkbookChangedFired());		
		assertFalse(testChangeListener.isSheetAddedFired());
		assertFalse(testChangeListener.isSheetRenamedFired());
		assertEquals(0,manager.getWorkbook().getSheets().size());
	}
	
	@Test
	public void testRenameSheet() throws Exception {
		manager.renameSheet("Sheet0", "fred");
		assertEquals(1,manager.getWorkbook().getSheets().size());
		assertEquals("fred",manager.getWorkbook().getSheet(0).getName());
		assertTrue(testChangeListener.isSheetRenamedFired());
		assertFalse(testChangeListener.isSheetRemovedFired());
		//FIXME: workbookChanged should also probably be fired
		assertFalse(testChangeListener.isWorkbookChangedFired());		
		assertFalse(testChangeListener.isSheetAddedFired());
	}
	
	@Test
	public void testGetOntologyTermValidations() throws Exception {
		WorkbookManager manager=new WorkbookManager();
		manager.loadWorkbook(DocumentsCatalogue.bookWithPropertiesURI());
		Collection<OntologyTermValidation> ontologyTermValidations = manager.getOntologyManager().getOntologyTermValidations();
		assertEquals(2,ontologyTermValidations.size());
		OntologyTermValidation selectedValidation=null;		
		for (OntologyTermValidation validation : ontologyTermValidations) {
			assertTrue(validation.getRange().toFixedAddress().equals("Sheet0!$E$8:$E$8") || validation.getRange().toFixedAddress().equals("Sheet0!$E$10:$E$10"));
			if (validation.getRange().toFixedAddress().equals("Sheet0!$E$8:$E$8")) {
				selectedValidation = validation;
			}
		}
		OntologyTermValidationDescriptor descriptor = selectedValidation.getValidationDescriptor();
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",descriptor.getOntologyIRIs().iterator().next().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#isAssociatedWith",descriptor.getOWLPropertyItem().getIRI().toString());
		assertEquals(OWLPropertyType.OBJECT_PROPERTY,descriptor.getOWLPropertyItem().getPropertyType());
	}
	
	@Test
	public void testWorkbookChangeListenersRetainedAferNewOrLoad() throws Exception {
		Workbook book = manager.getWorkbook();
		WorkbookChangeListener l1=new DummyWorkbookChangeListener();
		WorkbookChangeListener l2=new DummyWorkbookChangeListener();
		book.addChangeListener(l1);
		book.addChangeListener(l2);
		manager.createNewWorkbook();
		Workbook newBook = manager.getWorkbook();
		assertNotSame(book, newBook);
		assertTrue(newBook.getAllChangeListeners().contains(l1));
		assertTrue(newBook.getAllChangeListeners().contains(l2));
		URI workbookURI = DocumentsCatalogue.simpleAnnotatedworkbookURI();
		manager.loadWorkbook(workbookURI);
		Workbook newBook2 = manager.getWorkbook();
		assertNotSame(newBook, newBook2);
		assertTrue(newBook2.getAllChangeListeners().contains(l1));
		assertTrue(newBook2.getAllChangeListeners().contains(l2));
	}
	
	
	
	
}
