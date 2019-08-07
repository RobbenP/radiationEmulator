package com.sck.RadiationEmulator.Model;

public class RadiationSource {
    private final String name;
    private final double radiationConstant;

    public RadiationSource(String name, double radiationConstant) {
        this.name = name;
        this.radiationConstant = radiationConstant;
    }

    public String getName() {
        return name;
    }

    public double getRadiationConstant() {
        return radiationConstant;
    }
}
