package com.patcornejo.awsconnections.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBTable(tableName = "Users")
public class User {
    private String UserID;
    private String Name;
    private String Email;
    private String ImgURL;

    @DynamoDBHashKey(attributeName = "UserID")
    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @DynamoDBAttribute(attributeName = "ImgURL")
    public String getImgURL() {
        return ImgURL;
    }

    public void setImgURL(String imgURL) {
        ImgURL = imgURL;
    }
}
