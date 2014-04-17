package uk.co.garyhomewood.steptracker.app.datastore;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class StepTrackerProvider extends ContentProvider {

    private StepTrackerDbHelper db;
    private static final String AUTHORITY = "uk.co.garyhomewood.steptracker.app.datastore.StepTrackerProvider";
    private static final String CLIMBS_BASE_PATH = "climbs";
    public static final int CLIMBS = 100;
    public static final int CLIMB_ID = 110;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CLIMBS_BASE_PATH);
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, CLIMBS_BASE_PATH, CLIMBS );
        uriMatcher.addURI(AUTHORITY, CLIMBS_BASE_PATH + "/#", CLIMB_ID );
    }

    @Override
    public boolean onCreate() {
        db = new StepTrackerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(StepTrackerContract.Climb.TABLE_NAME);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case CLIMBS:
                break;
            case CLIMB_ID:
                break;
            default:
                throw new IllegalArgumentException("Unkown URI");
        }

        Cursor cursor = queryBuilder.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
