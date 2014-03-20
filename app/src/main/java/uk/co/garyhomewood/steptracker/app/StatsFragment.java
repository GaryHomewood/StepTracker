package uk.co.garyhomewood.steptracker.app;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerContract;
import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerDbHelper;

public class StatsFragment extends Fragment {

    private ListView statsList;

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        if (rootView != null) {
            statsList = (ListView) rootView.findViewById(R.id.statsList);
        }

        new GetStats(getActivity()).execute();

        return rootView;
    }

    private class GetStats extends AsyncTask<Void, Void, ArrayList<String>> {

        private Context context;

        private GetStats(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> stats = new ArrayList<String>();

            StepTrackerDbHelper dbHelper = new StepTrackerDbHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    StepTrackerContract.Climb._ID,
                    StepTrackerContract.Climb.COLUMN_NAME_STAIRCASE_ID,
                    StepTrackerContract.Climb.COLUMN_NAME_DATE,
                    StepTrackerContract.Climb.COLUMN_NAME_DURATION
            };

            String sortOrder = StepTrackerContract.Climb.COLUMN_NAME_DATE + " DESC";

            if (db != null) {
                Cursor c = db.query(
                        StepTrackerContract.Climb.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );

                while (c.moveToNext()) {
                    Integer duration = c.getInt(c.getColumnIndexOrThrow(StepTrackerContract.Climb.COLUMN_NAME_DURATION));
                    Log.d("Stats", "duration: " + duration);
                    stats.add(duration.toString());
                }
            }
            return stats;
        }


        @Override
        protected void onPostExecute(ArrayList<String> stats) {

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    stats
            );

            statsList.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }

}
