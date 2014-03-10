package uk.co.garyhomewood.steptracker.app.datastore;

import android.provider.BaseColumns;

/**
 * Created by user1 on 10/03/2014.
 */
public final class StepTrackerContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public StepTrackerContract() {}

    public static abstract class Climb implements BaseColumns {
        public static final String TABLE_NAME = "climb";
        public static final String COLUMN_NAME_STAIRCASE_ID = "staircase_id";
        public static final String COLUMN_NAME_DATE = "climb_date";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String SQL_CREATE =
                "CREATE TABLE " + StepTrackerContract.Climb.TABLE_NAME + " (" +
                        StepTrackerContract.Climb._ID + " INTEGER PRIMARY KEY," +
                        StepTrackerContract.Climb.COLUMN_NAME_STAIRCASE_ID + TEXT_TYPE + COMMA_SEP +
                        StepTrackerContract.Climb.COLUMN_NAME_DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                        StepTrackerContract.Climb.COLUMN_NAME_DURATION + " INTEGER" +
                        " )";
    }
}
