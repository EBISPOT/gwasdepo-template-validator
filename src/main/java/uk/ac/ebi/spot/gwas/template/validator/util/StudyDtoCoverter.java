package uk.ac.ebi.spot.gwas.template.validator.util;

import uk.ac.ebi.spot.gwas.deposition.dto.StudyDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.Study;

public class StudyDtoCoverter {

    public static StudyDto convert(Study study) {
        return new StudyDto(study.getStudy_tag(),
                null,
                study.getStudy_accession(),
                study.getGenotyping_technology(),
                study.getArray_manufacturer(),
                study.getArray_information(),
                study.getImputation(),
                study.getVariant_count(),
                study.getSample_description(),
                study.getStatistical_model(),
                study.getStudy_description(),
                study.getTrait(),
                study.getEfo_trait(),
                study.getBackground_trait(),
                study.getBackground_efo_trait(),
                study.getSummary_statistics_file(),
                study.getRaw_sumstats_file(),
                study.getChecksum(),
                study.getSummary_statistics_assembly(),
                study.getReadme_file(),
                study.getCohort(),
                study.getCohort_id(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                study.getSs_flag(),
                study.getPooled_flag(),
                study.getGxe_flag());
    }
}
