package com.test.HowlFirebaseAuth.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.test.HowlFirebaseAuth.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarListActivity extends AppCompatActivity {

    String datePickerMsg;
    Button confirmDatePickerButton;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_list);

        Locale.setDefault(Locale.JAPAN);
        datePicker = (DatePicker) findViewById(R.id.workInfo_datePicker);

        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int argYear, int argMonth, int argDay) {
                        // TODO Auto-generated method stub
                    }
                });
        //datePicker.setMinDate();
        Calendar c = Calendar.getInstance();
        datePicker.setMaxDate(c.getTimeInMillis());

        confirmDatePickerButton = (Button) findViewById(R.id.confirmDatePicker_button);
        confirmDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN);
                Date date = new Date();
                datePickerMsg = datePicker.getYear() + "/" + (datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth();
                try{
                    date = dateFormat.parse(datePickerMsg);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                datePickerMsg = dateFormat.format(date);

                Intent intent = new Intent(CalendarListActivity.this, WorkInfoListActivity.class);
                intent.putExtra("datePickerMsg", datePickerMsg);
                startActivity(intent);
            }
        });



    }
}
