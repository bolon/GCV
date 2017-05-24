package com.rere.fish.gcv.result.preexec;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Android dev on 5/22/17.
 */

public class ResponseEngine {
    @SerializedName("pairs") List<ResponsePair> listResponsePair;

    class ResponsePair {
        String keyword;
        double score;
    }
}
