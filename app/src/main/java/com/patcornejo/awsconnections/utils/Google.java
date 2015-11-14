package com.patcornejo.awsconnections.utils;

import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.patcornejo.awsconnections.R;
import com.patcornejo.awsconnections.managers.AppManager;

import java.io.IOException;

/**
 * Created by patcornejo on 12-11-15.
 */
public class Google {

    public static void getToken(final String email, final Google.EventListener gel) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String token = "";

                try {
                    token = GoogleAuthUtil.getToken(
                            AppManager.getInstance().getContext(),
                            email,
                            "audience:server:client_id:" + AppManager.getInstance().getContext().getString(R.string.client_id));
                } catch (IOException | GoogleAuthException | IllegalArgumentException e) {
                    e.printStackTrace();
                }

                return token;
            }

            @Override
            protected void onPostExecute(String result) {
                gel.onToken(result);
            }
        }.execute();
    }

    public interface EventListener {
        void onToken(String token);
    }
}
