/*
 * Copyright 2018 Google LLC
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

package com.sck.RadiationEmulator.Model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual image
 * on the augmented image trackable.
 */
public class AugmentedImageNode extends AnchorNode {

    private static final String TAG = "AugmentedImageNode";

    private static CompletableFuture<ModelRenderable> startAndStop;

    // The augmented image represented by this node.
    private AugmentedImage image;

    public AugmentedImageNode(Context context) {
        // Upon construction, start loading the model
        if (startAndStop == null) {
            startAndStop =
                    ModelRenderable.builder()
                            //.setSource(context, R.raw.andy)
                            .setSource(context, Uri.parse("21386_Exclamation_Point_v1.sfb"))
                            .build();
        }
    }

    public AugmentedImage getImage() {
        return image;
    }

    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image. The corners are then positioned based on the
     * extents of the image. There is no need to worry about world coordinates since everything is
     * relative to the center of the image, which is the parent node of the corners.
     */
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void setImage(AugmentedImage image) {
        this.image = image;

        // If any of the models are not loaded, then recurse when all are loaded.
        if (!startAndStop.isDone()) {
            CompletableFuture.allOf(startAndStop)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
        }

        // Set the anchor based on the center of the image.
        Anchor anchor = image.createAnchor(image.getCenterPose());
        setAnchor(anchor);
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(this);

        Node andy = new Node();
        //Turns the 3D image 90degrees
        andy.setLocalRotation(Quaternion.axisAngle(new Vector3(1f, 0, 0), -90f));

        andy.setParent(anchorNode);
        andy.setRenderable(startAndStop.getNow(null));


    }
}
