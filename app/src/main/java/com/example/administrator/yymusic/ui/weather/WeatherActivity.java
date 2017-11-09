package com.example.administrator.yymusic.ui.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.yymusic.R;
import com.example.administrator.yymusic.model.WeatherInfo;
import com.example.administrator.yymusic.sys.WeatherSys;
import com.example.administrator.yymusic.util.YLog;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 *
 * @author yysleep
 */

public class WeatherActivity extends AppCompatActivity {

    private List<WeatherInfo.Data.Forecast> mList;
    public static final String TAG = "WeatherActivity";
    WeatherSys weaInstance;
    private TextView tvStreet;
    private TextView tvCurrentTemperature;
    private TextView tvWeatherDetail;
    private TextView tvWind;
    private TextView tvPower;
    private TextView tvShiduB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        weaInstance = WeatherSys.getInstance();
        WeatherInfo info = weaInstance.getWeatherInfo();
        if (info == null || info.getData() == null)
            return;

        List<WeatherInfo.Data.Forecast> forecasts = info.getData().getForecast();
        YLog.d(TAG, "[onStart] forecasts = " + forecasts);
        if (forecasts == null || forecasts.isEmpty())
            return;
        mList = forecasts;
        YLog.d(TAG, "[onStart] mList = " + mList + mList.get(0).getDate());
        if (mList.isEmpty())
            return;
        WeatherAdapter adapter = new WeatherAdapter();
        ListView listView = findViewById(R.id.weather_fragment_lv);
        listView.setAdapter(adapter);

        WeatherInfo.Data.Forecast forecastInfo = forecasts.get(0);
        if(forecastInfo == null)
            return;
        tvStreet.setText(weaInstance.getStreetDetail());
        tvCurrentTemperature.setText(weaInstance.getHighTemperature(forecastInfo));
        tvWeatherDetail.setText(weaInstance.getWeatherDetail(forecastInfo));
        tvWind.setText(forecastInfo.getFx());
        tvPower.setText(forecastInfo.getFl());
        tvShiduB.setText(info.getData().getShidu());
    }

    private void initView() {
        tvStreet = findViewById(R.id.weather_street_tv);
        tvCurrentTemperature = findViewById(R.id.weather_current_temperature_tv);
        tvWeatherDetail = findViewById(R.id.weather_weather_detail_tv);
        tvWind = findViewById(R.id.weather_wind_tv);
        tvPower = findViewById(R.id.weather_power_tv);
        tvShiduB = findViewById(R.id.weather_shidu_b_tv);
    }

    private class WeatherAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.item_weather, viewGroup, false);
                holder = new ViewHolder();
                holder.tvDay = view.findViewById(R.id.item_weather_day_tv);
                holder.tvWeather = view.findViewById(R.id.item_weather_weather_tv);
                holder.tvPower = view.findViewById(R.id.item_weather_power_tv);
                holder.tvTemperature = view.findViewById(R.id.item_weather_temperature_tv);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            WeatherInfo.Data.Forecast info = mList.get(i);
            holder.tvDay.setText(info.getDate());
            holder.tvWeather.setText(info.getType());
            holder.tvPower.setText(info.getFx() + " : " + info.getFl());
            holder.tvTemperature.setText(weaInstance.getTemperature(info));
            return view;
        }
    }

    private static class ViewHolder {
        private TextView tvDay;
        private TextView tvWeather;
        private TextView tvPower;
        private TextView tvTemperature;
    }
}
