package com.luohuo.flex.ai.core.model.strategy;

public class SiliconFlowVideoStatusStrategy implements VideoStatusStrategy {

    @Override
    public boolean isSuccessStatus(String status) {
        return "Succeed".equalsIgnoreCase(status);
    }

    @Override
    public boolean isFailureStatus(String status) {
        return "Failed".equalsIgnoreCase(status);
    }

    @Override
    public boolean isProcessingStatus(String status) {
        return "Processing".equalsIgnoreCase(status) ||
               "InProgress".equalsIgnoreCase(status) ||
               "Pending".equalsIgnoreCase(status) ||
               "InQueue".equalsIgnoreCase(status);
    }

    @Override
    public String getPlatformName() {
        return "SiliconFlow";
    }
}
