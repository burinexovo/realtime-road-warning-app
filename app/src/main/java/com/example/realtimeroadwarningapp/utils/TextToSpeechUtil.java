package com.example.realtimeroadwarningapp.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechUtil extends ContextWrapper {

    private final String TAG = this.getClass().getSimpleName();
    TextToSpeech textToSpeech;

    public TextToSpeechUtil(Context base) {
        super(base);
    }

    public void speak(String body) {
        textToSpeech = new TextToSpeech(this, i -> {
            if (i == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.CHINESE);
                textToSpeech.speak(body, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }
}
