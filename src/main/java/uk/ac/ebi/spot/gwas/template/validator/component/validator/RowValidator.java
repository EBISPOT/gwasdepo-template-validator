package uk.ac.ebi.spot.gwas.template.validator.component.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import uk.ac.ebi.spot.gwas.template.validator.config.ErrorType;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ErrorMessage;
import uk.ac.ebi.spot.gwas.template.validator.util.ValidationUtil;

import java.text.DecimalFormat;
import java.util.*;

public class RowValidator {

    public final static String BOOL_VALUE_YES = "yes";

    public final static String BOOL_VALUE_NO = "no";

    private Map<String, CellValidation> columns;

    private String studyTag;

    private boolean valid;

    private boolean empty;

    private Map<String, ErrorMessage> errorMessageMap;

    private Map<Integer, String> columnIndex;

    private List<String> columnsProcessed;

    public RowValidator(Row row, Map<String, CellValidation> columns, String studyTagColumnName, Map<Integer, String> columnIndex) {
        this.columns = columns;
        this.valid = true;
        this.empty = false;
        this.columnIndex = columnIndex;
        this.columnsProcessed = new ArrayList<>();
        this.errorMessageMap = new LinkedHashMap<>();
        this.processRow(row, studyTagColumnName);
    }

    private void processRow(Row currentRow, String studyTagColumnName) {
        if (isEmptyRow(currentRow)) {
            this.empty = true;
            return;
        }
        for (int i : columnIndex.keySet()) {
            String heading = columnIndex.get(i);
            CellValidation cellValidation = columns.get(heading);

            Cell cell = currentRow.getCell(i);
            if (cell != null) {
                columnsProcessed.add(heading);
                if (cellValidation.getBaseType().equalsIgnoreCase(String.class.getSimpleName())) {
                    String value = null;
                    if (cellValidation.isRequired()) {
                        boolean err = false;
                        try {
                            if (cell.getCellType().equals(CellType.NUMERIC)) {
                                Double numericValue = cell.getNumericCellValue();
                                if (numericValue != null) {
                                    if (cellValidation.getLowerBound() != null) {
                                        if (numericValue < cellValidation.getLowerBound()) {
                                            valid = false;
                                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                                    new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                            ErrorType.RANGE, cellValidation.getLowerBound() + "-" + cellValidation.getUpperBound()));
                                            err = true;
                                        }
                                    }
                                    if (cellValidation.getUpperBound() != null) {
                                        if (numericValue > cellValidation.getUpperBound()) {
                                            valid = false;
                                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                                    new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                            ErrorType.RANGE, cellValidation.getLowerBound() + "-" + cellValidation.getUpperBound()));
                                            err = true;
                                        }
                                    }
                                    if (!err) {
                                        value = new DecimalFormat("#0.00000000000000000000").format(numericValue);
                                        value = ValidationUtil.trimZeros(value);
                                    }
                                }
                            } else {
                                value = cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : cell.getStringCellValue();
                            }
                        } catch (Exception e) {
                            valid = false;
                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                    new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                        }
                        if (value == null || "".equals(value)) {
                            if (!err) {
                                valid = false;
                                errorMessageMap.put(cellValidation.getColumnHeading(),
                                        new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                            }
                        }
                    } else {
                        value = cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : null;
                        if (value != null) {
                            if (value.equalsIgnoreCase("")) {
                                value = null;
                            }
                        }
                    }
                    if (cellValidation.isMultivalue()) {
                        if (cellValidation.getSeparator() != null && value != null) {
                            String separator = "\\" + cellValidation.getSeparator();
                            String[] multiValues = value.split(separator);
                            for (String multiValue : multiValues) {
                                multiValue = multiValue.trim();
                                if (cellValidation.getAcceptedValues() != null) {
                                    if (!cellValidation.getAcceptedValues().contains(multiValue)) {
                                        valid = false;
                                        errorMessageMap.put(cellValidation.getColumnHeading(),
                                                new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                        ErrorType.ACCEPTED_VALUES, StringUtils.join(cellValidation.getAcceptedValues(), "; ")));
                                    }
                                }
                                if (cellValidation.getPattern() != null) {
                                    if (!multiValue.matches(cellValidation.getPattern())) {
                                        valid = false;
                                        errorMessageMap.put(cellValidation.getColumnHeading(),
                                                new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                        ErrorType.PATTERN, cellValidation.getPattern()));
                                    }
                                }
                            }
                        }
                    } else {
                        if (cellValidation.getAcceptedValues() != null) {
                            if (value != null) {
                                if (!cellValidation.getAcceptedValues().contains(value)) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.ACCEPTED_VALUES, StringUtils.join(cellValidation.getAcceptedValues(), "; ")));
                                }
                            }
                        }
                        if (cellValidation.getPattern() != null) {
                            if (value != null) {
                                if (!value.matches(cellValidation.getPattern())) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.PATTERN, cellValidation.getPattern()));
                                }
                            }
                        }
                    }
                    if (cellValidation.getColumnName().equalsIgnoreCase(studyTagColumnName)) {
                        if (value != null) {
                            studyTag = value.trim();
                        }
                    }
                } else {
                    if (cellValidation.getBaseType().equalsIgnoreCase(Double.class.getSimpleName()) ||
                            cellValidation.getBaseType().equalsIgnoreCase(Integer.class.getSimpleName())) {
                        Double value = null;
                        boolean ok = true;
                        if (cellValidation.isRequired()) {
                            try {
                                value = cell.getNumericCellValue();
                            } catch (Exception e) {
                                ok = false;
                            }
                            if (value != null) {
                                try {
                                    String sVal = cell.getStringCellValue();
                                    if (sVal == null || "".equals(sVal)) {
                                        ok = false;
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    String sVal = cell.getStringCellValue();
                                    if (sVal != null && !"".equals(sVal)) {
                                        value = Double.parseDouble(sVal);
                                        ok = true;
                                    } else {
                                        ok = false;
                                    }
                                } catch (Exception e) {
                                    ok = false;
                                }
                            }
                        }
                        if (!ok) {
                            valid = false;
                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                    new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                        }
                        if (value != null) {
                            if (cellValidation.getLowerBound() != null) {
                                if (value < cellValidation.getLowerBound()) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.RANGE, cellValidation.getLowerBound() + "-" + cellValidation.getUpperBound()));
                                }
                            }
                            if (cellValidation.getUpperBound() != null) {
                                if (value > cellValidation.getUpperBound()) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.RANGE, cellValidation.getLowerBound() + "-" + cellValidation.getUpperBound()));
                                }
                            }
                        }
                    } else {
                        if (cellValidation.getBaseType().equalsIgnoreCase(Boolean.class.getSimpleName())) {
                            String value = null;
                            if (cellValidation.isRequired()) {
                                try {
                                    value = cell.getStringCellValue();
                                } catch (Exception e) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                                }
                                if (value == null || "".equals(value)) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                                }
                            }
                            if (value != null) {
                                if (!value.equalsIgnoreCase(BOOL_VALUE_YES) && !value.equalsIgnoreCase(BOOL_VALUE_NO)) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.ACCEPTED_VALUES, StringUtils.join(Arrays.asList(new String[]{"Yes", "No"}), "; ")));
                                }
                            }
                        }
                    }
                }
            } else {
                if (cellValidation.isRequired()) {
                    valid = false;
                    errorMessageMap.put(cellValidation.getColumnHeading(),
                            new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                }
            }
        }

        for (String heading : columns.keySet()) {
            CellValidation cellValidation = columns.get(heading);
            if (!columnsProcessed.contains(heading)) {
                if (cellValidation.isRequired()) {
                    valid = false;
                    errorMessageMap.put(cellValidation.getColumnHeading(),
                            new ErrorMessage(ErrorType.MISSING_VALUE, null, null));
                }
            }
        }
    }

    private boolean isEmptyRow(Row currentRow) {
        Iterator<Cell> cellIterator = currentRow.cellIterator();
        boolean isEmpty = true;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getStringCellValue() != null) {
                if (!cell.getStringCellValue().equals("")) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    public String getStudyTag() {
        return studyTag;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isValid() {
        return valid;
    }

    public Map<String, ErrorMessage> getErrorMessageMap() {
        return errorMessageMap;
    }
}
