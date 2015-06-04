package vandy.mooc.cache;

import vandy.mooc.aidl.WeatherData;

public interface Cache {

    public void release(WeatherData weatherData);

    public WeatherData acquire(String locationName);

    public void deleteFromCache(WeatherData weatherData);

}