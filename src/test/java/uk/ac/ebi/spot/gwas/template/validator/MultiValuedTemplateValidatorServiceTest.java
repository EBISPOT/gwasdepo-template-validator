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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiValuedTemplateValidatorServiceTest extends IntegrationTest {

    private final static String[] TEST_FILES = new String[]{
            "multi_country.xlsx",
            "no_sample.xlsx"
    };

    private final static String SCHEMA_FILE = "schema_v1.6m.json";

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
    public void shouldValidateMutipleValues() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 0);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertTrue(validationOutcome.isValid());
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateEmptySampleSheet() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 1);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("sample"));
        assertEquals(1, validationOutcome.getErrorMessages().get("sample").size());
        assertEquals("No sample data provided although studies are present.", validationOutcome.getErrorMessages().get("sample").get(0));
        submissionTemplateReader.close();
    }
}