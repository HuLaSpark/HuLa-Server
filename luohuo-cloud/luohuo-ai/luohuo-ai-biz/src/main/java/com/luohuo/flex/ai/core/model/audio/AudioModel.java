package com.luohuo.flex.ai.core.model.audio;

import java.util.Set;

public interface AudioModel {

    byte[] generateSpeech(String prompt, AudioOptions options);

    Set<String> getSupportedVoices();
}
