package com.planetway.relyingpartyapp.model;

import lombok.Data;

@Data
public class PlanetXSubsystem {

    private String instance;
    private String memberClass;
    private String memberCode;
    private String subsystemCode;

    public PlanetXSubsystem(String subsystemIdentifier) {
        String[] split = subsystemIdentifier.split("/");
        instance = split[0].trim();
        memberClass = split[1].trim();
        memberCode = split[2].trim();
        subsystemCode = split[3].trim();
    }

    @Override
    public String toString() {
        return instance
                + "/" + memberClass
                + "/" + memberCode
                + "/" + subsystemCode;
    }
}
