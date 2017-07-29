package com.family.donghyunlee.family;

import java.util.StringTokenizer;

/**
 * Created by DONGHYUNLEE on 2017-07-26.
 *
 * Uset Data set
 */

public class User {

    private String id;
    private String userEmail;
    private String userPassword;
    private String userNicname;
    private String userPhone;
    private String userType;
    private int userImage;
    private String userDate;

    User(){

    }

    // Overload
    User(String userEmail, String userPassword,
         String userNicname, String userPhone, String userType, int userImage){
        id = new String(userEmail);
        StringTokenizer stringTokenizer = new StringTokenizer(id, ".");
        this.id = stringTokenizer.nextToken();

        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNicname = userNicname;
        this.userPhone = userPhone;
        this.userType = userType;
        this.userImage = userImage;
    }


    // Getter
    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserNicname() {
        return userNicname;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserType() {
        return userType;
    }

    public int getUserImage() {
        return userImage;
    }

    public String getId() {
        return id;
    }


    // Setter
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserNicname(String userNicname) {
        this.userNicname = userNicname;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

}
