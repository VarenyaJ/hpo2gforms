package org.monarchinitiative.hpo2gforms.cmd;

import org.monarchinitiative.hpo2gforms.proposal.ProposalGoogleForm;
import org.monarchinitiative.hpo2gforms.proposal.ProposalTerm;
import org.monarchinitiative.hpo2gforms.proposal.ProposalYamlReader;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "proposal-forms",
        aliases = {"proposal_form", "proposalforms"},
        mixinStandardHelpOptions = true,
        description = "Create Google Forms Code from proposed HPO terms in YAML")
public class ProposalFormsCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-i", "--input"}, required = true, description = "path to proposed terms YAML")
    private Path input;

    @CommandLine.Option(names = {"-t", "--title"}, description = "questionnaire title")
    private String title = "Proposed HPO terms";

    @CommandLine.Option(names = {"-o", "--out"}, description = "output filename prefix")
    private String outputPrefix = "proposal_terms";

    @CommandLine.Option(names = {"-x", "--max"}, description = "maximum items per questionnaire (default: ${DEFAULT-VALUE})")
    private Integer maxItemsPerQuestionnaire = 12;

    @Override
    public Integer call() {
        List<ProposalTerm> terms = ProposalYamlReader.read(input);
        List<List<ProposalTerm>> batches = partition(terms, maxItemsPerQuestionnaire);
        for (int i = 0; i < batches.size(); i++) {
            int part = i + 1;
            String fname = "%s_%d.txt".formatted(outputPrefix, part);
            ProposalGoogleForm gform = new ProposalGoogleForm(batches.get(i), title, part);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fname))) {
                bw.write(gform.getFunction());
            } catch (IOException e) {
                throw new PhenolRuntimeException(e);
            }
            System.out.println("Wrote " + fname);
        }
        return 0;
    }

    static List<List<ProposalTerm>> partition(List<ProposalTerm> terms, Integer maxSize) {
        int batchSize = maxSize == null || maxSize < 1 ? terms.size() : maxSize;
        List<List<ProposalTerm>> batches = new ArrayList<>();
        for (int start = 0; start < terms.size(); start += batchSize) {
            batches.add(terms.subList(start, Math.min(start + batchSize, terms.size())));
        }
        return batches;
    }
}
