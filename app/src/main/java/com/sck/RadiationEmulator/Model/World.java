package com.sck.RadiationEmulator.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.ar.core.Pose;
import com.google.ar.sceneform.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton class that keeps track of the world. It stores a list of all the emulatedMeasurements and the world size
 * It also provides some methods to do calculations in this world
 *
 * @see EmulatedMeasurement
 */
public class World implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<World> CREATOR = new Parcelable.Creator<World>() {
        @Override
        public World createFromParcel(Parcel in) {
            return new World(in);
        }

        @Override
        public World[] newArray(int size) {
            return new World[size];
        }
    };
    private static final int WORLD_SIZE = 100;
    private static World world_instance = new World();
    /**
     * A list of all the EmulatedMeasurements in this world
     */
    private List<EmulatedMeasurement> measurementsList = new ArrayList<>();

//    private World(Parcel in) {
//        measurementsList = in.readList();
//    }

    private World() {
    }

    protected World(Parcel in) {
        if (in.readByte() == 0x01) {
            measurementsList = new ArrayList<>();
            in.readList(measurementsList, EmulatedMeasurement.class.getClassLoader());
        } else {
            measurementsList = null;
        }
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
        return myRelativeCoords(start, end, camera.tx(), camera.tz());
    }


    /**
     * Calculates the coordinates from the real world camera coordinates to
     * the coordinates in this world, based on 2 real world nodes start and end.
     *
     * @param start  start is a node in the real world that will be used as the
     *               origin for this world
     * @param end    end is a node in the real world that will be used as the
     *               point (WORLD_SIZE,WORLD_SIZE) in this world
     * @param camera camera is the node of the camera or any other position in the real world, these
     *               coordinates will be converted to the coordinates in this world
     * @return returns the coordinates in this world of the camera
     * in a 2 dimensional list
     */
    public static double[] myRelativeCoords(Node start, Node end, Node camera) {
        return myRelativeCoords(start, end, camera.getWorldPosition().x, camera.getWorldPosition().z);
    }

    /**
     * Calculates the coordinates from the real world camera coordinates to
     * the coordinates in this world, based on 2 real world nodes start and end.
     *
     * @param start start is a node in the real world that will be used as the
     *              origin for this world
     * @param end   end is a node in the real world that will be used as the
     *              point (WORLD_SIZE,WORLD_SIZE) in this world
     * @param x     the x coordinate of the real world position, that is used to calculate
     *              the relative coordinates
     * @param y     the coordinate of the real world position, that is used to calculate
     *              the relative coordinates
     * @return returns the coordinates in this world of the camera
     * in a 2 dimensional list
     */
    public static double[] myRelativeCoords(Node start, Node end, double x, double y) {
        double[] result = new double[2];
        if (end == null) {
            throw new IllegalArgumentException("Start and end have to be set first!");
        }

        double xMultiplier = WORLD_SIZE / (end.getWorldPosition().x - start.getWorldPosition().x);
        double zMultiplier = WORLD_SIZE / (end.getWorldPosition().z - start.getWorldPosition().z);
        result[0] = (x - start.getWorldPosition().x) * xMultiplier;
        result[1] = (y - start.getWorldPosition().z) * zMultiplier;

        return result;
    }

    /**
     * Calculates the distance between point A(x1,y1) and point B(x2,y2)
     *
     * @param x1 X coordinate of the first point
     * @param y1 Y coordinate of the first point
     * @param x2 X coordinate of the second point
     * @param y2 Y coordinate of the second point
     * @return The distance between point A(x1,y1) and point B(x2,y2) as a double
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        dx = dx * dx;
        dy = dy * dy;
        return Math.sqrt(dx + dy);
    }

    /**
     * Calculates the distance between point A(x1,y1,z1) and point B(x2,y2,z2)
     *
     * @param x1 X coordinate of the first point
     * @param y1 Y coordinate of the first point
     * @param z1 Z coordinate of the first point
     * @param x2 X coordinate of the second point
     * @param y2 Y coordinate of the second point
     * @param z2 Z coordinate of the second point
     * @return The distance between point A(x1,y1) and point B(x2,y2) as a double
     */
    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        dx = dx * dx;
        dy = dy * dy;
        dz = dz * dz;
        return Math.sqrt(dx + dy + dz);
    }

    public static World getInstance() {
        return world_instance;
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

    /**
     * Empties the measurementsList
     */
    public void clearMeasurements() {
        measurementsList.clear();
    }

    /**
     * Deletes an EmulatedMeasurement from the list of measurements
     *
     * @param measurement the EmulatedMeasurement to delete
     */
    public void deleteMeasurement(EmulatedMeasurement measurement) {
        measurementsList.remove(measurement);
    }

    /**
     * Calculates the measurement in a point in this world, taking into account all of
     * the EmulatedMeasurements. Does not use realworld distance but distance in this world
     *
     * @param myRelativeLocation The location where there has to be a measurement
     * @return The measurement on myRelativeLocation as a double
     */
    //TODO now it just uses the distance to EmulatedMeasurements to calculate the measurement, this probably has to be changed to a more scientific calculation
    public double GetMeasurementHere(double[] myRelativeLocation) {
        if (measurementsList.isEmpty()) {
            return 0;
        } else {
            double result = 0;
            for (EmulatedMeasurement m : measurementsList) {
                double temp = m.getMeasurement() - calculateDistance(myRelativeLocation[0], myRelativeLocation[1], m.getX(), m.getY());
                result += temp < 0 ? 0 : temp;
            }
            for (EmulatedMeasurement m : measurementsList) {
                double a = 1;
                double b = 1;
                double temp = a * (1 / Math.pow(calculateDistance(myRelativeLocation[0], myRelativeLocation[1], m.getX(), m.getY()), 2)) + b;
                result += temp < 0 ? 0 : temp;
            }
            return result;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (measurementsList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(measurementsList);
        }
    }
}
