package com.sck.RadiationEmulator;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.sck.RadiationEmulator.Model.Constants;
import com.sck.RadiationEmulator.Model.EmulatedMeasurement;
import com.sck.RadiationEmulator.Model.RadiationSource;
import com.sck.RadiationEmulator.Model.World;
import com.sck.common.helpers.EmulatedMeasurementAdapter;
import com.sck.common.helpers.RadiationSourceAdapter;

import java.util.ArrayList;

public class SetUpWorld extends AppCompatActivity {

    private static int WORLDSIZE;
    private World world;
    private EditText xComponent;
    private EditText yComponent;
    private EditText radiationSourceActivity;
    private EditText radiationConstant;
    private ListView listAllMeasurements;
    private Spinner radiationConstantsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        WORLDSIZE = settings.getInt(Constants.WORLD_SIZE, 100);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_set_up_world);
        xComponent = findViewById(R.id.editX);
        yComponent = findViewById(R.id.editY);
        radiationSourceActivity = findViewById(R.id.editMeasure);
        radiationConstant = findViewById(R.id.radiationconstant);
        listAllMeasurements = findViewById(R.id.scrollListAll);
        listAllMeasurements.setVisibility(View.VISIBLE);
        radiationConstantsSpinner = findViewById(R.id.source);
        if (settings.getBoolean(Constants.USE_MCI_FOR_ACTIVITY_OR_BQ, true))
            radiationSourceActivity.setHint("Activity in mCi");
        else radiationSourceActivity.setHint("Activity in MBq");
        if (settings.getBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true)) {
            radiationConstantsSpinner.setVisibility(View.VISIBLE);
            radiationConstant.setVisibility(View.INVISIBLE);
            //todo add to constants
            ArrayList<RadiationSource> radiationSources = new ArrayList<>();

            radiationSources.add(new RadiationSource("Cesium-137", 3.3));
            radiationSources.add(new RadiationSource("Cobalt-57", 0.9));
            radiationSources.add(new RadiationSource("Cobalt-60", 13.2));
            radiationSources.add(new RadiationSource("Iodine-125", 0.7));
            radiationSources.add(new RadiationSource("Iodine-131", 2.2));
            radiationSources.add(new RadiationSource("Magnese-54", 4.7));
            radiationSources.add(new RadiationSource("Radium-226", 8.25));
            radiationSources.add(new RadiationSource("Sodium-22", 12));
            radiationSources.add(new RadiationSource("Sodium-24", 18.4));
            radiationSources.add(new RadiationSource("Zinc-65", 2.7));
            RadiationSourceAdapter myAdapter = new RadiationSourceAdapter(radiationSources, android.R.layout.simple_spinner_item, this);
            myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            radiationConstantsSpinner.setAdapter(myAdapter);
        } else {
            radiationConstantsSpinner.setVisibility(View.INVISIBLE);
            radiationConstant.setVisibility(View.VISIBLE);
        }
        world = getIntent().getParcelableExtra("world") == null ? World.getInstance() : getIntent().getParcelableExtra("world");
        updateMeasureList();

    }

    /**
     * If there are no measurements we display some text to explain how it works, if there are
     * measurements it hides it.
     */
    private void checkWhetherToShowExplanation() {
        TextView explanation = findViewById(R.id.explanation);
        if (world.getMeasurementsList().isEmpty()) {
            explanation.setVisibility(View.VISIBLE);
            explanation.setMovementMethod(new ScrollingMovementMethod());
            explanation.setText("The start and end point we set in Augmented Reality, are the start and end of a square (" + WORLDSIZE + " by " + WORLDSIZE + ") in our virtual world. Here you can set measurements using these virtual world coordinates." +
                    "\n\nAfter adding a measurement it will be shown in a list here, if you click on of the coordinates it will be removed from the list.");
        } else explanation.setVisibility(View.GONE);
    }

    /**
     * When the user has pressed submit this happens. It checks if all fields are filled in
     * and if the values are within our world (possibly we should allow values outside the world).
     * Also checks if it already has been added to avoid duplicates.
     */
    public void addToWorld(View v) {
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        if (xComponent.getText().toString().equals("") || yComponent.getText().toString().equals("") || radiationSourceActivity.getText().toString().equals("") || (radiationConstant.getText().toString().equals("") && !settings.getBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true))) {
            Snackbar.make(v, "Please fill in the fields!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        double x = Double.parseDouble(xComponent.getText().toString());
        double y = Double.parseDouble(yComponent.getText().toString());
        double sourceActivity = Double.parseDouble(radiationSourceActivity.getText().toString());
        if (!settings.getBoolean(Constants.USE_MCI_FOR_ACTIVITY_OR_BQ, true))
            sourceActivity = sourceActivity / 37;
        double constant;
        if (settings.getBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true)) {
            constant = ((RadiationSource) radiationConstantsSpinner.getSelectedItem()).getRadiationConstant();
        } else constant = Double.parseDouble(radiationConstant.getText().toString());
        boolean ret = false;
        if (x > WORLDSIZE) {
            Toast.makeText(this, "The X-coordinate cannot be bigger than " + WORLDSIZE + "!",
                    Toast.LENGTH_SHORT).show();
            xComponent.setText("");
            ret = true;
        }
        if (y > WORLDSIZE) {
            Toast.makeText(this, "The Y-coordinate cannot be bigger than " + WORLDSIZE + "!",
                    Toast.LENGTH_SHORT).show();
            yComponent.setText("");
            ret = true;
        }

        if (ret) return;
        EmulatedMeasurement toAdd = new EmulatedMeasurement(x, y, constant, sourceActivity);
        if (!world.addMeasurement(toAdd)) {
            Snackbar.make(v, "Already in list, not added", Snackbar.LENGTH_SHORT).show();
        }
        xComponent.setText("");
        yComponent.setText("");
        radiationSourceActivity.setText("");
        radiationConstant.setText("");
        updateMeasureList();
    }

    /**
     * Displays all of the measurements in a TextView
     */
    private void updateMeasureList() {
        EmulatedMeasurementAdapter myAdapter = new EmulatedMeasurementAdapter(world.getMeasurementsList(), getApplicationContext());
        listAllMeasurements.setAdapter(myAdapter);
        listAllMeasurements.setOnItemClickListener((adapterView, view, i, l) -> {
            EmulatedMeasurement measurement = world.getMeasurementsList().get(i);
            world.deleteMeasurement(measurement);
            Snackbar.make(view, measurement.toString() + " has been removed!", Snackbar.LENGTH_SHORT).show();
            updateMeasureList();
            checkWhetherToShowExplanation();
        });
        checkWhetherToShowExplanation();
    }

    /**
     * Goes to the scanner but displays a warning if the fields aren't empty
     */
    public void goToARscanner(View v) {
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        if (xComponent.getText().toString().equals("") && yComponent.getText().toString().equals("") && radiationSourceActivity.getText().toString().equals("") && (radiationConstant.getText().toString().equals("") || settings.getBoolean(Constants.USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM, true))) {
            Intent intent = new Intent(this, ARscanner.class);

            intent.putExtra("world", world);
            this.startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit setup?")
                    .setMessage("Some fields are not saved, are you sure you want to leave this page?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        Intent intent = new Intent(this, ARscanner.class);

                        intent.putExtra("world", world);
                        this.startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .create().show();
        }


    }

    /**
     * Empties the list of measurements and also empties the fields
     */
    public void clearWorld(View v) {
        xComponent.setText("");
        yComponent.setText("");
        radiationSourceActivity.setText("");
        radiationConstant.setText("");
        world.clearMeasurements();
        updateMeasureList();
    }

    /**
     * Pressing back goes to the MainActivity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("world", world);
        this.startActivity(intent);
    }
}
