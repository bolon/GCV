package com.rere.fish.gcv.result.product;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Android dev on 5/22/17.
 */

@Parcel
public class ResponseBL {
    String status;
    List<Product> products;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Parcel
    static class Product {
        String id;
        String category;
        String url;
        String name;
        String province;
        String desc;
        String condition;
        boolean active;
        double price;

        List<Label> labels;

        @SerializedName("sold_count") double soldCount;
        @SerializedName("seller_term_condition") String sellerTerms;
        @SerializedName("category_structure") List<String> categoryStructure;
        @SerializedName("deal_request_state") String dealRequestState;
        @SerializedName("images") List<String> images;
        @SerializedName("small_images") List<String> smallImages;
    }

    @Parcel
    static class Label {
        String id;
        String name;
        String slug;
        String description;
    }


    class Spec {
        String brand;
        String type;
        String bahan;
        String ukuran;
    }
}
