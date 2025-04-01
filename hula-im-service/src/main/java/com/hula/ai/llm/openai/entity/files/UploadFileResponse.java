package com.hula.ai.llm.openai.entity.files;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadFileResponse extends File implements Serializable {
}
