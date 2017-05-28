package com.rere.fish.gcv.modules;

/**
 * Created by Android dev on 5/11/17.
 */

import com.rere.fish.gcv.App;
import com.rere.fish.gcv.MainActivity;
import com.rere.fish.gcv.result.ResultActivity;
import com.rere.fish.gcv.result.preexec.PreProcessFragment;

import dagger.Module;

@Module(includes = {APIModule.class}, library = true, injects = {MainActivity.class, ResultActivity.class,
        //frag
        PreProcessFragment.class})
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }
}
