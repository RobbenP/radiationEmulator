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
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.sck.RadiationEmulator.Model.World;
import com.sck.common.helpers.SnackbarHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ARscanner extends AppCompatActivity {
    // if set to true it will use image recognition to set start and end point
    // if set to false it will use a tap on the screen
    public static final boolean USE_AUGMENTED_IMAGES = true;
    private static final String TAG = ARscanner.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    //if set to true it wil use distancing in our virtual world, if set tot true it will use real world distancing
    private static final boolean USE_RELATIVE_DISTANCES = true;
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();
    private World world;
    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    //private List<Node> myNodes = new ArrayList<>();
    private Node start = null;
    private Node end = null;
    private TextView myTextView;
    private ImageView fitToScanView;

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
        //if a world already exists fetch it, if not build a new world
        world = (World) getIntent().getSerializableExtra("world");
        if (world == null) world = new World();

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        myTextView = findViewById(R.id.textView);
        //myTextView.setVisibility(View.INVISIBLE);
        fitToScanView = findViewById(R.id.image_view_fit_to_scan);
        fitToScanView.setVisibility(View.INVISIBLE);

        if (!USE_AUGMENTED_IMAGES) setupTapOnScreendForStartAndEnd();


        //add an On Update listener -> everytime the camera moves we will execute cameraMoved
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            arFragment.onUpdate(frameTime);
            cameraMoved();
        });
    }

    private void setupImageRecognitionForStartAndEnd() {
    }

    private void setupTapOnScreendForStartAndEnd() {
        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        //todo find a better 3D model, instead of the android icon
        ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                //.setSource(this, Uri.parse("exclamation.sfb"))
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


        /**
         * Listener for detecting taps on recognized surfaces
         *
         * only listens for 2 taps, the starting point and the end point. If those are already found ignore tap
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
        andy.setParent(anchorNode);
        andy.setRenderable(render);
        //if (myNodes.isEmpty()) myTextView.setVisibility(View.VISIBLE);
        myTextView.setVisibility(View.VISIBLE);
        //myNodes.add(andy);
        Log.d("Mijn debug", "amount of nodes: " + arFragment.getArSceneView().getScene().getChildren().size());
        return andy;
    }

    /**
     * Executes on every update
     */
    private void cameraMoved() {

        if (USE_AUGMENTED_IMAGES) recognizeImage();

        Log.d("Mijn debug", "cameraMoved: " + arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().toString());
        Log.d("Mijn debug", "amount of nodes: " + arFragment.getArSceneView().getScene().getChildren().size());
//        int i = 0;
//        for (Node node : arFragment.getArSceneView().getScene().getChildren()){
//
        myTextView.setText("");

        if (start != null && end != null) {
            double measurementHere = world.GetMeasurementHere(World.myRelativeCoords(start, end, arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose()));
            String text = "Measurement here = " + measurementHere + "\n";
            myTextView.setText(text);
        }

        try {
            Log.d("Mijn debug", "Node start with position " + start.getWorldPosition().toString());
            Log.d("Mijn debug", "Node end with position " + end.getWorldPosition().toString());
        } catch (NullPointerException e) {
            Log.d("Mijn debug", "waiting for start and end points");
        }
//            i++;
//        }
        if (start != null && end != null) {
            String text = "";
            double[] myCoords;
            double[] nodeCoords = new double[2];
            if (USE_RELATIVE_DISTANCES) {
                myCoords = World.myRelativeCoords(start, end, arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose());

            } else {
                myCoords[0] = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx();
                myCoords[1] = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tz();

            }

            if (USE_RELATIVE_DISTANCES) {
                nodeCoords = World.myRelativeCoords(start, end, start);

            } else {
                nodeCoords[0] = start.getWorldPosition().x;
                nodeCoords[1] = start.getWorldPosition().z;
            }
            text += "2D distance to start is " + String.format("%.2f", World.calculateDistance(myCoords[0], myCoords[1], nodeCoords[0], nodeCoords[1])) + "\n";
            //text += " 3D distance is " + String.format("%.2f", World.calculateDistance(arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tz(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().ty(), node.getWorldPosition().x, node.getWorldPosition().z, node.getWorldPosition().y)) + "\n";
            if (USE_RELATIVE_DISTANCES) {
                nodeCoords = World.myRelativeCoords(start, end, end);

            } else {
                nodeCoords[0] = end.getWorldPosition().x;
                nodeCoords[1] = end.getWorldPosition().z;
            }
            text += "2D distance to end is " + String.format("%.2f", World.calculateDistance(myCoords[0], myCoords[1], nodeCoords[0], nodeCoords[1])) + "\n";
            //text += " 3D distance is " + String.format("%.2f", World.calculateDistance(arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tx(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().tz(), arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().ty(), node.getWorldPosition().x, node.getWorldPosition().z, node.getWorldPosition().y)) + "\n";


            text += "My relative coordinates are " + String.format("%.2f", myCoords[0]) + "," + String.format("%.2f", myCoords[1]) + "\n";
            //tex += "Distance between nodes is " + Math.pow(start.getWorldPosition().x - end.getWorldPosition().z,2 ) ;


            myTextView.append(text);
        } else myTextView.append("Waiting for start and end point te be set. \n");

    }

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
                    String text = "Detected Image " + augmentedImage.getIndex() + " but image is in paused state";
                    SnackbarHelper.getInstance().showMessage(this, text);
                    Log.d("paused", "recognizeImage: " + augmentedImage.getCenterPose().toString());
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
                        if (augmentedImage.getName().equals("start")) start = node;
                        else if (augmentedImage.getName().equals("stop")) end = node;
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("world", world);
        this.startActivity(intent);
    }

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
