package uk.ac.ebi.spot.gwas.template.validator.util;

import uk.ac.ebi.spot.gwas.deposition.dto.AssociationDto;
import uk.ac.ebi.spot.gwas.template.validator.domain.Association;

public class AssociationDtoConverter {

    public static AssociationDto convert(Association association) {
        return new AssociationDto(association.getStudy_tag(),
                association.getHaplotype_id(),
                association.getVariant_id(),
                association.getPvalue(),
                association.getPvalue_text(),
                association.getProxy_variant(),
                association.getEffect_allele(),
                association.getOther_allele(),
                association.getEffect_allele_frequency(),
                association.getOdds_ratio(),
                association.getBeta(),
                association.getBeta_unit(),
                association.getCi_lower(),
                association.getCi_upper(),
                association.getStandard_error());
    }
}
