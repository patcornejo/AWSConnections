package com.patcornejo.awsconnections.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.patcornejo.awsconnections.R;
import com.patcornejo.awsconnections.managers.AmazonManager;
import com.patcornejo.awsconnections.managers.AppManager;

import java.io.File;

/**
 * Created by patcornejo on 11-11-15.
 */
public class TransferLoader {

    public static void getImage(String key, final TransferLoader.OnImageListener imgl) {
        final File f = new File(Environment.getExternalStorageDirectory() + "/AWSConnections", key);

        if(f.exists()) {
            imgl.onComplete(BitmapFactory.decodeFile(f.getAbsolutePath()));
        } else {
            AmazonManager.getTransfer().download(AppManager.getInstance().getContext().getString(R.string.bucket_name), key, f).setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        imgl.onComplete(BitmapFactory.decodeFile(f.getAbsolutePath()));
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

                @Override
                public void onError(int id, Exception ex) {
                    imgl.onError(ex.getMessage());
                }
            });
        }
    }

    public static void putImage(String key, final TransferLoader.OnImageListener imgl) {

        final File f = new File(Environment.getExternalStorageDirectory() + "/AWSConnections", key);

        if(f.exists()) {
            AmazonManager.getTransfer().upload(AppManager.getInstance().getContext().getString(R.string.bucket_name), "uploads/" + key, f)
                    .setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if (state == TransferState.COMPLETED) {
                                imgl.uploadComplete();
                            }
                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            imgl.onError(ex.getMessage());
                        }
                    });
        } else {
            imgl.onError("Image not exist");
        }
    }

    public interface OnImageListener {
        void onComplete(Bitmap b);
        void onError(String message);
        void uploadComplete();
    }
}
