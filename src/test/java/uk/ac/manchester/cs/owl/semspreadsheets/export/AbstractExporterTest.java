package uk.ac.manchester.cs.owl.semspreadsheets.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import org.junit.Test;

import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.ValidationType;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

public class AbstractExporterTest {		
	
	@Test
	public void testInitWithManager() throws Exception {
		WorkbookManager manager = new WorkbookManager();
		manager.loadWorkbook(jermWorkbookURI());
		AbstractExporter exporter = new AbstractExporterTestImpl(manager);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}

	@Test
	public void testInitiWithURI() throws Exception {
		URI uri = jermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void testInitiWithFile() throws Exception {
		File file = jermWorkbookFile();
		AbstractExporter exporter = new AbstractExporterTestImpl(file);
		assertNotNull(exporter.getWorkbook());
		assertEquals("Metadata Template",exporter.getWorkbook().getSheet(0).getName());
		assertNotNull(exporter.getWorkbookManager());	
		assertEquals(9,exporter.getValidations().size());
	}
	
	@Test
	public void getPopulatedValidatedCellDetails() throws Exception {
		URI uri = partiallyPopulatedWorkbookURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertEquals(4,exporter.getPopulatedValidatedCellDetails().size());
		List<PopulatedValidatedCellDetails> list = new ArrayList<PopulatedValidatedCellDetails>(exporter.getPopulatedValidatedCellDetails());
		Collections.sort(list,new Comparator<PopulatedValidatedCellDetails>() {
			@Override
			public int compare(PopulatedValidatedCellDetails o1,
					PopulatedValidatedCellDetails o2) {
				return o1.getTextValue().compareTo(o2.getTextValue());
			}
		});
		String [] textValues = new String [] {"Bacillus_subtilis","cell_size","concentration","dilution_rate"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : list) {
			assertEquals(textValues[x],pop.getTextValue());
			x++;
		}
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#Bacillus_subtilis",list.get(0).getTerm().getIRI().toString());
		assertEquals("cell_size",list.get(1).getTerm().getName());		
		assertEquals(1,list.get(1).getCell().getColumn());
		assertEquals(10,list.get(1).getCell().getRow());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#concentration",list.get(2).getTerm().getIRI().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#FactorsStudied",list.get(3).getEntityIRI().toString());
		assertEquals(1,list.get(2).getValidation().getValidationDescriptor().getOntologyIRIs().size());
	}	
	
	/**
	 * Uses a spreadsheet created with the latest RightField that does some formatting of terms (i.e. removing underscores and switching with spaces)
	 * and also contains a cell that contains the same text as an annotated cell, but isn't marked up with RightField (so that cell should not be returend).
	 * @throws Exception
	 */
	@Test
	public void getPopulatedValidatedCellDetails2() throws Exception {
		URI uri = AbstractExporterTest.class.getResource("/partially_populated_mged_book.xls").toURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertEquals(3,exporter.getPopulatedValidatedCellDetails().size());
		List<PopulatedValidatedCellDetails> list = new ArrayList<PopulatedValidatedCellDetails>(exporter.getPopulatedValidatedCellDetails());
		Collections.sort(list,new Comparator<PopulatedValidatedCellDetails>() {
			@Override
			public int compare(PopulatedValidatedCellDetails o1,
					PopulatedValidatedCellDetails o2) {
				return o1.getTextValue().compareTo(o2.getTextValue());
			}
		});
		String [] textValues = new String [] {"BioAssayData","list of booleans","primary site"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : list) {
			assertEquals(textValues[x],pop.getTextValue());
			x++;
		}
		assertEquals(ValidationType.SUBCLASSES,list.get(0).getValidation().getValidationDescriptor().getType());
		assertEquals(ValidationType.INDIVIDUALS,list.get(1).getValidation().getValidationDescriptor().getType());
		
		assertEquals("primary site",list.get(2).getTerm().getName());
		assertEquals("primary site",list.get(2).getTerm().getFormattedName());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl#primary_site",list.get(2).getTerm().getIRI().toString());		
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl#CancerSite",list.get(2).getEntityIRI().toString());
		assertEquals(1,list.get(2).getOntologyIRIs().size());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl",list.get(2).getOntologyIRIs().iterator().next().toString());
	}
	
	/**
	 * Tests where 2 ontologies are used, and whether the terms correctly link to the correct ontology
	 * @throws Exception
	 */
	@Test
	public void getPopulatedValidatedCellDetails3() throws Exception {
		URI uri = AbstractExporterTest.class.getResource("/two_ontologies.xls").toURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		assertEquals(2,exporter.getPopulatedValidatedCellDetails().size());
		List<PopulatedValidatedCellDetails> list = new ArrayList<PopulatedValidatedCellDetails>(exporter.getPopulatedValidatedCellDetails());
		Collections.sort(list,new Comparator<PopulatedValidatedCellDetails>() {
			@Override
			public int compare(PopulatedValidatedCellDetails o1,
					PopulatedValidatedCellDetails o2) {
				return o1.getTextValue().compareTo(o2.getTextValue());
			}
		});
		String [] textValues = new String [] {"COSMIC","mean and p values"};
		int x=0;
		for (PopulatedValidatedCellDetails pop : list) {
			assertEquals(textValues[x],pop.getTextValue());
			x++;
		}
		assertEquals(ValidationType.DIRECTINDIVIDUALS,list.get(0).getValidation().getValidationDescriptor().getType());
		assertEquals(ValidationType.INDIVIDUALS,list.get(1).getValidation().getValidationDescriptor().getType());
		assertEquals(1,list.get(0).getOntologyIRIs().size());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology",list.get(0).getOntologyIRIs().iterator().next().toString());
		assertEquals(1,list.get(1).getOntologyIRIs().size());
		assertEquals("http://mged.sourceforge.net/ontologies/MGEDOntology.owl",list.get(1).getOntologyIRIs().iterator().next().toString());
	}

	@Test
	public void testGetValidations() throws Exception {
		URI uri = jermWorkbookURI();
		AbstractExporter exporter = new AbstractExporterTestImpl(uri);
		Collection<OntologyTermValidation> vals = exporter.getValidations();
		assertEquals(9,vals.size());
		List<OntologyTermValidation> list = new ArrayList<OntologyTermValidation>(vals);
		Collections.sort(list,new Comparator<OntologyTermValidation>() {
			@Override
			public int compare(OntologyTermValidation o1,
					OntologyTermValidation o2) {
				return o1.getRange().compareTo(o2.getRange());
			}
		});
		OntologyTermValidation val = list.get(0);
		assertEquals("Metadata Template!B17:B17",val.getRange().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#Project",val.getValidationDescriptor().getEntityIRI().toString());
		assertEquals("BaCell",val.getValidationDescriptor().getTerms().iterator().next().getFormattedName());
		
		val = list.get(8);
		assertEquals("Metadata Template!P28:P37",val.getRange().toString());
		assertEquals("http://www.mygrid.org.uk/ontology/JERMOntology#organism",val.getValidationDescriptor().getEntityIRI().toString());
		assertEquals("Bacillus subtilis",val.getValidationDescriptor().getTerms().iterator().next().getFormattedName());
		
	}
	
	//test helper methods
	
	private URI partiallyPopulatedWorkbookURI() throws Exception {
		return AbstractExporterTest.class.getResource("/partially_populated_JERM_template.xls").toURI();		
	}
	
	private File jermWorkbookFile() throws Exception {
		String filename = AbstractExporterTest.class.getResource("/populated_JERM_template.xls").getFile();
		return new File(filename);
	}
	
	private URI jermWorkbookURI() throws Exception {
		return AbstractExporterTest.class.getResource("/populated_JERM_template.xls").toURI();
	}	

	/**
	 * A concrete implementation of {@link AbstractExporter} for testing purposes only.
	 */
	private class AbstractExporterTestImpl extends AbstractExporter {
		public AbstractExporterTestImpl(WorkbookManager manager) {
			super(manager);			
		}

		public AbstractExporterTestImpl(File workbookFile) throws IOException {
			super(workbookFile);			
		}
		
		public AbstractExporterTestImpl(URI uri) throws IOException {
			super(uri);			
		}				
	}
}
