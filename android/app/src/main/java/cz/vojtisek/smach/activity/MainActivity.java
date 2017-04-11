package cz.vojtisek.smach.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;

import cz.vojtisek.smach.API;
import cz.vojtisek.smach.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewTotalYearCon;
    private TextView mTextViewTotalMonthCon;
    private TextView mTextViewTotalWeekCon;
    private TextView mTextViewTotalYearCost;
    private TextView mTextViewTotalMonthCost;
    private TextView mTextViewTotalWeekCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getTitle() + " - Counters");

        mTextViewTotalYearCon = (TextView) findViewById(R.id.textViewTotalYearCon);
        mTextViewTotalMonthCon = (TextView) findViewById(R.id.textViewTotalMonthCon);
        mTextViewTotalWeekCon = (TextView) findViewById(R.id.textViewTotalWeekCon);
        mTextViewTotalYearCost = (TextView) findViewById(R.id.textViewTotalYearCost);
        mTextViewTotalMonthCost = (TextView) findViewById(R.id.textViewTotalMonthCost);
        mTextViewTotalWeekCost = (TextView) findViewById(R.id.textViewTotalWeekCost);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_map) {
            startActivity(new Intent(this, MapsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://parabolic-might-163914.appspot.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<String> call = api.getStats();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                int totalWats = Integer.valueOf(response.body().trim());
                long totalCost = Math.round(Integer.valueOf(response.body().trim()) * Float.valueOf(prefs.getString("kwh_price", "5.5")) / 1000);

                mTextViewTotalYearCon.setText(formatToWattsWithUnits(totalWats));
                mTextViewTotalMonthCon.setText(formatToWattsWithUnits(totalWats));
                mTextViewTotalWeekCon.setText(formatToWattsWithUnits(totalWats));

                mTextViewTotalYearCost.setText(String.format(Locale.ENGLISH,
                        "%d Kč", totalCost));
                mTextViewTotalMonthCost.setText(String.format(Locale.ENGLISH,
                        "%d Kč", totalCost));
                mTextViewTotalWeekCost.setText(String.format(Locale.ENGLISH,
                        "%d Kč", totalCost));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private String formatToWattsWithUnits(int value) {
        if (value >= 1000000) {
            return String.format(Locale.ENGLISH, "%.2f mWh", (float) value / 1000000);
        } else if (value >= 1000) {
            return String.format(Locale.ENGLISH, "%.2f kWh", (float) value / 1000);
        } else {
            return String.format(Locale.ENGLISH, "%d Wh", value);
        }
    }
}
