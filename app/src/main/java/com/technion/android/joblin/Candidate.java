package com.technion.android.joblin;

import java.util.Set;

public class Candidate extends User {
    private int age;
    private String jobLocation;
    private int scope;
    private String education;
    private Set<String> skillsSet;
    private String moreInfo;
    private String jobCategory;

    public Candidate(String mail,
                     String name,
                     String lastName,
                     int age,
                     String jobLocation,
                     int scope,
                     String education,
                     Set<String> skillsSet,
                     String moreInfo,
                     String jobCategory) {

        super(mail, name, lastName);
        this.age = age;
        this.jobLocation = jobLocation;
        this.scope = scope;
        this.education = education;
        this.skillsSet = skillsSet;
        this.moreInfo = moreInfo;
        this.jobCategory = jobCategory;
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

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Set<String> getSkillsSet() {
        return skillsSet;
    }

    public void setSkillsSet(Set<String> skillsSet) {
        this.skillsSet = skillsSet;
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
