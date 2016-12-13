# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/n3/Android/android-sdk-mac_x86/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Required for kodein
-keepattributes Signature

# Keeping attributes `EnclosingMethod` and `InnerClasses`
# eliminates warnigs: Ignoring InnerClasses attribute for an anonymous inner class
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Logback classes must be kept for correct function (especially when using xml configuration)
# -keep class ch.qos.logback.classic.** { *; }

-keep class org.sqldroid.** { *; }

# Parceler library
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# Warnings
-dontwarn au.com.bytecode.opencsv.bean.**
-dontwarn ch.qos.logback.**
-dontwarn com.google.common.**

-dontwarn javax.**

-dontwarn com.fasterxml.jackson.**
-dontwarn com.github.davidmoten.rx.**
-dontwarn com.github.salomonbrys.kodein.**
-dontwarn com.trello.rxlifecycle.**
-dontwarn okio.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.eclipse.persistence.**
-dontwarn org.flywaydb.**
-dontwarn org.ini4j.spi.**
-dontwarn org.slf4j.**
-dontwarn rx.internal.**
-dontwarn sx.concurrent.**
-dontwarn sx.io.**
-dontwarn sx.junit.**
-dontwarn sx.platform.**
-dontwarn sx.EmbeddedExecutable**
-dontwarn sx.ProcessExecutor**
-dontwarn sx.Disposable
-dontwarn sx.LazyInstance
-dontwarn sx.Process**

-dontwarn feign.DefaultMethodHandler
