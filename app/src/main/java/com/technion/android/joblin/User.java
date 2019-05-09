package com.technion.android.joblin;

public class User {
    private String mail;
    private String name;
    private String lastName;

    public User(String mail, String name, String lastName) {
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
}
