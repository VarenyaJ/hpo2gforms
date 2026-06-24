package org.monarchinitiative.hpo2gforms.proposal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProposalYamlReaderTest {

    @Test
    void parseProposalTerm() {
        List<ProposalTerm> terms = ProposalYamlReader.parse(List.of(
                "terms:",
                "  - id: P001",
                "    preferred_label: Fetal test term",
                "    status_note: Ready",
                "    synonyms:",
                "      - Test synonym",
                "    definition: |-",
                "      First sentence.",
                "      Second sentence.",
                "    pmids:",
                "      - PMID:123",
                "    parent: Abnormal fetal morphology",
                "    disease_associations:",
                "      - Example disease - OMIM:1",
                "    nano_attribution: https://orcid.org/0000-0002-5177-8109",
                "    notes: Existing term"
        ));

        assertEquals(1, terms.size());
        ProposalTerm term = terms.getFirst();
        assertEquals("P001", term.id());
        assertEquals("Fetal test term", term.preferredLabel());
        assertEquals("Test synonym", term.synonyms().getFirst());
        assertEquals("First sentence.\nSecond sentence.", term.definition());
        assertEquals("PMID:123", term.pmids().getFirst());
    }
}
