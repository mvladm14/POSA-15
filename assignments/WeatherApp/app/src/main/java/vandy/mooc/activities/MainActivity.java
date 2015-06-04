package vandy.mooc.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.R;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.cache.Cache;
import vandy.mooc.cache.CacheImpl;
import vandy.mooc.operations.WeatherOps;
import vandy.mooc.operations.WeatherOpsImpl;
import vandy.mooc.utils.RetainedFragmentManager;
import vandy.mooc.utils.WeatherDataArrayAdapter;
import vandy.mooc.utils.WeatherUtils;

/**
 * The main Activity that prompts the user for Acronyms to expand via
 * various implementations of WeatherServiceSync and
 * WeatherServiceAsync and view via the results.  Extends
 * LifecycleLoggingActivity so its lifecycle hook methods are logged
 * automatically.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Used to retain the ImageOps state between runtime configuration
     * changes.
     */
    protected final RetainedFragmentManager mRetainedFragmentManager =
            new RetainedFragmentManager(this.getFragmentManager(),
                    TAG);

    private Cache cache;

    /**
     * Provides acronym-related operations.
     */
    private WeatherOps mWeatherOps;

    /**
     * The ListView that will display the results to the user.
     */
    protected ListView mListView;

    /**
     * Acronym entered by the user.
     */
    protected EditText mEditText;

    /**
     * A custom ArrayAdapter used to display the list of AcronymData
     * objects.
     */
    protected WeatherDataArrayAdapter mAdapter;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Get references to the UI components.
        setContentView(R.layout.main_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = (EditText) findViewById(R.id.editText1);

        // Store the ListView for displaying the results entered.
        mListView = (ListView) findViewById(R.id.listView1);

        // Create a local instance of our custom Adapter for our
        // ListView.
        mAdapter = new WeatherDataArrayAdapter(this);

        // Set the adapter to the ListView.
        mListView.setAdapter(mAdapter);

        cache = new CacheImpl();

        // Handle any configuration change.
        handleConfigurationChanges();
    }

    /**
     * Hook method called by Android when this Activity is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Unbind from the Service.
        mWeatherOps.unbindService();

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }

    /**
     * Handle hardware reconfigurations, such as rotating the display.
     */
    protected void handleConfigurationChanges() {
        // If this method returns true then this is the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG,
                    "First time onCreate() call");

            // Create the WeatherOps object one time.  The "true"
            // parameter instructs WeatherOps to use the
            // DownloadImagesBoundService.
            mWeatherOps = new WeatherOpsImpl(this);

            // Store the WeatherOps into the RetainedFragmentManager.
            mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                    mWeatherOps);

            // Initiate the service binding protocol (which may be a
            // no-op, depending on which type of DownloadImages*Service is
            // used).
            mWeatherOps.bindService();
        } else {
            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change
            // occured.

            Log.d(TAG,
                    "Second or subsequent onCreate() call");

            // Obtain the WeatherOps object from the
            // RetainedFragmentManager.
            mWeatherOps =
                    mRetainedFragmentManager.get("WEATHER_OPS_STATE");

            // This check shouldn't be necessary under normal
            // circumtances, but it's better to lose state than to
            // crash!
            if (mWeatherOps == null) {
                // Create the WeatherOps object one time.  The "true"
                // parameter instructs WeatherOps to use the
                // DownloadImagesBoundService.
                mWeatherOps = new WeatherOpsImpl(this);

                // Store the WeatherOps into the RetainedFragmentManager.
                mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                        mWeatherOps);

                // Initiate the service binding protocol (which may be
                // a no-op, depending on which type of
                // DownloadImages*Service is used).
                mWeatherOps.bindService();
            } else
                // Inform it that the runtime configuration change has
                // completed.
                mWeatherOps.onConfigurationChange(this);
        }
    }

    /*
     * Initiate the synchronous weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandWeatherSync(View v) {
        // Get the weather entered by the user.
        final String weather =
                mEditText.getText().toString();

        // Reset the display for the next weather expansion.
        resetDisplay();

        // get results from cache
        if (!getResultsFromCache(weather)) {
            // if not in cache, then
            // Asynchronously expand the weather.
            mWeatherOps.getWeatherSync(weather);
        }

    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandWeatherAsync(View v) {
        // Get the weather entered by the user.
        final String weather =
                mEditText.getText().toString();

        // Reset the display for the next weather expansion.
        resetDisplay();

        if (!getResultsFromCache(weather)) {
            // if not in cache, then
            // Asynchronously expand the weather.
            mWeatherOps.getWeatherAsync(weather);
        }
    }

    /**
     * Display the results to the screen.
     *
     * @param results List of Results to be displayed.
     */
    public void displayResults(List<WeatherData> results,
                               String errorMessage) {
        if (results == null || results.size() == 0)
            WeatherUtils.showToast(this,
                    errorMessage);
        else {
            if (cache.acquire(results.get(0).getName()) == null) {
                cache.release(results.get(0));
            }
            // Set/change data set.
            mAdapter.clear();
            mAdapter.addAll(results);
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean getResultsFromCache(String weather) {
        WeatherData weatherData = cache.acquire(weather);
        if (weatherData != null) {
            List<WeatherData> weatherDataList = new ArrayList<>();
            weatherDataList.add(weatherData);
            displayResults(weatherDataList, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reset the display prior to attempting to expand a new acronym.
     */
    private void resetDisplay() {
        WeatherUtils.hideKeyboard(this,
                mEditText.getWindowToken());
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }
}
