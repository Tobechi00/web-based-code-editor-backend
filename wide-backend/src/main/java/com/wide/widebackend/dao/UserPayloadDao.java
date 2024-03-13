package com.wide.widebackend.dao;

public class UserPayloadDao {

    private String username;
    private String firstName;
    private String lastName;
    private Long id;
    private String token;

    public UserPayloadDao(){}

    public UserPayloadDao(String username, String firstName, String lastName, Long id, String token) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserPayloadDao{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", id='" + id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
