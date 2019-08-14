RadiationEmulator
========

RadiationEmulator is an app to support emergency responders in their training, with this app there should be no, or at least less, need for a real radioactive source. The app uses [ARCore](https://developers.google.com/ar/) to know your location in 3d space. You can add virtual sources by entering their coordinates and their measurement, the application will then calculate the measurement at your location.


## How does it work?
You need to set the start and end point of the area where the exercise will be done. These points can either be set by tapping on the screen where you want them, or by scanning a printout of [start](/app/src/main/assets/start.jpg) and one of [stop](/app/src/main/assets/stop.jpg).

The application will then use those coordinates to construct a virtual square of 100x100(can be changed in the settings). With the start point having the coordinates (0,0) and the endpoint (100,100). Now it is able to calculate a virtual measurement, using the measurements that have been added in the setup screen.

In the setup screen you can added the measurements using the virtual coordinates (anything between (0,0) and 100,100) and a measurement. All of these will be used to calculate the measurement if the location of the camera.

## Get started
To build the project, download it and open it in Android Studio 3.0. All dependencies should automatically be fetched by Android Studio. 

Before launching the app on your phone you have to install [ARCore](https://play.google.com/store/apps/details?id=com.google.ar.core&hl=en) on it. 

You can check if your device is supported on [this list](https://developers.google.com/ar/discover/#supported_devices)

## How to use it?
1. Go to `SETUP` to add some sources: you need to enter their coordinates in the virtual world, their activity either in mCl or MBq (changeable in the settings) and their exposure rate constant, either via a drop-down or by manual input(changeable in the settings). Now you can start the measurements step 3 or send your sources to another device step 2.
1. `Optional` Go back to the main screen, if NFC and android beam or disabled there will be buttons to enable them. To send your sources to another device just make sure the other device also has NFC and android beam enabled, now just put both phones with their backs to each other. After pressing the touch to beam the other device will open the application and your sources will also be loaded in their application.
2. Go to `START MEASUREMENTS` depending on your settings you will now have to scan 2 images or tap twice to set the start and end of the area. Afterwards these points will stay visible on your camera because there is an object placed in augmented reality.

## Settings
* Image recognition (or tapping the screen)  
If this is enabled the start and end points have to be set by scanning an image, if disabled they have to be set by tapping on the screen.  

* Relative distance  
If this is turned on distances will be calculated using the world size and your coordinates in this virtual world, if turned off it will use real life distances in m (accuracy not guaranteed).  

* Use predefined radiation constants  
If this is turned on on the `SETUP` screen you will have a drop down list with some predefined sources and their radiation constants, if it's turned off the radiation constant has to be typed by he user.  

* Activity in mCi (or MBq)  
If turned on the activity will be measured in mCi, if turned off it will be in MBq.  

* Barchart maximum value  
Sets the highest possible value for the gauge in the `START MEASUREMENTS` screen.  

* World size  
You can change the size of the virtual world.  

* Values and colors  
You can change when it should change colors in `START MEASUREMENTS`, the color will be the color of the highest value that is lower than the measurement.

## Warning
Augmented reality is fairly new, results may vary under different circumstances!

To get the best possible results keep in mind the following:

1. Needs good lighting to recognize surfaces
2. Works better outside than indoors
3. Surfaces need textures, plain colored surfaces are hard to detect
 
 
In good circumstances it can be fairly accurate, in testing the start and end point only move a couple of centimeters. This makes it that AR is better suited for this than gps, which is a lot less accurate on such small distances even though it is more reliable.

## Developer notes
Notes for developers are found [here](developerNotes.md)