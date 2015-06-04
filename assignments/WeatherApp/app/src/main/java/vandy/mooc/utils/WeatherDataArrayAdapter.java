package vandy.mooc.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.aidl.WeatherData;

/**
 * Custom ArrayAdapter for the AcronymData class, which makes each row
 * of the ListView have a more complex layout than just a single
 * textview (which is the default for ListViews).
 */
public class WeatherDataArrayAdapter extends ArrayAdapter<WeatherData> {
    /**
     * Construtor that declares which layout file is used as the
     * layout for each row.
     */
    public WeatherDataArrayAdapter(Context context) {
        super(context, R.layout.acronym_data_row);
    }

    /**
     * Construtor that declares which layout file is used as the
     * layout for each row.
     */
    public WeatherDataArrayAdapter(Context context,
                                   List<WeatherData> objects) {
        super(context, R.layout.acronym_data_row, objects);
    }

    /**
     * Method used by the ListView to "get" the "view" for each row of
     * data in the ListView.
     *
     * @param position The position of the item within the adapter's data set of the
     *                 item whose view we want. convertView The old view to reuse, if
     *                 possible. Note: You should check that this view is non-null
     *                 and of an appropriate type before using. If it is not possible
     *                 to convert this view to display the correct data, this method
     *                 can create a new view. Heterogeneous lists can specify their
     *                 number of view types, so that this View is always of the right
     *                 type (see getViewTypeCount() and getItemViewType(int)).
     * @param parent   The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        WeatherData data = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.acronym_data_row,
                    parent,
                    false);
        }

        TextView windTV =
                (TextView) convertView.findViewById(R.id.name);

        windTV.setText(data.toString());

        return convertView;
    }
}
