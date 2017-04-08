package cz.vojtisek.smach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static cz.vojtisek.smach.MonitoringActivity.ACTION_CHARGING_END;

public class AmpsetActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public static String EXTRA_CHARGING_SESSION_ID = "EXTRA_CHARGING_SESSION_ID";
    private SeekBar mSeekBar;
    private TextView mTextView;
    private String mChargingSessionId;
    private EditText mEditTextPin;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CHARGING_END.equals(intent.getAction())) {
                finish();
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

        setContentView(R.layout.activity_ampset);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mTextView = (TextView) findViewById(R.id.textView);
        mSeekBar.setOnSeekBarChangeListener(this);
        mEditTextPin = (EditText) findViewById(R.id.editTextPin);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("public_station", false)) {
            mEditTextPin.setVisibility(View.VISIBLE);
        } else {
            mEditTextPin.setVisibility(View.GONE);
        }

        findViewById(R.id.button).setOnClickListener(this);

        onProgressChanged(mSeekBar, mSeekBar.getProgress(), false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mTextView.setText(String.format(Locale.ENGLISH, "%d A", i+1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("public_station", false)) {
            if (!"1234".equals(mEditTextPin.getText().toString())) {
                new AlertDialog.Builder(this)
                        .setTitle("Invalid PIN")
                        .setMessage("Please enver valid PIN.")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return;
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://parabolic-might-163914.appspot.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        Call<String> call = api.setAmp(mChargingSessionId, mSeekBar.getProgress()+1);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Toast.makeText(AmpsetActivity.this, response.body(), Toast.LENGTH_LONG).show();
                Intent i = new Intent(AmpsetActivity.this, MonitoringActivity.class);
                i.putExtra(AmpsetActivity.EXTRA_CHARGING_SESSION_ID, mChargingSessionId);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("AmpsetActivity", t.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, new IntentFilter(ACTION_CHARGING_END));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);

        super.onPause();
    }
}