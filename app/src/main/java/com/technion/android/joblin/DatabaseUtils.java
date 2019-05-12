package com.technion.android.joblin;

class DatabaseUtils {
    enum Side {
        LEFT, RIGHT
    }

    static final String AGE_KEY = "age";
    static final String EMAIL_KEY = "email";
    static final String JOB_CATEGORY_KEY = "job category";
    static final String JOB_LOCATION_KEY = "job location";
    static final String LAST_NAME_KEY = "last name";
    static final String MORE_INFO_KEY = "more info";

    static final String NAME_KEY = "name";
    static final String SCOPE_KEY = "scope";
    static final String SKILLS_KEY = "skills";
    static final String EDUCATION_KEY = "education";

    static final String REQUIRED_EDUCATION = "required education";
    static final String REQUIRED_SCOPE_KEY = "required scope";
    static final String REQUIRED_SKILLS_KEY = "required skills";
    static final String JOB_DESCRIPTION_KEY = "job description";

    static final String SWIPES_KEY = "swipes";
    static final String SIDE_KEY = "side";
    static final String MATCHES_KEY = "matches";

    static final String candidatesCollectionName = "candidates";
    static final String recruitersCollectionName = "recruiters";
    static final String jobCategoriesCollectionName = "job categories";
    static final String usersCollectionName = "users";

    static final String TAG = "DatabaseAPI";

}
