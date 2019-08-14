/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sck.RadiationEmulator;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sck.RadiationEmulator.Model.AugmentedImageNode;
import com.sck.RadiationEmulator.Model.ColorAndValue;
import com.sck.RadiationEmulator.Model.Constants;
import com.sck.RadiationEmulator.Model.World;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ARscanner extends AppCompatActivity {
    public static final String MODEL_3D = "21386_Exclamation_Point_v1.sfb";
    private static final String TAG = ARscanner.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    // if set to true it will use image recognition to set start and end point
    // if set to false it will use a tap on the screen
    public static boolean USE_AUGMENTED_IMAGES;
    //if set to true it wil use distancing in our virtual world, if set tot true it will use real world distancing
    private static boolean USE_RELATIVE_DISTANCES;
    private static int BARCHART_MAXIMUM;
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();
    private World world;
    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    //private List<Node> myNodes = new ArrayList<>();
    private Node start = null;
    private Node end = null;
    private TextView myTextView;
    private ImageView fitToScanView;
    private BarChart barChart;
    private TextView measurement;
    private Integer waitTimeInMiliSeconds;
    private long lastBeep = 0;

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        USE_AUGMENTED_IMAGES = settings.getBoolean(Constants.IMAGE_RECOGNITION_OR_TAPPING, true);
        USE_RELATIVE_DISTANCES = settings.getBoolean(Constants.RELATIVE_DISTANCE_OR_REAL, true);
        BARCHART_MAXIMUM = settings.getInt(Constants.BARCHART_MAXIMUM_VALUE, 200);

        //if a world already exists fetch it, if not build a new world
        world = getIntent().getParcelableExtra("world");
        if (world == null) world = World.getInstance();
        //initialize all the view elements
        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        myTextView = findViewById(R.id.textView);
        fitToScanView = findViewById(R.id.image_view_fit_to_scan);
        fitToScanView.setVisibility(View.INVISIBLE);
        barChart = findViewById(R.id.chart);
        barChart.setVisibility(View.INVISIBLE);
        measurement = findViewById(R.id.txtMeasurement);
        measurement.setVisibility(View.INVISIBLE);

        // Is USE_AUGMENTED_IMAGES is set to true no need to initialize the an tap
        // listener
        if (!USE_AUGMENTED_IMAGES) setupTapOnScreendForStartAndEnd();


        //add an On Update listener -> everytime the camera moves we will execute cameraMoved
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            arFragment.onUpdate(frameTime);
            cameraMoved();
        });
    }


    /**
     * Sets up the barchart that displays the measurement on screen
     *
     * @param measurement the measurement we wish to display on the barchart
     */
    private void setupBarChart(double measurement) {
        barChart.setVisibility(View.VISIBLE);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) measurement));
        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(getColorBasedOnMeasurement(measurement));


        BarData data = new BarData(set);
        data.setBarWidth(2f);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getAxisLeft().setAxisMaximum(BARCHART_MAXIMUM);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.getXAxis().setDrawLabels(false);
        barChart.getDescription().setEnabled(false);
        // Hide graph legend
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
    }

    /**
     * Returns the right color depending on how dangerous the measurement is
     * If the measurement exceeds the highest value in the list it will take
     * the color of the highest value in the last
     *
     * @param measurement the measurement of which we need to know to color
     * @return a color based on the settings
     */
    private int getColorBasedOnMeasurement(double measurement) {

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ColorAndValue>>() {
        }.getType();
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        String jsonColorAndValues = settings.getString(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART, "");
        ArrayList<ColorAndValue> colorAndValues = gson.fromJson(jsonColorAndValues, type);
        Collections.sort(colorAndValues);
        for (ColorAndValue cv : colorAndValues) {
            if (measurement < cv.getValue()) return cv.getColor();
        }
        return colorAndValues.get(colorAndValues.size() - 1).getColor();

    }

    /**
     * if you get higher measurements it should beep faster
     * uses same logic as the colorcalculator
     *
     * @param measurementHere measurement we base the beep interval on
     */
    private void setUpBeeps(double measurementHere) { //todo test this and maybe improve it, written in a hurry. Also add an option to disable beeps in the settings
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ColorAndValue>>() {
        }.getType();
        SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
        String jsonColorAndValues = settings.getString(Constants.LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART, "");
        ArrayList<ColorAndValue> colorAndValues = gson.fromJson(jsonColorAndValues, type);
        Collections.sort(colorAndValues);
        int len = colorAndValues.size();
        //todo these local values should probably be changeable by the user in the settings
        int slowestInterval = 2000; //2 sec
        int fastestInterval = 400; // 0.4 sec
        int lowestInterval = 300;
        int step = (slowestInterval - fastestInterval) / len;
        for (ColorAndValue cv : colorAndValues) {
            if (measurementHere < cv.getValue()) {
                int i = colorAndValues.indexOf(cv);
                waitTimeInMiliSeconds = slowestInterval - (i * step);
                return;
            }
        }
        waitTimeInMiliSeconds = lowestInterval; //if it exceeds all values in the list
    }

    /**
     * Sets up the on tap listener to place the start and end with taps on the screen
     */
    private void setupTapOnScreendForStartAndEnd() {
        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().

        ModelRenderable.builder()
                //.setSource(this, R.raw.andy)
                .setSource(this, Uri.parse(MODEL_3D))
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });


        /*
          Listener for detecting taps on recognized surfaces

          only listens for 2 taps, the starting point and the end point. If those are already found ignore tap
         */
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }

                    if (start == null) {
                        start = addToScene(hitResult, andyRenderable);
                    } else if (end == null) {
                        ModelRenderable redAndy = andyRenderable.makeCopy();
                        Material red = redAndy.getMaterial().makeCopy();
                        red.setFloat3("baseColor", 255, 0, 0);
                        redAndy.setMaterial(red);
                        end = addToScene(hitResult, redAndy);
                    } else {
                        // Turn off the plane discovery since we've found our start and end
                        arFragment.getPlaneDiscoveryController().hide();
                        arFragment.getPlaneDiscoveryController().setInstructionView(null);
                        arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
                    }


                });
    }

    /**
     * @param hitResult Position where the renderable has to be placed
     * @param render    3D object that has to be placed
     * @return Node of the newly placed object
     */
    private Node addToScene(HitResult hitResult, ModelRenderable render) {
        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable andy and add it to the anchor.
        Node andy = new Node();
        andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), -90f));
        andy.setParent(anchorNode);
        andy.setRenderable(render);
        //if (myNodes.isEmpty()) myTextView.setVisibility(View.VISIBLE);
        myTextView.setVisibility(View.VISIBLE);
        //myNodes.add(andy);
        Log.d("Mijn debug", "amount of nodes: " + arFragment.getArSceneView().getScene().getChildren().size());
        return andy;
    }

    /**
     * Executes on every update. Updates all the text in the view and starts the image recognition
     */
    private void cameraMoved() {
        beepWhenTheMeasurementsGetHigh();
        if (USE_AUGMENTED_IMAGES) recognizeImage();

//        Log.d("Mijn debug", "cameraMoved: " + arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().toString());
//        Log.d("Mijn debug", "amount of nodes: " + arFragment.getArSceneView().getScene().getChildren().size());
//        try {
//            Log.d("Mijn debug", "Node start with position " + start.getWorldPosition().toString());
//            Log.d("Mijn debug", "Node end with position " + end.getWorldPosition().toString());
//        } catch (NullPointerException e) {
//            Log.d("Mijn debug", "waiting for start and end points");
//        }
        myTextView.setText("");

        if (start != null && end != null) {
            // does the measurement and displays it
            SharedPreferences settings = getSharedPreferences(Constants.SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME, MODE_PRIVATE);
            int worldSize = settings.getInt(Constants.WORLD_SIZE, 100);
            double measurementHere;
            if (USE_RELATIVE_DISTANCES)
                measurementHere = world.getMeasurementHere(World.myRelativeCoords(start, end, arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose(), worldSize), settings.getBoolean(Constants.USE_MCI_FOR_ACTIVITY_OR_BQ, true));
            else
                measurementHere = world.getMeasurementHere(new double[]{arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().ty()
                }, settings.getBoolean(Constants.USE_MCI_FOR_ACTIVITY_OR_BQ, true));
            ;
            String text = "Measurement here = " + measurementHere + "\n";
            myTextView.setText(text);
            setupBarChart(measurementHere);
            setUpBeeps(measurementHere);
            measurement.setVisibility(View.VISIBLE);
            measurement.setText(String.format(Locale.ENGLISH, "%.2f", measurementHere) + "μSv/h");
            measurement.setTextColor(getColorBasedOnMeasurement(measurementHere));


            text = "";
            double[] myCoords = new double[2];
            double[] nodeCoords = new double[2];
            if (USE_RELATIVE_DISTANCES) {
                myCoords = World.myRelativeCoords(start, end, arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose(), worldSize);
                nodeCoords = World.myRelativeCoords(start, end, start, worldSize);

            } else {
                myCoords[0] = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx();
                myCoords[1] = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tz();
                nodeCoords[0] = start.getWorldPosition().x;
                nodeCoords[1] = start.getWorldPosition().z;
            }


            text += "2D distance to start is " + String.format(Locale.ENGLISH, "%.2f", World.calculateDistance(myCoords[0], myCoords[1], nodeCoords[0], nodeCoords[1])) + "\n";
            //text += " 3D distance is " + String.format("%.2f", World.calculateDistance(arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tz(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().ty(), node.getWorldPosition().x, node.getWorldPosition().z, node.getWorldPosition().y)) + "\n";
            if (USE_RELATIVE_DISTANCES) {
                nodeCoords = World.myRelativeCoords(start, end, end, worldSize);

            } else {
                nodeCoords[0] = end.getWorldPosition().x;
                nodeCoords[1] = end.getWorldPosition().z;
            }
            text += "2D distance to end is " + String.format(Locale.ENGLISH, "%.2f", World.calculateDistance(myCoords[0], myCoords[1], nodeCoords[0], nodeCoords[1])) + "\n";
            //text += " 3D distance is " + String.format("%.2f", World.calculateDistance(arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tz(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().ty(), node.getWorldPosition().x, node.getWorldPosition().z, node.getWorldPosition().y)) + "\n";


            text += "My relative coordinates are " + String.format(Locale.ENGLISH, "%.2f", myCoords[0]) + "," + String.format(Locale.ENGLISH, "%.2f", myCoords[1]) + "\n";
            //tex += "Distance between nodes is " + Math.pow(start.getWorldPosition().x - end.getWorldPosition().z,2 ) ;

            //TODO maybe add some useful text here
            myTextView.append(text);
        } else myTextView.append("Waiting for start and end point te be set. \n");

    }


    private void beepWhenTheMeasurementsGetHigh() {
        if (waitTimeInMiliSeconds != null) {
            long timeNow = System.currentTimeMillis();
            if (timeNow - lastBeep >= waitTimeInMiliSeconds) {
                lastBeep = System.currentTimeMillis();
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
            }
        }
    }

    /**
     * Tries to recognize an image loaded in the imagedatabase if found keeps track of it
     * and puts a 3d object on it
     */
    private void recognizeImage() {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    //String text = "Detected Image " + augmentedImage.getIndex() + " but image is in paused state";
                    //SnackbarHelper.getInstance().showMessage(this, text);
                    //Log.d("paused", "recognizeImage: " + augmentedImage.getCenterPose().toString());
                    break;

                case TRACKING:
                    if (start != null && end != null) {
                        // Have to switch to UI Thread to update View.
                        fitToScanView.setVisibility(View.GONE);
                    }

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        AugmentedImageNode node = new AugmentedImageNode(this);
                        node.setImage(augmentedImage);
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
                        String name = augmentedImage.getName();
                        if (name.equals("start")) start = node;
                        else if (name.equals("stop")) end = node;
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }

    /**
     * overriding default method to try and make sure we do not lose our world
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit scanner?")
                .setMessage("Are you sure you want to leave the scanner, start and end points will be lost?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("world", world);
                    this.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .create().show();

    }

    /**
     * If we lose our images after returning show the fitToScanView again
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (USE_AUGMENTED_IMAGES) {
            if (augmentedImageMap.isEmpty()) {
                fitToScanView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void backToMain(View view) {
        onBackPressed();
    }

}
