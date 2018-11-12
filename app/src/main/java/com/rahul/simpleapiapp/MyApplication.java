package com.rahul.simpleapiapp;

import android.app.Application;
import io.realm.Realm;

public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    //MultiDex.install(this);

    Realm.init(this);
  }
}
