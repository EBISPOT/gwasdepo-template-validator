package uk.ac.ebi.spot.gwas.template.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSchemaDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationOutcome;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateValidatorService;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SSTemplateValidatorServiceTest extends IntegrationTest {

    private final static String[] TEST_FILES = new String[]{
            "ss_template_invalid.xlsx",
            "ss_template.xlsx"
    };

    private final static String SCHEMA_FILE = "schema_v1.2_SS.json";

    @Autowired
    private TemplateValidatorService templateValidatorService;

    private TemplateSchemaDto templateSchemaDto;

    @Before
    public void setup() throws IOException {
        File file = new File(getClass().getClassLoader().getResource(SCHEMA_FILE).getFile());
        String content = FileUtils.readFileToString(file);
        templateSchemaDto = new ObjectMapper().readValue(content, TemplateSchemaDto.class);
    }

    @Test
    public void shouldValidateInvalidSubmission() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 0);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, false);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertEquals(3, validationOutcome.getErrorMessages().get("study").size());
        submissionTemplateReader.close();
    }

    @Test
    public void shouldValidateValidSubmission() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 1);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, false);
        assertTrue(validationOutcome.isValid());
        assertTrue(validationOutcome.getErrorMessages().isEmpty());
        submissionTemplateReader.close();
    }
}
