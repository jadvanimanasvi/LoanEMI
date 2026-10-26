
# ==========================================
# Loan EMI Calculator - ProGuard Rules
# ==========================================

# Keep source file and line number information for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations and generic type information
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod

# ==========================================
# AndroidX and Android Components
# ==========================================

-keepclassmembers class * extends androidx.activity.ComponentActivity {
    public void *(android.view.View);
}

# Keep custom views with XML constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ==========================================
# Firebase
# ==========================================

-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }

# Firebase Remote Config
-keep class com.google.firebase.remoteconfig.** { *; }

# Firebase Cloud Messaging
-keep class com.google.firebase.messaging.** { *; }

# Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }

# ==========================================
# Google Mobile Ads SDK
# ==========================================

-keep class com.google.android.libraries.ads.mobile.sdk.** { *; }

# Preserve ad callback implementations
-keep class * extends com.loanemi.calculator.emi.Ads.AdConfig { *; }

# ==========================================
# Glide
# ==========================================

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# ==========================================
# Lottie
# ==========================================

-keep class com.airbnb.lottie.** { *; }

# ==========================================
# Application-specific model classes
# ==========================================

# Keep model fields used by JSON/reflection if applicable.
# Uncomment and adjust the package/class names if your app uses them.
# -keep class com.loanemi.calculator.emi.model.** { *; }

# ==========================================
# Preserve enum classes
# ==========================================

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==========================================
# General optimization and warnings
# ==========================================

# Do not disable optimization globally.
# Let the Android Gradle Plugin apply its default optimization rules.
