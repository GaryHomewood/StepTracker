package uk.co.garyhomewood.steptracker.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class NewStaircaseActivity extends ActionBarActivity {

    private Button buttonTop;
    private Button buttonBottom;
    private NfcAdapter nfcAdapter;
    private IntentFilter[] writeTagFilters;
    private PendingIntent nfcPendingIntent;
    private boolean silent=false;
    private boolean writeProtect = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staircase);

        buttonTop = (Button) findViewById(R.id.button_top);
        buttonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        buttonBottom = (Button) findViewById(R.id.button_bottom);
        buttonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        context = getApplicationContext();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        IntentFilter discovery=new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        // Intent filters for writing to a tag
        writeTagFilters = new IntentFilter[] { discovery };

    }


}
