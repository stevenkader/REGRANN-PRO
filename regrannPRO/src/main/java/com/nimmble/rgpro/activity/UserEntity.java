package com.nimmble.rgpro.activity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 15-08-15.
 */
public class UserEntity implements
        java.io.Serializable {

    private ArrayList<User> userList = new ArrayList<User>();

    public List<User> getUserList() {
        return userList;
    }

    public void setPostList(List<User> userList) {
        this.userList = (ArrayList<User>) userList;
    }
}