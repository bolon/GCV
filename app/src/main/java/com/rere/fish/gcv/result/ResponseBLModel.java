package com.rere.fish.gcv.result;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Android dev on 5/22/17.
 */

public class ResponseBLModel {
    private String status;
    private List<Product> products;

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

    class Product {
        private String id;
        private String category;
        private String url;
        private String name;
        private String province;
        private String desc;
        private String condition;
        private boolean active;
        private double price;

        private List<Label> labels;

        @SerializedName("sold_count")
        private double soldCount;
        @SerializedName("seller_term_condition")
        private String sellerTerms;
        @SerializedName("category_structure")
        private List<String> categoryStructure;
        @SerializedName("deal_request_state")
        private String dealRequestState;
        @SerializedName("images")
        private List<String> images;
        @SerializedName("small_images")
        private List<String> smallImages;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public List<Label> getLabels() {
            return labels;
        }

        public void setLabels(List<Label> labels) {
            this.labels = labels;
        }

        public double getSoldCount() {
            return soldCount;
        }

        public void setSoldCount(double soldCount) {
            this.soldCount = soldCount;
        }

        public String getSellerTerms() {
            return sellerTerms;
        }

        public void setSellerTerms(String sellerTerms) {
            this.sellerTerms = sellerTerms;
        }

        public List<String> getCategoryStructure() {
            return categoryStructure;
        }

        public void setCategoryStructure(List<String> categoryStructure) {
            this.categoryStructure = categoryStructure;
        }

        public String getDealRequestState() {
            return dealRequestState;
        }

        public void setDealRequestState(String dealRequestState) {
            this.dealRequestState = dealRequestState;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<String> getSmallImages() {
            return smallImages;
        }

        public void setSmallImages(List<String> smallImages) {
            this.smallImages = smallImages;
        }
    }

    class Label {
        String id;
        String name;
        String slug;
        String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    class Spec {
        String brand;
        String type;
        String bahan;
        String ukuran;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBahan() {
            return bahan;
        }

        public void setBahan(String bahan) {
            this.bahan = bahan;
        }

        public String getUkuran() {
            return ukuran;
        }

        public void setUkuran(String ukuran) {
            this.ukuran = ukuran;
        }
    }
}
