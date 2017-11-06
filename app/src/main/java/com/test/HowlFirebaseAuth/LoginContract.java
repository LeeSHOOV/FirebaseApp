package com.test.HowlFirebaseAuth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by admin on 2017/10/24.
 */

public interface LoginContract {

    interface View {
        void showHomeActivity();
        void showFail();
    }

    interface Presenter {
        void subscribe();
        void unSubscribe();
    }
}
