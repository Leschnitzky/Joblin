package com.technion.android.joblin;

public class User {
    private String email;
    private String name;
    private String lastName;
    static private int totalNumberOfSwipes = 10;
    private int numberOfSwipesLeft = 10;

    User(String email, String name, String lastName) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
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

    public static int getTotalNumberOfSwipes() {
        return totalNumberOfSwipes;
    }

    public static void setTotalNumberOfSwipes(int totalNumberOfSwipes) {
        User.totalNumberOfSwipes = totalNumberOfSwipes;
    }

    public int getNumberOfSwipesLeft() {
        return numberOfSwipesLeft;
    }

    public void setNumberOfSwipesLeft(int numberOfSwipesLeft) {
        this.numberOfSwipesLeft = numberOfSwipesLeft;
    }
}
