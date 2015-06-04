package vandy.mooc.services;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.aidl.WeatherCall;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.utils.WeatherUtils;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @class WeatherServiceSync
 * 
 * @brief This class uses synchronous AIDL interactions to expand
 *        acronyms via an Acronym Web service.  The AcronymActivity
 *        that binds to this Service will receive an IBinder that's an
 *        instance of AcronymCall, which extends IBinder.  The
 *        Activity can then interact with this Service by making
 *        two-way method calls on the AcronymCall object asking this
 *        Service to lookup the meaning of the Acronym string.  After
 *        the lookup is finished, this Service sends the Acronym
 *        results back to the Activity by returning a List of
 *        AcronymData.
 * 
 *        AIDL is an example of the Broker Pattern, in which all
 *        interprocess communication details are hidden behind the
 *        AIDL interfaces.
 */
public class WeatherServiceSync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceSync when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                          WeatherServiceSync.class);
    }

    /**
     * Called when a client (e.g., AcronymActivity) calls
     * bindService() with the proper Intent.  Returns the
     * implementation of AcronymCall, which is implicitly cast as an
     * IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAcronymCallImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface AcronymCall,
     * which extends the Stub class that implements AcronymCall,
     * thereby allowing Android to handle calls across process
     * boundaries.  This method runs in a separate Thread as part of
     * the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final WeatherCall.Stub mAcronymCallImpl =
        new WeatherCall.Stub() {
            /**
             * Implement the AIDL AcronymCall expandAcronym() method,
             * which forwards to DownloadUtils getResults() to obtain
             * the results from the Acronym Web service and then
             * returns the results back to the Activity.
             */
            @Override
            public List<WeatherData> getCurrentWeather(String weather)
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

                    // Return the list of weather expansions back to the
                    // AcronymActivity.
                    return weatherResults;
                } else {
                    // Create a zero-sized weatherResults object to
                    // indicate to the caller that the weather had no
                    // expansions.
                    return new ArrayList<WeatherData>();
                }
            }
	};
}
