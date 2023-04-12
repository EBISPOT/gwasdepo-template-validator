package uk.ac.ebi.spot.gwas.template.validator.util;

import uk.ac.ebi.spot.gwas.deposition.dto.SampleDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.Sample;

public class SampleDtoConverter {

    public static SampleDto convert(Sample sample) {
        return new SampleDto(sample.getStudy_tag(),
                sample.getStage(),
                sample.getSize(),
                sample.getCases(),
                sample.getControls(),
                sample.getSample_description(),
                sample.getAncestry_category(),
                sample.getAncestry(),
                sample.getAncestry_description(),
                sample.getCountry_recruitement(),
                sample.getCase_control_study(),
                sample.getAncestry_method());
    }

}
