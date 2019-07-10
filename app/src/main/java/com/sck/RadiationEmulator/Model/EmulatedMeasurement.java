package com.sck.RadiationEmulator.Model;

import android.os.Parcel;
import android.os.Parcelable;

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
    private final double measurement;


    public EmulatedMeasurement(double x, double y, double measurement) {
        this.x = x;
        this.y = y;
        this.measurement = measurement;
    }

    private EmulatedMeasurement(Parcel in) {
        measurement = in.readDouble();
        x = in.readDouble();
        y = in.readDouble();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMeasurement() {
        return measurement;
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
        return "X = " + x + ", Y = " + y + " and measurement = " + measurement;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.measurement);
        parcel.writeDouble(this.x);
        parcel.writeDouble(this.y);
    }
}
