package com.onoma.go4lunch.model;

import android.net.Uri;

import java.util.Objects;

public class User {

    private String name;
    private String email;
    private Uri photoUrl;

    public User(String name, String email, Uri photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) && email.equals(user.email) && photoUrl.equals(user.photoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, photoUrl);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl=" + photoUrl +
                '}';
    }
}
