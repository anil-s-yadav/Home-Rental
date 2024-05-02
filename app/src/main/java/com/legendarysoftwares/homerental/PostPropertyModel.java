package com.legendarysoftwares.homerental;

public class PostPropertyModel {

    private long timestamp;
    private String postTitle, postAddress, postPrice, propertyId, ownerId, ownerName, ownerPhoto, reraID,
            postImageUrl1, postImageUrl2, postImageUrl3, postImageUrl4, postImageUrl5, postImageUrl6;

    // Constructor with image URLs
    public PostPropertyModel(long timestamp, String propertyId, String ownerId, String postTitle,
                             String postAddress, String postPrice,
                             String ownerName, String ownerPhoto,String reraID,
                             String postImageUrl1, String postImageUrl2,
                             String postImageUrl3, String postImageUrl4,
                             String postImageUrl5, String postImageUrl6) {
        this.timestamp = timestamp;
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.postTitle = postTitle;
        this.postAddress = postAddress;
        this.postPrice = postPrice;
        this.ownerName = ownerName;
        this.ownerPhoto = ownerPhoto;
        this.reraID = reraID;
        this.postImageUrl1 = postImageUrl1;
        this.postImageUrl2 = postImageUrl2;
        this.postImageUrl3 = postImageUrl3;
        this.postImageUrl4 = postImageUrl4;
        this.postImageUrl5 = postImageUrl5;
        this.postImageUrl6 = postImageUrl6;
    }


    // Constructor without image URLs
    public PostPropertyModel(long timestamp,String propertyId, String ownerId, String postTitle,
                             String postAddress, String postPrice,
                             String ownerName, String ownerPhoto, String reraID) {
        this.timestamp = timestamp;
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.postTitle = postTitle;
        this.postAddress = postAddress;
        this.postPrice = postPrice;
        this.ownerName = ownerName;
        this.ownerPhoto = ownerPhoto;
        this.reraID = reraID;
    }

    // Constructor with only image URLs
    public PostPropertyModel(String postImageUrl1, String postImageUrl2,
                             String postImageUrl3, String postImageUrl4,
                             String postImageUrl5, String postImageUrl6) {
        this.postImageUrl1 = postImageUrl1;
        this.postImageUrl2 = postImageUrl2;
        this.postImageUrl3 = postImageUrl3;
        this.postImageUrl4 = postImageUrl4;
        this.postImageUrl5 = postImageUrl5;
        this.postImageUrl6 = postImageUrl6;
    }


    public PostPropertyModel() {}

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(String postPrice) {
        this.postPrice = postPrice;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhoto() {
        return ownerPhoto;
    }

    public void setOwnerPhoto(String ownerPhoto) {
        this.ownerPhoto = ownerPhoto;
    }

    public String getPostImageUrl1() {
        return postImageUrl1;
    }

    public void setPostImageUrl1(String postImageUrl1) {
        this.postImageUrl1 = postImageUrl1;
    }

    public String getPostImageUrl2() {
        return postImageUrl2;
    }

    public void setPostImageUrl2(String postImageUrl2) {
        this.postImageUrl2 = postImageUrl2;
    }

    public String getPostImageUrl3() {
        return postImageUrl3;
    }

    public void setPostImageUrl3(String postImageUrl3) {
        this.postImageUrl3 = postImageUrl3;
    }

    public String getPostImageUrl4() {
        return postImageUrl4;
    }

    public void setPostImageUrl4(String postImageUrl4) {
        this.postImageUrl4 = postImageUrl4;
    }

    public String getPostImageUrl5() {
        return postImageUrl5;
    }

    public void setPostImageUrl5(String postImageUrl5) {
        this.postImageUrl5 = postImageUrl5;
    }

    public String getPostImageUrl6() {
        return postImageUrl6;
    }

    public void setPostImageUrl6(String postImageUrl6) {
        this.postImageUrl6 = postImageUrl6;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReraID() {
        return reraID;
    }

    public void setReraID(String reraID) {
        this.reraID = reraID;
    }
}
