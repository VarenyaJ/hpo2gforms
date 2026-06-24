package org.monarchinitiative.hpo2gforms.proposal;

import java.util.ArrayList;
import java.util.List;

public final class ProposalGoogleForm {
    private static final String DESCRIPTION = "Review one proposed HPO term per page. Term review grids are optional so you can skip unfamiliar terms, browse the full form, and return before submission. Please provide your name and email for response tracking.";

    private final List<String> lines;

    public ProposalGoogleForm(List<ProposalTerm> terms, String title, int part) {
        lines = new ArrayList<>();
        lines.add("function hpo_questionnaire() {");
        addWithIndent("var form = FormApp.create('%s part %d');".formatted(
                ProposalFormItem.appsScriptSingleQuoted(title), part));
        addWithIndent("form.setDescription('%s');".formatted(ProposalFormItem.appsScriptSingleQuoted(DESCRIPTION)));
        lines.add(getNameAndEmail());
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
}
