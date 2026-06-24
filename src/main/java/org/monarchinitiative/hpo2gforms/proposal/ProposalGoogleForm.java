package org.monarchinitiative.hpo2gforms.proposal;

import java.util.ArrayList;
import java.util.List;

public final class ProposalGoogleForm {
    private static final String DESCRIPTION = "Prenatal skeletal HPO term E-Delphi review.";
    private static final String STUDY_INFORMATION = """
            Thank you for agreeing to participate in this E-Delphi of prenatal skeletal dysplasia-related HPO terms. HPO terms are standardized terms used to describe clinical and imaging findings in a consistent way, facilitating communication, research, and genotype-phenotype studies.
            
            There is a total of 43 terms to review; the review process may take 30-60 minutes to complete. Review one proposed HPO term per page. Term review grids are optional so you can skip unfamiliar terms, browse the full form, and return before submission.
            
            Participant names and email addresses are being collected solely for the purpose of tracking survey responses and preventing duplicate submissions. All data will be reported in aggregate form.
            """;

    private final List<String> lines;

    public ProposalGoogleForm(List<ProposalTerm> terms, String title, int part) {
        lines = new ArrayList<>();
        lines.add("function hpo_questionnaire() {");
        addWithIndent("var form = FormApp.create('%s part %d');".formatted(
                ProposalFormItem.appsScriptSingleQuoted(title), part));
        addWithIndent("form.setDescription('%s');".formatted(ProposalFormItem.appsScriptSingleQuoted(DESCRIPTION)));
        lines.add(getStudyInformation());
        lines.add(getNameAndEmail());
        lines.add(getBeginTermReviewPageBreak());
        for (int i = 0; i < terms.size(); i++) {
            lines.add(new ProposalFormItem(terms.get(i)).getQuestionnaireItem(i > 0));
        }
        lines.add("}");
    }

    public String getFunction() {
        return String.join("\n", lines);
    }

    private void addWithIndent(String line) {
        lines.add("\t" + line);
    }

    private String getStudyInformation() {
        return """
                var studyInformationItem = form.addSectionHeaderItem();
                studyInformationItem.setTitle('Study information')
                     .setHelpText('%s');
                """.formatted(ProposalFormItem.appsScriptSingleQuoted(STUDY_INFORMATION));
    }

    private String getNameAndEmail() {
        return """
                form.addTextItem()
                    .setTitle('Name:')
                    .setRequired(true);
                var emailValidation = FormApp.createTextValidation()
                     .requireTextMatchesPattern('[^@\\\\s]+@[^@\\\\s]+\\\\.[^@\\\\s]+')
                     .setHelpText('Please enter a valid email address')
                     .build();
                form.addTextItem()
                     .setTitle('Email address:')
                     .setRequired(true)
                     .setValidation(emailValidation);
                """;
    }

    private String getBeginTermReviewPageBreak() {
        return """
                var beginReviewPageBreakItem = form.addPageBreakItem();
                beginReviewPageBreakItem.setTitle('Begin term review');
                """;
    }
}
