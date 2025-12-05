package com.luohuo.flex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TranslateSegmentsResponse {
    private List<String> segments;
    private String provider;
}
