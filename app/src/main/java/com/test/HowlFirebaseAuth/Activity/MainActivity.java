package com.test.HowlFirebaseAuth.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
// FIXME: 不要なインポート文は消してください
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.test.HowlFirebaseAuth.Presenter.LoginPresenter;
import com.test.HowlFirebaseAuth.R;
import com.test.HowlFirebaseAuth.Utility.ProgressDialogTask;
import com.test.HowlFirebaseAuth.ValueObject.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements com.test.HowlFirebaseAuth.LoginContract.View{


    private static final int RC_SIGN_IN = 10;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private FirebaseDatabase mFirebaseDatabase;

    //--------------------------------------------------
    // FirebaseDB DAO
    //--------------------------------------------------
    private List<Member> memberList = new ArrayList<>();

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locale.setDefault(Locale.JAPAN);
        presenter = new LoginPresenter(this, this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unSubscribe();
    }

    private void initView() {
        // FIXME: このコメントを日本語で書いて欲しいです
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // FIXME: mGoogleApiClientはgoogleLoginButton.setOnClickListenerの中でしか使っていないようなので、
        // ローカル変数化した方が良いかと思います
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // FIXME: ここのコメントは現実装と合っていないようなので消した方が良いです
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton googleLoginButton = (SignInButton) findViewById(R.id.login_button);
        googleLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // FIXME: ここもコメントを日本語にして欲しいです
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                presenter.firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    public void showHomeActivity(){
        Toast.makeText(MainActivity.this, "Go to HomeActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); //현재 Activity 사라짐*//**//**//**//*
    }

    public void showFail(){
        Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();
    }

    public void failTask(){
        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_LONG).show();
    }


}
