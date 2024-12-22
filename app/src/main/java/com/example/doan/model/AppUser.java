package com.example.doan.model;

import com.google.gson.annotations.JsonAdapter;

public class AppUser {

    private Integer id;
    private String username;
    private String email;
    private String password;
    private String date_created;

    public AppUser(){}

    public AppUser(String username, String email, String password){
        this.username = username;
        this.email =email;
        this.password = password;
    }

    // Getters and Setters...
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate_created() {
        return date_created;
    }
//    public void setDate_created(Date date_created) {
//        this.date_created = date_created;
//    }

}
