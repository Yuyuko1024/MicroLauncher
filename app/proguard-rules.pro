# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# androidx
-keep class androidx.** {*;}
-keep interface androidx.** {*;}
-keep public class * extends androidx.**
-dontwarn androidx.**

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepattributes SourceFile,LineNumberTable

-assumenosideeffects class android.util.Log{
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

-renamesourcefileattribute SourceFile

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# dont mix the data bean
-keep class org.exthmui.microlauncher.duoqin.bean.** {*;}
-dontwarn org.exthmui.microlauncher.duoqin.bean.**
