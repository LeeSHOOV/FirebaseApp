package com.test.HowlFirebaseAuth.ValueObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/10/05.
 */

public class WorkInfo implements Serializable{
    // notificationID  = memberEmail + createOnWorkDate
    private String key;
    private String name;
    // onWork Value Data
    private Date createOnWorkDate;
    // OffWork Value Data
    private Date createOffWorkDate;
    // walkTime
    private int workingTime;
    private String memberEmail;
    private String onOffWork;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateOnWorkDate() {
        return createOnWorkDate;
    }

    public void setCreateOnWorkDate(Date createOnWorkDate) {
        this.createOnWorkDate = createOnWorkDate;
    }

    public Date getCreateOffWorkDate() {
        return createOffWorkDate;
    }

    public void setCreateOffWorkDate(Date createOffWorkDate) {
        this.createOffWorkDate = createOffWorkDate;
    }

    public int getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(int workingTime) {
        this.workingTime = workingTime;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getOnOffWork() {
        return onOffWork;
    }

    public void setOnOffWork(String onOffWork) {
        this.onOffWork = onOffWork;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("name", name);
        result.put("createOnWorkDate", createOnWorkDate);
        result.put("createOffWorkDate", createOffWorkDate);
        result.put("workingTime", workingTime);
        result.put("memberEmail", memberEmail);

        return result;
    }

    @Override
    public String toString() {
        return "WorkInfo{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", createOnWorkDate=" + createOnWorkDate +
                ", createOffWorkDate=" + createOffWorkDate +
                ", workingTime=" + workingTime +
                ", memberEmail='" + memberEmail + '\'' +
                '}';
    }
}
