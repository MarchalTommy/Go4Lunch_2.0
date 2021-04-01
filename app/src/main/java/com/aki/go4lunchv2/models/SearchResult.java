package com.aki.go4lunchv2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    @SerializedName("candidates")
    @Expose
    private List<Candidate> candidates = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public SearchResult withCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SearchResult withStatus(String status) {
        this.status = status;
        return this;
    }

}
