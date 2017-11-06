package com.test.HowlFirebaseAuth.Utility;

import com.test.HowlFirebaseAuth.ValueObject.Member;

/**
 * Created by admin on 2017/10/06.
 */

public class Singleton {
    public static Member connectedMember;
    private Singleton(){}
    public static Singleton getInstance(){
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final Singleton INSTANCE = new Singleton();
    }
}
/*
    ・Lazy Loding Pattern
    インスタンスの生成をDelayさせるパータン。
    LazyHolder.INSTANCEを参照した瞬間、ClassがLodingして初期化する。
    synchronizedなしで動作する。そして、Thread-safeなので性能も優秀だ。
*/
