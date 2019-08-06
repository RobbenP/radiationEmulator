package com.sck.RadiationEmulator.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ColorAndValue implements Parcelable, Comparable<ColorAndValue> {
    public static final Creator<ColorAndValue> CREATOR = new Creator<ColorAndValue>() {
        @Override
        public ColorAndValue createFromParcel(Parcel in) {
            return new ColorAndValue(in);
        }

        @Override
        public ColorAndValue[] newArray(int size) {
            return new ColorAndValue[size];
        }
    };
    private int color;
    private int value;

    public ColorAndValue(int c, int v) {
        color = c;
        value = v;
    }

    protected ColorAndValue(Parcel in) {
        color = in.readInt();
        value = in.readInt();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(color);
        parcel.writeDouble(value);
    }

    @Override
    public int compareTo(ColorAndValue other) {
        return Double.compare(this.value, other.value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));

        return result;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof ColorAndValue)) {
            return false;
        }
        ColorAndValue other = (ColorAndValue) obj;
        return (other.value == value);

    }
}
