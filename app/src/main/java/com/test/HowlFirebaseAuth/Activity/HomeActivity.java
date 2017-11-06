package com.test.HowlFirebaseAuth.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.test.HowlFirebaseAuth.CheckInOutContract;
import com.test.HowlFirebaseAuth.Presenter.CheckInOutPresenter;
import com.test.HowlFirebaseAuth.R;
import com.test.HowlFirebaseAuth.Utility.ProgressDialogTask;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CheckInOutContract.View {

    private final static int GALLERY_CODE = 10;

    private Button checkInOutButton;
    private Button moveManualActivityButton;
    private Button moveTableActivityButton;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog.Builder imageDialogBuilder;

    private FirebaseAuth mFirebaseAuth;

    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;

    private CheckInOutPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null) //2번째 인자값이 this인 이유는 GoogleApiClient.OnConnectionFailedListener 상속
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();


        mFirebaseAuth = FirebaseAuth.getInstance();

        presenter = new CheckInOutPresenter(this, this);
        presenter.initCheckInOutButtonUIByMemberData();

        //Button Init
        checkInOutButton = (Button) findViewById(R.id.checkInOut_button);
        checkInOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //自分のWorkingFlagの状態がTrueかFalseか確認してAlertDialogUIをUPDATEをする。
                //void updateAlertDialogUI(boolean isWorkingFlag, String dateMsg)
                presenter.showAlertDialog();
            }
        });

        //Button Init
        moveManualActivityButton = (Button) findViewById(R.id.moveManualActivity_button);
        moveManualActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ManualActivity.class);
                startActivity(intent);
            }
        });

        //Button Init
        moveTableActivityButton = (Button) findViewById(R.id.moveTableActivity_button);
        moveTableActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CalendarListActivity.class);
                startActivity(intent);
            }
        });

    }


    public void setCheckInOutButtonUI(boolean isWorkingFlag) {
        if(isWorkingFlag) {
            checkInOutButton.setText("退勤");
            checkInOutButton.setBackgroundColor(Color.parseColor("#33B5E5"));
        }else{
            checkInOutButton.setText("出勤");
            checkInOutButton.setBackgroundColor(Color.parseColor("#FF4081"));
        }
    }


    public void updateAlertDialogUI(boolean isWorkingFlag, String dateMsg) {
        //Dialog Setting
        alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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
                                presenter.insertUpdateWorkInfo();
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
        LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
        View customView = factory.inflate(R.layout.dialog_custom, null);
        ImageView imageView = (ImageView) customView.findViewById(R.id.dialog_imageView);

        Glide.with(customView)
                .load(selectedImageUri)
                .into(imageView);

        imageDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        imageDialogBuilder
                //.setTitle("")
                //.setMessage("")
                .setCancelable(true)
                .setView(customView)
                .setPositiveButton("確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { }
                        }).show();
    }

    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            startActivity(new Intent(this, HomeActivity.class));
        } else if (id == R.id.nav_send) {
            sendNotification("Hello", "FIREBASE");
        } else if (id == R.id.nav_logout){
            // Google sign out
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            mFirebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(HomeActivity.this, MainActivity.class));
                        }
                    });


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == GALLERY_CODE){
            if(data.getData() != null){

            }
        }

        Bundle extras = data.getExtras();
        if (extras == null || extras.get("data") == null) return;

        if(resultCode == RESULT_CANCELED){
            finish();
            Toast.makeText(HomeActivity.this, "BACK",Toast.LENGTH_LONG).show();
        }

    }


    public void sendNotification(String connectedMemberName, String titleName){
        OkHttpClient client = new OkHttpClient();

        String to = "/topics/all";
        String name = connectedMemberName;
        String title = titleName;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "{\r\n  \"to\": \"" + to + "\",\r\n  \"data\": {\r\n  \t\"name\" :\"" + name + "\",\r\n    \"title\" :\"" + title + "\"\r\n   }\r\n}");
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "key=AAAAjK4zVoE:APA91bHStDhnWFSZzspIvS45nvlb7M4z8rzXnWBvrkQnDwyHe9mwwpIo7EqE2Uqy8kZ1sX5DY_oli_2uVAoSfsOUw9iJyrcMcLVKsrKP6Y9Kj2bi-8aEy1t2nANEVs6q0T6f7hicQMV5")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "dac049ee-ed75-0861-6517-173fee88deae")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, "Send Failed ", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String myResponse = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, "Send Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }


}
