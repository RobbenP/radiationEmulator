package com.sck.RadiationEmulator.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * An EmulatedMeasurement are 2D coordinates (X and Y) and a measurement.
 */
public class EmulatedMeasurement implements Parcelable {
    public static final Creator<EmulatedMeasurement> CREATOR = new Creator<EmulatedMeasurement>() {
        @Override
        public EmulatedMeasurement createFromParcel(Parcel in) {
            return new EmulatedMeasurement(in);
        }

        @Override
        public EmulatedMeasurement[] newArray(int size) {
            return new EmulatedMeasurement[size];
        }
    };

    private final double x;
    private final double y;
    private final double radiationConstant;
    private final double radiationSourceActivity;

    public EmulatedMeasurement(double x, double y, double radiationConstant, double radiationSourceActivity) {
        this.x = x;
        this.y = y;

        this.radiationConstant = radiationConstant;
        this.radiationSourceActivity = radiationSourceActivity;
    }

    private EmulatedMeasurement(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        radiationConstant = in.readDouble();
        radiationSourceActivity = in.readDouble();

    }

    public double getRadiationConstantInMBecquerel() {
        return radiationConstant * 37;
    }

    public double getRadiationConstant() {
        return radiationConstant;
    }

    public double getRadiationSourceActivity() {
        return radiationSourceActivity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof EmulatedMeasurement)) {
            return false;
        }
        EmulatedMeasurement other = (EmulatedMeasurement) obj;
        return (other.getX() == x && other.getY() == y);

    }

    @Override
    public String toString() {
        return "X = " + x + ", Y = " + y + " and radiation constant = " + radiationConstant + " with " + String.format(Locale.ENGLISH, "%.2f", radiationSourceActivity) + "mCi.";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.x);
        parcel.writeDouble(this.y);
        parcel.writeDouble(this.radiationConstant);
        parcel.writeDouble(this.radiationSourceActivity);
    }
}
