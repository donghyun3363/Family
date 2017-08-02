package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-07-26.
 * <p>
 * Uset Data set
 */

public class User {

    private String id;
    private String userEmail;
    private String userPassword;
    private String userNicname;
    private String userPhone;
    private String userType;
    private String userImage;
    private String groupId;

    User() {

    }

    // Overload
    public User(String id, String userEmail, String userPassword,
                String userNicname, String userPhone, String userType, String userImage, String groupId) {
//        id = new String(userEmail);
//        StringTokenizer stringTokenizer = new StringTokenizer(id, ".");
//        this.id = stringTokenizer.nextToken();

        this.id = id;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNicname = userNicname;
        this.userPhone = userPhone;
        this.userType = userType;
        this.userImage = userImage;
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
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

    public String getUserImage() {
        return userImage;
    }

    public String getId() {
        return id;
    }


    // Setter


    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

}
