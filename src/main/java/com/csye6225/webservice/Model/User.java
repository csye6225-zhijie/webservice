package com.csye6225.webservice.Model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="user")
@GenericGenerator(name = "system-uuid",strategy = "uuid.hex")
public class User {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(length = 32)
    private String id;

    @NotBlank(message = "Your First Name is mandatory")
    private String first_name;

    @NotBlank(message = "Your Last Name is mandatory")
    private String last_name;

    @NotBlank(message = "Your Email is mandatory")
    @Email
    private String username;//email
    private String accountCreated;
    private String accountUpdated;

    @NotBlank(message = "Your Password is mandatory")
    private String password;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String firstName) {
        this.first_name = firstName;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String lastName) {
        this.last_name = lastName;
    }

    public String getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(String accountCreated) {
        this.accountCreated = accountCreated;
    }

    public String getAccountUpdated() {
        return accountUpdated;
    }

    public void setAccountUpdated(String accountUpdated) {
        this.accountUpdated = accountUpdated;
    }

    public String getId() {
        return id;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
