package com.technion.android.joblin;

public class User {

    private String email;
    private String name;
    private String lastName;
    private String imageUrl;

    private long maxDistance;

    static long totalNumberOfSwipes = 10;
    private long numberOfSwipesLeft = 10;

    User() {}

    User(String email, String name, String lastName, String imageUrl, long maxDistance) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.maxDistance = maxDistance;
    }

    public User(User other) {
        this.email = other.email;
        this.name = other.name;
        this.lastName = other.lastName;
        this.numberOfSwipesLeft = other.numberOfSwipesLeft;
        this.imageUrl = other.imageUrl;
        this.maxDistance = other.maxDistance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean reduceNumberOfSwipesLeft() {
        if(numberOfSwipesLeft == 0) {
            return false;
        }
        numberOfSwipesLeft -= 1;
        return true;
    }

    public void restoreNumberOfSwipesLeft() {
        numberOfSwipesLeft = totalNumberOfSwipes;
    }

    public static long getTotalNumberOfSwipes() {
        return totalNumberOfSwipes;
    }

    public static void setTotalNumberOfSwipes(long totalNumberOfSwipes) {
        User.totalNumberOfSwipes = totalNumberOfSwipes;
    }

    public long getNumberOfSwipesLeft() {
        return numberOfSwipesLeft;
    }

    public void setNumberOfSwipesLeft(long  numberOfSwipesLeft) {
        this.numberOfSwipesLeft = numberOfSwipesLeft;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(long maxDistance) {
        this.maxDistance = maxDistance;
    }
}
