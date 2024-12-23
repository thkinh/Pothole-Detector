package com.example.doan.model;

public class AppUser {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private String date_created;
    private Integer distanceTraveled;

    public AppUser(){}

    public AppUser(String username, String email, String password, Integer distanceTraveled, Integer id){
        this.username = username;
        this.email =email;
        this.password = password;
        this.distanceTraveled = distanceTraveled;
        this.id = id;
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

    public Integer getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(Integer distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }
//    public void setDate_created(Date date_created) {
//        this.date_created = date_created;
//    }

}
