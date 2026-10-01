# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Strip verbose and debug logs in release builds for maximum performance & privacy
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# Preserve Room database entities and DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class com.example.data.** { *; }
-keep class com.example.model.** { *; }

# Preserve Coroutines and ViewModels
-keepclassmembers class kotlinx.coroutines.** { *; }
-keepclassmembers class androidx.lifecycle.ViewModel { *; }

