# Firebase & Google Services
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep Room entities & DAOs
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Keep Moshi & Retrofit
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
