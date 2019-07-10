package com.sck.RadiationEmulator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sck.RadiationEmulator.Model.World;

public class MainActivity extends AppCompatActivity {

    private World world;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if a world already exists fetch it
        world = getIntent().getParcelableExtra("world");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

    }

    public void goToScanner(View view) {
        Intent intent = new Intent(this, ARscanner.class);
        intent.putExtra("world", world);
        this.startActivity(intent);

    }

    public void goToSetUp(View view) {
        Intent intent = new Intent(this, setUpWorld.class);
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
