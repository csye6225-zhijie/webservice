package com.csye6225.webservice.Model.VO;

public class UserVO {
    private final String id;
    private String first_name;
    private String last_name;
    private final String username;
    private final String account_created;
    private final String account_updated;

    public UserVO(String id, String first_name, String last_name, String username, String account_created, String account_updated) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.account_created = account_created;
        this.account_updated = account_updated;
    }

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getAccount_created() {
        return account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }
}
