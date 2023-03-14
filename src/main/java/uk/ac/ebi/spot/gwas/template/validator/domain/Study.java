package uk.ac.ebi.spot.gwas.template.validator.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Study {

    private String study_tag;

    private String study_accession;


    private String genotyping_technology;

    private String array_manufacturer;

    private String array_information;

    private Boolean imputation;

    private Integer variant_count;

    private String sample_description;

    private String statistical_model;

    private String study_description;

    private String trait;

    private String efo_trait;

    private String background_trait;

    private String background_efo_trait;

    private String summary_statistics_file;

    private String raw_sumstats_file;

    private String checksum;

    private String summary_statistics_assembly;

    private String readme_file;

    private String cohort;

    private String cohort_id;

    private Boolean ss_flag;

    private Boolean gxe_flag;

    private Boolean pooled_flag;

    private String submissionId;

    private String imputation_panel;

    private String imputation_software;

    private String adjusted_covariates;

    private Double minor_allele_frequency_lower_limit;

    private String sex;

    private String coordinate_system;

    public Study() {

    }

    public String getStudy_tag() {
        return study_tag;
    }

    public void setStudy_tag(String study_tag) {
        this.study_tag = study_tag;
    }

    public String getGenotyping_technology() {
        return genotyping_technology;
    }

    public void setGenotyping_technology(String genotyping_technology) {
        this.genotyping_technology = genotyping_technology;
    }

    public String getArray_manufacturer() {
        return array_manufacturer;
    }

    public void setArray_manufacturer(String array_manufacturer) {
        this.array_manufacturer = array_manufacturer;
    }

    public String getArray_information() {
        return array_information;
    }

    public void setArray_information(String array_information) {
        this.array_information = array_information;
    }

    public Boolean getImputation() {
        return imputation;
    }

    public void setImputation(Boolean imputation) {
        this.imputation = imputation;
    }

    public Integer getVariant_count() {
        return variant_count;
    }

    public void setVariant_count(Integer variant_count) {
        this.variant_count = variant_count;
    }

    public String getStatistical_model() {
        return statistical_model;
    }

    public void setStatistical_model(String statistical_model) {
        this.statistical_model = statistical_model;
    }

    public String getStudy_description() {
        return study_description;
    }

    public void setStudy_description(String study_description) {
        this.study_description = study_description;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getEfo_trait() {
        return efo_trait;
    }

    public void setEfo_trait(String efo_trait) {
        this.efo_trait = efo_trait;
    }

    public String getBackground_trait() {
        return background_trait;
    }

    public void setBackground_trait(String background_trait) {
        this.background_trait = background_trait;
    }

    public String getBackground_efo_trait() {
        return background_efo_trait;
    }

    public void setBackground_efo_trait(String background_efo_trait) {
        this.background_efo_trait = background_efo_trait;
    }

    public String getSummary_statistics_file() {
        return summary_statistics_file;
    }

    public void setSummary_statistics_file(String summary_statistics_file) {
        this.summary_statistics_file = summary_statistics_file;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getSummary_statistics_assembly() {
        return summary_statistics_assembly;
    }

    public void setSummary_statistics_assembly(String summary_statistics_assembly) {
        this.summary_statistics_assembly = summary_statistics_assembly;
    }

    public String getStudy_accession() {
        return study_accession;
    }

    public void setStudy_accession(String study_accession) {
        this.study_accession = study_accession;
    }

    public String getSample_description() {
        return sample_description;
    }

    public void setSample_description(String sample_description) {
        this.sample_description = sample_description;
    }

    public String getCohort() {
        return cohort;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }

    public String getCohort_id() {
        return cohort_id;
    }

    public void setCohort_id(String cohort_id) {
        this.cohort_id = cohort_id;
    }

    public String getReadme_file() {
        return readme_file;
    }

    public void setReadme_file(String readme_file) {
        this.readme_file = readme_file;
    }

    public String getRaw_sumstats_file() {
        return raw_sumstats_file;
    }

    public void setRaw_sumstats_file(String raw_sumstats_file) {
        this.raw_sumstats_file = raw_sumstats_file;
    }

    public Boolean getSs_flag() {
        return ss_flag;
    }

    public void setSs_flag(Boolean ss_flag) {
        this.ss_flag = ss_flag;
    }

    public Boolean getGxe_flag() {
        return gxe_flag;
    }

    public void setGxe_flag(Boolean gxe_flag) {
        this.gxe_flag = gxe_flag;
    }

    public Boolean getPooled_flag() {
        return pooled_flag;
    }

    public void setPooled_flag(Boolean pooled_flag) {
        this.pooled_flag = pooled_flag;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getImputation_panel() {
        return imputation_panel;
    }

    public void setImputation_panel(String imputation_panel) {
        this.imputation_panel = imputation_panel;
    }

    public String getImputation_software() {
        return imputation_software;
    }

    public void setImputation_software(String imputation_software) {
        this.imputation_software = imputation_software;
    }

    public String getAdjusted_covariates() {
        return adjusted_covariates;
    }

    public void setAdjusted_covariates(String adjusted_covariates) {
        this.adjusted_covariates = adjusted_covariates;
    }

    public Double getMinor_allele_frequency_lower_limit() {
        return minor_allele_frequency_lower_limit;
    }

    public void setMinor_allele_frequency_lower_limit(Double minor_allele_frequency_lower_limit) {
        this.minor_allele_frequency_lower_limit = minor_allele_frequency_lower_limit;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCoordinate_system() {
        return coordinate_system;
    }

    public void setCoordinate_system(String coordinate_system) {
        this.coordinate_system = coordinate_system;
    }
}
