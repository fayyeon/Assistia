# Keep entry points referenced by AndroidManifest.xml
-keep class com.fayyeon.assistia.assistant.AppVoiceInteractionService { <init>(...); }
-keep class com.fayyeon.assistia.assistant.AppVoiceInteractionSessionService { <init>(...); }
-keep class com.fayyeon.assistia.assistant.AppVoiceInteractionSession { <init>(...); }
-keep class com.fayyeon.assistia.assistant.AssistActivity { <init>(...); }
-keep class com.fayyeon.assistia.ui.MainActivity { <init>(...); }
-keep class com.fayyeon.assistia.AssistiaApplication { <init>(...); }

# Strip logging calls in release builds to reduce bytecode
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
