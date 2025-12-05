package com.luohuo.flex.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IceServer implements Serializable {
    private List<String> urls;
    private String username;
    private String credential;
}
