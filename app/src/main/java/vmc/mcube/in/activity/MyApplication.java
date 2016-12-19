package vmc.mcube.in.activity;

import android.app.Application;
import android.content.Context;
import vmc.mcube.in.database.MDatabase;
import vmc.mcube.in.syncadapter.SyncUtils;
import vmc.mcube.in.utils.ConnectivityReceiver;

/**
 * Created by mukesh on 14/3/16.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;
    private static MDatabase mDatabase;
    public String gcmKey;

    @Override
    public void onCreate() {
        super.onCreate();
        SyncUtils.CreateSyncAccount(getApplicationContext());
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAplicationContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static MDatabase getWritableDatabase() {
        if (mDatabase == null) {
            mDatabase = new MDatabase(getAplicationContext());
        }
        return mDatabase;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
