package com.sck.RadiationEmulator.Model;

import com.google.ar.core.Pose;
import com.google.ar.sceneform.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that keeps track of the world: stores a list of all the emulatedMeasurements and the world size
 * It also provides some methods to do calculations in the world
 */
//TODO instead of serializable let it implement parcelable, better for android performance
public class World implements Serializable {
    private static final int WORLD_SIZE = 100;
    private static final long serialVersionUID = 6942458360697049542L;
    private List<EmulatedMeasurement> measurementsList = new ArrayList<>();

    public World() {
    }

    public static int getWorldSize() {
        return WORLD_SIZE;
    }

    /**
     * Calculates the coordinates from the real world camera coordinates to
     * the coordinates in this world, based on 2 real world nodes start and end.
     *
     * @param start  start is a node in the real world that will be used as the
     *               origin for this world
     * @param end    end is a node in the real world that will be used as the
     *               point (WORLD_SIZE,WORLD_SIZE) in this world
     * @param camera camera is the pose of the camera in the real world, these
     *               coordinates will be converted to the coordinates in this world
     * @return returns the coordinates in this world of the camera
     * in a 2 dimensional list
     */
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

    public static double[] myRelativeCoords(Node start, Node end, Node camera) {
        double[] result = new double[2];
        if (end == null) {
            throw new IllegalArgumentException("Start and end have to be set first!");
        }

        double xMultiplier = WORLD_SIZE / (end.getWorldPosition().x - start.getWorldPosition().x);
        double zMultiplier = WORLD_SIZE / (end.getWorldPosition().z - start.getWorldPosition().z);
        result[0] = (camera.getWorldPosition().x - start.getWorldPosition().x) * xMultiplier;
        result[1] = (camera.getWorldPosition().z - start.getWorldPosition().z) * zMultiplier;

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

    /**
     * Adds a EmulatedMeasurement to the list of EmulatedMeasurements of this world, if it
     * has not been added yet.
     *
     * @param measurement The measurement that needs to be added
     * @return <code>true</code> if the measurement is not in the EmulatedMeasurementlist
     * <code>false</code> if the measurement is already in the EmulatedMeasurementlist
     */
    public boolean addMeasurement(EmulatedMeasurement measurement) {
        if (!measurementsList.contains(measurement)) {
            measurementsList.add(measurement);
            return true;
        } else return false;


    }

    public void clearMeasurements() {
        measurementsList.clear();
    }

    public void deleteMeasurement(EmulatedMeasurement measurement) {
        measurementsList.remove(measurement);
    }
//TODO now it just uses the distance to EmulatedMeasurements to calculate the measurement, this probably has to be changed to a more scientific calculation

    /**
     * Calculates the measurement in a point in this world, taking into account all of
     * the EmulatedMeasurements. Does not use realworld distance but distance in this world
     *
     * @param myRelativeLocation The location where there has to be a measurement
     * @return The measurement on myRelativeLocation
     */
    public double GetMeasurementHere(double[] myRelativeLocation) {
        if (measurementsList.isEmpty()) {
            return 0;
        } else {
            double result = 0;
            for (EmulatedMeasurement m : measurementsList) {
                double temp = m.getMeasurement() - calculateDistance(myRelativeLocation[0], myRelativeLocation[1], m.getX(), m.getY());
                result += temp < 0 ? 0 : temp;
            }
            return result;
        }

    }
}
