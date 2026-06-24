package org.monarchinitiative.hpo2gforms.proposal;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public record ProposalFormItem(ProposalTerm term) {

    public String getQuestionnaireItem() {
        return getQuestionnaireItem(false);
    }

    public String getQuestionnaireItem(boolean addPageBreak) {
        return getPageBreak(addPageBreak)
                + getCoreDetails()
                + getDefinition()
                + getReviewHeader()
                + getGrid()
                + getComments()
                + getSupportingDetails();
    }

    private String getPageBreak(boolean addPageBreak) {
        if (!addPageBreak) {
            return "";
        }
        return """
        var pageBreakItem = form.addPageBreakItem();
        pageBreakItem.setTitle('%s');
        """.formatted(appsScriptSingleQuoted(termTitle()));
    }

    private String getCoreDetails() {
        List<String> items = new ArrayList<>();
        items.add(field("Status", valueOrNone(term.statusNote())));
        items.add(field("Synonyms", joinInlineOrNone(term.synonyms())));
        items.add(field("Proposed parent", valueOrNone(term.parent())));
        items.add(field("PMIDs", joinInlineOrNone(term.pmids())));

        return """
                 var sectionHeaderItem = form.addSectionHeaderItem();
                 sectionHeaderItem.setTitle('Core details for %s')
                         .setHelpText('%s');
                 """.formatted(appsScriptSingleQuoted(termTitle()), appsScriptSingleQuoted(String.join("\n\n", items)));
    }

    private String getDefinition() {
        return """
        var definitionItem = form.addSectionHeaderItem();
        definitionItem.setTitle('Definition')
                .setHelpText('%s');
        """.formatted(appsScriptSingleQuoted(valueOrNone(term.definition())));
    }

    private String getReviewHeader() {
        return """
        var reviewItem = form.addSectionHeaderItem();
        reviewItem.setTitle('Review')
                .setHelpText('For each row, choose the option that best reflects your assessment. You may leave rows blank if you are unsure or prefer to return later.');
        """;
    }

    private String getGrid() {
        return """
        var gridItem = form.addGridItem();
        gridItem.setTitle('[%s] %s')
                .setRows(['Term label', 'Synonyms', 'Parent(s)', 'Definition', 'PMIDs', 'Disease associations'])
                .setColumns(['Accept', 'Reject', 'Revise', 'n/a']);
        """.formatted(appsScriptSingleQuoted(term.id()), appsScriptSingleQuoted(term.preferredLabel()));
    }

    private String getComments() {
        return """
        var commentsHeaderItem = form.addSectionHeaderItem();
        commentsHeaderItem.setTitle('Comments')
                .setHelpText('Optional: suggest edits, missing synonyms, parent changes, definition changes, PMIDs, or disease associations.');
        var commentItem = form.addParagraphTextItem();
        commentItem.setTitle('Comments for [%s] %s');
        """.formatted(appsScriptSingleQuoted(term.id()), appsScriptSingleQuoted(term.preferredLabel()));
    }

    private String getSupportingDetails() {
        List<String> items = new ArrayList<>();
        items.add(field("Disease associations", bulletListOrNone(term.diseaseAssociations())));
        items.add(field("Notes", valueOrNone(term.notes())));

        return """
        var supportingDetailsItem = form.addSectionHeaderItem();
        supportingDetailsItem.setTitle('Supporting details for [%s] %s')
                .setHelpText('%s');
        """.formatted(
                appsScriptSingleQuoted(term.id()),
                appsScriptSingleQuoted(term.preferredLabel()),
                appsScriptSingleQuoted(String.join("\n\n", items)));
    }

    private String termTitle() {
        return "[%s] %s".formatted(
                normalizeInline(term.id()),
                normalizeInline(term.preferredLabel()));
    }

    static String appsScriptSingleQuoted(String value) {
        return normalizeMultiline(value).replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    static String normalizeInline(String value) {
        return normalizeAscii(value).replaceAll("\\s+", " ").strip();
    }

    static String normalizeMultiline(String value) {
        return normalizeAscii(value).replaceAll("[ \\t]+", " ").replaceAll(" *\\n *", "\n").strip();
    }

    private static String normalizeAscii(String value) {
        if (value == null) {
            return "";
        }
        String text = value.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2212', '-')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201C', '"')
                .replace('\u201D', '"')
                .replace('\u00A0', ' ')
                .replace("\u2265", ">=")
                .replace("\u2264", "<=")
                .replace("\u00D7", "x")
                .replace("\u2192", "->")
                .replace("\u00B1", "+/-");
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").replaceAll("[^\\x00-\\x7F]", "");
    }

    private static String field(String label, String value) {
        return "%s:\n%s".formatted(normalizeInline(label), normalizeMultiline(value));
    }

    private static String joinInlineOrNone(List<String> values) {
        if (values.isEmpty()) {
            return "None";
        }
        return values.stream()
                .map(ProposalFormItem::normalizeInline)
                .filter(value -> !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("None");
    }

    private static String bulletListOrNone(List<String> values) {
        if (values.isEmpty()) {
            return "None";
        }
        return values.stream()
                .map(ProposalFormItem::normalizeInline)
                .filter(value -> !value.isBlank())
                .map(value -> "- " + value)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("None");
    }

    private static String valueOrNone(String value) {
        String normalized = normalizeMultiline(value);
        return normalized.isBlank() ? "None" : normalized;
    }
}
