package com.test.HowlFirebaseAuth.Activity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.test.HowlFirebaseAuth.Utility.ProgressDialogTask;
import com.test.HowlFirebaseAuth.ValueObject.WorkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModifyWorkInfoActivity extends AppCompatActivity implements CheckInOutContract.View{

    private Button modifyCheckInOutButton;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog.Builder imageDialogBuilder;
    // FIXME: この辺りのメンバ変数についても頭にmをつけた形で名前を修正してください
    private DatePicker modifyDatePicker;
    private TimePicker modifyTimePicker;

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

    private WorkInfo workInfo;

    private CheckInOutPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_work_info);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        date = new Date();


        workInfo = (WorkInfo) getIntent().getSerializableExtra("workinfo");
        init();
        presenter = new CheckInOutPresenter(this, this);
        presenter.initCheckInOutButtonUIByMemberData();

        modifyCheckInOutButton = (Button) findViewById(R.id.modifyCheckInOut_button);
        modifyCheckInOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateAlertDialogUI();
            }
        });
    }

    public void init(){

        Calendar calendar = Calendar.getInstance();

        Log.d("TAG", workInfo.toString());

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");


        if(workInfo.getOnOffWork().equals("onwork")){
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            //String dateToString = dateFormat.format(workInfo.getCreateOnWorkDate());
            year = Integer.parseInt( yearFormat.format(workInfo.getCreateOnWorkDate()) );
            month = Integer.parseInt( monthFormat.format(workInfo.getCreateOnWorkDate()) );
            day = Integer.parseInt( dayFormat.format(workInfo.getCreateOnWorkDate()) );
            hour = Integer.parseInt( hourFormat.format(workInfo.getCreateOnWorkDate()) );
            minute = Integer.parseInt( minuteFormat.format(workInfo.getCreateOnWorkDate()) );
        }else if(workInfo.getOnOffWork().equals("offwork")){
            year = Integer.parseInt( yearFormat.format(workInfo.getCreateOffWorkDate()) );
            month = Integer.parseInt( monthFormat.format(workInfo.getCreateOffWorkDate()) );
            day = Integer.parseInt( dayFormat.format(workInfo.getCreateOffWorkDate()) );
            hour = Integer.parseInt( hourFormat.format(workInfo.getCreateOffWorkDate()) );
            minute = Integer.parseInt( minuteFormat.format(workInfo.getCreateOffWorkDate()) );
        }


        modifyDatePicker = (DatePicker) findViewById(R.id.modify_datePicker);
        Calendar c = Calendar.getInstance();
        modifyDatePicker.setMaxDate(c.getTimeInMillis());
        modifyDatePicker.init(modifyDatePicker.getYear(),
                modifyDatePicker.getMonth(),
                modifyDatePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int argYear, int argMonth, int argDay) {
                        // TODO Auto-generated method stub
                        year = argYear;
                        month = argMonth + 1;
                        day = argDay;
                        datePickerMsg = String.format("%04d/%02d/%02d", year , month, day);
                        updateAlertDialogUI();
                    }
                });
        modifyDatePicker.updateDate(year, month - 1 , day);

        modifyTimePicker = (TimePicker) findViewById(R.id.modify_timePicker);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            //use checkSelfPermission()
            modifyTimePicker.setHour(hour);
            modifyTimePicker.setMinute(minute);
        } else {
            //simply use the required feature
            //as the user has already granted permission to them during installation
            modifyTimePicker.setCurrentHour(hour);
            modifyTimePicker.setCurrentMinute(minute);
        }

        modifyTimePicker.setIs24HourView(true);
        modifyTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
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
                updateAlertDialogUI();
            }
        });


        datePickerMsg = String.format("%04d/%02d/%02d", year, month, day);
        timePickerMsg = String.format("%02d:%02d", hour, minute);

        updateAlertDialogUI();
    }

    public void updateAlertDialogUI(){
        //Dialog Setting
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", new Locale("en", "US"));
        String dateString = String.format("%04d/%02d/%02d %02d:%02d", year, month, day, hour, minute);
        alertDialogBuilder = new AlertDialog.Builder(ModifyWorkInfoActivity.this);
        alertDialogBuilder
                .setTitle("修正")
                .setMessage(mFirebaseAuth.getCurrentUser().getDisplayName()+"様、" +
                        dateString +"で修正しますか？")
                .setCancelable(false)
                .setPositiveButton("はい",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //OK
                                //workingFlag変換
                                String dateString = String.format("%04d/%02d/%02d %02d:%02d", year, month, day, hour, minute);
                                presenter.updateWorkDateOfWorkInfo(workInfo, dateString);
                                presenter.updateWorkingFlagOfWorkInfo();
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
    }

    public void showUpdateAlertDialogUI(){
        alertDialogBuilder.show();
    }

    public void displayImageDialog(final Uri selectedImageUri) {
        //Image 적용
        LayoutInflater factory = LayoutInflater.from(ModifyWorkInfoActivity.this);
        View customView = factory.inflate(R.layout.dialog_custom, null);
        ImageView imageView = (ImageView) customView.findViewById(R.id.dialog_imageView);

        Glide.with(customView)
                .load(selectedImageUri)
                .into(imageView);

        imageDialogBuilder = new AlertDialog.Builder(ModifyWorkInfoActivity.this);
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

    public void setCheckInOutButtonUI(boolean isWorkingFlag){ }
    public void updateAlertDialogUI(boolean isWorkingFlag, String dateMsg){ }
}
