package uk.co.garyhomewood.steptracker.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerContract;
import uk.co.garyhomewood.steptracker.app.datastore.StepTrackerDbHelper;

public class MainActivity extends ActionBarActivity {

    private StepTrackerPagerAdapter stepTrackerPagerAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private StopwatchFragment stopwatchFragment;
    private StatsFragment statsFragment;
    private StatsLoaderFragment statsLoaderFragment;
    private ActionBar ab;
    private View actionBarButtons;
    private long elapsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create the fragments for the viewpager
        stopwatchFragment = StopwatchFragment.newInstance();
        statsFragment = StatsFragment.newInstance();
        statsLoaderFragment = StatsLoaderFragment.newInstance();
        fragmentList.add(0, stopwatchFragment);
        fragmentList.add(1, statsFragment);
        fragmentList.add(2, statsLoaderFragment);

        stepTrackerPagerAdapter = new StepTrackerPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(stepTrackerPagerAdapter);

        stepTrackerPagerAdapter.notifyDataSetChanged();
        processTag(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_new:
                Intent intent = new Intent(this, NewStaircaseActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processTag(intent);
    }

    private void processTag(Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage msg = (NdefMessage) messages[0];
                NdefRecord ndefRecord = msg.getRecords()[0];
                String nfcMessage = new String(ndefRecord.getPayload());

                StopwatchFragment stopwatchFragment = (StopwatchFragment) fragmentList.get(0);

                Toast.makeText(this, "Tag detected: " + nfcMessage, Toast.LENGTH_LONG).show();

                if (nfcMessage.contains("start")) {
                    Toast.makeText(this, "Start...", Toast.LENGTH_LONG).show();
                    stopwatchFragment.startClock();
                    ab.hide();
                }

                if (nfcMessage.contains("stop")) {
                    Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();

                    // custom actionbar with save and cancel
                    ab = getSupportActionBar();
                    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    actionBarButtons = inflater.inflate(R.layout.time_actionbar, new LinearLayout(this), false);
                    View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
                    View doneActionView = actionBarButtons.findViewById(R.id.action_done);
                    cancelActionView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
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

                    elapsedTime = stopwatchFragment.stopClock();
                    ab.show();
                    ab.setCustomView(actionBarButtons);
                }

                stepTrackerPagerAdapter.notifyDataSetChanged();
            }
        }
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

    public class StepTrackerPagerAdapter extends FragmentPagerAdapter {

        public StepTrackerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MainActivity.this.fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    private class Save extends AsyncTask<ContentValues, Void, Long> {

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
            stopwatchFragment.resetClock();

            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(statsFragment)
                    .attach(statsFragment)
                    .commit();
        }
    }
}
