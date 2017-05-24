package com.rere.fish.gcv.result;

import com.rere.fish.gcv.result.preexec.ResponseEngine;

/**
 * Created by Android dev on 5/24/17.
 */

public interface OnFinishEngineProcess {
    void onReceivedResultFromEngine(ResponseEngine resp);
}
