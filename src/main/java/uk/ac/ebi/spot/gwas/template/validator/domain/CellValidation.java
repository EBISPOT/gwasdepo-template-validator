package uk.ac.ebi.spot.gwas.template.validator.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class CellValidation implements Serializable {

    private static final long serialVersionUID = -3440290320750216439L;

    private String columnName;

    private String columnHeading;

    private String baseType;

    private boolean required;

    private boolean multivalue;

    private String separator;

    private String pattern;

    private String lowerBound;

    private String upperBound;

    private Integer size;

    private Map<String, List<String>> acceptedValues;

    private List<String> acceptedValuesCore;

    public CellValidation() {

    }

    public CellValidation(String columnName, String columnHeading, String baseType, boolean required) {
        this.columnName = columnName;
        this.columnHeading = columnHeading;
        this.baseType = baseType;
        this.required = required;
    }

    public CellValidation(String columnName, String baseType, String columnHeading, boolean required,
                          String lowerBound, String upperBound) {
        this.columnName = columnName;
        this.columnHeading = columnHeading;
        this.baseType = baseType;
        this.required = required;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public CellValidation(String columnName, String baseType, String columnHeading, boolean required, String pattern) {
        this.columnName = columnName;
        this.columnHeading = columnHeading;
        this.baseType = baseType;
        this.required = required;
        this.pattern = pattern;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public Double getLowerBoundAsDouble() {
        return lowerBound != null ? Double.parseDouble(lowerBound) : null;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public Double getUpperBoundAsDouble() {
        return upperBound != null ? Double.parseDouble(upperBound) : null;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }

    public Map<String, List<String>> getAcceptedValues() {
        return acceptedValues;
    }

    public void setAcceptedValues(Map<String, List<String>> acceptedValues) {
        this.acceptedValues = acceptedValues;
    }

    public String getColumnHeading() {
        return columnHeading;
    }

    public void setColumnHeading(String columnHeading) {
        this.columnHeading = columnHeading;
    }

    public boolean isMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<String> getAcceptedValuesCore() {
        return acceptedValuesCore;
    }

    public void setAcceptedValuesCore(List<String> acceptedValuesCore) {
        this.acceptedValuesCore = acceptedValuesCore;
    }

    @Override
    public String toString() {
        return columnName + " [" + baseType + " | " + required + "]";
    }
}
