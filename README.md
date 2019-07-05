Radiation Emulator
========

## Language: Java vs Kotlin

For now the application is written in Java, although google sponsors Kotlin and Kotlin has some interesting features. For now I have chosen Java because I am more familiar with it and I started building on an example that was made in Java. There should be no difference in performance since both Kotlin and Java compile to Bytecode. Which means it is possible to use Kotlin when adding new classes without any problems. Possibly I will rewrite it to Kotlin because Kotlin is more concise and a bit more readable, and also because it would be a great learning experience.

## Outlining the area

There are 3 possibilities
1. Manually selecting 2 points on the screen
2. Image recognition
3. Cloud anchors

#### Manually selecting 2 points on the screen
For now this is how it is done, mainly because it is the easiest way that can be tested in the android emulator.

Cons:
* Each device has to manually define the start and end, different devices will have different measurements
* Inaccurate  

Pros:
* Easy to implement
* Can be tested on an emulator

#### Image recognition
This is how I would prefer to do it, but I do not have an android device that is capable of handling AR

Cons:
* Needs printouts of the images that have to be recognized

Pros:
* Easy to implement
* Multiple devices can have the same start and end points, resulting in comparable measurements
* More accurate

#### Cloud anchors
Stores to start and end points on googles servers for 24 hours, more info [here](https://developers.google.com/ar/develop/java/cloud-anchors/overview-android). This would be nice to implement next to one of the above so the application is also useable without an internet connection.

Cons:
* Needs to be setup by another device
* Takes around 10 sec to save and to recognize points
* Difficult to implement
* Saved for only 24 hours
* Requires an active internet connection

Pros:
* Multiple devices can have the same start and end points, resulting in comparable measurements
* No need for extra materials like print outs
* More accurate (?)

## Models EmulatedMeasurement and World

### EmulatedMeasurement

This class is used to store a measurement on a 2D coordinate, 2D because height feels like it's a bit irrelevant and would just add more innaccuracy. Made into a class instead of an array[3] because it makes it more expendable.

### World

This class stores a list of all the EmulatedMeasurements and a static field for the virtual world size. It also provides methods to get the distance between points in the world, to translate the real world coordinates to the coordinates in the virtual world and a method to calculate the measurement on a certain point.

#### Calculating measurement

For now it just calculates the distance between your location and the measurements and subtracts the distance from the measurement, if it's less than zero it becomes zero.

This is probably not the right way to do it, but it provides a method which can always be altered in a later stage.

## Build warning

When building we get the following warning: "WARNING: API 'variant.getMergeAssets()' is obsolete and has been replaced with 'variant.getMergeAssetsProvider()'. It will be removed at the end of 2019." This is a sceneform bug that is in the hands of those developpers. They have acknowledged the problem and should fix it some time in the future, [github reference](https://github.com/google-ar/sceneform-android-sdk/issues/513).

