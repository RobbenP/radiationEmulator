package com.sck.RadiationEmulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sck.RadiationEmulator.Model.EmulatedMeasurement;
import com.sck.RadiationEmulator.Model.World;
import com.sck.common.helpers.EmulatedMeasurementAdapter;

public class setUpWorld extends AppCompatActivity {

    private World world;
    private EditText xComponent;
    private EditText yComponent;
    private EditText measureComponent;
    private ListView listAllMeasurements;

    private EmulatedMeasurementAdapter myAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_world);
        xComponent = findViewById(R.id.editX);
        yComponent = findViewById(R.id.editY);
        measureComponent = findViewById(R.id.editMeasure);
        listAllMeasurements = findViewById(R.id.scrollListAll);
        listAllMeasurements.setVisibility(View.VISIBLE);
        if (getIntent().getParcelableExtra("world") == null)
            world = new World();
        else {
            world = getIntent().getParcelableExtra("world");
            updateMeasureList();
        }

    }

    /**
     * When the user has pressed submit this happens. It checks if all fields are filled in
     * and if the values are within our world (possibly we should allow values outside the world).
     * Also checks if it already has been added to avoid duplicates.
     */
    public void addToWorld(View v) {

        if (xComponent.getText().toString().equals("") || yComponent.getText().toString().equals("") || measureComponent.getText().toString().equals("")) {
            Snackbar.make(v, "Please fill in the fields!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        double x = Double.parseDouble(xComponent.getText().toString());
        double y = Double.parseDouble(yComponent.getText().toString());
        double m = Double.parseDouble(measureComponent.getText().toString());
        if (y > World.getWorldSize()) {
            Toast.makeText(this, "The Y-coordinate cannot be bigger than " + World.getWorldSize() + "!",
                    Toast.LENGTH_LONG).show();
            yComponent.setText("");
            return;
        } else if (x > World.getWorldSize()) {
            Toast.makeText(this, "The X-coordinate cannot be bigger than " + World.getWorldSize() + "!",
                    Toast.LENGTH_LONG).show();
            xComponent.setText("");
            return;
        }

        EmulatedMeasurement toAdd = new EmulatedMeasurement(x, y, m);
        if (!world.addMeasurement(toAdd)) {
            Snackbar.make(v, "Already in list, not added", Snackbar.LENGTH_SHORT).show();
        }
        xComponent.setText("");
        yComponent.setText("");
        measureComponent.setText("");
        updateMeasureList();
    }

    /**
     * Displays all of the measurements in a TextView
     */
    //TODO change this to a listview so when pressed they can be removed
    private void updateMeasureList() {
//        String measureList = "";
//        for (EmulatedMeasurement em : world.getMeasurementsList()) {
//            measureList += em.toString() + "\n";
//        }
        //listAllMeasurements.setText(measureList);
        myAdapter = new EmulatedMeasurementAdapter(world.getMeasurementsList(), getApplicationContext());
        listAllMeasurements.setAdapter(myAdapter);
        listAllMeasurements.setOnItemClickListener((adapterView, view, i, l) -> {
            EmulatedMeasurement measurement = world.getMeasurementsList().get(i);
            world.deleteMeasurement(measurement);
            Snackbar.make(view, measurement.toString() + " has been removed!", Snackbar.LENGTH_SHORT).show();
            updateMeasureList();
        });

    }

    public void goToARscanner(View v) {
        Intent intent = new Intent(this, ARscanner.class);

        intent.putExtra("world", world);
        this.startActivity(intent);

    }

    public void clearWorld(View v) {
        world.clearMeasurements();
        updateMeasureList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("world", world);
        this.startActivity(intent);
    }
}
