package com.sck.RadiationEmulator.Model;

import java.io.Serializable;

public class EmulatedMeasurement implements Serializable {
    private static final long serialVersionUID = -2136764637217359393L;
    private final double x;
    private final double y;
    private final double measurement;

    public EmulatedMeasurement(double x, double y, double measurement) {
        this.x = x;
        this.y = y;
        this.measurement = measurement;
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
        return "X = " + x + ", Y = " + y + " and with a measurement of " + measurement;
    }


}
