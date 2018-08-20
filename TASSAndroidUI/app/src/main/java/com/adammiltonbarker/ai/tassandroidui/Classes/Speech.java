package com.adammiltonbarker.ai.tassandroidui.Classes;

import android.speech.tts.TextToSpeech;

public class Speech {

    private static TextToSpeech tts;

    public static void Speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
