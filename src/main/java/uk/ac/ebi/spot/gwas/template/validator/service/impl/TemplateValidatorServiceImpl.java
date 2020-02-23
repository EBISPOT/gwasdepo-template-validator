package uk.ac.ebi.spot.gwas.template.validator.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationConfiguration;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationOutcome;
import uk.ac.ebi.spot.gwas.template.validator.service.TemplateValidatorService;
import uk.ac.ebi.spot.gwas.template.validator.util.SchemaFromName;
import uk.ac.ebi.spot.gwas.template.validator.util.SubmissionTemplateReader;

import java.util.*;

@Service
public class TemplateValidatorServiceImpl implements TemplateValidatorService {

    private static final Logger log = LoggerFactory.getLogger(TemplateValidatorService.class);

    @Autowired
    private TemplateValidatorAdapterFactory templateValidatorAdapterFactory;

    @Autowired
    private CellValidationParserAdapterFactory cellValidationParserAdapterFactory;

    @Override
    public ValidationOutcome validate(SubmissionTemplateReader submissionTemplateReader,
                                      TemplateSchemaDto templateSchemaDto,
                                      boolean studySheetEnforced) {
        log.info("Validating template ...");
        Map<String, List<String>> errors = new LinkedHashMap<>();
        Map<String, String> studyTagMap;
        try {
            log.info("Reading studies sheet ...");
            studyTagMap = readStudiesSheet(submissionTemplateReader, errors, templateSchemaDto, studySheetEnforced);
            log.info("Found {} entries in study map.", studyTagMap.size());
        } catch (Exception e) {
            log.error("Unable to process studies sheet: {}", e.getMessage(), e);
            return null;
        }

        if (!studyTagMap.isEmpty()) {
            Iterator<Sheet> sheets = submissionTemplateReader.sheets();
            while (sheets.hasNext()) {
                Sheet sheet = sheets.next();
                if (sheet.getSheetName().equalsIgnoreCase(ValidatorConstants.STUDY)) {
                    continue;
                }
                List<String> errorList;
                try {
                    errorList = processSheet(sheet,
                            sheet.getSheetName(), studyTagMap,
                            SchemaFromName.fromName(sheet.getSheetName(), templateSchemaDto),
                            submissionTemplateReader.getHeaderSize(),
                            studySheetEnforced);
                } catch (Exception e) {
                    log.error("Unable to process sheet: {}", e.getMessage(), e);
                    return null;
                }
                if (!errorList.isEmpty()) {
                    errors.put(sheet.getSheetName(), errorList);
                }
            }
        }

        return new ValidationOutcome(errors.isEmpty(), errors);
    }

    private Map<String, String> readStudiesSheet(SubmissionTemplateReader submissionTemplateReader,
                                                 Map<String, List<String>> errors,
                                                 TemplateSchemaDto templateSchemaDto,
                                                 boolean studySheetEnforced) throws JsonProcessingException {
        Iterator<Sheet> sheets = submissionTemplateReader.sheets();

        Map<String, String> studyTagMap = new HashMap<>();
        while (sheets.hasNext()) {
            Sheet sheet = sheets.next();
            log.info("Reading sheet: {}", sheet.getSheetName());
            if (sheet.getSheetName().equalsIgnoreCase(ValidatorConstants.STUDY)) {
                List<String> errorMap = processSheet(sheet,
                        sheet.getSheetName(), studyTagMap,
                        SchemaFromName.fromName(sheet.getSheetName(),
                                templateSchemaDto),
                        submissionTemplateReader.getHeaderSize(),
                        studySheetEnforced);
                if (!errorMap.isEmpty()) {
                    errors.put(sheet.getSheetName(), errorMap);
                }
                break;
            }
        }
        return studyTagMap;
    }

    private List<String> processSheet(Sheet sheet, String validatorComponent, Map<String, String> studyTagMap,
                                      TemplateSheetDto templateSheetDto, int headerSize, boolean studySheetEnforced) throws JsonProcessingException {
        log.info("Processing sheet: {}", sheet.getSheetName());
        TemplateValidator templateValidator = null;
        try {
            templateValidator = templateValidatorAdapterFactory.getAdapter(validatorComponent);
        } catch (Exception e) {
            log.error("Unable to find adapter for component: {}", validatorComponent);
        }
        if (templateValidator != null) {
            CellValidationParser cellValidationParser = cellValidationParserAdapterFactory.getAdapter(ValidatorConstants.SCHEMA);
            ValidationConfiguration validationConfiguration = cellValidationParser.parseCellValidations(new ObjectMapper().writeValueAsString(templateSheetDto));
            SheetMapper sheetMapper = new SheetMapper(sheet, validationConfiguration.getColumns());

            log.info("Sheet mapper configuration: {}", sheetMapper.getColumnIndex());
            return templateValidator.validateSheet(sheet, headerSize, validationConfiguration, studyTagMap, sheetMapper.getColumnIndex(), studySheetEnforced);
        }

        return new ArrayList<>();
    }
}
