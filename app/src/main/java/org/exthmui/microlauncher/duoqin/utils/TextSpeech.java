package org.exthmui.microlauncher.duoqin.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class TextSpeech {

    private Context context;
    private static TextToSpeech textToSpeech;
    private static TextSpeech instance;

    public static TextSpeech getInstance(Context context) {
        if (instance == null) {
            instance = new TextSpeech(context);
        }
        return instance;
    }

    public TextSpeech(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(context.getResources().getConfiguration().locale);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "TTS 初始化失败, E1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void read(String appName) {
        speak(appName);
    }

    public static void readNumber(String number) {
        speak(number);
    }

    private static void speak(String text) {
        if (!textToSpeech.isSpeaking()) {
            stop();
        }
        textToSpeech.setPitch(1.0f);
        textToSpeech.setSpeechRate(1.0f);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null,"app");
    }

    public static void close() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech=null;
        }
    }

    public static void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

}
