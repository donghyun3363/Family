package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-09.
 */

public class WishListRecyclerItem {

    private String imgProfilePath;
    private String nickName;
    private String date;
    private String question;
    private String answer;


    public WishListRecyclerItem() {

    }
    public WishListRecyclerItem(String imgProfilePath, String nickName, String date, String question, String answer) {
        this.imgProfilePath = imgProfilePath;
        this.nickName = nickName;
        this.date = date;
        this.question = question;
        this.answer = answer;
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
}
