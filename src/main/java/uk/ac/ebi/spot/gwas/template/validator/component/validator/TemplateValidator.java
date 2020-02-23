package uk.ac.ebi.spot.gwas.template.validator.component.validator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ErrorMessage;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationConfiguration;

import java.util.List;
import java.util.Map;

public interface TemplateValidator {

    List<String> validateSheet(Sheet sheet, int headerSize, ValidationConfiguration validationConfiguration,
                               Map<String, String> studyTags, Map<Integer, String> columnIndex,
                               boolean studySheetEnforced);

    boolean handleValidRow(String studyTag, Map<String, String> studyTags, String sheetName);

    void convertSheet(Sheet sheet, ValidationConfiguration validationConfiguration, Map<Integer, String> columnIndex, SubmissionDocument submissionDocument);

    void convertRow(Row row, Map<String, CellValidation> columnValidation, Map<Integer, String> columnIndex, SubmissionDocument submissionDocument);

    List<String> processErrorMessages(Pair<String, List<Integer>> generalError, Map<Pair<String, ErrorMessage>, List<Integer>> errorMap);

    List<String> processNoMappingErrorMessage();

    List<String> extractValues(Sheet actualSheet, ValidationConfiguration validationConfiguration, Map<Integer, String> columnIndex, String referenceColumn);
}
