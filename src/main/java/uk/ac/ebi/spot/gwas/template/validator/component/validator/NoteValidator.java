package uk.ac.ebi.spot.gwas.template.validator.component.validator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.template.validator.config.ErrorType;
import uk.ac.ebi.spot.gwas.template.validator.config.ValidatorConstants;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ErrorMessage;
import uk.ac.ebi.spot.gwas.template.validator.domain.Note;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;
import uk.ac.ebi.spot.gwas.template.validator.util.ErrorMessageTemplateProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(ValidatorConstants.NOTES)
public class NoteValidator extends AbstractTemplateValidator implements TemplateValidator {

    @Autowired
    private ErrorMessageTemplateProcessor errorMessageTemplateProcessor;

    @Override
    public boolean handleValidRow(String studyTag, Map<String, Integer> studyTags, String sheetName) {
        if (studyTag == null) {
            return false;
        }
        if (!studyTags.containsKey(studyTag.toLowerCase())) {
            return false;
        }

        return true;
    }

    @Override
    public void convertRow(Row row, Map<String, CellValidation> columnValidation, Map<Integer, String> columnIndex, SubmissionDocument submissionDocument) {
        if (isEmptyRow(row)) {
            return;
        }
        Note note = new Note();
        convertToObject(note, row, columnValidation, columnIndex);
        submissionDocument.addNote(note);
    }

    @Override
    public List<String> processErrorMessages(Pair<String, List<String>> generalError, Map<Pair<String, ErrorMessage>, List<Integer>> errorMap) {
        return errorMessageTemplateProcessor.process(generalError, errorMap);
    }

    @Override
    public List<String> processNoMappingErrorMessage() {
        List<String> errors = new ArrayList<>();
        errors.add(errorMessageTemplateProcessor.processMessage(ErrorType.NO_MAPPING, null));
        return errors;
    }

}
