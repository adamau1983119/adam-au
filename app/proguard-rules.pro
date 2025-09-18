# Keep rules for Compose and Kotlin metadata
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Keep AdMob classes
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }

# Keep application classes
-keep class com.wts.dsfortune.** { *; }

# ===== CRITICAL FIX: Dynamic Resource Loading =====
# Keep R classes to prevent getIdentifier() failures
-keep class **.R$* { *; }
-keepclassmembers class **.R$* { 
    public static <fields>; 
}

# Keep specific drawable resources that are dynamically loaded
-keep class com.wts.dsfortune.R$drawable { *; }
-keepclassmembers class com.wts.dsfortune.R$drawable {
    public static final int wong_tai_sin;
    public static final int wts03;
    public static final int wts04;
    public static final int wts05;
}

# Keep data classes
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
