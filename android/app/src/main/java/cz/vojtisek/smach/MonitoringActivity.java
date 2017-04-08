package cz.vojtisek.smach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cz.vojtisek.smach.AmpsetActivity.EXTRA_CHARGING_SESSION_ID;

public class MonitoringActivity extends AppCompatActivity {

    public static String ACTION_CHARGING_END = "cz.vojtisek.smach.ACTION_CHARGING_END";

    private static final long TIME_BETWEEN_MESSAGES = 2 * DateUtils.SECOND_IN_MILLIS;
    private static final int MSG_DO_IT = 1;
    private String mChargingSessionId;
    private TextView mTextViewCurrentWatt;
    private TextView mTextViewCurrentAmp;
    private TextView mTextViewSetAmp;
    private TextView mTextViewTotalWatt;
    private TextView mTextViewTotalPrice;
    private View mLayoutReview;
    private EditText mEditTextReview;
    private Button mButtonStop;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            readData();

            Message newMsg = obtainMessage(MSG_DO_IT);
            sendMessageDelayed(newMsg, TIME_BETWEEN_MESSAGES);
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CHARGING_END.equals(intent.getAction())) {
                onChargingEnd();
            }
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
        mLayoutReview = findViewById(R.id.layoutReview);
        mEditTextReview = (EditText) findViewById(R.id.editTextReview);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mButtonStop = (Button) findViewById(R.id.buttonStop);
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCharging();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Message newMsg = mHandler.obtainMessage(MSG_DO_IT);
        mHandler.sendMessage(newMsg);
        registerReceiver(mReceiver, new IntentFilter(ACTION_CHARGING_END));
    }

    @Override
    protected void onPause() {
        mHandler.removeMessages(MSG_DO_IT);
        unregisterReceiver(mReceiver);

        super.onPause();
    }

    private void onChargingEnd() {
        mButtonStop.setVisibility(View.GONE);
        mLayoutReview.setVisibility(View.VISIBLE);
        mEditTextReview.requestFocus();
    }

    private void stopCharging() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://parabolic-might-163914.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<String> call = api.stopCharging(mChargingSessionId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
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
                //Toast.makeText(AmpsetActivity.this, response.body(), Toast.LENGTH_LONG).show();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MonitoringActivity.this);
                JsonObject json = response.body();
                mTextViewCurrentWatt.setText(String.format(Locale.ENGLISH, "%d W",
                        json.get("watt_current").getAsInt()));
                mTextViewTotalWatt.setText(String.format(Locale.ENGLISH, "%d W/h",
                        json.get("watt_total").getAsInt()));
                mTextViewCurrentAmp.setText(String.format(Locale.ENGLISH, "%d A",
                        json.get("amp_current").getAsInt()));
                mTextViewSetAmp.setText(String.format(Locale.ENGLISH, "%d A",
                        json.get("amp_set").getAsInt()));
                mTextViewTotalPrice.setText(String.format(Locale.ENGLISH, "%.2f Kč",
                        json.get("watt_total").getAsInt() * Float.valueOf(prefs.getString("kwh_price", "5.5"))/1000));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("MonitoringActivity", t.getMessage());
            }
        });

    }
}
