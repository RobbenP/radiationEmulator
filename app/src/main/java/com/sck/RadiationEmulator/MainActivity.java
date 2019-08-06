package com.sck.RadiationEmulator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.sck.RadiationEmulator.Model.ColorAndValue;
import com.sck.RadiationEmulator.Model.Constants;
import com.sck.RadiationEmulator.Model.World;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private World world;

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

        preferencesEditor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if a world already exists fetch it
        world = getIntent().getParcelableExtra("world");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        if (!settings.contains(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART)) {
            resetSettings(settings);
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
}
