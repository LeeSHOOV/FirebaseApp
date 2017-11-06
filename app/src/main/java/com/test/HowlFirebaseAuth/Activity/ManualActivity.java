package com.test.HowlFirebaseAuth.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.test.HowlFirebaseAuth.CheckInOutContract;
import com.test.HowlFirebaseAuth.Presenter.CheckInOutPresenter;
import com.test.HowlFirebaseAuth.R;
import com.test.HowlFirebaseAuth.ValueObject.Member;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ManualActivity extends AppCompatActivity implements CheckInOutContract.View{

    private Button manualCheckInOutButton;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog.Builder imageDialogBuilder;
    // FIXME: この辺りのメンバ変数についても頭にmをつけた形で名前を修正してください
    private DatePicker datePicker;
    private TimePicker timePicker;

    private Date date;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;

    private String datePickerMsg;
    private String timePickerMsg;

    private Member searchMember;

    private CheckInOutPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        //Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();


        presenter = new CheckInOutPresenter(this, this);
        presenter.initCheckInOutButtonUIByMemberData();
        init();

        manualCheckInOutButton = (Button) findViewById(R.id.manualCheckInOut_button);
        manualCheckInOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showAlertDialog(datePickerMsg + " " +timePickerMsg);
            }
        });
    }

    public void init(){

        date = new Date();
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar c = Calendar.getInstance();
        datePicker.setMaxDate(c.getTimeInMillis());

        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int argYear, int argMonth, int argDay) {
                        // TODO Auto-generated method stub
                        year = argYear;
                        month = argMonth + 1;
                        day = argDay;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        datePickerMsg = String.format("%04d/%02d/%02d", argYear , argMonth + 1, argDay);
                        try{
                            date = dateFormat.parse(datePickerMsg + " " + timePickerMsg);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }

                    }
                });

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int argHour, int argMinute) {
                hour = argHour;
                minute = argMinute;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                timePickerMsg = String.format("%02d:%02d", hour , minute);
                try{
                    date = dateFormat.parse(datePickerMsg + " " + timePickerMsg);
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        });

        year = datePicker.getYear();
        month = datePicker.getMonth() + 1;
        day = datePicker.getDayOfMonth();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        }else{
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        datePickerMsg = String.format("%04d/%02d/%02d", year , month, day);
        timePickerMsg = String.format("%02d:%02d", hour , minute);


    }

    public void setCheckInOutButtonUI(boolean isWorkingFlag) {
        if(isWorkingFlag) {
            manualCheckInOutButton.setText("退勤");
            manualCheckInOutButton.setBackgroundColor(Color.parseColor("#33B5E5"));
        }else{
            manualCheckInOutButton.setText("出勤");
            manualCheckInOutButton.setBackgroundColor(Color.parseColor("#FF4081"));
        }
    }


    public void updateAlertDialogUI(boolean isWorkingFlag, String dateMsg) {
        //Dialog Setting
        alertDialogBuilder = new AlertDialog.Builder(ManualActivity.this);
        if(isWorkingFlag) {
            alertDialogBuilder
                    .setTitle("退勤")
                    .setMessage(mFirebaseAuth.getCurrentUser().getDisplayName()+"様、" + dateMsg +"に退勤しますか？");
        }else{
            alertDialogBuilder
                    .setTitle("出勤")
                    .setMessage(mFirebaseAuth.getCurrentUser().getDisplayName()+"様、" + dateMsg +"に出勤しますか？");
        }
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("はい",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //OK
                                //出退勤情報をFirebaseDBに入れる。
                                presenter.insertUpdateWorkInfo(date);
                                //WorkingFlagをスイッチングしてDBをUPDATEする。
                                presenter.updateWorkingFlagOfWorkInfo();
                                //presenter.updateAleatDialogUIBySearchMember();
                                //ImageDialogを出力する。
                                presenter.displayImageDialog();
                            }
                        })
                .setNegativeButton("いいえ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancle
                                dialog.cancel();
                            }
                        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void displayImageDialog(final Uri selectedImageUri) {
        //Image 적용
        LayoutInflater factory = LayoutInflater.from(ManualActivity.this);
        View customView = factory.inflate(R.layout.dialog_custom, null);
        ImageView imageView = (ImageView) customView.findViewById(R.id.dialog_imageView);

        Glide.with(customView)
                .load(selectedImageUri)
                .into(imageView);

        imageDialogBuilder = new AlertDialog.Builder(ManualActivity.this);
        imageDialogBuilder
                //.setTitle("")
                //.setMessage("")
                .setCancelable(true)
                .setView(customView)
                .setPositiveButton("確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //OK
                            }
                        }).show();
    }
}
