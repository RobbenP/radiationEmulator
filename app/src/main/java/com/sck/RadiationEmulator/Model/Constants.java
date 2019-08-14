package com.sck.RadiationEmulator.Model;

import java.util.ArrayList;

public final class Constants {
    public static final String SHAREDPREFERENCES_FOR_SETTINGS_FILE_NAME = "settings";
    public static final String IMAGE_RECOGNITION_OR_TAPPING = "imgortapping";
    public static final String RELATIVE_DISTANCE_OR_REAL = "relorreal";
    public static final String BARCHART_MAXIMUM_VALUE = "barchartmax";
    public static final String WORLD_SIZE = "worldsize";
    public static final String LIST_OF_VALUES_WITH_COLORS_FOR_BARCHART = "jsonvalues";
    public static final String USE_RADIATION_CONSTANTS_FROM_SPINNER_OR_CUSTUM = "spinnerorconstants";
    public static final String USE_MCI_FOR_ACTIVITY_OR_BQ = "unitForActivity";

    public static ArrayList<ColorAndValue> GET_COLORS_AND_VALUES() {
        //todo add better values
        ArrayList<ColorAndValue> colorAndValues = new ArrayList<>();
        colorAndValues.add(new ColorAndValue(-16711936, 50)); //green
        colorAndValues.add(new ColorAndValue(-256, 100)); //yellow
        colorAndValues.add(new ColorAndValue(-32768, 150)); // orange
        colorAndValues.add(new ColorAndValue(-65536, Integer.MAX_VALUE)); //red
        return colorAndValues;
    }

    public static ArrayList<RadiationSource> GET_SOURCES() {
        ArrayList<RadiationSource> radiationSources = new ArrayList<>();

        radiationSources.add(new RadiationSource("Cesium-137", 3.3));
        radiationSources.add(new RadiationSource("Cobalt-57", 0.9));
        radiationSources.add(new RadiationSource("Cobalt-60", 13.2));
        radiationSources.add(new RadiationSource("Iodine-125", 0.7));
        radiationSources.add(new RadiationSource("Iodine-131", 2.2));
        radiationSources.add(new RadiationSource("Magnese-54", 4.7));
        radiationSources.add(new RadiationSource("Radium-226", 8.25));
        radiationSources.add(new RadiationSource("Sodium-22", 12));
        radiationSources.add(new RadiationSource("Sodium-24", 18.4));
        radiationSources.add(new RadiationSource("Zinc-65", 2.7));
        return radiationSources;
    }

}
