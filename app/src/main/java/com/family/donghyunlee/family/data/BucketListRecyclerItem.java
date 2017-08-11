package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

/**
    리사이클러뷰에 뿌려질 데이터 아이템
 */
public class BucketListRecyclerItem {

    private String question;
    private String answerHint;

    public BucketListRecyclerItem() {
    }

    public BucketListRecyclerItem(String question, String answerHint) {
        this.question = question;
        this.answerHint = answerHint;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswerHint() {
        return answerHint;
    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswerHint(String answer) {
        this.answerHint = answer;
    }
}
