package org.monarchinitiative.hpo2gforms.proposal;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProposalYamlReader {

    private ProposalYamlReader() {
    }

    public static List<ProposalTerm> read(Path path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new PhenolRuntimeException(e);
        }
        return parse(lines);
    }

    static List<ProposalTerm> parse(List<String> lines) {
        List<ProposalTerm> terms = new ArrayList<>();
        ProposalTerm current = null;
        String listField = "";
        String blockField = "";
        StringBuilder block = new StringBuilder();

        for (String rawLine : lines) {
            if (rawLine.isBlank() || rawLine.stripLeading().startsWith("#") || rawLine.strip().equals("terms:")) {
                continue;
            }
            int indent = rawLine.length() - rawLine.stripLeading().length();
            String line = rawLine.strip();

            if (!blockField.isEmpty() && indent > 4) {
                if (!block.isEmpty()) {
                    block.append('\n');
                }
                block.append(rawLine.substring(Math.min(rawLine.length(), 6)));
                continue;
            } else if (!blockField.isEmpty()) {
                setBlockValue(current, blockField, block.toString());
                blockField = "";
                block.setLength(0);
            }

            if (line.startsWith("- id:")) {
                if (current != null) {
                    current.validate();
                    terms.add(current);
                }
                current = new ProposalTerm();
                current.setId(unquote(valueAfterColon(line)));
                listField = "";
                continue;
            }

            if (current == null) {
                continue;
            }

            if (line.endsWith(":")) {
                listField = line.substring(0, line.length() - 1);
                continue;
            }

            if (line.startsWith("- ")) {
                addListValue(current, listField, unquote(line.substring(2)));
                continue;
            }

            int colon = line.indexOf(':');
            if (colon < 0) {
                continue;
            }
            String key = line.substring(0, colon).strip();
            String value = line.substring(colon + 1).strip();
            listField = "";
            if (value.equals("|") || value.equals("|-")) {
                blockField = key;
                block.setLength(0);
            } else {
                setScalarValue(current, key, unquote(value));
            }
        }

        if (!blockField.isEmpty()) {
            setBlockValue(current, blockField, block.toString());
        }
        if (current != null) {
            current.validate();
            terms.add(current);
        }
        return terms;
    }

    private static String valueAfterColon(String line) {
        int colon = line.indexOf(':');
        return colon < 0 ? "" : line.substring(colon + 1).strip();
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static void setScalarValue(ProposalTerm term, String key, String value) {
        switch (key) {
            case "id" -> term.setId(value);
            case "preferred_label" -> term.setPreferredLabel(value);
            case "status_note" -> term.setStatusNote(value);
            case "definition" -> term.setDefinition(value);
            case "parent" -> term.setParent(value);
            case "nano_attribution" -> term.setNanoAttribution(value);
            case "notes" -> term.setNotes(value);
            default -> {
            }
        }
    }

    private static void setBlockValue(ProposalTerm term, String key, String value) {
        if (term == null) {
            return;
        }
        setScalarValue(term, key, value.strip());
    }

    private static void addListValue(ProposalTerm term, String key, String value) {
        switch (key) {
            case "synonyms" -> term.synonyms().add(value);
            case "pmids" -> term.pmids().add(value);
            case "disease_associations" -> term.diseaseAssociations().add(value);
            default -> {
            }
        }
    }
}
