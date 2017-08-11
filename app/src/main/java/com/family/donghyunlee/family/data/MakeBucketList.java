package com.family.donghyunlee.family.data;

import java.util.List;

/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */

public class MakeBucketList {

    List<String> answer;
    List<String> answerhint;
    int length;

    public MakeBucketList() {

    }

    public MakeBucketList(List<String> answer, List<String> answerhint) {
       this.answer = answer;
        this.answerhint = answerhint;
        this.length = answer.size();
    }

    public List<String> getAnswer() {
        return answer;
    }
    public List<String> getAnswerhint() {
        return answerhint;
    }

    public int getLength() {
        return length;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }
    public void setAnswerhint(List<String> answerhint) {
        this.answerhint = answerhint;
    }
}
