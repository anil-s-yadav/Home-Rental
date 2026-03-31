package com.legendarysoftwares.homerental;

import com.google.firebase.database.PropertyName;

public class PostPropertyModel {

    private long timestamp;
    private String postTitle, postAddress, postPrice, propertyId, ownerId, ownerName, ownerPhoto;
    private String postImageUrl1, postImageUrl2, postImageUrl3, postImageUrl4, postImageUrl5, postImageUrl6;
    private String postImageUrl; // Used in SavedPosts
    private String Type; // Matching Firebase "Type" (Uppercase)
    private String reraID; // Matching Firebase "reraID"

    public PostPropertyModel() {}

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

    public String getPostAddress() { return postAddress; }
    public void setPostAddress(String postAddress) { this.postAddress = postAddress; }

    public String getPostPrice() { return postPrice; }
    public void setPostPrice(String postPrice) { this.postPrice = postPrice; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhoto() { return ownerPhoto; }
    public void setOwnerPhoto(String ownerPhoto) { this.ownerPhoto = ownerPhoto; }

    public String getPostImageUrl1() { return postImageUrl1; }
    public void setPostImageUrl1(String postImageUrl1) { this.postImageUrl1 = postImageUrl1; }

    public String getPostImageUrl2() { return postImageUrl2; }
    public void setPostImageUrl2(String postImageUrl2) { this.postImageUrl2 = postImageUrl2; }

    public String getPostImageUrl3() { return postImageUrl3; }
    public void setPostImageUrl3(String postImageUrl3) { this.postImageUrl3 = postImageUrl3; }

    public String getPostImageUrl4() { return postImageUrl4; }
    public void setPostImageUrl4(String postImageUrl4) { this.postImageUrl4 = postImageUrl4; }

    public String getPostImageUrl5() { return postImageUrl5; }
    public void setPostImageUrl5(String postImageUrl5) { this.postImageUrl5 = postImageUrl5; }

    public String getPostImageUrl6() { return postImageUrl6; }
    public void setPostImageUrl6(String postImageUrl6) { this.postImageUrl6 = postImageUrl6; }

    public String getPostImageUrl() { return postImageUrl; }
    public void setPostImageUrl(String postImageUrl) { this.postImageUrl = postImageUrl; }

    @PropertyName("Type")
    public String getType() { return Type; }
    
    @PropertyName("Type")
    public void setType(String Type) { this.Type = Type; }

    @PropertyName("reraID")
    public String getReraID() { return reraID; }
    
    @PropertyName("reraID")
    public void setReraID(String reraID) { this.reraID = reraID; }
}