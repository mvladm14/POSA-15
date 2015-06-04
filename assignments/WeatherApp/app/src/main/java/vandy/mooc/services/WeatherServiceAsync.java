package vandy.mooc.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.aidl.WeatherRequest;
import vandy.mooc.aidl.WeatherResults;
import vandy.mooc.utils.WeatherUtils;

/**
 * @class WeatherServiceAsync
 * @brief This class uses asynchronous AIDL interactions to expand
 * acronyms via an Acronym Web service.  The AcronymActivity
 * that binds to this Service will receive an IBinder that's an
 * instance of AcronymRequest, which extends IBinder.  The
 * Activity can then interact with this Service by making
 * one-way method calls on the AcronymRequest object asking
 * this Service to lookup the Acronym's meaning, passing in an
 * AcronymResults object and the Acronym string.  After the
 * lookup is finished, this Service sends the Acronym results
 * back to the Activity by calling sendResults() on the
 * AcronymResults object.
 * <p/>
 * AIDL is an example of the Broker Pattern, in which all
 * interprocess communication details are hidden behind the
 * AIDL interfaces.
 */
public class WeatherServiceAsync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceAsync when passed to bindService().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceAsync.class);
    }

    /**
     * Called when a client (e.g., AcronymActivity) calls
     * bindService() with the proper Intent.  Returns the
     * implementation of AcronymRequest, which is implicitly cast as
     * an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mWeatherRequestImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * AcronymRequest, which extends the Stub class that implements
     * AcronymRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     * <p/>
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final WeatherRequest.Stub mWeatherRequestImpl =
            new WeatherRequest.Stub() {
                /**
                 * Implement the AIDL AcronymRequest expandAcronym()
                 * method, which forwards to DownloadUtils getResults() to
                 * obtain the results from the Acronym Web service and
                 * then sends the results back to the Activity via a
                 * callback.
                 */
                @Override
                public void getCurrentWeather(String weather,
                                              WeatherResults callback)
                        throws RemoteException {

                    // Call the Acronym Web service to get the list of
                    // possible expansions of the designated weather.
                    final List<WeatherData> weatherResults =
                            WeatherUtils.getResults(weather);

                    if (weatherResults != null) {
                        Log.d(TAG, ""
                                + weatherResults.size()
                                + " results for weather: "
                                + weather);
                    } else {
                        Log.d(TAG, "results for nononweather: ");
                    }
                    // Invoke a one-way callback to send list of
                    // weather expansions back to the AcronymActivity.
                    callback.sendResults(weatherResults);
                }
            };
}
