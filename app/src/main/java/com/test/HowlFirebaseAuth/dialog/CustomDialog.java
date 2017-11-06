package com.test.HowlFirebaseAuth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.test.HowlFirebaseAuth.R;

public class CustomDialog extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.dialog_custom;

    private Context context;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
    }

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        /*switch (view.getId()){
            case R.id.findPwDialogCancelTv:
                cancel();
                break;
            case R.id.findPwDialogFindTv:
                break;
        }*/
    }
}
