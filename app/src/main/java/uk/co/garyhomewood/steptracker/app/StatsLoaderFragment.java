package uk.co.garyhomewood.steptracker.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerContract;
import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerProvider;

public class StatsLoaderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter adapter;
    ListView statsList;

    static final String[] PROJECTION = new String[] {
            StepTrackerContract.Climb._ID,
            StepTrackerContract.Climb.COLUMN_NAME_STAIRCASE_ID,
            StepTrackerContract.Climb.COLUMN_NAME_DATE,
            StepTrackerContract.Climb.COLUMN_NAME_DURATION
    };

    static final String SORT_ORDER = StepTrackerContract.Climb.COLUMN_NAME_DATE + " DESC";

    public static StatsLoaderFragment newInstance() {
        return new StatsLoaderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        if (rootView != null) {
            statsList = (ListView) rootView.findViewById(R.id.statsList);
        }

        // mapping of db columns to listview views
        String[] fromColumns = { StepTrackerContract.Climb.COLUMN_NAME_DURATION };
        int[] toViews = { android.R.id.text1 };

        adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                fromColumns,
                toViews,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        statsList.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                StepTrackerProvider.CONTENT_URI,
                PROJECTION,
                null,
                null,
                SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
