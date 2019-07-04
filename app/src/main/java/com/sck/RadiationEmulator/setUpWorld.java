package com.sck.RadiationEmulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sck.RadiationEmulator.Model.EmulatedMeasurement;
import com.sck.RadiationEmulator.Model.World;

public class setUpWorld extends AppCompatActivity {

    private World world;
    private EditText xComponent;
    private EditText yComponent;
    private EditText measureComponent;
    private TextView listAllMeasurements;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_world);
        xComponent = findViewById(R.id.editX);
        yComponent = findViewById(R.id.editY);
        measureComponent = findViewById(R.id.editMeasure);
        listAllMeasurements = findViewById(R.id.allMeasurements);
        if (getIntent().getSerializableExtra("world") == null)
            world = new World();
        else {
            world = (World) getIntent().getSerializableExtra("world");
            updateMeasureList();
        }

    }

    public void addToWorld(View v) {

        if (xComponent.getText().toString().equals("") || yComponent.getText().toString().equals("") || measureComponent.getText().toString().equals("")) {
            Snackbar.make(v, "Please fill in the fields!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        double x = Double.parseDouble(xComponent.getText().toString());
        double y = Double.parseDouble(yComponent.getText().toString());
        double m = Double.parseDouble(measureComponent.getText().toString());

        EmulatedMeasurement toAdd = new EmulatedMeasurement(x, y, m);
        if (!world.addMeasurement(toAdd)) {
            Snackbar.make(v, "Already in list, not added", Snackbar.LENGTH_SHORT).show();
        }
        updateMeasureList();
    }

    private void updateMeasureList() {
        String measureList = "";
        for (EmulatedMeasurement em : world.getMeasurementsList()) {
            measureList += em.toString() + "\n";
        }
        listAllMeasurements.setText(measureList);
    }

    public void goToARscanner(View v) {
        Intent intent = new Intent(this, ARscanner.class);
        intent.putExtra("world", world);
        this.startActivity(intent);

    }

    public void clearWorld(View v) {
        world.clearMeasurements();
        listAllMeasurements.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("world", world);
        this.startActivity(intent);
    }
}
