package com.sck.RadiationEmulator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sck.RadiationEmulator.Model.ColorAndValue;
import com.sck.RadiationEmulator.Model.Constants;
import com.sck.RadiationEmulator.Model.World;
import com.sck.common.helpers.ColorAndValueAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsActivity extends AppCompatActivity implements ColorAndValueAdapter.ColorAndValueCallback {

    private Switch relativeDistance;
    private Switch imageRecognition;
    private Switch useSpinner;
    private EditText barchartMax;
    private EditText worldSize;
    private ArrayList<ColorAndValue> colorAndValues = new ArrayList<>();
    private ListView listAllColorAndValues;
    private String valuetoadd;

    public void addColorAndValue(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Value");


        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Add a value and a color to the list");

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newVal = input.getText().toString();
            valuetoadd = newVal;
            if (newVal.equals("")) dialog.cancel();
            else addColor();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addColor() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton("ok", (dialo, selectedColor, allColors) -> {
                    if (!colorAndValues.contains(new ColorAndValue(selectedColor, Integer.parseInt(valuetoadd)))) {
                        colorAndValues.add(new ColorAndValue(selectedColor, Integer.parseInt(valuetoadd)));
                        updateColorAndValues();
                    }
                })
                .setNegativeButton("cancel", (dialo, whic) -> {
                })
                .build()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveSettings();
        Intent intent = new Intent(this, MainActivity.class);
        World world = getIntent().getParcelableExtra("world");
        intent.putExtra("world", world);
        this.startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);
        relativeDistance = findViewById(R.id.reldistance);
        imageRecognition = findViewById(R.id.imgrecog);
        barchartMax = findViewById(R.id.barchartmax);
        worldSize = findViewById(R.id.worldsize);
        listAllColorAndValues = findViewById(R.id.listcolorandvalues);
        useSpinner = findViewById(R.id.customConstants);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        updateAllSettings();
    }

    private void updateAllSettings() {
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);


        boolean booldist = settings.getBoolean(Constants.RELATIVE_DISTANCE_OR_REAL, true);
        boolean boolrecog = settings.getBoolean(Constants.IMAGE_RECOGNITION_OR_TAPPING, true);
        boolean useCustom = settings.getBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true);
        String strbar = String.valueOf((settings.getInt(Constants.BARCHART_MAXIMUM_VALUE, 200)));
        String worldsize = String.valueOf(settings.getInt(Constants.WORLD_SIZE, 100));
        relativeDistance.setChecked(booldist);
        imageRecognition.setChecked(boolrecog);
        useSpinner.setChecked(useCustom);
        barchartMax.setText(strbar);
        worldSize.setText(worldsize);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ColorAndValue>>() {
        }.getType();
        String jsonColorAndValues = settings.getString(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART, "h");
        colorAndValues = gson.fromJson(jsonColorAndValues, type);
        Collections.sort(colorAndValues);

        updateColorAndValues();
    }

    @Override
    public void deletePressed(ColorAndValue colorAndValue) {
        colorAndValues.remove(colorAndValue);
        if (colorAndValues.isEmpty()) {
            colorAndValues.add(new ColorAndValue(-65536, Integer.MAX_VALUE));
            Snackbar.make(findViewById(android.R.id.content), "All items were deleted, added max value item.", Snackbar.LENGTH_LONG).show();
        }
        updateColorAndValues();

    }

    @Override
    public void changeColor(ColorAndValue colorAndValue, int color) {
        colorAndValues.remove(colorAndValue);
        colorAndValue.setColor(color);
        colorAndValues.add(colorAndValue);
        Collections.sort(colorAndValues);
        updateColorAndValues();
    }

    @Override
    public void changeValue(ColorAndValue colorAndValue, int value) {
        colorAndValues.remove(colorAndValue);
        colorAndValue.setValue(value);
        colorAndValues.add(colorAndValue);
        Collections.sort(colorAndValues);
        updateColorAndValues();
    }

    @Override
    protected void onPause() {

        saveSettings();

        super.onPause();
    }

    private void saveSettings() {
        Gson gson = new Gson();
        String colorAndValuesJson = gson.toJson(colorAndValues);
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);

        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putBoolean(Constants.IMAGE_RECOGNITION_OR_TAPPING, imageRecognition.isChecked());
        preferencesEditor.putInt(Constants.BARCHART_MAXIMUM_VALUE, Integer.parseInt(barchartMax.getText().toString()));
        preferencesEditor.putInt(Constants.WORLD_SIZE, Integer.parseInt(worldSize.getText().toString()));
        preferencesEditor.putBoolean(Constants.RELATIVE_DISTANCE_OR_REAL, relativeDistance.isChecked());
        preferencesEditor.putBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, useSpinner.isChecked());
        preferencesEditor.putString(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART, colorAndValuesJson);

        preferencesEditor.apply();
    }

    private void updateColorAndValues() {
        Collections.sort(colorAndValues);
        ColorAndValueAdapter myAdapter = new ColorAndValueAdapter(colorAndValues, SettingsActivity.this);
        myAdapter.setCallback(this);
        listAllColorAndValues.setAdapter(myAdapter);

    }


    public void resetSettings(View view) {
        ArrayList<ColorAndValue> colorAndValues = new ArrayList<>();
        //TODO better reset values
        colorAndValues.add(new ColorAndValue(-16711936, 50)); //green
        colorAndValues.add(new ColorAndValue(-256, 100)); //yellow
        colorAndValues.add(new ColorAndValue(-32768, 150)); // orange
        colorAndValues.add(new ColorAndValue(-65536, Integer.MAX_VALUE)); //red
        Collections.sort(colorAndValues);

        Gson gson = new Gson();
        String colorAndValuesJson = gson.toJson(colorAndValues);

        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putBoolean(Constants.IMAGE_RECOGNITION_OR_TAPPING, true);
        preferencesEditor.putInt(Constants.BARCHART_MAXIMUM_VALUE, 200);
        preferencesEditor.putInt(Constants.WORLD_SIZE, 100);
        preferencesEditor.putBoolean(Constants.RELATIVE_DISTANCE_OR_REAL, true);
        preferencesEditor.putString(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART, colorAndValuesJson);
        preferencesEditor.putBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true);

        preferencesEditor.apply();
        updateAllSettings();
    }
}
