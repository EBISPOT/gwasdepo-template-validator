package uk.ac.ebi.spot.gwas.template.validator.component.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.gwas.template.validator.config.ErrorType;
import uk.ac.ebi.spot.gwas.template.validator.config.ValidatorConstants;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ErrorMessage;
import uk.ac.ebi.spot.gwas.template.validator.util.PValueValidator;
import uk.ac.ebi.spot.gwas.template.validator.util.ValidationUtil;

import java.text.DecimalFormat;
import java.util.*;

public class RowValidator {


    private static final Logger log = LoggerFactory.getLogger(RowValidator.class);
    public final static String BOOL_VALUE_YES = "yes";

    public final static String BOOL_VALUE_NO = "no";

    private Map<String, CellValidation> columns;

    private String studyTag;

    private String checksum;

    private String summary_statistics_file;

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
                        if (cellValidation.getPattern() != null) {
                            if (cellValidation.getPattern().equalsIgnoreCase(ValidatorConstants.PATTERN_PVALUE)) {
                                value = cell.getStringCellValue() != null ? ValidationUtil.trimSpaces(cell.getStringCellValue()) : cell.getStringCellValue();

                                if (value != null) {
                                    if (!PValueValidator.validate(value, cellValidation.getLowerBound(), cellValidation.getUpperBound())) {
                                        valid = false;
                                        errorMessageMap.put(cellValidation.getColumnHeading(),
                                                new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                        ErrorType.PVALUE, ValidationUtil.rangeMess(
                                                        PValueValidator.formatBound(cellValidation.getLowerBound()),
                                                        PValueValidator.formatBound(cellValidation.getUpperBound()))));
                                        err = true;
                                    }
                                    continue;
                                }
                            }
                        }

                        try {
                            if (cell.getCellType().equals(CellType.NUMERIC)) {
                                Double numericValue = cell.getNumericCellValue();
                                if (numericValue != null) {
                                    if (cellValidation.getLowerBound() != null) {
                                        if (numericValue < cellValidation.getLowerBoundAsDouble()) {
                                            valid = false;
                                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                                    new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                            ErrorType.RANGE, ValidationUtil.rangeMess(cellValidation.getLowerBound(), cellValidation.getUpperBound())));
                                            err = true;
                                        }
                                    }
                                    if (cellValidation.getUpperBound() != null) {
                                        if (numericValue > cellValidation.getUpperBoundAsDouble()) {
                                            valid = false;
                                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                                    new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                            ErrorType.RANGE, ValidationUtil.rangeMess(cellValidation.getLowerBound(), cellValidation.getUpperBound())));
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
                            log.error("Exception in row "+e.getMessage(),e);
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
                        value = cell.getStringCellValue() != null ? ValidationUtil.trimSpaces(cell.getStringCellValue()) : null;
                        if (value != null) {
                            if (value.equalsIgnoreCase("")) {
                                value = null;
                            }
                        }
                    }
                    if (cellValidation.getSize() != null) {
                        if (cellValidation.getSize() != -1) {
                            if (value != null) {
                                if (value.length() >= cellValidation.getSize()) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_SIZE,
                                                    ErrorType.SIZE, cellValidation.getSize().toString()));
                                }
                            }
                        }
                    }
                    if (cellValidation.isMultivalue()) {
                        if (cellValidation.getSeparator() != null && value != null) {
                            String separator = "\\" + cellValidation.getSeparator();
                            String[] multiValues = value.split(separator);
                            for (String multiValue : multiValues) {
                                multiValue = ValidationUtil.trimSpaces(multiValue);
                                if (cellValidation.getAcceptedValues() != null) {
                                    if (!isAcceptedValue(cellValidation.getAcceptedValues(), multiValue.toLowerCase())) {
                                        valid = false;
                                        errorMessageMap.put(cellValidation.getColumnHeading(),
                                                new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                        ErrorType.ACCEPTED_VALUES, StringUtils.join(cellValidation.getAcceptedValuesCore(), "; ")));
                                    }
                                }
                                if (cellValidation.getPattern() != null) {
                                    try {
                                        if (!multiValue.matches(cellValidation.getPattern())) {
                                            valid = false;
                                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                                    new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                            ErrorType.PATTERN, cellValidation.getPattern()));
                                        }
                                    } catch(Exception ex ){
                                        log.error("Exception in Regex match"+ex.getMessage(),ex);
                                    }
                                }
                            }
                        }
                    } else {
                        if (cellValidation.getAcceptedValues() != null) {
                            if (value != null) {
                                if (!isAcceptedValue(cellValidation.getAcceptedValues(), value.toLowerCase())) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.ACCEPTED_VALUES, StringUtils.join(cellValidation.getAcceptedValuesCore(), "; ")));
                                }
                            }
                        }
                        if (cellValidation.getPattern() != null) {
                            if (!cellValidation.getPattern().equalsIgnoreCase(ValidatorConstants.PATTERN_PVALUE)) {
                                if (value != null) {
                                    try {
                                        if (!value.matches(cellValidation.getPattern())) {
                                            valid = false;
                                            errorMessageMap.put(cellValidation.getColumnHeading(),
                                                    new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                            ErrorType.PATTERN, cellValidation.getPattern()));
                                        }
                                    } catch(Exception ex) {
                                        log.error("Exception in Regex"+ex.getMessage(),ex);
                                    }
                                }
                            }
                        }
                    }
                    if (cellValidation.getColumnName().equalsIgnoreCase(studyTagColumnName)) {
                        if (value != null) {
                            studyTag = value.trim();
                        }
                    }

                    if (cellValidation.getColumnName().equalsIgnoreCase("summary_statistics_file")) {
                        if (value != null) {
                            summary_statistics_file = value.trim().equals("NR") ? null : value.trim();
                        }
                    }

                    if (cellValidation.getColumnName().equalsIgnoreCase("checksum")) {
                        if (value != null) {
                            checksum = value.trim().equals("NR") ? null : value.trim();
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
                                    String sVal = ValidationUtil.trimSpaces(cell.getStringCellValue());
                                    if (sVal == null || "".equals(sVal)) {
                                        ok = false;
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    String sVal = ValidationUtil.trimSpaces(cell.getStringCellValue());
                                    if (sVal != null && !"".equals(sVal)) {
                                        value = Double.parseDouble(sVal);
                                        ok = true;
                                    } else {
                                        ok = false;
                                    }
                                } catch (Exception e) {
                                    log.error("Exception in Row validation2 "+e.getMessage(),e);
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
                                if (value < cellValidation.getLowerBoundAsDouble()) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.RANGE, ValidationUtil.rangeMess(cellValidation.getLowerBound(), cellValidation.getUpperBound())));
                                }
                            }
                            if (cellValidation.getUpperBound() != null) {
                                if (value > cellValidation.getUpperBoundAsDouble()) {
                                    valid = false;
                                    errorMessageMap.put(cellValidation.getColumnHeading(),
                                            new ErrorMessage(ErrorType.INCORRECT_VALUE_RANGE,
                                                    ErrorType.RANGE, ValidationUtil.rangeMess(cellValidation.getLowerBound(), cellValidation.getUpperBound())));
                                }
                            }
                        }
                    } else {
                        if (cellValidation.getBaseType().equalsIgnoreCase(Boolean.class.getSimpleName())) {
                            String value = null;
                            if (cellValidation.isRequired()) {
                                try {
                                    value = ValidationUtil.trimSpaces(cell.getStringCellValue());
                                } catch (Exception e) {
                                    log.error("Exception in Row validation3 "+e.getMessage(),e);
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

    private boolean isAcceptedValue(Map<String, List<String>> acceptedValues, String value) {
        for (List<String> list : acceptedValues.values()) {
            if (list.contains(value)) {
                return true;
            }
        }

        return false;
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

    public String getChecksum() {
        return checksum;
    }

    public String getSummary_statistics_file() {
        return summary_statistics_file;
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
