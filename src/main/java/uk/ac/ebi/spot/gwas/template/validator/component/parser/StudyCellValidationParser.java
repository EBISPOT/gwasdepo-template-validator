package uk.ac.ebi.spot.gwas.template.validator.component.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateColumnDto;
import uk.ac.ebi.spot.gwas.deposition.dto.templateschema.TemplateSheetDto;
import uk.ac.ebi.spot.gwas.template.validator.config.ValidatorConstants;
import uk.ac.ebi.spot.gwas.template.validator.domain.CellValidation;
import uk.ac.ebi.spot.gwas.template.validator.domain.ValidationConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(ValidatorConstants.SCHEMA)
public class StudyCellValidationParser implements CellValidationParser {

    private final static String NUMBER = "number";

    @Override
    public ValidationConfiguration parseCellValidations(String templateSheetContent) {
        try {
            TemplateSheetDto templateSheetDto = new ObjectMapper().readValue(templateSheetContent, TemplateSheetDto.class);
            ValidationConfiguration validationConfiguration = new ValidationConfiguration();

            validationConfiguration.setStudyTagColumnName(templateSheetDto.getStudyTagColumnName());
            validationConfiguration.setTriggerRow(templateSheetDto.getTriggerRow());

            Map<String, CellValidation> columns = new LinkedHashMap<>();
            for (TemplateColumnDto templateColumnDto : templateSheetDto.getColumns()) {
                CellValidation cellValidation = new CellValidation();
                cellValidation.setColumnHeading(templateColumnDto.getColumnHeading());
                cellValidation.setColumnName(templateColumnDto.getColumnName());

                String baseType = templateColumnDto.getBaseType().equalsIgnoreCase(NUMBER) ?
                        Double.class.getSimpleName() : templateColumnDto.getBaseType();
                cellValidation.setBaseType(baseType);
                cellValidation.setRequired(templateColumnDto.getRequired().booleanValue());
                cellValidation.setMultivalue(templateColumnDto.getMultivalue() != null ? templateColumnDto.getMultivalue().booleanValue() : false);
                cellValidation.setSeparator(templateColumnDto.getSeparator());
                cellValidation.setPattern(templateColumnDto.getPattern());
                if (templateColumnDto.getLowerBound() != null) {
                    cellValidation.setLowerBound(templateColumnDto.getLowerBound());
                }
                if (templateColumnDto.getUpperBound() != null) {
                    cellValidation.setUpperBound(templateColumnDto.getUpperBound());
                }
                if (templateColumnDto.getSize() != null) {
                    cellValidation.setSize(templateColumnDto.getSize());
                }
                List<String> processedValues = null;
                if (templateColumnDto.getAcceptedValues() != null) {
                    processedValues = new ArrayList<>();
                    for (String acceptedValue : templateColumnDto.getAcceptedValues()) {
                        processedValues.add(acceptedValue);
                    }
                }
                cellValidation.setAcceptedValues(processedValues);
                columns.put(cellValidation.getColumnHeading(), cellValidation);
            }
            validationConfiguration.setColumns(columns);
            return validationConfiguration;
        } catch (IOException e) {
            return null;
        }
    }
}
