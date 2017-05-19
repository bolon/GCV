package com.rere.fish.gcv;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.rere.fish.gcv.modules.AppModule;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by Android dev on 5/11/17.
 */

public class App extends Application {
    private final static int SCHEMA_VERSION = 1;
    private ObjectGraph injector;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Timber for logging -> no pun intended
        Timber.plant(new Timber.DebugTree());

        // Configure default configuration for Realm
/*        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(SCHEMA_VERSION)
                .build();
        Realm.setDefaultConfiguration(realmConfig);*/

        //Iconify
        Iconify.with(new FontAwesomeModule());

        injector = ObjectGraph.create(new AppModule(this));
    }

    public ObjectGraph getInjector() {
        return injector;
    }

}
