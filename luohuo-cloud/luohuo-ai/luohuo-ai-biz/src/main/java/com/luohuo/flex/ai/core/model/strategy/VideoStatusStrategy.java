package com.luohuo.flex.ai.core.model.strategy;

public interface VideoStatusStrategy {

    boolean isSuccessStatus(String status);

    boolean isFailureStatus(String status);

    boolean isProcessingStatus(String status);

    String getPlatformName();
}
