package uk.co.garyhomewood.steptracker.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewStaircaseActivity extends ActionBarActivity {

    Button buttonTop;
    Button buttonBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staircase);

        final TextView staircaseName = (TextView) findViewById(R.id.staircaseName);

        buttonTop = (Button) findViewById(R.id.button_top);
        buttonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // write the tag for start
                String nfcMessage = "START|" + staircaseName.getText();
                setupIntents(nfcMessage);
            }
        });
        buttonBottom = (Button) findViewById(R.id.button_bottom);
        buttonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // write the tag for stop
                String nfcMessage = "STOP|" + staircaseName.getText();
                setupIntents(nfcMessage);
            }
        });
    }

    private void setupIntents(String nfcMessage) {
        Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcIntent.putExtra("nfcMessage", nfcMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        IntentFilter tagDiscovered = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{tagDiscovered}, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String nfcMessage = intent.getStringExtra("nfcMessage");

        if (nfcMessage != null) {
            NFC.writeTag(this, tag, nfcMessage);
        }
    }
}
