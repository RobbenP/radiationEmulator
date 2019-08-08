package com.sck.RadiationEmulator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sck.RadiationEmulator.Model.ColorAndValue;
import com.sck.RadiationEmulator.Model.Constants;
import com.sck.RadiationEmulator.Model.EmulatedMeasurement;
import com.sck.RadiationEmulator.Model.World;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private static final int MESSAGE_SENT = 1;
    /**
     * This handler receives a message from onNdefPushComplete
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Sent our sources!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private World world;
    private NfcAdapter myNfcAdapter;

    public static void resetSettings(SharedPreferences settings) {

        ArrayList<ColorAndValue> colorAndValues = new ArrayList<>();
        colorAndValues.add(new ColorAndValue(-16711936, 50));
        colorAndValues.add(new ColorAndValue(-256, 100));
        colorAndValues.add(new ColorAndValue(-32768, 150));
        colorAndValues.add(new ColorAndValue(-65536, Integer.MAX_VALUE));
        Collections.sort(colorAndValues);

        Gson gson = new Gson();
        String colorAndValuesJson = gson.toJson(colorAndValues);

        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putBoolean(Constants.IMAGE_RECOGNITION_OR_TAPPING, true);
        preferencesEditor.putInt(Constants.BARCHART_MAXIMUM_VALUE, 200);
        preferencesEditor.putInt(Constants.WORLD_SIZE, 100);
        preferencesEditor.putBoolean(Constants.RELATIVE_DISTANCE_OR_REAL, true);
        preferencesEditor.putString(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART, colorAndValuesJson);
        preferencesEditor.putBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true);

        preferencesEditor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if a world already exists fetch it
        world = getIntent().getParcelableExtra("world");
        super.onCreate(savedInstanceState);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        if (!settings.contains(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART)) {
            resetSettings(settings);
        }
        if (myNfcAdapter == null) {
            Toast.makeText(this, "No NFC available", Toast.LENGTH_SHORT).show();
        } else {
            // Register callback to set NDEF message
            myNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            myNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }

    }

    public void goToScanner(View view) {
        Intent intent = new Intent(this, ARscanner.class);
        intent.putExtra("world", world);
        this.startActivity(intent);

    }

    public void goToSetUp(View view) {
        Intent intent = new Intent(this, SetUpWorld.class);
        intent.putExtra("world", world);
        this.startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("world", world);
        this.startActivity(intent);
    }

    /**
     * Closes the application when we press on back here
     */
    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        this.finish();
        this.finishAffinity();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    private void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<EmulatedMeasurement>>() {
        }.getType();
        world.setMeasurementsList(gson.fromJson(new String(msg.getRecords()[0].getPayload()), type));
        Toast.makeText(getApplicationContext(), "Received sources in the world!", Toast.LENGTH_LONG).show();
        // record 0 contains the MIME type, record 1 is the AAR, if present

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        Gson gson = new Gson();
        String sourcesInWorld = gson.toJson(world.getMeasurementsList());
        NdefMessage ndefMessage = new NdefMessage(NdefRecord.createMime(
                "application/com.sck.radiationemulator", sourcesInWorld.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the AAR
                 * is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this
                 * activity starts when receiving a beamed message. For now, this code
                 * uses the tag dispatch system.
                 */
                //,NdefRecord.createApplicationRecord("com.example.android.beam")
        );
        return ndefMessage;
    }

    /**
     * Callback for when the message is successfully sent
     */
    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();

    }
}
