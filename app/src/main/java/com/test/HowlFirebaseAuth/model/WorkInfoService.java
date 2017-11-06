package com.test.HowlFirebaseAuth.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.HowlFirebaseAuth.ValueObject.Member;
import com.test.HowlFirebaseAuth.ValueObject.WorkInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/10/31.
 */

public class WorkInfoService {
    private DatabaseReference databaseRef;

    public WorkInfoService() {
        this.databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void createWorkInfo(Member member) {
        databaseRef.child("members")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(member);
    }

    public DatabaseReference getUserByEmail(String userUid) {
        return databaseRef.child("members").child(userUid);
    }

    public void updateWorkInfo(WorkInfo searchInfo) {
        String key = searchInfo.getKey();
        FirebaseDatabase.getInstance().getReference().child("workinfo");
        Map<String, Object> infoValues = searchInfo.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/workinfo/"+ key , infoValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public void deleteMember(String key) {

    }
}
