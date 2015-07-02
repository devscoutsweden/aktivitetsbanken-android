How To Release To Google Play
=============================

== Preparing

1. Increment /manifest/@android:versionCode in /AndroidManifest.xml by 1.

2. Make sure /manifest/@android:versionName in /AndroidManifest.xml is correct.

3. Commit changes to Git repository. Suggested commit comment: "Bumped versionCode."

== Compiling

Create APK file suitable for release:

    aktivitetsbanken-android\> ant clean release

Problems which may occur:

One or more "build-related files", like google-play-services_lib\build.xml, may be missing (or specify incorrect paths). If so, try running this command:

    $ android update lib-project --target android-15 --path .

    Updated project.properties
    Updated local.properties
    No project name specified, using project folder name 'google-play-services_lib'.
    If you wish to change it, edit the first line of build.xml.
    Added file ...\google-play-services_lib\build.xml
    Added file ...\google-play-services_lib\proguard-project.txt

== Signing

Sign using /dev/scout private key:

    aktivitetsbanken-android\bin> jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore %PATH_TO_AKTIVITETSBANKEN_KEYSTORE% Aktivitetsbanken-release-unsigned.apk aktivitetsbanken-android

Rename file (not necessary but useful to avoid confusion):

    aktivitetsbanken-android\bin> del Aktivitetsbanken-release-signed.apk
    aktivitetsbanken-android\bin> move Aktivitetsbanken-release-unsigned.apk Aktivitetsbanken-release-signed.apk

Verify that all files were signed correctly:

    aktivitetsbanken-android\bin> jarsigner -verify -verbose -certs Aktivitetsbanken-release-signed.apk

== Aligning

Remove previously created APK file:

    aktivitetsbanken-android\bin> del Aktivitetsbanken-release.apk

The final touch:

    aktivitetsbanken-android\bin> "%PATH_TO_ANDROID_DEVELOPER_TOOLS%\sdk\tools\zipalign.exe" -v 4 Aktivitetsbanken-release-signed.apk Aktivitetsbanken-release.apk

    or...

    aktivitetsbanken-android\bin> "%PATH_TO_ANDROID_DEVELOPER_TOOLS%\sdk\build-tools\19.1.0\zipalign.exe" -v 4 Aktivitetsbanken-release-signed.apk Aktivitetsbanken-release.apk

== Test Release Version on Locally Connected Device

    %PATH_TO_ANDROID_DEVELOPER_TOOLS%\sdk\platform-tools>adb install %PATH_TO_SOURCE_CODE%\bin\Aktivitetsbanken-release.apk
    2269 KB/s (1761524 bytes in 0.758s)
            pkg: /data/local/tmp/Aktivitetsbanken-release.apk
    Failure [INSTALL_FAILED_ALREADY_EXISTS]

    %PATH_TO_ANDROID_DEVELOPER_TOOLS%\sdk\platform-tools>adb install -r %PATH_TO_SOURCE_CODE%\bin\Aktivitetsbanken-release.apk
    3359 KB/s (1761524 bytes in 0.512s)
            pkg: /data/local/tmp/Aktivitetsbanken-release.apk
    Failure [INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES]

    %PATH_TO_ANDROID_DEVELOPER_TOOLS%\sdk\platform-tools>adb install %PATH_TO_SOURCE_CODE%\bin\Aktivitetsbanken-release.apk
    3399 KB/s (1761524 bytes in 0.506s)
            pkg: /data/local/tmp/Aktivitetsbanken-release.apk
    Success

== Publishing on Google Play

Finally, you upload Aktivitetsbanken-release.apk to Google Play using the Developer Console.