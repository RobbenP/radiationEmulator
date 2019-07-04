package com.sck.RadiationEmulator.Model;

public class EmulatedMeasurement {
    private final double x;
    private final double y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMeasurement() {
        return measurement;
    }

    private final double measurement;

    public EmulatedMeasurement(double x, double y, double measurement) {
        this.x = x;
        this.y = y;
        this.measurement = measurement;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }else if (!(obj instanceof EmulatedMeasurement)){
            return false;
        }
        EmulatedMeasurement other = (EmulatedMeasurement) obj;
        return (other.getMeasurement() != measurement || other.getX() != x || other.getY()!= y);

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
        temp = Double.doubleToLongBits(measurement);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;

    }
}
