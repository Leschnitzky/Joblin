package com.technion.android.joblin;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Candidate extends User {
    private Timestamp birthday;
    private String jobLocation;
    private Integer jobRadius;
    private GeoPoint jobPoint;
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
                     Timestamp birthDate,
                     String jobLocation,
                     GeoPoint jobPoint,
                     Integer jobRadius,
                     String scope,
                     String education,
                     List<String> skillsList,
                     String moreInfo,
                     String jobCategory) {

        super(email, name, lastName, imageUrl);
        this.birthday = birthDate;
        this.jobLocation = jobLocation;
        this.jobPoint = jobPoint;
        this.jobRadius = jobRadius;
        this.scope = scope;
        this.education = education;
        this.skillsList = skillsList;
        this.moreInfo = moreInfo;
        this.jobCategory = jobCategory;
    }

    public Candidate(Candidate other) {
        super(other);
        this.birthday = other.birthday;
        this.jobLocation = other.jobLocation;
        this.jobPoint = other.jobPoint;
        this.jobRadius = other.jobRadius;
        this.scope = other.scope;
        this.education = other.education;
        this.skillsList = other.skillsList;
        this.moreInfo = other.moreInfo;
        this.jobCategory = other.jobCategory;
    }

    public Timestamp getBirthday() { return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public GeoPoint getJobPoint() {
        return jobPoint;
    }

    public void setjobPoint(GeoPoint jobPoint) {
        this.jobPoint = jobPoint;
    }

    public Integer getJobRadius() {
        return jobRadius;
    }

    public void setJobRadius(Integer jobRadius) {
        this.jobRadius = jobRadius;
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
