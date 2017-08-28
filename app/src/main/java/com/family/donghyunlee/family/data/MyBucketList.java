package com.family.donghyunlee.family.data;

import java.util.List;

/**
 * Created by DONGHYUNLEE on 2017-08-11.
 */

/**
 * DB에 올릴 DATA
*/
 public class MyBucketList {


    private List<String> answer;
    private String date;
    private List<String> question;
    private String userId;


    public MyBucketList(){

    }
    public MyBucketList(String userId, String date, List<String> answer, List<String> question, int color) {
        this.userId = userId;
        this.date = date;
        this.answer = answer;
        this.question = question;
    }


    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public List<String> getQuestion() {
        return question;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public void setQuestion(List<String> question) {
        this.question = question;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
