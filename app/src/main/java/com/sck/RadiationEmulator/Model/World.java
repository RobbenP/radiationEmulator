package com.sck.RadiationEmulator.Model;

import com.google.ar.core.Pose;
import com.google.ar.sceneform.Node;

import java.util.ArrayList;
import java.util.List;

public class World {
    private static final int WORLD_SIZE = 100;
    private List<EmulatedMeasurement> measurementsList = new ArrayList<>();

    public static int getWorldSize() {
        return WORLD_SIZE;
    }

    public static double[] myRelativeCoords(Node start, Node end, Pose camera) {
        double[] result = new double[2];
        if (end == null) {
            throw new IllegalArgumentException("Start and end have to be set first!");
        }

        double xMultiplier = WORLD_SIZE / (end.getWorldPosition().x - start.getWorldPosition().x);
        double zMultiplier = WORLD_SIZE / (end.getWorldPosition().z - start.getWorldPosition().z);
        result[0] = (camera.tx() - start.getWorldPosition().x) * xMultiplier;
        result[1] = (camera.tz() - start.getWorldPosition().z) * zMultiplier;

        return result;
    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        dx = dx * dx;
        dy = dy * dy;
        return Math.sqrt(dx + dy);
    }

    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        dx = dx * dx;
        dy = dy * dy;
        dz = dz * dz;
        return Math.sqrt(dx + dy + dz);
    }

    public List<EmulatedMeasurement> getMeasurementsList() {
        return measurementsList;
    }

    public void addMeasurement(EmulatedMeasurement measurement) {
        if (!measurementsList.contains(measurement)) measurementsList.add(measurement);
    }

    public void deleteMeasurement(EmulatedMeasurement measurement) {
        measurementsList.remove(measurement);
    }

    public double GetMeasurementHere(double[] myRelativeLocation) {
        if (measurementsList.isEmpty()) {
            return 0;
        } else {
            double result = 0;
            for (EmulatedMeasurement m : measurementsList)
                result += calculateDistance(myRelativeLocation[0], myRelativeLocation[1], m.getX(), m.getY());
            return result;
        }

    }
}
