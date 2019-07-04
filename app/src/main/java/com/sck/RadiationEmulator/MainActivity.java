package com.sck.RadiationEmulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void goToScanner(View view) {
        Intent intent = new Intent(this, ARscanner.class);
        this.startActivity(intent);

    }

    public void goToSetUp(View view) {
        Intent intent = new Intent(this, setUpWorld.class);
        this.startActivity(intent);
    }
}
