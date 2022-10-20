package com.planetway.relyingpartyapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pcore")
@Data
public class PCoreProperties {
    private String url;
    private String rpId;
    private String rpSecret;
}
