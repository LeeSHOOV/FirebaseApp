package com.test.HowlFirebaseAuth;

import android.net.Uri;

import com.test.HowlFirebaseAuth.ValueObject.WorkInfo;

import java.util.Date;

/**
 * Created by admin on 2017/10/30.
 */

public interface CheckInOutContract {

    interface View{
        void setCheckInOutButtonUI(boolean isWorkingFlag);
        void updateAlertDialogUI(boolean isWorkingFlag, String dateMsg);
        void displayImageDialog(Uri selectedImageUri);
    }
    interface Presenter{
        void initCheckInOutButtonUIByMemberData();
        void updateWorkingFlagOfWorkInfo();
        void showAlertDialog();
        void showAlertDialog(String dateMsg);
        void insertUpdateWorkInfo();
        void insertUpdateWorkInfo(Date date);
        void updateWorkDateOfWorkInfo(WorkInfo argWorkInfo, String dateToString);
        void displayImageDialog();
    }
}
