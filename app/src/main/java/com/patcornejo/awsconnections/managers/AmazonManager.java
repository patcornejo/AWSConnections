package com.patcornejo.awsconnections.managers;

import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.patcornejo.awsconnections.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nerses on 03.03.2015.
 */
public class AmazonManager {

    private static AmazonS3Client s3;
    private static AmazonDynamoDBClient ddb;
    private static CognitoSyncManager cognito;
    private static TransferUtility transferUtility;
    private static CognitoCachingCredentialsProvider credentials;
    private static MobileAnalyticsManager analytics;

    private static AmazonS3Client getS3() {
        if(s3 == null) {
            s3 = new AmazonS3Client(getCredentials());
        }

        return s3;
    }

    public static CognitoSyncManager getCognito() {
        if(cognito == null)
            cognito = new CognitoSyncManager(
                    AppManager.getInstance().getContext(),
                    Regions.US_EAST_1,
                    getCredentials()
            );

        return cognito;
    }

    public static TransferUtility getTransfer() {
        if(transferUtility == null)
            transferUtility = new TransferUtility(getS3(), AppManager.getInstance().getContext());

        return transferUtility;
    }

    public static AmazonDynamoDBClient getDynamoDB() {
        if(ddb == null)
            ddb = new AmazonDynamoDBClient(getCredentials());

        return ddb;
    }

    public static CognitoCachingCredentialsProvider getCredentials() {
        if(credentials == null) {
            credentials = new CognitoCachingCredentialsProvider(
                    AppManager.getInstance().getContext(),
                    AppManager.getInstance().getContext().getString(R.string.account_id),
                    AppManager.getInstance().getContext().getString(R.string.identity_pool),
                    AppManager.getInstance().getContext().getString(R.string.unauth_role),
                    AppManager.getInstance().getContext().getString(R.string.auth_role),
                    Regions.US_EAST_1);
        }

        return credentials;
    }

    public static void addLogins(Map<String, String> logins) {
        AmazonManager.getCredentials().setLogins(logins);
    }

    public static MobileAnalyticsManager getAnalytics() {

        if(analytics == null) {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    AppManager.getInstance().getContext(),
                    AppManager.getInstance().getString(R.string.analytics_id),
                    AppManager.getInstance().getString(R.string.identity_pool));
        }

        return analytics;

    }
}
