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
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.sck.RadiationEmulator.Model.World;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class ARscanner extends AppCompatActivity {
    private static final String TAG = ARscanner.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private List<Node> myNodes = new ArrayList<>();
    private Node start = null;
    private Node end = null;
    private TextView myTextView;


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

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        myTextView = findViewById(R.id.textView);
        //myTextView.setVisibility(View.INVISIBLE);


        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this, R.raw.andy)
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

        //add an On Update listener -> everytime the camera moves we will execute cameraMoved
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            arFragment.onUpdate(frameTime);
            cameraMoved();
        });
    }

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
        myNodes.add(andy);
        Log.d("Mijn debug", "amount of nodes: " + arFragment.getArSceneView().getScene().getChildren().size());
        return andy;
    }

    private void cameraMoved() {


        Log.d("Mijn debug", "cameraMoved: " + arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose().toString());
        Log.d("Mijn debug", "amount of nodes: " + arFragment.getArSceneView().getScene().getChildren().size());
//        int i = 0;
//        for (Node node : arFragment.getArSceneView().getScene().getChildren()){
//
        try {
            Log.d("Mijn debug", "Node start with position " + start.getWorldPosition().toString());
            Log.d("Mijn debug", "Node end with position " + end.getWorldPosition().toString());
        } catch (NullPointerException e) {
            Log.d("Mijn debug", "waiting for start and end points");
        }
//            i++;
//        }
        if (!myNodes.isEmpty()) {
            String text = "";
            for (Node node : myNodes) {
                text += "2D distance to node is " + String.format("%.2f", calculate2dDistanceFromNodeToCamera(node)) + " 3D distance is " + String.format("%.2f", calculate3dDistanceFromNodeToCamera(node)) + "\n";
            }
            try {
                double[] myCoords = World.myRelativeCoords(start, end, arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose());
                text += "My relative coordinates are " + String.format("%.2f", myCoords[0]) + "," + String.format("%.2f", myCoords[1]);
                //tex += "Distance between nodes is " + Math.pow(start.getWorldPosition().x - end.getWorldPosition().z,2 ) ;
            } catch (IllegalArgumentException e) {
                text += "Waiting for start and end point te be set. ";
            }

            myTextView.setText(text);
        }

    }

    private double calculate2dDistanceFromNodeToCamera(Node node) {
        Pose cameraPose = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose();

        //return Math.sqrt(Math.pow((cameraPose.tx()-node.getWorldPosition().x),2)+Math.pow(cameraPose.ty()-node.getWorldPosition().y,2));
        return World.calculateDistance(cameraPose.tx(), cameraPose.tz(), node.getWorldPosition().x, node.getWorldPosition().z);
    }

    private double calculate3dDistanceFromNodeToCamera(Node node) {
        Pose cameraPose = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose();

        //return Math.sqrt(Math.pow((cameraPose.tx()-node.getWorldPosition().x),2)+Math.pow(cameraPose.ty()-node.getWorldPosition().y,2)+Math.pow(cameraPose.tz()-node.getWorldPosition().z,2));
        return World.calculateDistance(cameraPose.tx(), cameraPose.ty(), cameraPose.tz(), node.getWorldPosition().x, node.getWorldPosition().y, node.getWorldPosition().z);
    }


    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }


}
