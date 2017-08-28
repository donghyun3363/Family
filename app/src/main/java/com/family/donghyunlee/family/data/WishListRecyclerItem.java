package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-09.
 */

public class WishListRecyclerItem {

    private String wishListKey;
    private String imgProfilePath;
    private String nickName;
    private String date;
    private String question;
    private String answer;
    private String userId;
    private int color;
    // 0 일반, 1 진행, 2 완료
    private boolean share;
    private String bucketKeyRegistered;
    public WishListRecyclerItem() {

    }
    public WishListRecyclerItem(String wishListKey, String userId, String imgProfilePath, String nickName, String date, String question, String answer, int color) {
        this.userId = userId;
        this.imgProfilePath = imgProfilePath;
        this.nickName = nickName;
        this.date = date;
        this.question = question;
        this.answer = answer;
        this.color = color;
        this.wishListKey = wishListKey;
    }

    public boolean getShare() {
        return share;
    }
    public void setShare(boolean share) {
        this.share = share;
    }

    public String getWishListKey() {
        return wishListKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getBucketKeyRegistered() {
        return bucketKeyRegistered;
    }

    public int getColor() {
        return color;
    }

    public String getImgProfilePath() {
        return imgProfilePath;
    }

    public String getNickName() {
        return nickName;
    }

    public String getDate() {
        return date;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }


    public void setBucketKeyRegistered(String bucketKeyRegistered) {
        this.bucketKeyRegistered = bucketKeyRegistered;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWishListKey(String wishListKey) {
        this.wishListKey = wishListKey;
    }

    public void setImgProfilePath(String imgProfilePath) {
        this.imgProfilePath = imgProfilePath;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
