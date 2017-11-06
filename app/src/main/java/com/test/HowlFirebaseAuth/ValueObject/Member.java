package com.test.HowlFirebaseAuth.ValueObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/10/05.
 */

public class Member {
    private String key;
    private String memberEmail;
    private String name;
    private String position;
    private boolean workingFlag;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isWorkingFlag() {
        return workingFlag;
    }

    public void setWorkingFlag(boolean workingFlag) {
        this.workingFlag = workingFlag;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("memberEmail", memberEmail);
        result.put("name", name);
        result.put("position", position);
        result.put("workingFlag", workingFlag);

        return result;
    }

    @Override
    public String toString() {
        return "Member{" +
                "key='" + key + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", workingFlag=" + workingFlag +
                '}';
    }
}
