package uk.co.garyhomewood.steptracker.app;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Toast;

import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerContract;
import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerDbHelper;

public class HomeActivity extends ActionBarActivity {

    private Chronometer chronometer;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarButtons = inflater.inflate(R.layout.time_actionbar, new LinearLayout(this), false);
        View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
        //cancelActionView.setOnClickListener(mActionBarListener);
        doneActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(StepTrackerContract.Climb.COLUMN_NAME_STAIRCASE_ID, "1");
                values.put(StepTrackerContract.Climb.COLUMN_NAME_DURATION, elapsedTime);
                new Save(getApplicationContext()).execute(values);
            }
        });

        ab.setCustomView(actionBarButtons);
        ab.hide();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {

        }

        intentFilters  = new IntentFilter[] {ndef};
        techLists = new String[][] { new String[] { NfcF.class.getName() } };

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
    }

    private long elapsedTime;

    @Override
    protected void onNewIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMsgs != null) {
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            String s = GetText(msg.getRecords()[0]);

            if (s.contains("start")) {
                Toast.makeText(this, "Start...", Toast.LENGTH_LONG).show();
                elapsedTime = 0;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                ab.hide();
            }

            if (s.contains("stop")) {
                Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
                elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.stop();
                ab.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private String GetText(NdefRecord record) {
        try {
            byte[] payload = record.getPayload();

            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0077;
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            // Get the Text

            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (Exception e) {
            throw new RuntimeException("Record Parsing Failure!!");
        }
    }

    private class Save extends AsyncTask<ContentValues, Void, Long>{

        private Context context;

        public Save(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(ContentValues... values) {
            StepTrackerDbHelper dbHelper = new StepTrackerDbHelper(context);
            long id = 0;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                id = db.insert(StepTrackerContract.Climb.TABLE_NAME, null, values[0] );
            }
            return id;
        }

        @Override
        protected void onPostExecute(Long i) {
            super.onPostExecute(i);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
