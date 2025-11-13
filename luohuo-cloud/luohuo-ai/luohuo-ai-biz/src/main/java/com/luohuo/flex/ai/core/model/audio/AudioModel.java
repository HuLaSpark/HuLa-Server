package com.luohuo.flex.ai.core.model.audio;

public interface AudioModel {

    byte[] generateSpeech(String prompt, AudioOptions options);

    java.util.Set<String> getSupportedVoices();
}
