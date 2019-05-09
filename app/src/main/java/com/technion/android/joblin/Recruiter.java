package com.technion.android.joblin;

public class Recruiter extends User {
    private String jobCategory;
    private int requiredAge;
    private int requiredScope;
    private String jobLocation;
    private String jobDescription;

    public Recruiter(String email,
                     String name,
                     String lastName,
                     String jobCategory,
                     int requiredAge,
                     int requiredScope,
                     String jobLocation,
                     String jobDescription) {

        super(email, name, lastName);
        this.jobCategory = jobCategory;
        this.requiredAge = requiredAge;
        this.requiredScope = requiredScope;
        this.jobLocation = jobLocation;
        this.jobDescription = jobDescription;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public int getRequiredAge() {
        return requiredAge;
    }

    public void setRequiredAge(int requiredAge) {
        this.requiredAge = requiredAge;
    }

    public int getRequiredScope() {
        return requiredScope;
    }

    public void setRequiredScope(int requiredScope) {
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
}
