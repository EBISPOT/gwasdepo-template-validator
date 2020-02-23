package uk.ac.ebi.spot.gwas.template.validator.component.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SheetMapper {

    public Map<Integer, String> columnIndex;

    private Map<String, CellValidation> columnValidation;

    public SheetMapper(Sheet sheet, Map<String, CellValidation> columnValidation) {
        columnIndex = new LinkedHashMap<>();
        this.columnValidation = columnValidation;

        Iterator<Row> rowIterator = sheet.rowIterator();
        Row goodRow = null;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Iterator<Cell> columnIterator = row.cellIterator();
            int found = 0;
            while (columnIterator.hasNext()) {
                Cell cell = columnIterator.next();
                if (cell.getCellType().equals(CellType.STRING)) {
                    if (cell.getStringCellValue() != null) {
                        if (columnValidation.containsKey(cell.getStringCellValue())) {
                            found++;
                        }
                    }
                }
            }

            if (found > row.getPhysicalNumberOfCells() / 2) {
                goodRow = row;
                break;
            }
        }

        if (goodRow != null) {
            runMapping(goodRow);
        }
    }

    private void runMapping(Row row) {
        Iterator<Cell> columnIterator = row.cellIterator();
        int index = 0;
        while (columnIterator.hasNext()) {
            Cell cell = columnIterator.next();
            if (cell.getStringCellValue() != null) {
                if (columnValidation.containsKey(cell.getStringCellValue())) {
                    columnIndex.put(index, cell.getStringCellValue());
                }
            }
            index++;
        }
    }

    public Map<Integer, String> getColumnIndex() {
        return columnIndex;
    }
}
