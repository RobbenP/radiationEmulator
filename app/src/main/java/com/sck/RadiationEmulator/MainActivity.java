package com.sck.RadiationEmulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToScanner(View view){
        Intent intent = new Intent(this,ARscanner.class);
        this.startActivity(intent);

    }
}
