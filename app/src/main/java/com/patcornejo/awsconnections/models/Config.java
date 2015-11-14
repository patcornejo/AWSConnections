package com.patcornejo.awsconnections.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "Configs")
public class Config {
    private List<Map<String, String>> Categories;
    private String ConfigID;

    @DynamoDBHashKey(attributeName = "ConfigID")
    public String getConfigID() {
        return ConfigID;
    }

    public void setConfigID(String configID) {
        ConfigID = configID;
    }

    @DynamoDBAttribute(attributeName = "Categories")
    public List<Map<String, String>> getCategories() {
        return Categories;
    }

    public void setCategories(List<Map<String, String>> categories) {
        Categories = categories;
    }
}
