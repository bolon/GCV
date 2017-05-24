package com.rere.fish.gcv;

import android.content.Context;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

/**
 * Created by Android dev on 5/13/17.
 */

public class PermissionListenerImpl implements PermissionListener {
    Context context;


    public PermissionListenerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {

    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {

    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

    }
}
