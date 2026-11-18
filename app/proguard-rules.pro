# Keep entry points referenced by AndroidManifest.xml
-keep class com.powerhold.app.assistant.AppVoiceInteractionService { <init>(...); }
-keep class com.powerhold.app.assistant.AppVoiceInteractionSessionService { <init>(...); }
-keep class com.powerhold.app.assistant.AppVoiceInteractionSession { <init>(...); }
-keep class com.powerhold.app.assistant.AssistActivity { <init>(...); }
-keep class com.powerhold.app.ui.MainActivity { <init>(...); }
-keep class com.powerhold.app.PowerholdApplication { <init>(...); }

# Strip logging calls in release builds to reduce bytecode
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
