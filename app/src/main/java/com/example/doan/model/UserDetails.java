package com.example.doan.model;


import java.sql.Date;

public class UserDetails {


    private Integer id;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String jobTitle;
    private String fullName;

    public UserDetails(){};

    public UserDetails(String phoneNumber, Date dateOfBirth, String jobTitle, String fullName){
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.jobTitle = jobTitle;
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}