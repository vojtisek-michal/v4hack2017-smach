package cz.vojtisek.smach;

import android.app.Application;
import com.onesignal.OneSignal;

public class SmachApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).init();
    }
}
