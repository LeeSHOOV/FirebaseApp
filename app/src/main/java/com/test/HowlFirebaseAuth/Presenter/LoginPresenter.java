package com.test.HowlFirebaseAuth.Presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.test.HowlFirebaseAuth.Activity.MainActivity;
import com.test.HowlFirebaseAuth.ValueObject.Member;
import com.test.HowlFirebaseAuth.model.UserService;

/**
 * Created by admin on 2017/10/24.
 */

public class LoginPresenter implements com.test.HowlFirebaseAuth.LoginContract.Presenter{

    com.test.HowlFirebaseAuth.LoginContract.View mView;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    MainActivity mainActivity;
    UserService userService;

    Member searchMember;

    public LoginPresenter(com.test.HowlFirebaseAuth.LoginContract.View mView, MainActivity mainActivity) {
        //
        this.mView = mView;
        mFirebaseAuth = FirebaseAuth.getInstance();
        this.mainActivity = mainActivity;
        userService = new UserService();

    }

    @Override
    public void subscribe() {
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    //接続状態
                    userService.getUserByEmail(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    searchMember = dataSnapshot.getValue(Member.class);
                                    if(searchMember != null){
                                        //mView.showHomeActivity();
                                    }else{
                                        Member member = new Member();
                                        member.setKey(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        member.setMemberEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        member.setWorkingFlag(false);
                                        userService.createMember(member);
                                        //mView.showHomeActivity();
                                    }

                                    mView.showHomeActivity();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else {
                    //Logoutされた時
                    firebaseAuth.signOut();
                    //activity.showLoginActivity();
                }
            }
        };

        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    public void unSubscribe() {
        if(mFirebaseAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
        }
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(mainActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "SUCCESS");


                            userService.getUserByEmail(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    searchMember = dataSnapshot.getValue(Member.class);
                                    if(searchMember != null){
                                        //mView.showHomeActivity();
                                    }else{
                                        Member member = new Member();
                                        member.setKey(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        member.setMemberEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        member.setWorkingFlag(false);
                                        userService.createMember(member);
                                        //mView.showHomeActivity();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            mainActivity.showFail();
                        }

                        // ...
                    }
                });
    }



}

