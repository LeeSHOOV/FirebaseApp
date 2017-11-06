package com.test.HowlFirebaseAuth.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.HowlFirebaseAuth.ValueObject.Member;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/10/30.
 */

public class UserService {
    private DatabaseReference databaseRef;

    public UserService() {
        this.databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void createMember(Member member) {
        databaseRef.child("members")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(member);
    }

    public DatabaseReference getUserByEmail(String userUid) {
        return databaseRef.child("members").child(userUid);
    }

    public void updateMember(Member member) {
        //DB key
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //searchMember.getKey();
        //修正したMemberのData
        Map<String, Object> memberValues = member.toMap();
        //FirebaseDBにDataを入れるプロセス
        Map<String, Object> childUpdates = new HashMap<>();
        //   /members/-Kw98VPQ1hhHkKQEFSmh/{memberEmail : "ascomjapan@gmail.com", workingFlag = TRUE}
        childUpdates.put("/members/" + key, memberValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public void deleteMember(String key) {

    }
}
