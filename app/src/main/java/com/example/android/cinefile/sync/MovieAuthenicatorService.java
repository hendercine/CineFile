package com.example.android.cinefile.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Hendercine on 10/20/16.
 * * The service which allows the sync adapter framework to access the authenticator.
 */

public class MovieAuthenicatorService extends Service {

    private MovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        //Create a new authenticator object
        mAuthenticator = new MovieAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
