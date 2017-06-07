package com.rere.fish.gcv;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.rere.fish.gcv.bgservice.CBWatcherService;
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
        startService(new Intent(getApplicationContext(), CBWatcherService.class));

        //Timber for logging -> no pun intended
        Timber.plant(new Timber.DebugTree());

        //Iconify
        Iconify.with(new FontAwesomeModule());

        injector = ObjectGraph.create(new AppModule(this));
    }

    public ObjectGraph getInjector() {
        return injector;
    }
}
