package com.test.HowlFirebaseAuth.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by admin on 2017/10/04.
 */

public class ProgressDialogTask extends AsyncTask<Void, Void, Void> {

    public ProgressDialog asyncDialog;

    public ProgressDialogTask(Context context){
        //HomeActivity.this
        this.asyncDialog = new ProgressDialog(context);
    }

    @Override
    public void onPreExecute() {
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("Loding...");
        asyncDialog.setCancelable(false);

        // show dialog
        asyncDialog.show();
        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... arg0) {

        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        asyncDialog.dismiss();
        asyncDialog.cancel();
        super.onPostExecute(result);
    }

    public void dismissDialog() { asyncDialog.dismiss();}


}
