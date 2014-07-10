package uk.co.garyhomewood.steptracker.app;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class NFC {
    public static boolean writeTag(Context context, Tag tag, String data) {
        // record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // record with specified data
        NdefRecord relayRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "application/uk.co.garyhomewood.steptracker.app".getBytes(Charset.forName("US-ASCII")),
                new byte[0],
                data.getBytes(Charset.forName("US-ASCII"))
        );

        // complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[] {relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if (!ndef.isWritable()) {
                    Toast.makeText(context, "read only", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(context, "not enough space", Toast.LENGTH_SHORT).show();
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);
                    Toast.makeText(context, "tag written", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (TagLostException tle) {
                    Toast.makeText(context, "tag lost error", Toast.LENGTH_SHORT).show();
                    return false;

                } catch (IOException ioe) {
                    Toast.makeText(context, "formatting error", Toast.LENGTH_SHORT).show();
                    return false;

                } catch (FormatException fe) {
                    Toast.makeText(context, "formatting exception", Toast.LENGTH_SHORT).show();
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if(format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        Toast.makeText(context, "NFC written", Toast.LENGTH_SHORT).show();
                        return true;

                    } catch (TagLostException tle) {
                        Toast.makeText(context, "tag lost error", Toast.LENGTH_SHORT).show();
                        return false;

                    } catch (IOException ioe) {
                        Toast.makeText(context, "formatting error", Toast.LENGTH_SHORT).show();
                        return false;

                    } catch (FormatException fe) {
                        Toast.makeText(context, "formatting error", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(context, "no ndef error", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch(Exception e) {
            Toast.makeText(context, "unknown error", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
