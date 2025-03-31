package com.hula.core.chat.service;

import com.hula.core.chat.domain.dto.ConverseMessageDto;

public interface ConverseService {

    void video(Long uid, ConverseMessageDto ConverseMessageDto);

	void phone(Long uid, ConverseMessageDto ConverseMessageDto);

}
