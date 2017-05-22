package com.rere.fish.gcv.result;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Android dev on 5/22/17.
 */

public class ResponseEngineModel {
    @SerializedName("pairs")
    List<ResponsePair> listResponsePair;

    public List<ResponsePair> getListResponsePair() {
        return listResponsePair;
    }

    public void setListResponsePair(List<ResponsePair> listResponsePair) {
        this.listResponsePair = listResponsePair;
    }

    private class ResponsePair {
        String keyword;
        double score;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
