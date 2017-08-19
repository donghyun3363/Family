package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-19.
 */

public class TimelineCountItem {
    private int likeCnt;
    private int commentCnt;

    TimelineCountItem(){

    }
    public TimelineCountItem(int likeCnt, int commentCnt) {
        this.likeCnt = likeCnt;
        this.commentCnt = commentCnt;
    }

    public void setLikeCnt(int likeCnt) {
        this.likeCnt = likeCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }

    public int getLikeCnt() {
        return likeCnt;
    }

    public int getCommentCnt() {
        return commentCnt;
    }
}
