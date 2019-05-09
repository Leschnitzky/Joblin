package com.technion.android.joblin;

public class User {
    private String mail;
    private String name;
    private String lastName;
    static private int totalNumberOfSwipes = 10;
    private int numberOfSwipes = 10;

    User(String mail, String name, String lastName) {
        this.mail = mail;
        this.name = name;
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public boolean reduceNumberOfSwipes() {
        if(numberOfSwipes == 0) {
            return false;
        }
        numberOfSwipes -= 1;
        return true;
    }

    public void restoreNumberOfSwipes() {
        numberOfSwipes = totalNumberOfSwipes;
    }

    public static int getTotalNumberOfSwipes() {
        return totalNumberOfSwipes;
    }

    public static void setTotalNumberOfSwipes(int totalNumberOfSwipes) {
        User.totalNumberOfSwipes = totalNumberOfSwipes;
    }

    public int getNumberOfSwipes() {
        return numberOfSwipes;
    }

    public void setNumberOfSwipes(int numberOfSwipes) {
        this.numberOfSwipes = numberOfSwipes;
    }
}
