-keepnames class com.designlife.setwork.common.utils.enums.ScreenType
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

-keep class com.designlife.setwork.** { *; }

-keepattributes Signature
-keepattributes Exceptions

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

-keep interface com.designlife.setwork.** { *; }

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okhttp3.ResponseBody { *; }

-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase


-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keepattributes Signature
-keepattributes Exceptions
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keepclassmembers interface com.designlife.setwork.** {
    @retrofit2.http.* <methods>;
}

-keep class kotlin.coroutines.** { *; }
-keep interface kotlin.coroutines.** { *; }
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}