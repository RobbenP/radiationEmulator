RadiationEmulator
========

RadiationEmulator is an app to support emergency responders in their training, with this app there should be no, or atleast less, need for a real radioactive source. The app uses [ARCore](https://developers.google.com/ar/) to know your location in 3d space. You can add virtual sources by entering their coordinates and their measurement, the application will then calculate the measurement at your location.

## How does it work?
You need to set the start and end point of the area where the exercice will be done. These points can either be set by tapping on the screen where you want them, or by scanning a printout of [start](/app/src/main/assets/start.jpg) and one of [stop](/app/src/main/assets/stop.jpg).

The application will then use those coordinates to construct a virtual square of 100x100. With the start point having the coordinates (0,0) and the endpoint (100,100). Now it is able to calculate a virtual measurement, using the measurements that have been added in the setup screen.

In the setup screen you can added the measurements using the virtual coordinates (anything between (0,0) and 100,100) and a measurement. All of these will be used to calculate the measurement if the location of the camera.

## Get started
To build the project, download it and open it in Android Studio 3.0. All dependencies should automatically be fetched by Android Studio. 

Before launching the app on your phone you have to install [ARCore](https://play.google.com/store/apps/details?id=com.google.ar.core&hl=en) on it. 

You can check if your device is supported on [this list](https://developers.google.com/ar/discover/#supported_devices)

## Warning
Augmented reality is fairly new, results may vary under different circumstances!

To get the best possible results keep in mind the folowing:

1. Needs good lighting to recognize surfaces
2. Works better outside than indoors
3. Surfaces need textures, plain colored surfaces are hard to detect
 
 
In good circumstances it can be fairly accurate, in testing the start and end point only move a couple of centimeters. This makes it that AR is better suited for this than gps, which is alot less accarute on such small distances even though it is more reliable.

## Developer notes
Notes for developers are found [here](developerNotes.md)