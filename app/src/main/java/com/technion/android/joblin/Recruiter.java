package com.technion.android.joblin;

import java.util.List;

public class Recruiter extends User {
    private String jobCategory;
    private String requiredScope;
    private String requiredEducation;
    private String jobLocation;
    private String jobDescription;
    private List<String> requiredSkillsList;

    Recruiter () {}

    public Recruiter(String email,
                     String name,
                     String lastName,
                     String jobCategory,
                     String requiredScope,
                     String jobLocation,
                     String jobDescription,
                     String requiredEducation,
                     List<String> requiredSkillsList) {

        super(email, name, lastName);
        this.jobCategory = jobCategory;
        this.requiredScope = requiredScope;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
        this.requiredEducation = requiredEducation;
        this.requiredSkillsList = requiredSkillsList;
    }

    public Recruiter(Recruiter other) {
        super(other);
        this.jobCategory = other.jobCategory;
        this.requiredScope = other.requiredScope;
        this.requiredEducation = other.requiredEducation;
        this.jobLocation = other.jobLocation;
        this.jobDescription = other.jobDescription;
        this.requiredSkillsList = other.requiredSkillsList;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getRequiredScope() {
        return requiredScope;
    }

    public void setRequiredScope(String requiredScope) {
        this.requiredScope = requiredScope;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getRequiredEducation() {
        return requiredEducation;
    }

    public void setRequiredEducation(String requiredEducation) {
        this.requiredEducation = requiredEducation;
    }

    public List<String> getRequiredSkillsList() {
        return requiredSkillsList;
    }

    public void setRequiredSkillsList(List<String> requiredSkillsList) {
        this.requiredSkillsList = requiredSkillsList;
    }
}
