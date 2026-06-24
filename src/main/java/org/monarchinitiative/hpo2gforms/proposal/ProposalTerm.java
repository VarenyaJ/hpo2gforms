package org.monarchinitiative.hpo2gforms.proposal;

import java.util.ArrayList;
import java.util.List;

public final class ProposalTerm {
    private String id = "";
    private String preferredLabel = "";
    private String statusNote = "";
    private final List<String> synonyms = new ArrayList<>();
    private String definition = "";
    private final List<String> pmids = new ArrayList<>();
    private String parent = "";
    private final List<String> diseaseAssociations = new ArrayList<>();
    private String nanoAttribution = "";
    private String notes = "";

    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = valueOrBlank(id);
    }

    public String preferredLabel() {
        return preferredLabel;
    }

    public void setPreferredLabel(String preferredLabel) {
        this.preferredLabel = valueOrBlank(preferredLabel);
    }

    public String statusNote() {
        return statusNote;
    }

    public void setStatusNote(String statusNote) {
        this.statusNote = valueOrBlank(statusNote);
    }

    public List<String> synonyms() {
        return synonyms;
    }

    public String definition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = valueOrBlank(definition);
    }

    public List<String> pmids() {
        return pmids;
    }

    public String parent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = valueOrBlank(parent);
    }

    public List<String> diseaseAssociations() {
        return diseaseAssociations;
    }

    public String nanoAttribution() {
        return nanoAttribution;
    }

    public void setNanoAttribution(String nanoAttribution) {
        this.nanoAttribution = valueOrBlank(nanoAttribution);
    }

    public String notes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = valueOrBlank(notes);
    }

    public void validate() {
        if (id.isBlank()) {
            throw new IllegalArgumentException("Proposal term is missing id");
        }
        if (preferredLabel.isBlank()) {
            throw new IllegalArgumentException("Proposal term " + id + " is missing preferred_label");
        }
    }

    private static String valueOrBlank(String value) {
        return value == null ? "" : value.strip();
    }
}
