/*******************************************************************************
 * Copyright (c) 2009-2012, University of Manchester
 * 
 * Licensed under the New BSD License. 
 * Please see LICENSE file that is distributed with the source code
 ******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.model.hssf.impl;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import uk.ac.manchester.cs.owl.semspreadsheets.model.Cell;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Stuart Owen
 * @author Matthew Horridge
 */
public class CellHSSFImpl implements Cell {
	
	private static Logger logger = Logger.getLogger(CellHSSFImpl.class);

    public static final Font DEFAULT_FONT = new Font("verdana", Font.PLAIN, 10);

    private static Map<HSSFFont, Font> fontCache = new HashMap<HSSFFont, Font>();
    
    private static Map<HSSFWorkbook,Map<Color,HSSFCellStyle>> colourStylesForWorkbook = new HashMap<HSSFWorkbook, Map<Color,HSSFCellStyle>>();

    private HSSFCell theCell;

    private HSSFWorkbook workbook;

    private Color foreground;

    public CellHSSFImpl(HSSFWorkbook workbook, HSSFCell theCell) {
        this.workbook = workbook;        
        this.theCell = theCell;        
    }

    public Font getDefaultFont() {
        HSSFFont font = getWorkbook().getFontAt((short) 0);
        if (font == null) {
            return DEFAULT_FONT;
        }
        return getFont(font);
    }    

    public int getRow() {
        return theCell.getRowIndex();
    }

    public int getColumn() {
        return theCell.getColumnIndex();
    }

    public String getComment() {
        HSSFComment hssfComment = theCell.getCellComment();
        if (hssfComment == null) {
            return null;
        }
        else {
            return hssfComment.toString();
        }

    }

    public boolean isStrikeThrough() {
        HSSFFont hssfFont = theCell.getCellStyle().getFont(getWorkbook());
        return hssfFont.getStrikeout();
    }
    public void setCellStyleFormula()
    {
        theCell.setCellType(CellType.FORMULA);
    }
    public boolean isUnderline() {
        HSSFFont hssfFont = theCell.getCellStyle().getFont(getWorkbook());
        return hssfFont.getUnderline() != 0;
    }

    public boolean isItalic() {
        HSSFFont hssfFont = theCell.getCellStyle().getFont(getWorkbook());
        return hssfFont.getItalic();
    }

    public String getValue() {
        if (theCell.getCellTypeEnum() == CellType.BLANK) {
            return "";
        }
        else if (theCell.getCellTypeEnum() == CellType.BOOLEAN) {
            return Boolean.toString(theCell.getBooleanCellValue());
        }
        else if (theCell.getCellTypeEnum() == CellType.ERROR) {
            return "<ERROR?>";
        }
        else if (theCell.getCellTypeEnum() == CellType.FORMULA) {
            return theCell.getCellFormula();
        }
        else if (theCell.getCellTypeEnum() == CellType.NUMERIC) {
            return Double.toString(theCell.getNumericCellValue());
        }
        else if (theCell.getCellTypeEnum() == CellType.STRING) {
            return theCell.getRichStringCellValue().getString();
        }
        return "";
    }
    public void setCellFormula(String formula)
    {
        theCell.setCellFormula(formula);
    }
    public void setValue(String value) {
        if (theCell.getCellTypeEnum() == CellType.BLANK) {
            theCell.setCellValue(new HSSFRichTextString(value));
        }
        else if (theCell.getCellTypeEnum() == CellType.BOOLEAN) {
            theCell.setCellValue(Boolean.parseBoolean(value));
        }
        else if (theCell.getCellTypeEnum() == CellType.ERROR) {
        }
        else if (theCell.getCellTypeEnum() == CellType.FORMULA) {
            theCell.setCellFormula(value);
        }
        else if (theCell.getCellTypeEnum() == CellType.NUMERIC) {
            theCell.setCellValue(Double.parseDouble(value));
        }
        else if (theCell.getCellTypeEnum() == CellType.STRING) {
            theCell.setCellValue(new HSSFRichTextString(value));
        }
    }

    public boolean isBold() {
        return getFont().isBold();
    }

    public void setBold(boolean b) {
        HSSFCellStyle cellStyle = theCell.getCellStyle();        
        if (cellStyle == null) {
            cellStyle = getWorkbook().createCellStyle();
            theCell.setCellStyle(cellStyle);
        }
        HSSFFont font = cellStyle.getFont(getWorkbook());
        if (font == null) {
            font = getWorkbook().createFont();
            cellStyle.setFont(font);
        }
        font.setBold(b);
        fontCache.clear();
    }

    public Font getFont() {
        HSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            return getDefaultFont();
        }
        HSSFFont hssfFont = cellStyle.getFont(getWorkbook());
        return getFont(hssfFont);
    }

    private Font getFont(HSSFFont hssfFont) {
        Font font = fontCache.get(hssfFont);
        if (font == null) {
            String name = hssfFont.getFontName();
            int size = hssfFont.getFontHeightInPoints();
            int style = Font.PLAIN;
            if (hssfFont.getBold()) {
                style = Font.BOLD;
                if (hssfFont.getItalic()) {
                    style = style | Font.ITALIC;
                }
            }
            else if (hssfFont.getItalic()) {
                style = Font.ITALIC;
            }
            font = new Font(name, style, size);
            fontCache.put(hssfFont, font);
        }
        return font;

    }
    
    @Override
	public Color getBackgroundFill() {		
    	HSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
        	logger.debug("Cell style not found, so using background colour of WHITE");
            return Color.WHITE;
        }
		short colorIndex=cellStyle.getFillForegroundColor();
		logger.debug("Background fill colour index found as "+colorIndex);
		return translateColour(colorIndex);
	}

    @Override
	public void setBackgroundFill(Color colour) {
		HSSFColor col = translateColour(colour);
		if (col==null) {
			logger.warn("Unable to find similar colour in palette for "+colour.toString());
		}
		else {						
			theCell.setCellStyle(getFillStyleForColour(colour));
			if (logger.isDebugEnabled()) {
				logger.debug("Cell colour changed to "+col.getHexString()+"with index: "+col.getIndex());
			}
		}
	}
	public void setBorders(Color colour) {
        HSSFColor col = translateColour(colour);

        if (col==null) {
            logger.warn("Unable to find similar colour in palette for "+colour.toString());
        }
        else {
            theCell.setCellStyle(getBorderStyleForColour(colour));
            if (logger.isDebugEnabled()) {
                logger.debug("Cell colour changed to "+col.getHexString()+"with index: "+col.getIndex());
            }
        }

    }

    private HSSFCellStyle getBorderStyleForColour(Color colour) {
        Map<Color,HSSFCellStyle> styles = colourStylesForWorkbook.get(getWorkbook());
        if (styles == null) {
            styles = new HashMap<Color,HSSFCellStyle>();
            colourStylesForWorkbook.put(getWorkbook(), styles);
        }
        HSSFCellStyle style = styles.get(colour);
        if (style == null) {
            HSSFColor col = translateColour(colour);
            style = getWorkbook().createCellStyle();

            style.setBorderTop(BorderStyle.THICK);
            style.setBorderBottom(BorderStyle.THICK);
            style.setBorderLeft(BorderStyle.THICK);
            style.setBorderRight(BorderStyle.THICK);

            style.setBottomBorderColor(col.getIndex());
            style.setTopBorderColor(col.getIndex());
            style.setLeftBorderColor(col.getIndex());
            style.setRightBorderColor(col.getIndex());

            styles.put(colour, style);
        }
        return style;
    }
    
    private HSSFCellStyle getFillStyleForColour(Color colour) {
    	Map<Color,HSSFCellStyle> styles = colourStylesForWorkbook.get(getWorkbook());
    	if (styles == null) {
    		styles = new HashMap<Color,HSSFCellStyle>();
    		colourStylesForWorkbook.put(getWorkbook(), styles);
    	}
    	HSSFCellStyle style = styles.get(colour);    	    	
    	if (style == null) {
    		HSSFColor col = translateColour(colour);
    		style = getWorkbook().createCellStyle();
    		style.setFillPattern(FillPatternType.SOLID_FOREGROUND );
    		style.setFillForegroundColor(col.getIndex());
    		styles.put(colour, style);
    	}
    	return style;
    }
 
    public Color getForeground() {
        if (foreground == null) {
            HSSFCellStyle cellStyle = theCell.getCellStyle();
            if (cellStyle == null) {
                return Color.BLACK;
            }
            HSSFFont hssfFont = cellStyle.getFont(getWorkbook());
            short colorIndex = hssfFont.getColor();
            Color theColor = translateColour(colorIndex);
            foreground = theColor;
        }
        return foreground;
    }

    /**
     * Translates a java Color to the colour index in the workbook palette
     * @param colour
     * @return
     */
    private HSSFColor translateColour(Color colour) {
    	HSSFPalette palette = getWorkbook().getCustomPalette();
    	
    	HSSFColor col = palette.findSimilarColor((byte)colour.getRed(), (byte)colour.getGreen(), (byte)colour.getBlue());
    	return col;
    }
    /**
     * Translates the colorIndex from the workbook palette to a <br> 
     * java Color.
     * @param colorIndex
     * @return java Color
     */
	private Color translateColour(short colorIndex) {
		HSSFPalette palette = getWorkbook().getCustomPalette();
		HSSFColor color = palette.getColor(colorIndex);
		Color theColor = Color.BLACK;
		if (color != null) {
		    short[] triplet = color.getTriplet();
		    theColor = new Color(triplet[0], triplet[1], triplet[2]);
		}
		return theColor;
	}

    public int getAlignment() {
        HSSFCellStyle cellStyle = theCell.getCellStyle();
        if (cellStyle == null) {
            return SwingConstants.LEFT;
        }
        HorizontalAlignment hssfAlignment = cellStyle.getAlignmentEnum();
        if (hssfAlignment == HorizontalAlignment.LEFT) {
            return SwingConstants.LEFT;
        }
        else if (hssfAlignment == HorizontalAlignment.CENTER) {
            return SwingConstants.CENTER;
        }
        else if (hssfAlignment == HorizontalAlignment.RIGHT) {
            return SwingConstants.RIGHT;
        }
        else {
            return SwingConstants.LEFT;
        }
    }

    public String getValidationListName() {
        return null;
    }

    public boolean isEmpty() {
        return false;
    }    

	@Override
	public int hashCode() {
		return theCell.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CellHSSFImpl) {
			CellHSSFImpl cell = (CellHSSFImpl)obj;
			return cell.theCell.equals(this.theCell);			
		}
		else {
			return false;
		}
	}

	/**
	 * Gets access to the POI internals for this cell - for debugging,testing and subclassing purposes only
	 * @return
	 */
	public HSSFCell getInnards() {
		return theCell;
	}

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	@Override
	public String getSheetName() {
		return workbook.getSheetName(getSheetIndex());
	}

	@Override
	public int getSheetIndex() {
		HSSFSheet sheet = theCell.getSheet();
		return workbook.getSheetIndex(sheet);
	}	
}
