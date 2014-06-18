package fr.tvbarthel.apps.simpleweatherforcast.ui;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.tvbarthel.apps.simpleweatherforcast.R;
import fr.tvbarthel.apps.simpleweatherforcast.openweathermap.DailyForecastModel;
import fr.tvbarthel.apps.simpleweatherforcast.utils.SharedPreferenceUtils;
import fr.tvbarthel.apps.simpleweatherforcast.utils.TemperatureUtils;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WeatherRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private List<DailyForecastModel> mDailyForecasts;
    private int[] mGradientDrawables;
    private int[] mColors;
    private SimpleDateFormat mSimpleDateFormat;

    public WeatherRemoteViewsFactory(Context context, Intent intent, List<DailyForecastModel> dailyForecasts) {
        mContext = context;
        mDailyForecasts = dailyForecasts;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mSimpleDateFormat = new SimpleDateFormat("EEEE dd MMMM", Locale.getDefault());
    }

    @Override
    public void onCreate() {
        mGradientDrawables = new int[]{R.drawable.bg_blue_purple,
                R.drawable.bg_purple_yellow,
                R.drawable.bg_yellow_red,
                R.drawable.bg_red_green,
                R.drawable.bg_green_blue};

        mColors = new int[]{R.color.holo_blue,
                R.color.holo_purple,
                R.color.holo_yellow,
                R.color.holo_red,
                R.color.holo_green};
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mDailyForecasts.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final DailyForecastModel dailyForecast = mDailyForecasts.get(position);
        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.row_app_widget);
        final String temperatureUnit = SharedPreferenceUtils.getTemperatureUnitSymbol(mContext);
        final long temperature = TemperatureUtils.convertTemperature(mContext, dailyForecast.getTemperature(), temperatureUnit);
        final long minTemperature = TemperatureUtils.convertTemperature(mContext, dailyForecast.getMinTemperature(), temperatureUnit);
        final long maxTemperature = TemperatureUtils.convertTemperature(mContext, dailyForecast.getMaxTemperature(), temperatureUnit);
        final int gradient = mGradientDrawables[position % mGradientDrawables.length];
        final int backgroundColor = mColors[position % mColors.length];
        final String date = mSimpleDateFormat.format(dailyForecast.getDateTime() * 1000);

        remoteViews.setTextViewText(R.id.row_app_widget_date, date);
        remoteViews.setTextViewText(R.id.row_app_widget_temperature, temperature + temperatureUnit);
        remoteViews.setTextViewText(R.id.row_app_widget_weather, dailyForecast.getDescription());
        remoteViews.setTextViewText(R.id.row_app_widget_min_max, mContext.getString(
                R.string.forecast_fragment_min_max_temperature, minTemperature, maxTemperature));
        remoteViews.setInt(R.id.row_app_widget_gradient, "setBackgroundResource", gradient);
        remoteViews.setInt(R.id.row_app_widget_background, "setBackgroundResource", backgroundColor);
        remoteViews.setOnClickFillInIntent(R.id.row_app_widget_root, new Intent());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}