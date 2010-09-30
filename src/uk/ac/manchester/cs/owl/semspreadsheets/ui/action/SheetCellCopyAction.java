package uk.ac.manchester.cs.owl.semspreadsheets.ui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Collection;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;
import uk.ac.manchester.cs.owl.semspreadsheets.model.OntologyTermValidation;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Range;
import uk.ac.manchester.cs.owl.semspreadsheets.model.WorkbookManager;

@SuppressWarnings("serial")
public class SheetCellCopyAction extends SelectedCellsAction {

	private static Logger logger = Logger.getLogger(SheetCellCopyAction.class);
	private final Toolkit toolkit;

	public SheetCellCopyAction(WorkbookManager workbookManager, Toolkit toolkit) {
		this("Copy", workbookManager, toolkit);
	}

	protected SheetCellCopyAction(String name, WorkbookManager workbookManager,
			Toolkit toolkit) {
		super(name, workbookManager);
		this.toolkit = toolkit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.debug("Copy action invoked");
		Range selectedRange = getSelectedRange();
		Clipboard validationClipboard = new Clipboard("ontology_validations");
		if (selectedRange.isCellSelection()) {
			if (selectedRange.isSingleCellSelected()) {
				Collection<OntologyTermValidation> containingValidations = getWorkbookManager()
						.getOntologyTermValidationManager()
						.getContainingValidations(selectedRange);
				logger.debug("Selected validations = " + containingValidations);
				if (containingValidations.size() > 0) {
					validationClipboard.setContents(new StringSelection(
							"Some validations"), null);
				} else {
					validationClipboard.setContents(null, null);
				}

				int row = selectedRange.getFromRow();
				int col = selectedRange.getFromColumn();
				Cell cell = selectedRange.getSheet().getCellAt(col, row);
				String value = "";
				if (cell != null) {
					value = cell.getValue();
				} else {
					logger.debug("Selected cell is returned as NULL");
					// we assume that we are copying an empty value, rather than
					// leaving the clipboard intact
					value = "";
				}
				Clipboard clippy = toolkit.getSystemClipboard();
				clippy.setContents(new StringSelection(value), null);
			} else {
				logger.info("Copying a range of cells is not yet supported");
			}
		} else {
			logger.info("Nothing selected");
		}

	}

}