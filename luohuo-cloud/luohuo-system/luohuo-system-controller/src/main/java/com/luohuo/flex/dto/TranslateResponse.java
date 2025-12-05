package com.luohuo.flex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslateResponse {
    private String text;
    private String provider;
}
