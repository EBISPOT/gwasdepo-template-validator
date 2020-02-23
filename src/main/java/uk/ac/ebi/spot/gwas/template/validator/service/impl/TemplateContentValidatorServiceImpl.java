package uk.ac.ebi.spot.gwas.template.validator.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSchemaDto;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSheetDto;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.CellValidationParser;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.CellValidationParserAdapterFactory;
import uk.ac.ebi.spot.gwas.template.validator.component.parser.SheetMapper;
import uk.ac.ebi.spot.gwas.template.validator.component.validator.TemplateValidator;
import uk.ac.ebi.spot.gwas.template.validator.component.validator.TemplateValidatorAdapterFactory;
import uk.ac.ebi.spot.gwas.template.validator.config.ValidatorConstants;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationConfiguration;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationOutcome;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateContentValidatorService;
import uk.ac.ebi.spot.gwas.template.validator.util.SchemaFromName;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.util.*;

@Service
public class TemplateContentValidatorServiceImpl implements TemplateContentValidatorService {

    private static final Logger log = LoggerFactory.getLogger(TemplateContentValidatorService.class);

    @Autowired
    private TemplateValidatorAdapterFactory templateValidatorAdapterFactory;

    @Autowired
    private CellValidationParserAdapterFactory cellValidationParserAdapterFactory;

    @Override
    public ValidationOutcome validate(SubmissionTemplateReader referenceTemplateReader,
                                      SubmissionTemplateReader actualTemplateReader,
                                      String referenceSheet,
                                      String referenceColumn,
                                      TemplateSchemaDto templateSchemaDto) {
        log.info("Validating content using sheet [{}] and column [{}] as reference.", referenceSheet, referenceColumn);

        referenceTemplateReader.reinitialize();
        actualTemplateReader.reinitialize();

        try {
            TemplateSheetDto schema = SchemaFromName.fromName(referenceSheet, templateSchemaDto);
            List<String> referenceValues = extractColumnValues(referenceTemplateReader, referenceSheet, referenceColumn, schema);
            List<String> actualValues = extractColumnValues(actualTemplateReader, referenceSheet, referenceColumn, schema);

            List<String> errorList = new ArrayList<>();
            for (String actualValue : actualValues) {
                if (!referenceValues.contains(actualValue)) {
                    errorList.add(actualValue);
                }
            }
            referenceTemplateReader.close();
            actualTemplateReader.close();

            Map<String, List<String>> errorMap = new HashMap<>();
            if (!errorList.isEmpty()) {
                errorMap.put(referenceSheet, errorList);
            }

            return new ValidationOutcome(errorList.isEmpty(), errorMap);
        } catch (Exception e) {
            log.error("Unable to validate content: {}", e.getMessage(), e);
            referenceTemplateReader.close();
            actualTemplateReader.close();
            return null;
        }
    }

    private List<String> extractColumnValues(SubmissionTemplateReader templateReader,
                                             String referenceSheet,
                                             String referenceColumn,
                                             TemplateSheetDto templateSheetDto) {
        Sheet actualSheet = null;
        Iterator<Sheet> sheets = templateReader.sheets();
        while (sheets.hasNext()) {
            Sheet sheet = sheets.next();
            if (sheet.getSheetName().equalsIgnoreCase(referenceSheet)) {
                actualSheet = sheet;
                break;
            }
        }

        if (actualSheet == null) {
            log.error("Unable to find reference sheet: {}", referenceSheet);
            return null;
        }

        try {
            TemplateValidator templateValidator = templateValidatorAdapterFactory.getAdapter(referenceSheet);
            CellValidationParser cellValidationParser = cellValidationParserAdapterFactory.getAdapter(ValidatorConstants.SCHEMA);
            ValidationConfiguration validationConfiguration = cellValidationParser.parseCellValidations(new ObjectMapper().writeValueAsString(templateSheetDto));
            boolean found = false;
            for (String key : validationConfiguration.getColumns().keySet()) {
                CellValidation cellValidation = validationConfiguration.getColumns().get(key);
                if (cellValidation.getColumnName().equals(referenceColumn)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.error("Reference column provided [{}] does not exist in the template schema.", referenceColumn);
                return null;
            }

            SheetMapper sheetMapper = new SheetMapper(actualSheet, validationConfiguration.getColumns());
            log.info("Sheet mapper configuration: {}", sheetMapper.getColumnIndex());
            return templateValidator.extractValues(actualSheet, validationConfiguration, sheetMapper.getColumnIndex(), referenceColumn);
        } catch (Exception e) {
            log.error("Unable to find adapter for component: {}", referenceSheet);
            return null;
        }
    }
}
