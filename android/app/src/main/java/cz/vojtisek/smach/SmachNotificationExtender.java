package cz.vojtisek.smach;

import android.content.Intent;
import android.text.TextUtils;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

public class SmachNotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        JSONObject data = receivedResult.payload.additionalData;
        if (data != null) {
            String chargingSessionId = data.optString("charging_session_id");
            if (!TextUtils.isEmpty(chargingSessionId)) {
                String event = data.optString("event");
                if ("charging_start".equals(event)) {
                    Intent i = new Intent(this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(MainActivity.EXTRA_CHARGING_SESSION_ID, chargingSessionId);
                    startActivity(i);
                    return true;
                } else if ("charging_end".equals(event)) {
                    Intent i = new Intent(MonitoringActivity.ACTION_CHARGING_END);
                    sendBroadcast(i);
                    return true;
                }
            }
        }

        // Return true to stop the notification from displaying.
        return false;
    }
}