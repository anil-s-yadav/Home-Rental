package com.legendarysoftwares.homerental;

public class PostPropertyModel {

    private String postTitle, postAddress, postPrice, propertyId, ownerId, postImageUrl, ownerName, ownerPhoto;

    public PostPropertyModel(String propertyId, String ownerId, String postTitle, String postAddress,
                             String postPrice, String postImageUrl, String ownerName, String ownerPhoto) {
        this.postTitle = postTitle;
        this.postAddress = postAddress;
        this.postPrice = postPrice;
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.postImageUrl = postImageUrl;
        this.ownerName = ownerName;
        this.ownerPhoto = ownerPhoto;
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

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
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
}
