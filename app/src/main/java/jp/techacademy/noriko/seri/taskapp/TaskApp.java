package jp.techacademy.noriko.seri.taskapp;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Noriko on 2017/07/11.
 */

public class TaskApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}
