package uk.ac.ebi.spot.gwas.template.validator.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Association {

    private String study_tag;

    private String haplotype_id;

    private String variant_id;

    private String pvalue;

    private String pvalue_text;

    private String proxy_variant;

    private String effect_allele;

    private String other_allele;

    private Double effect_allele_frequency;

    private Double odds_ratio;

    private Double beta;

    private String beta_unit;

    private Double ci_lower;

    private Double ci_upper;

    private Double standard_error;

    public Association() {

    }

    public String getStudy_tag() {
        return study_tag;
    }

    public void setStudy_tag(String study_tag) {
        this.study_tag = study_tag;
    }

    public String getHaplotype_id() {
        return haplotype_id;
    }

    public void setHaplotype_id(String haplotype_id) {
        this.haplotype_id = haplotype_id;
    }

    public String getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(String variant_id) {
        this.variant_id = variant_id;
    }

    public String getPvalue() {
        return pvalue;
    }

    public void setPvalue(String pvalue) {
        this.pvalue = pvalue;
    }

    public String getPvalue_text() {
        return pvalue_text;
    }

    public void setPvalue_text(String pvalue_text) {
        this.pvalue_text = pvalue_text;
    }

    public String getProxy_variant() {
        return proxy_variant;
    }

    public void setProxy_variant(String proxy_variant) {
        this.proxy_variant = proxy_variant;
    }

    public String getEffect_allele() {
        return effect_allele;
    }

    public void setEffect_allele(String effect_allele) {
        this.effect_allele = effect_allele;
    }

    public String getOther_allele() {
        return other_allele;
    }

    public void setOther_allele(String other_allele) {
        this.other_allele = other_allele;
    }

    public Double getEffect_allele_frequency() {
        return effect_allele_frequency;
    }

    public void setEffect_allele_frequency(Double effect_allele_frequency) {
        this.effect_allele_frequency = effect_allele_frequency;
    }

    public Double getOdds_ratio() {
        return odds_ratio;
    }

    public void setOdds_ratio(Double odds_ratio) {
        this.odds_ratio = odds_ratio;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public String getBeta_unit() {
        return beta_unit;
    }

    public void setBeta_unit(String beta_unit) {
        this.beta_unit = beta_unit;
    }

    public Double getCi_lower() {
        return ci_lower;
    }

    public void setCi_lower(Double ci_lower) {
        this.ci_lower = ci_lower;
    }

    public Double getCi_upper() {
        return ci_upper;
    }

    public void setCi_upper(Double ci_upper) {
        this.ci_upper = ci_upper;
    }

    public Double getStandard_error() {
        return standard_error;
    }

    public void setStandard_error(Double standard_error) {
        this.standard_error = standard_error;
    }
}
