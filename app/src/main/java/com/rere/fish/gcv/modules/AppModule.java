package com.rere.fish.gcv.modules;

/**
 * Created by Android dev on 5/11/17.
 */

import com.rere.fish.gcv.App;
import com.rere.fish.gcv.MainActivity;

import dagger.Module;

@Module(
        includes = {
                APIModule.class
        },
        library = true,
        injects = {
                MainActivity.class
        }
)
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }
}
