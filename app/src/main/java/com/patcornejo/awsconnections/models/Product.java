package com.patcornejo.awsconnections.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Products")
public class Product {
    private String ProductID;
    private String Category;
    private String CountryCode;
    private String Name;

    private int Price;

    @DynamoDBHashKey(attributeName = "ProductID")
    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "Category-index", attributeName = "Category")
    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    @DynamoDBAttribute(attributeName = "CountryCode")
    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @DynamoDBAttribute(attributeName = "Price")
    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
