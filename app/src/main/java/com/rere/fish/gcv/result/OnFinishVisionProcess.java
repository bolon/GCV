package com.rere.fish.gcv.result;

import com.google.gson.JsonObject;

/**
 * Created by Android dev on 5/23/17.
 */

public interface OnFinishVisionProcess {
    void onReceivedResultFromGCV(JsonObject result);
}
