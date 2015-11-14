package com.patcornejo.awsconnections.managers;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.patcornejo.awsconnections.models.Config;
import com.patcornejo.awsconnections.models.Product;
import com.patcornejo.awsconnections.models.User;
import com.patcornejo.awsconnections.utils.Globals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by patcornejo on 12-11-15.
 */
public class DDBManager {
    public static final String TAG = "DDBManager";

    public static void registerUser(final User user, final DDBEventListener ddbl) {
        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                DynamoDBMapper mapper = new DynamoDBMapper(AmazonManager.getDynamoDB());

                try { mapper.save(user); }
                catch (Exception ex) { Log.i(TAG, ex.getMessage()); return false; }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    Log.i(TAG, "ok");
                    Globals.USER = user;
                    ddbl.onRegister();
                }
            }
        }.execute();
    }

    public static void getProducts() {
        new AsyncTask<String, Void, PaginatedQueryList<Product>>() {

            @Override
            protected PaginatedQueryList<Product> doInBackground(String... params) {
                DynamoDBMapper mapper = new DynamoDBMapper(AmazonManager.getDynamoDB());

                /*
                Collection<AttributeValue> attributeValues = new ArrayList<>();
                attributeValues.add(new AttributeValue().withS("cl"));

                Condition c = new Condition();
                c.setAttributeValueList(attributeValues);
                c.setComparisonOperator(ComparisonOperator.EQ);

                Map<String, Condition> scanFiler = new HashMap<>();
                scanFiler.put("CountryCode", c);

                DynamoDBScanExpression exp = new DynamoDBScanExpression();
                exp.withScanFilter(scanFiler);

                PaginatedList<Product> products = mapper.scan(Product.class, exp);*/

                /*
                Map<String, AttributeValue> attributeValueMap = new HashMap<>();
                attributeValueMap.put(":val1", new AttributeValue().withS("cl"));

                DynamoDBScanExpression exp = new DynamoDBScanExpression();
                exp.withExpressionAttributeValues(attributeValueMap);
                exp.withFilterExpression("CountryCode = :val1");

                PaginatedList<Product> products = mapper.scan(Product.class, exp);*/

                /*
                ScanResult result = AmazonManager.getDynamoDB().scan(request);

                Map<String, AttributeValue> lastKeyEvaluated = null;
                do {
                    ScanRequest request = new ScanRequest();
                    request.withTableName("Products");
                    request.withFilterExpression("CountryCode = :val1");
                    request.withExclusiveStartKey(lastKeyEvaluated);

                    ScanResult result = client.scan(scanRequest);
                    for (Map<String, AttributeValue> item : result.getItems()){
                        printItem(item);
                    }
                    lastKeyEvaluated = result.getLastEvaluatedKey();
                } while (lastKeyEvaluated != null);
                */

                /*
                Product product = new Product();
                product.setCategory("Tecnología");

                DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression();
                queryExpression.withIndexName("Category-index");
                queryExpression.withHashKeyValues(product);
                queryExpression.withConsistentRead(false);

                PaginatedQueryList<Product> products = mapper.query(Product.class, queryExpression);*/

                return null;
            }

            @Override
            protected void onPostExecute(PaginatedQueryList<Product> result) {
                Log.i(TAG, result.get(0).getName());
            }
        }.execute();
    }

    public static void getConfig(final DDBEventListener ddbl) {
        new AsyncTask<String, Void, Config>() {

            @Override
            protected Config doInBackground(String... params) {
                DynamoDBMapper mapper = new DynamoDBMapper(AmazonManager.getDynamoDB());

                Config user = null;

                try { user = mapper.load(Config.class, "es"); }
                catch (Exception ex) { Log.i(TAG, ex.getMessage()); }

                return user;
            }

            @Override
            protected void onPostExecute(Config result) {
                if(result != null) {
                    ddbl.onConfig(result);
                }
            }
        }.execute();
    }

    public static void getUser(final DDBEventListener ddbl) {
        new AsyncTask<String, Void, User>() {

            @Override
            protected User doInBackground(String... params) {
                DynamoDBMapper mapper = new DynamoDBMapper(AmazonManager.getDynamoDB());

                User user = null;

                try { user = mapper.load(User.class, PrefsManager.getInstance().getUserID()); }
                catch (Exception ex) { Log.i(TAG, ex.getMessage()); }

                return user;
            }

            @Override
            protected void onPostExecute(User result) {
                Log.i(TAG, "onPostExecute");

                if(result != null) {
                    ddbl.onUser(result);
                } else {
                    Log.i(TAG, "User not Exist");
                }
            }
        }.execute();
    }

    public interface DDBEventListener {
        void onRegister();
        void onUser(User user);
        void onConfig(Config config);
        void onError(String message);
    }
}
