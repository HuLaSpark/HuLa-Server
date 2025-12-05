package com.luohuo.flex.service;

import java.util.List;

public interface TranslateService {

    String translate(String text, String provider, String source, String target);

    List<String> translateSegments(String text, String provider, String source, String target);
}
