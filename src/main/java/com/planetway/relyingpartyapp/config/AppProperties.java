package com.planetway.relyingpartyapp.config;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.planetway.relyingpartyapp.model.DataBank;
import com.planetway.relyingpartyapp.model.PlanetXSubsystem;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class AppProperties {

    @NotBlank
    private String baseUrl;
    private String timezone;

    @NotBlank
    private String planetXSecurityServerUrl;

    @NotNull
    private PlanetXSubsystem planetXSubsystem;

    @NotNull
    private Map<String, DataBank> dataBanks;
}
