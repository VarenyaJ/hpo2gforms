package org.monarchinitiative.hpo2gforms.proposal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProposalFormItemTest {

    @Test
    void generatedItemEscapesStringsAndIncludesCleanReviewControls() {
        ProposalTerm term = new ProposalTerm();
        term.setId("P001");
        term.setPreferredLabel("Fetal Kleeblattschadel's test");
        term.setDefinition("Line one\nLine two");
        term.setNanoAttribution("https://orcid.org/0000-0002-5177-8109");
        term.pmids().add("PMID:123");
        term.diseaseAssociations().add("Thanatophoric dysplasia type I - OMIM 187600");
        term.diseaseAssociations().add("Thanatophoric dysplasia type II - OMIM 187601");

        String item = new ProposalFormItem(term).getQuestionnaireItem();

        assertTrue(item.contains("form.addSectionHeaderItem()"));
        assertTrue(item.contains("Kleeblattschadel\\'s"));
        assertTrue(item.contains("Line one\\nLine two"));
        assertTrue(item.contains("- Thanatophoric dysplasia type I - OMIM 187600\\n- Thanatophoric dysplasia type II - OMIM 187601"));
        assertTrue(item.contains(".setRows(['Term label', 'Synonyms', 'Parent(s)', 'Definition', 'PMIDs', 'Disease associations'])"));
        assertFalse(item.contains(".setRequired(true)"));
        assertTrue(item.contains("form.addParagraphTextItem()"));
        assertTrue(item.contains("Core details for [P001] Fetal Kleeblattschadel\\'s test"));
        assertTrue(item.contains("definitionItem.setTitle('Definition')"));
        assertTrue(item.contains("reviewItem.setTitle('Review')"));
        assertTrue(item.contains("commentsHeaderItem.setTitle('Comments')"));
        assertTrue(item.contains("Comments for [P001] Fetal Kleeblattschadel\\'s test"));
        assertTrue(item.contains("Supporting details for [P001] Fetal Kleeblattschadel\\'s test"));
        assertAppearsBefore(item, "Core details for [P001]", "definitionItem.setTitle('Definition')");
        assertAppearsBefore(item, "definitionItem.setTitle('Definition')", "reviewItem.setTitle('Review')");
        assertAppearsBefore(item, "reviewItem.setTitle('Review')", "var gridItem = form.addGridItem();");
        assertAppearsBefore(item, "var gridItem = form.addGridItem();", "var commentItem = form.addParagraphTextItem();");
        assertAppearsBefore(item, "var commentItem = form.addParagraphTextItem();", "var supportingDetailsItem = form.addSectionHeaderItem();");
        assertFalse(item.contains("Nano-attribution"));
        assertFalse(item.contains("orcid.org"));
        assertFalse(item.contains("\\\\n"));
    }

    @Test
    void generatedItemCanStartWithPageBreak() {
        ProposalTerm term = new ProposalTerm();
        term.setId("P003");
        term.setPreferredLabel("Fetal page break test");
        term.setDefinition("A definition.");

        String firstItem = new ProposalFormItem(term).getQuestionnaireItem(false);
        String laterItem = new ProposalFormItem(term).getQuestionnaireItem(true);

        assertFalse(firstItem.contains("form.addPageBreakItem()"));
        assertTrue(laterItem.contains("form.addPageBreakItem()"));
        assertAppearsBefore(laterItem, "var pageBreakItem = form.addPageBreakItem();", "var sectionHeaderItem = form.addSectionHeaderItem();");
    }

    @Test
    void generatedItemUsesAsciiTextOnlyAndNoMarkdownBoldMarkers() {
        ProposalTerm term = new ProposalTerm();
        term.setId("P002");
        term.setPreferredLabel("Fetal test term");
        term.setParent("Parent one\nParent two");
        term.setDefinition("A definition with >= threshold, <= threshold, and cafe spelling.");

        String item = new ProposalFormItem(term).getQuestionnaireItem();

        assertTrue(item.contains("Status:\\nNone"));
        assertTrue(item.contains("definitionItem.setTitle('Definition')"));
        assertTrue(item.contains(".setHelpText('A definition with >= threshold, <= threshold, and cafe spelling.')"));
        assertFalse(item.contains("Definition:\\nA definition"));
        assertTrue(item.contains("[P002] Fetal test term"));
        assertTrue(item.contains("Parent one\\nParent two"));
        assertFalse(item.contains("**"));
        assertFalse(item.chars().anyMatch(c -> c > 127));
    }

    private static void assertAppearsBefore(String value, String first, String second) {
        int firstIndex = value.indexOf(first);
        int secondIndex = value.indexOf(second);
        assertTrue(firstIndex >= 0, "Missing expected text: " + first);
        assertTrue(secondIndex >= 0, "Missing expected text: " + second);
        assertTrue(firstIndex < secondIndex, "'%s' should appear before '%s'".formatted(first, second));
    }
}
