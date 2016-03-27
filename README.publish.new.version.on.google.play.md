How To Release To Google Play
=============================

== Preparing

1. Create Git tag with name pattern "release-X.Y.Z":

    git tag -a -m "Tagging for next release" release-0.1.16
    
It is important to set a Git tag prior to creating a new APK since the tag name is used as 
version number of the app. This is the app version number shown in Google Play (the app store). 

== Building APK

Create APK file suitable for release using terminal:

1. Put this in C:\Users\USERNAME\.gradle\gradle.properties:
    AKTIVITETSBANKEN_APP_STORE_PASSWORD=****************
    AKTIVITETSBANKEN_APP_KEY_PASSWORD=****************
2. $ gradle clean
3. $ gradle assembleStaging
4. Location APK files in app/build/outputs/apk

Create APK file suitable for release using Android Studio:

1. Build > Generate Signed APK...

2. Choose the app/keys.keystore key store. Use the same password for both keystore and the key itself.

3. Build "production" or "staging" flavor. Build type "release".

Problems which may occur:

One or more "build-related files", like google-play-services_lib\build.xml, may be missing (or specify incorrect paths). If so, try running this command:

    $ android update lib-project --target android-15 --path .

    Updated project.properties
    Updated local.properties
    No project name specified, using project folder name 'google-play-services_lib'.
    If you wish to change it, edit the first line of build.xml.
    Added file ...\google-play-services_lib\build.xml
    Added file ...\google-play-services_lib\proguard-project.txt

== Publishing on Google Play

Finally, you upload app-FLAVOR-release.apk to Google Play using the Developer Console.

https://play.google.com/apps/publish/