package vandy.mooc.cache;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import vandy.mooc.aidl.WeatherData;

public class CacheImpl implements Cache {

    protected final String TAG = getClass().getSimpleName();

    private Map<String, WeatherData> weatherMap;

    public CacheImpl() {
        this.weatherMap = new HashMap<>();
    }

    @Override
    public void release(WeatherData weatherData) {
        String name = weatherData.getName();
        Log.d(TAG, "Adding " + name + " to cache");
        weatherMap.put(name, weatherData);
        autoDestroyFromCache(weatherData);
    }

    @Override
    public WeatherData acquire(String locationName) {
        WeatherData weatherData = weatherMap.get(locationName);
        if (weatherData != null) {
            Log.d(TAG, "Retrieving " + locationName + " from cache");
        }
        return weatherData;
    }

    @Override
    public void deleteFromCache(WeatherData weatherData) {
        if (weatherMap.containsKey(weatherData.getName())) {
            Log.d(TAG, "Removing " + weatherData.getName() + " from cache");
            weatherMap.remove(weatherData.getName());
        }
    }

    public void autoDestroyFromCache(WeatherData weatherData) {
        Timer timer = new Timer();
        timer.schedule(new AutoDestroyTask(this, weatherData), 10000);
    }

    class AutoDestroyTask extends TimerTask {

        private final Cache cache;
        private final WeatherData weatherData;

        public AutoDestroyTask(Cache cache, WeatherData weatherData) {
            this.cache = cache;
            this.weatherData = weatherData;
        }

        public void run() {
            cache.deleteFromCache(weatherData);
        }
    }
}