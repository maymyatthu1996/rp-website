package com.planetway.relyingpartyapp.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.planetway.relyingpartyapp.config.AppProperties;
import com.planetway.relyingpartyapp.model.PlanetXService;
import com.planetway.relyingpartyapp.planetx.PlanetXSoapTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataBankService {

    private final PlanetXSoapTemplate planetXSoapTemplate;
    private final AppProperties appProperties;

    public DataBankService(PlanetXSoapTemplate planetXSoapTemplate, AppProperties appProperties) {
        this.planetXSoapTemplate = planetXSoapTemplate;
        this.appProperties = appProperties;
    }

    public Map<String, String> retrieveData(String planetId, String dataBankName) {
        PlanetXService planetXService = appProperties.getDataBanks()
                .get(dataBankName)
                .getPlanetXService();

        log.info("Retrieving data for planetId {} from producer {}", planetId, planetXService);

        Map<String, String> simpleMapData = planetXSoapTemplate.execute(planetId, planetXService);

        log.info("Retrieved data for planetId {} from producer {}: {}", planetId, planetXService, simpleMapData);

        return simpleMapData;
    }
}