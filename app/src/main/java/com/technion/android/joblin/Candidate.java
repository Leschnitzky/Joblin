package com.technion.android.joblin;

import java.util.List;
import java.util.Set;

public class Candidate extends User {
    private int age;
    private String jobLocation;
    private String scope;
    private String education;
    private List<String> skillsList;
    private String moreInfo;
    private String jobCategory;

    public Candidate() {}

    public Candidate(String email,
                     String name,
                     String lastName,
                     String imageUrl,
                     int age,
                     String jobLocation,
                     String scope,
                     String education,
                     List<String> skillsList,
                     String moreInfo,
                     String jobCategory) {

        super(email, name, lastName, imageUrl);
        this.age = age;
        this.jobLocation = jobLocation;
        this.scope = scope;
        this.education = education;
        this.skillsList = skillsList;
        this.moreInfo = moreInfo;
        this.jobCategory = jobCategory;
    }

    public Candidate(Candidate other) {
        super(other);
        this.age = other.age;
        this.jobLocation = other.jobLocation;
        this.scope = other.scope;
        this.education = other.education;
        this.skillsList = other.skillsList;
        this.moreInfo = other.moreInfo;
        this.jobCategory = other.jobCategory;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<String> getSkillsList() {
        return skillsList;
    }

    public void setSkillsList(List<String> skillsList) {
        this.skillsList = skillsList;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }
}
