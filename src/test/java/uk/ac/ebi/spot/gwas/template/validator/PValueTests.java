package uk.ac.ebi.spot.gwas.template.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSchemaDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.SubmissionDocument;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationOutcome;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateConverterService;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateValidatorService;
import uk.ac.ebi.spot.gwas.template.validator.util.StreamSubmissionTemplateReader;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PValueTests extends IntegrationTest {

    private final static String VALID_FILE = "pvalue_correct.xlsx";

    private final static String INVALID_FILE = "pvalue_incorrect.xlsx";

    private final static String SCHEMA_FILE = "schema_v1.7.json";

    @Autowired
    private TemplateConverterService templateConverterService;

    @Autowired
    private TemplateValidatorService templateValidatorService;

    private TemplateSchemaDto templateSchemaDto;

    private SubmissionTemplateReader submissionTemplateReader;

    @Before
    public void setup() throws IOException {
        File file = new File(getClass().getClassLoader().getResource(SCHEMA_FILE).getFile());
        String content = FileUtils.readFileToString(file);
        templateSchemaDto = new ObjectMapper().readValue(content, TemplateSchemaDto.class);
    }

    @Test
    public void shouldValidateValidSubmission() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(VALID_FILE).getFile());
        byte[] fileContent = IOUtils.toByteArray(new FileInputStream(file));
        submissionTemplateReader = new StreamSubmissionTemplateReader(fileContent, file.getName());

        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertTrue(validationOutcome.isValid());
    }

    @Test
    public void shouldValidateInvalidSubmission() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(INVALID_FILE).getFile());
        byte[] fileContent = IOUtils.toByteArray(new FileInputStream(file));
        submissionTemplateReader = new StreamSubmissionTemplateReader(fileContent, file.getName());

        ValidationOutcome validationOutcome = templateValidatorService.validate(submissionTemplateReader, templateSchemaDto, true);
        assertFalse(validationOutcome.isValid());
        assertEquals(1, validationOutcome.getErrorMessages().size());
        assertTrue(validationOutcome.getErrorMessages().containsKey("association"));
        assertEquals("Column 'p-value' in row(s) [7] contains incorrect value. Expected p-value exponent should be in the range: '-5.0-'", validationOutcome.getErrorMessages().get("association").get(0));
    }

    @Test
    public void shouldConvertValidSubmission() throws Exception {
        File file = new File(getClass().getClassLoader().getResource(VALID_FILE).getFile());
        byte[] fileContent = IOUtils.toByteArray(new FileInputStream(file));
        submissionTemplateReader = new StreamSubmissionTemplateReader(fileContent, file.getName());

        SubmissionDocument submissionDocument = templateConverterService.convert(submissionTemplateReader, templateSchemaDto);
        assertEquals(3, submissionDocument.getAssociationEntries().size());
        submissionDocument.getAssociationEntries().get(0).getPvalue().equalsIgnoreCase("3.70e-08");
        submissionDocument.getAssociationEntries().get(1).getPvalue().equalsIgnoreCase("9.6e-40");
        submissionDocument.getAssociationEntries().get(2).getPvalue().equalsIgnoreCase("1e-1000");
        submissionTemplateReader.close();
    }
}
