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

public class TemplateValidatorServiceTest extends IntegrationTest {

    private final static String[] TEST_FILES = new String[]{
            "valid.xlsx",
            "missing_mandatory_value.xlsx",
            "incorrect_accepted_value.xlsx",
            "orphan_association.xlsx",
            "orphan_sample.xlsx",
            "wrong_lower_bound.xlsx",
            "wrong_upper_bound.xlsx",
            "wrong_pattern_value.xlsx",
            "wrong_value_type.xlsx",
            "empty.xlsx",
            "duplicated_studytag.xlsx"
    };

    private final static String SCHEMA_FILE = "schema_v1.json";

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
    public void shouldValidateValidSubmission() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 0);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        /*
        PrintWriter pw = new PrintWriter("errors.out");
        for (String key : validationOutcome.getErrorMessages().keySet()) {
            pw.write(" - SHEET: " + key + "\n");
            for (String val : validationOutcome.getErrorMessages().get(key)) {
                pw.write(" -- " + val + "\n");
                pw.flush();
            }
            pw.write("=====\n");
            pw.flush();
        }
        pw.close();
        */
        assertTrue(validationOutcome.isValid());
        assertTrue(validationOutcome.getErrorMessages().isEmpty());
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateEmptySpreadsheet() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 9);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
    }

    @Test
    public void shouldNotValidateMissingMandatoryValue() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 1);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(2, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("association"));
        assertTrue(validationOutcome.getErrorMessages().containsKey("sample"));
        assertEquals(1, validationOutcome.getErrorMessages().get("association").size());
        assertEquals("Column 'p-value' in row(s) [7] lacks mandatory value", validationOutcome.getErrorMessages().get("association").get(0));
        assertEquals(1, validationOutcome.getErrorMessages().get("sample").size());
        assertEquals("Column 'Number of individuals' in row(s) [7] lacks mandatory value", validationOutcome.getErrorMessages().get("sample").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateIncorrectAcceptedValue() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 2);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("sample"));
        assertEquals(1, validationOutcome.getErrorMessages().get("sample").size());
        assertEquals("Column 'Stage' in row(s) [5] contains incorrect value. Accepted values are: 'discovery; replication'", validationOutcome.getErrorMessages().get("sample").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateOrphanAssociation() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 3);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("association"));
        assertEquals(1, validationOutcome.getErrorMessages().get("association").size());
        assertEquals("The study tag referenced in row(s) [8] is not defined in the study tab. Please check.", validationOutcome.getErrorMessages().get("association").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateOrphanSample() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 4);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("sample"));
        assertEquals(1, validationOutcome.getErrorMessages().get("sample").size());
        assertEquals("The study tag referenced in row(s) [11] is not defined in the study tab. Please check.", validationOutcome.getErrorMessages().get("sample").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateDuplicatedStudyTag() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 10);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("study"));
        assertEquals(1, validationOutcome.getErrorMessages().get("study").size());
        assertEquals("Study tags should be unique. Found duplicated study tags: '30237584_1'", validationOutcome.getErrorMessages().get("study").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateWrongLowerBound() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 5);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("association"));
        assertEquals(1, validationOutcome.getErrorMessages().get("association").size());
        assertEquals("Column 'p-value' in row(s) [5] contains incorrect value. Accepted values should be in the range: '0.0-1.0E-5'", validationOutcome.getErrorMessages().get("association").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateWrongUpperBound() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 6);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("association"));
        assertEquals(1, validationOutcome.getErrorMessages().get("association").size());
        assertEquals("Column 'p-value' in row(s) [6] contains incorrect value. Accepted values should be in the range: '0.0-1.0E-5'", validationOutcome.getErrorMessages().get("association").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateWrongPattern() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 7);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("association"));
        assertEquals(1, validationOutcome.getErrorMessages().get("association").size());
        assertEquals("Column 'Effect allele' in row(s) [6] contains incorrect value. Accepted values should follow the pattern: '^[actgACTG]*$'", validationOutcome.getErrorMessages().get("association").get(0));
        submissionTemplateReader.close();
    }

    @Test
    public void shouldNotValidateWrongValueType() throws IOException {
        SubmissionTemplateReader submissionTemplateReader = getForFile(TEST_FILES, 8);
        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("sample"));
        assertEquals(1, validationOutcome.getErrorMessages().get("sample").size());
        assertEquals("Column 'Number of individuals' in row(s) [9] lacks mandatory value", validationOutcome.getErrorMessages().get("sample").get(0));
        submissionTemplateReader.close();
    }
}