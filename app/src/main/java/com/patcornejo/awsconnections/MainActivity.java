package com.patcornejo.awsconnections;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.auth.policy.Principal;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.patcornejo.awsconnections.managers.AmazonManager;
import com.patcornejo.awsconnections.managers.DDBManager;
import com.patcornejo.awsconnections.managers.PrefsManager;
import com.patcornejo.awsconnections.models.Config;
import com.patcornejo.awsconnections.models.User;
import com.patcornejo.awsconnections.utils.Globals;
import com.patcornejo.awsconnections.utils.Google;
import com.patcornejo.awsconnections.utils.TransferLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 124;

    ImageView iv;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.image);

        findViewById(R.id.download).setOnClickListener(downloadl);
        findViewById(R.id.upload).setOnClickListener(uploadl);
        findViewById(R.id.login).setOnClickListener(loginl);
        findViewById(R.id.send).setOnClickListener(sendl);

        if(PrefsManager.getInstance().isRegistered()) {

            Dataset ds = AmazonManager.getCognito().openOrCreateDataset("datasetname");

            if(getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(ds.get("colorPrimary"))));

            Log.i("Sync", ds.get("colorPrimary"));

            Google.getToken(PrefsManager.getInstance().getEmail(), userl);
            findViewById(R.id.login).setVisibility(View.GONE);
        }
    }

    private Dataset.SyncCallback syncl = new Dataset.SyncCallback() {
        @Override
        public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
        }

        @Override
        public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
            Log.i("Sync", "onConfilct");

            List<Record> resolvedRecords = new ArrayList<>();

            for(SyncConflict sc : conflicts) {
                resolvedRecords.add(sc.resolveWithLocalRecord());
            }

            dataset.resolve(resolvedRecords);

            return true;
        }

        @Override
        public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
            return false;
        }

        @Override
        public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
            return false;
        }

        @Override
        public void onFailure(DataStorageException dse) {
            Log.i("Sync", dse.getMessage());
        }
    };

    private Google.EventListener userl = new Google.EventListener() {
        @Override
        public void onToken(String token) {
            Map<String, String> logins = new HashMap<>();
            logins.put(Principal.WebIdentityProviders.Google.getWebIdentityProvider(), token);
            AmazonManager.addLogins(logins);
            DDBManager.getUser(ddbl);
        }
    };

    private View.OnClickListener loginl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .enableAutoManage(MainActivity.this, failedl)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
        }
    };

    private GoogleApiClient.OnConnectionFailedListener failedl = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    };

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            acct = result.getSignInAccount();
            Google.getToken(acct.getEmail(), gel);

        } else {
            Log.i("AWS", "handleSignInResult");
        }
    }

    private Google.EventListener gel = new Google.EventListener() {
        @Override
        public void onToken(String token) {

            findViewById(R.id.login).setVisibility(View.GONE);
            findViewById(R.id.upload).setVisibility(View.VISIBLE);
            findViewById(R.id.send).setVisibility(View.VISIBLE);

            Map<String, String> logins = new HashMap<>();
            logins.put(Principal.WebIdentityProviders.Google.getWebIdentityProvider(), token);
            AmazonManager.addLogins(logins);
        }
    };

    private View.OnClickListener sendl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            User user = new User();
            user.setEmail(acct.getEmail());
            user.setName(acct.getDisplayName());
            user.setUserID(acct.getId());
            user.setImgURL(acct.getPhotoUrl().toString());

            DDBManager.registerUser(user, ddbl);
        }
    };

    private View.OnClickListener uploadl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TransferLoader.putImage("trianglify.jpg", imgl);
        }
    };

    private View.OnClickListener downloadl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TransferLoader.getImage("trianglify.jpg", imgl);
        }
    };

    private DDBManager.DDBEventListener ddbl = new DDBManager.DDBEventListener() {
        @Override
        public void onRegister() {
            Log.i("AWS", "onRegister");
            PrefsManager.getInstance().putRegistration();
            findViewById(R.id.send).setVisibility(View.GONE);

            Dataset dataset = AmazonManager.getCognito().openOrCreateDataset("datasetname");
            dataset.put("colorPrimary", "#FF0000");
            dataset.synchronize(syncl);

        }

        @Override
        public void onUser(User user) {
            Globals.USER = user;
            Log.i("AWS", Globals.USER.getEmail());
            findViewById(R.id.config).setVisibility(View.VISIBLE);
            findViewById(R.id.products).setVisibility(View.VISIBLE);
            findViewById(R.id.config).setOnClickListener(getl);
            findViewById(R.id.products).setOnClickListener(productsl);

            AnalyticsEvent event = AmazonManager.getAnalytics().getEventClient().createEvent("UserLogin");
            event.withAttribute("UserName", Globals.USER.getName());
            event.withMetric("Time", (double) System.currentTimeMillis() / 1000);

            AmazonManager.getAnalytics().getEventClient().recordEvent(event);
        }

        @Override
        public void onConfig(Config config) {
            Globals.CONFIG = config;
            Log.i("AWS", Globals.CONFIG.getCategories().get(0).get("name"));
            findViewById(R.id.config).setVisibility(View.GONE);
        }

        @Override
        public void onError(String message) {

        }
    };

    private View.OnClickListener productsl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DDBManager.getProducts();
        }
    };

    private View.OnClickListener getl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DDBManager.getConfig(ddbl);
        }
    };

    private TransferLoader.OnImageListener imgl = new TransferLoader.OnImageListener() {
        @Override
        public void onComplete(Bitmap b) {
            iv.setImageDrawable(new BitmapDrawable(getResources(), b));
        }

        @Override
        public void onError(String message) {

        }

        @Override
        public void uploadComplete() {
            Log.i("AWS", "UPLOAD READY");
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}
