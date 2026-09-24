# Add project specific ProGuard rules here.

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Moshi & Network DTOs
-keep class com.example.data.remote.** { *; }
-keepclassmembers class com.example.data.remote.** { *; }
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }

# Room Database & Entities
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class com.example.data.local.** { *; }
-keepclassmembers class com.example.data.local.** { *; }

# Domain Models
-keep class com.example.domain.model.** { *; }
-keepclassmembers class com.example.domain.model.** { *; }
