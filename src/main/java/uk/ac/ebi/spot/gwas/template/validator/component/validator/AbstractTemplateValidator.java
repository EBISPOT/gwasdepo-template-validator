package uk.ac.ebi.spot.gwas.template.validator.component.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.gwas.template.validator.config.ErrorType;
import uk.ac.ebi.spot.gwas.template.validator.config.ValidatorConstants;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ErrorMessage;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationConfiguration;
import uk.ac.ebi.spot.gwas.template.validator.util.ValidationUtil;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

public abstract class AbstractTemplateValidator implements TemplateValidator {

    private static final Logger log = LoggerFactory.getLogger(TemplateValidator.class);

    @Override
    public List<String> validateSheet(Sheet sheet, int headerSize, ValidationConfiguration validationConfiguration,
                                      Map<String, Integer> studyTags, Map<Integer, String> columnIndex,
                                      boolean studySheetEnforced) {
        if (columnIndex.isEmpty()) {
            return processNoMappingErrorMessage();
        }

        Iterator<Row> rowIterator = sheet.rowIterator();
        boolean ready = false;
        boolean valid = true;
        int count = headerSize;
        int actualRowCount = 0;
        Map<Pair<String, ErrorMessage>, List<Integer>> errorMap = new HashMap<>();
        List<String> orphanStudies = new ArrayList<>();
        List<String> duplicatedStudyTags = new ArrayList<>();

        while (rowIterator.hasNext()) {
            count++;
            Row row = rowIterator.next();
            if (isTriggerRow(row, validationConfiguration.getTriggerRow())) {
                ready = true;
                continue;
            }
            if (ready) {
                RowValidator rowValidator = new RowValidator(row, validationConfiguration.getColumns(), validationConfiguration.getStudyTagColumnName(), columnIndex);
                valid = valid && rowValidator.isValid();
                if (!valid) {
                    Map<String, ErrorMessage> errors = rowValidator.getErrorMessageMap();
                    if (!errors.isEmpty()) {
                        for (String column : errors.keySet()) {
                            Pair<String, ErrorMessage> pair = Pair.of(column, errors.get(column));
                            List<Integer> rows = errorMap.containsKey(pair) ? errorMap.get(pair) : new ArrayList<>();
                            rows.add(count);
                            errorMap.put(pair, rows);
                        }
                    }
                }
                if (!rowValidator.isEmpty()) {
                    if (!handleValidRow(rowValidator.getStudyTag(), studyTags, sheet.getSheetName())) {
                        orphanStudies.add(Integer.toString(count));
                        valid = false;
                    }
                }

                if (valid) {
                    actualRowCount++;
                }
            }
        }
        log.info("Processed total {} rows.", count);
        Pair<String, List<String>> generalError = null;
        if (studySheetEnforced && studyTags.isEmpty()) {
            valid = false;
            generalError = Pair.of(ErrorType.NO_DATA, new ArrayList<>());
        }
        if (sheet.getSheetName().equalsIgnoreCase(ValidatorConstants.SAMPLE) && !studyTags.isEmpty() && valid && actualRowCount == 0) {
            valid = false;
            generalError = Pair.of(ErrorType.NO_SAMPLE_DATA, new ArrayList<>());
        }

        for (String studyTag : studyTags.keySet()) {
            if (studyTags.get(studyTag) != 1) {
                duplicatedStudyTags.add(studyTag);
            }
        }
        if (!duplicatedStudyTags.isEmpty()) {
            valid = false;
            generalError = Pair.of(ErrorType.NON_UNIQUE_STUDY_TAG + "!", duplicatedStudyTags);
        }

        if (valid) {
            return new ArrayList<>();
        } else {
            if (!orphanStudies.isEmpty()) {
                generalError = Pair.of(ErrorType.ORPHAN_STUDY, orphanStudies);
            }
            return processErrorMessages(generalError, errorMap);
        }
    }

    protected boolean isTriggerRow(Row row, String triggerRowValue) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            try {
                String value = cell.getStringCellValue();
                return value.equalsIgnoreCase(triggerRowValue);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    protected boolean isEmptyRow(Row currentRow) {
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

    @Override
    public List<String> extractValues(Sheet sheet, ValidationConfiguration validationConfiguration, Map<Integer, String> columnIndex, String referenceColumn) {
        List<String> values = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.rowIterator();
        boolean ready = false;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isTriggerRow(row, validationConfiguration.getTriggerRow())) {
                ready = true;
                continue;
            }
            if (ready) {
                for (int i : columnIndex.keySet()) {
                    String heading = columnIndex.get(i);
                    CellValidation cellValidation = validationConfiguration.getColumns().get(heading);

                    if (cellValidation.getColumnName().equals(referenceColumn)) {
                        Cell cell = row.getCell(i);
                        String value = cell.getStringCellValue();

                        if (value != null) {
                            values.add(value);
                        }
                        break;
                    }
                }
            }
        }
        return values;
    }

    @Override
    public void convertSheet(Sheet sheet, ValidationConfiguration validationConfiguration, Map<Integer, String> columnIndex, SubmissionDocument submissionDocument) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        boolean ready = false;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isTriggerRow(row, validationConfiguration.getTriggerRow())) {
                ready = true;
                continue;
            }
            if (ready) {
                convertRow(row, validationConfiguration.getColumns(), columnIndex, submissionDocument);
            }
        }
    }

    protected void convertToObject(Object object, Row row, Map<String, CellValidation> columns, Map<Integer, String> columnIndex) {
        for (int i : columnIndex.keySet()) {
            String heading = columnIndex.get(i);
            CellValidation cellValidation = columns.get(heading);

            Cell cell = row.getCell(i);
            try {
                Field field = object.getClass().getDeclaredField(cellValidation.getColumnName());
                field.setAccessible(true);
                if (cellValidation.getBaseType().equalsIgnoreCase(String.class.getSimpleName())) {
                    if (cell != null) {
                        if (cell.getCellType().equals(CellType.NUMERIC)) {
                            Double numericValue = cell.getNumericCellValue();
                            if (numericValue != null) {
                                String value = new DecimalFormat("#0.00000000000000000000").format(numericValue);
                                value = ValidationUtil.trimZeros(value);
                                field.set(object, value);
                            }
                        } else {
                            if (cell.getStringCellValue() != null) {
                                if (cellValidation.getAcceptedValues() != null) {
                                    String finalValue = "";
                                    if (cellValidation.getSeparator() != null) {
                                        String separator = "\\" + cellValidation.getSeparator();
                                        String[] multiValues = cell.getStringCellValue().trim().split(separator);
                                        for (String multiValue : multiValues) {
                                            multiValue = ValidationUtil.trimSpaces(multiValue);
                                            if (!multiValue.equalsIgnoreCase("")) {
                                                String actualValue = findActualValue(cellValidation.getAcceptedValues(), multiValue.toLowerCase());

                                                if (actualValue != null) {
                                                    finalValue += actualValue + cellValidation.getSeparator();
                                                }
                                            }
                                        }
                                        if (finalValue.endsWith(cellValidation.getSeparator())) {
                                            finalValue = finalValue.substring(0, finalValue.length() - cellValidation.getSeparator().length()).trim();
                                        }
                                    } else {
                                        String val = ValidationUtil.trimSpaces(cell.getStringCellValue());
                                        if (!val.equals("")) {
                                            String actualValue = findActualValue(cellValidation.getAcceptedValues(), val.toLowerCase());
                                            if (actualValue != null) {
                                                finalValue = actualValue;
                                            }
                                        }
                                    }
                                    if (!finalValue.equalsIgnoreCase("")) {
                                        field.set(object, finalValue);
                                    }
                                } else {
                                    String val = ValidationUtil.trimSpaces(cell.getStringCellValue());
                                    if (!val.equalsIgnoreCase("")) {
                                        field.set(object, val);
                                    }
                                }
                            } else {
                                field.set(object, null);
                            }
                        }
                    }
                } else {
                    if (cellValidation.getBaseType().equalsIgnoreCase(Double.class.getSimpleName()) ||
                            cellValidation.getBaseType().equalsIgnoreCase(Integer.class.getSimpleName())) {
                        if (cell != null) {
                            Double numericValue = null;
                            boolean skip = false;
                            try {
                                String sVal = cell.getStringCellValue();
                                if (StringUtils.isBlank(sVal)) {
                                    skip = true;
                                }
                            } catch (Exception e) {
                            }

                            if (!skip) {
                                try {
                                    numericValue = cell.getNumericCellValue();
                                } catch (Exception e) {
                                }
                                if (numericValue != null) {
                                    if (cellValidation.getBaseType().equalsIgnoreCase(Double.class.getSimpleName())) {
                                        if (field.getType().getSimpleName().equalsIgnoreCase(cellValidation.getBaseType())) {
                                            field.set(object, numericValue.doubleValue());
                                        } else {
                                            field.set(object, numericValue.toString());
                                        }
                                    } else {
                                        field.set(object, numericValue.intValue());
                                    }
                                } else {
                                    String value = cell.getStringCellValue();
                                    if (value != null) {
                                        if (!"".equals(value)) {
                                            try {
                                                numericValue = Double.parseDouble(value);
                                                if (cellValidation.getBaseType().equalsIgnoreCase(Double.class.getSimpleName())) {
                                                    if (field.getType().getSimpleName().equalsIgnoreCase(cellValidation.getBaseType())) {
                                                        field.set(object, numericValue.doubleValue());
                                                    } else {
                                                        field.set(object, numericValue.toString());
                                                    }
                                                } else {
                                                    field.set(object, numericValue.intValue());
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (cellValidation.getBaseType().equalsIgnoreCase(Boolean.class.getSimpleName())) {
                            if (cell != null) {
                                if (cell.getStringCellValue() != null) {
                                    if (cell.getStringCellValue().equalsIgnoreCase(RowValidator.BOOL_VALUE_YES)) {
                                        field.set(object, true);
                                    } else {
                                        if (cell.getStringCellValue().equalsIgnoreCase(RowValidator.BOOL_VALUE_NO)) {
                                            field.set(object, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String findActualValue(Map<String, List<String>> acceptedValues, String value) {
        for (String key : acceptedValues.keySet()) {
            if (key.equalsIgnoreCase(value)) {
                return key;
            }
            if (acceptedValues.get(key).contains(value)) {
                return key;
            }
        }

        return null;
    }

}
