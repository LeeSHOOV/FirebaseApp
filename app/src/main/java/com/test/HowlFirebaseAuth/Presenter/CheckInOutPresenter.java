package com.test.HowlFirebaseAuth.Presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.test.HowlFirebaseAuth.Activity.HomeActivity;
import com.test.HowlFirebaseAuth.CheckInOutContract;
import com.test.HowlFirebaseAuth.R;
import com.test.HowlFirebaseAuth.Utility.ProgressDialogTask;
import com.test.HowlFirebaseAuth.ValueObject.Member;
import com.test.HowlFirebaseAuth.ValueObject.WorkInfo;
import com.test.HowlFirebaseAuth.model.UserService;
import com.test.HowlFirebaseAuth.model.WorkInfoService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by admin on 2017/10/30.
 */

public class CheckInOutPresenter {
    CheckInOutContract.View mView;
    UserService userService;
    WorkInfoService workInfoService;
    Context mContext;

    Member searchMember;
    String myEmail;
    String uid;

    Date currentDate;

    private static ProgressDialogTask task;

    public CheckInOutPresenter(CheckInOutContract.View mView, Context context){
        this.mView = mView;
        userService = new UserService();
        workInfoService = new WorkInfoService();
        myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mContext = context;
    }

    public void progressDialogTaskGetInstance(){
        task = new ProgressDialogTask(mContext);
    }


    public void initCheckInOutButtonUIByMemberData(){
        userService.getUserByEmail(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchMember = dataSnapshot.getValue(Member.class);
                try{
                    mView.setCheckInOutButtonUI(searchMember.isWorkingFlag());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateWorkingFlagOfWorkInfo(){
        userService.getUserByEmail(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchMember = dataSnapshot.getValue(Member.class);
                if(searchMember.isWorkingFlag()){
                    searchMember.setWorkingFlag(false);
                }else{
                    searchMember.setWorkingFlag(true);
                }
                mView.setCheckInOutButtonUI(searchMember.isWorkingFlag());

                userService.updateMember(searchMember);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showAlertDialog(){
        userService.getUserByEmail(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchMember = dataSnapshot.getValue(Member.class);
                mView.updateAlertDialogUI(searchMember.isWorkingFlag(), OutputCurrentDateToString()+" "+OutputCurrentTimeToString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showAlertDialog(final String dateMsg){
        userService.getUserByEmail(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchMember = dataSnapshot.getValue(Member.class);
                mView.updateAlertDialogUI(searchMember.isWorkingFlag(), dateMsg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void insertUpdateWorkInfo(){
        progressDialogTaskGetInstance();
        task.execute();
        userService.getUserByEmail(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchMember = dataSnapshot.getValue(Member.class);

                if(searchMember.isWorkingFlag()){
                    //退勤
                    //退勤時間登録
                    FirebaseDatabase.getInstance().getReference().child("workinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<WorkInfo> infoList = new ArrayList<>();
                            infoList.clear();
                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                WorkInfo info = snapshot.getValue(WorkInfo.class);
                                if(info.getMemberEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    infoList.add(info);
                                }
                            }

                            if(infoList.isEmpty()) { }else{ }
                            WorkInfo searchInfo = infoList.get(infoList.size()-1);

                            //SETTING WORKINFO DATA
                            searchInfo.setCreateOffWorkDate(outputCurrentDate());
                            searchInfo.setWorkingTime(
                                    //per minute
                                    (int)(searchInfo.getCreateOffWorkDate().getTime() - searchInfo.getCreateOnWorkDate().getTime() ) / (60 * 1000)
                            );

                            workInfoService.updateWorkInfo(searchInfo);
                            task.dismissDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    //出勤
                    //出勤時間登録

                    //SETTING WORKINFO DATA
                    WorkInfo info = new WorkInfo();
                    info.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    info.setCreateOnWorkDate(outputCurrentDate());
                    info.setMemberEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    FirebaseDatabase.getInstance().getReference().child("workinfo").push().setValue(info)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("workinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            List<WorkInfo> infoList = new ArrayList<>();
                                            infoList.clear();

                                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                WorkInfo info = snapshot.getValue(WorkInfo.class);
                                                if(info.getMemberEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                                    info.setKey(snapshot.getKey());
                                                    infoList.add(info);
                                                }
                                            }
                                            if(infoList.isEmpty()) { }else{ }
                                            WorkInfo searchInfo = infoList.get(infoList.size()-1);
                                            workInfoService.updateWorkInfo(searchInfo);

                                            task.dismissDialog();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void insertUpdateWorkInfo(final Date date){
        progressDialogTaskGetInstance();
        task.execute();
        userService.getUserByEmail(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchMember = dataSnapshot.getValue(Member.class);

                if(searchMember.isWorkingFlag()){
                    //退勤
                    //退勤時間登録
                    FirebaseDatabase.getInstance().getReference().child("workinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<WorkInfo> infoList = new ArrayList<>();
                            infoList.clear();
                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                WorkInfo info = snapshot.getValue(WorkInfo.class);
                                if(info.getMemberEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    infoList.add(info);
                                }
                            }

                            if(infoList.isEmpty()) { }else{ }
                            WorkInfo searchInfo = infoList.get(infoList.size()-1);

                            //SETTING WORKINFO DATA
                            searchInfo.setCreateOffWorkDate(date);
                            searchInfo.setWorkingTime(
                                    //per minute
                                    (int)(searchInfo.getCreateOffWorkDate().getTime() - searchInfo.getCreateOnWorkDate().getTime() ) / (60 * 1000)
                            );

                            workInfoService.updateWorkInfo(searchInfo);
                            task.dismissDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    //出勤
                    //出勤時間登録

                    //SETTING WORKINFO DATA
                    WorkInfo info = new WorkInfo();
                    info.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    info.setCreateOnWorkDate(date);
                    info.setMemberEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    FirebaseDatabase.getInstance().getReference().child("workinfo").push().setValue(info)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference().child("workinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            List<WorkInfo> infoList = new ArrayList<>();
                                            infoList.clear();

                                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                                                WorkInfo info = snapshot.getValue(WorkInfo.class);
                                                if(info.getMemberEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                                    info.setKey(snapshot.getKey());
                                                    infoList.add(info);
                                                }
                                            }
                                            if(infoList.isEmpty()) { }else{ }
                                            WorkInfo searchInfo = infoList.get(infoList.size()-1);
                                            workInfoService.updateWorkInfo(searchInfo);
                                            task.dismissDialog();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateWorkDateOfWorkInfo(WorkInfo argWorkInfo, String dateToString){
        progressDialogTaskGetInstance();
        task.execute();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", new Locale("en", "US"));
        Date date = new Date();
        try{
            date = dateFormat.parse(dateToString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        //勤務時間を計算
        if(argWorkInfo.getOnOffWork().equals("onwork")){
            argWorkInfo.setCreateOnWorkDate(date);

        }else if(argWorkInfo.getOnOffWork().equals("offwork")){
            argWorkInfo.setCreateOffWorkDate(date);
        }

        if(argWorkInfo.getCreateOffWorkDate() != null){
            int workingTime = (int)(argWorkInfo.getCreateOffWorkDate().getTime() - argWorkInfo.getCreateOnWorkDate().getTime()) / (60 * 1000);
            argWorkInfo.setWorkingTime(workingTime);
        }

        workInfoService.updateWorkInfo(argWorkInfo);
        task.dismissDialog();
    }

    public void displayImageDialog(){

        final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://howlfirebaseauth-24336.appspot.com");
        final StorageReference riversRef = storageReference.child("imageList/checkIn/dog.jpg");
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Image 적용
                userService.getUserByEmail(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Member selectedMember = dataSnapshot.getValue(Member.class);

                        Uri selectedImageUri;

                        Uri imageURI[] = new Uri[7];
                        imageURI[0] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckIn%2Fdog.jpg?alt=media&token=07a4a407-2d8c-412b-8d39-ce151482e33b");
                        imageURI[1] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckIn%2F036-pictogram-free.jpg?alt=media&token=75137f3e-3b98-4a70-82c3-532e883c2f8f");
                        imageURI[2] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckIn%2Fshukkin-tate.jpg?alt=media&token=c7723091-897d-47af-b0a1-cb5f4ecbf408");

                        imageURI[3] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckOut%2Fdog2.jpg?alt=media&token=f042b32f-69d9-4ac7-ae13-955a04f3bcca");
                        imageURI[4] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckOut%2F085.png?alt=media&token=ccf8edea-bd79-4a11-8c9d-cd764152a0a4");
                        imageURI[5] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckOut%2F20150713114655.png?alt=media&token=befb5bdb-0730-4804-83f8-414bd1281762");
                        imageURI[6] = Uri.parse("https://firebasestorage.googleapis.com/v0/b/howlfirebaseauth-24336.appspot.com/o/imageList%2FcheckOut%2F20160712.2.02.jpg?alt=media&token=a2449be9-d843-4246-acc8-736d83f9dc49");

                        Random random = new Random();
                        int x = random.nextInt(3);
                        int y = random.nextInt(4) + 3;
                        if(selectedMember.isWorkingFlag()){
                            //退勤
                            selectedImageUri = imageURI[x];
                        }else{
                            //出勤
                            selectedImageUri = imageURI[y];
                        }

                        mView.displayImageDialog(selectedImageUri);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }


    public String OutputCurrentDateToString(){
        String date = null;

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        date = dateFormat.format(currentDate);

        return date;
    }

    public String OutputCurrentTimeToString(){
        String time = null;

        Date currentDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        time = timeFormat.format(currentDate);

        return time;
    }

    public Date outputCurrentDate(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        String dateString = OutputCurrentDateToString()+" "+OutputCurrentTimeToString();

        try{
            date = dateFormat.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

}
