package com.family.donghyunlee.family.data;

/**
 * Created by DONGHYUNLEE on 2017-08-19.
 */

public class CommentCountItem {

    private int likeCnt;

    CommentCountItem(){

    }

    public CommentCountItem(int likeCnt) {
        this.likeCnt = likeCnt;
    }

    public int getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(int likeCnt) {
        this.likeCnt = likeCnt;
    }
}
