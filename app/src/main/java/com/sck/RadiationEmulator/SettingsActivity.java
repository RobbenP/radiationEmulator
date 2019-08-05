package com.sck.RadiationEmulator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sck.RadiationEmulator.Model.ColorAndValue;
import com.sck.common.helpers.ColorAndValueAdapter;

public class SettingsActivity extends AppCompatActivity implements ColorAndValueAdapter.ColorAndValueCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void deletePressed(ColorAndValue colorAndValue) {
        //todo
    }

    @Override
    public void changeColor(ColorAndValue colorAndValue, int color) {

    }

    @Override
    public void changeValue(ColorAndValue colorAndValue, int value) {

    }

    @Override
    protected void onPause() {
        //todo save all the data
        super.onPause();
    }
}
