package cz.vojtisek.smach;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static cz.vojtisek.smach.MainActivity.EXTRA_CHARGING_SESSION_ID;

public class MonitoringActivity extends AppCompatActivity {

    private static final long TIME_BETWEEN_MESSAGES = 2 * DateUtils.SECOND_IN_MILLIS;
    private static final int MSG_DO_IT = 1;
    private String mChargingSessionId;
    private TextView mTextViewCurrentWatt;
    private TextView mTextViewCurrentAmp;
    private TextView mTextViewSetAmp;
    private TextView mTextViewTotalWatt;
    private TextView mTextViewTotalPrice;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            readData();

            Message newMsg = obtainMessage(MSG_DO_IT);
            sendMessageDelayed(newMsg, TIME_BETWEEN_MESSAGES);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChargingSessionId = getIntent().getStringExtra(EXTRA_CHARGING_SESSION_ID);
        if (TextUtils.isEmpty(mChargingSessionId)) {
            finish();
        }

        setContentView(R.layout.activity_monitoring);
        mTextViewCurrentWatt = (TextView) findViewById(R.id.textViewCurrentWatt);
        mTextViewCurrentAmp = (TextView) findViewById(R.id.textViewCurrentAmp);
        mTextViewSetAmp = (TextView) findViewById(R.id.textViewSetAmp);
        mTextViewTotalWatt = (TextView) findViewById(R.id.textViewTotalWatt);
        mTextViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Message newMsg = mHandler.obtainMessage(MSG_DO_IT);
        mHandler.sendMessage(newMsg);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHandler.removeMessages(MSG_DO_IT);
    }

    private void readData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://parabolic-might-163914.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        Call<JsonObject> call = api.getCharging(mChargingSessionId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Toast.makeText(MainActivity.this, response.body(), Toast.LENGTH_LONG).show();
                JsonObject json = response.body();
                mTextViewCurrentWatt.setText(String.format(Locale.ENGLISH, "%d W",
                        json.get("watt_current").getAsInt()));
                mTextViewTotalWatt.setText(String.format(Locale.ENGLISH, "%d W/h",
                        json.get("watt_total").getAsInt()));
                mTextViewCurrentAmp.setText(String.format(Locale.ENGLISH, "%d A",
                        json.get("amp_current").getAsInt()));
                mTextViewSetAmp.setText(String.format(Locale.ENGLISH, "%d A",
                        json.get("amp_set").getAsInt()));
                mTextViewTotalPrice.setText(String.format(Locale.ENGLISH, "%d Kč",
                        Math.round(json.get("watt_total").getAsInt() * 5.5)));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("MonitoringActivity", t.getMessage());
            }
        });

    }
}
