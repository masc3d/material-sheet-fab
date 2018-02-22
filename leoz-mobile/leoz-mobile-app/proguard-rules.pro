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

# Keep class names in general, so logging rules can be applied
-keepnames class **

# Keep class and method names for leoz classes, for more readable stack traces
-keepnames class org.deku.leoz.** { *; }

# Required for JAX/RS, Feign
-keep class com.fasterxml.** { *; }
-keep class javax.ws.rs.** { *; }
-keep class org.deku.leoz.model.** { *; }
-keep class org.deku.leoz.rest.** { *; }
-keep class org.deku.leoz.service.** { *; }
-keep class sx.rs.PATCH { *; }
-keep @sx.io.serialization.Serializable public class * { *; }

# Logback classes must be kept for correct function (especially when using xml configuration)
# -keep class ch.qos.logback.classic.** { *; }

-keep class org.sqldroid.** { *; }

# Parceler library
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# Some parts of rx have issues with obfuscation (NoSuchFieldException eg.)
-keep class rx.** { *; }

# Serializers
-keep class com.esotericsoftware.** { *; }
-keepclassmembers class sx.io.serialization.** { *; }
# Doesn't work for nested/inner classes of annotated classes (tried *$*, **$**, ** etc.)
# which means all nested serializable classes must have @Serializable
-keep @sx.io.serialization.Serializable class * { *; }

# Warnings
# KEEP ALPHA SORTING
-dontwarn android.databinding.**
-dontwarn au.com.bytecode.opencsv.bean.**
-dontwarn ch.qos.logback.**
-dontwarn com.esotericsoftware.**
-dontwarn com.fasterxml.jackson.**
-dontwarn com.github.davidmoten.rx.**
-dontwarn com.github.salomonbrys.kodein.**
-dontwarn com.google.auto.**
-dontwarn com.google.common.**
-dontwarn com.sun.mail.**
-dontwarn com.trello.rxlifecycle.**
-dontwarn com.wonderkiln.camerakit.**
-dontwarn de.javakaffee.kryoserializers.**
-dontwarn feign.DefaultMethodHandler
-dontwarn io.swagger.jaxrs.**
-dontwarn javax.**
-dontwarn kotlin.**
-dontwarn okio.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.cxf.jaxrs.**
-dontwarn org.apache.commons.codec.**
-dontwarn org.eclipse.persistence.**
-dontwarn org.flywaydb.**
-dontwarn org.ini4j.spi.**
-dontwarn org.jooq.**
-dontwarn org.objenesis.**
-dontwarn org.yaml.snakeyaml.**
-dontwarn org.slf4j.**
-dontwarn rx.internal.**
-dontwarn sx.concurrent.**
-dontwarn sx.io.**
-dontwarn sx.jpa.**
-dontwarn sx.jsch.**
-dontwarn sx.junit.**
-dontwarn sx.mq.**
-dontwarn sx.persistence.querydsl.**
-dontwarn sx.platform.**
-dontwarn sx.rs.**
-dontwarn sx.EmbeddedExecutable**
-dontwarn sx.ProcessExecutor**
-dontwarn sx.Disposable
-dontwarn sx.LazyInstance
-dontwarn sx.Process**
