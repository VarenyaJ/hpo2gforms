package org.monarchinitiative.hpo2gforms.proposal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProposalGoogleFormTest {

    @Test
    void generatedFormAddsIntroPageBeforeTerms() {
        ProposalTerm first = term("P001", "First fetal term");
        ProposalTerm second = term("P002", "Second fetal term");

        String form = new ProposalGoogleForm(List.of(first, second), "Review", 1).getFunction();

        assertEquals(2, count(form, "form.addPageBreakItem()"));
        assertEquals(2, count(form, ".setRequired(true)"));
        assertTrue(form.indexOf("Study information") < form.indexOf("Name:"));
        assertTrue(form.indexOf("Name:") < form.indexOf("Begin term review"));
        assertTrue(form.indexOf("Begin term review") < form.indexOf("[P001] First fetal term"));
        assertTrue(form.indexOf("[P001] First fetal term") < form.lastIndexOf("form.addPageBreakItem()"));
        assertTrue(form.lastIndexOf("form.addPageBreakItem()") < form.indexOf("[P002] Second fetal term"));
        assertTrue(form.contains("There is a total of 43 terms to review"));
        assertTrue(form.contains("Review one proposed HPO term per page."));
        assertTrue(form.contains("Term review grids are optional"));
    }

    private static ProposalTerm term(String id, String label) {
        ProposalTerm term = new ProposalTerm();
        term.setId(id);
        term.setPreferredLabel(label);
        term.setDefinition("A definition.");
        return term;
    }

    private static int count(String value, String needle) {
        int count = 0;
        int index = value.indexOf(needle);
        while (index >= 0) {
            count++;
            index = value.indexOf(needle, index + needle.length());
        }
        return count;
    }
}
