package com.planetway.relyingpartyapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlanetXService extends PlanetXSubsystem {

    private String serviceCode;

    public PlanetXService(String serviceIdentifier) {
        super(serviceIdentifier);

        String[] split = serviceIdentifier.split("/");
        this.serviceCode = split[4].trim();
    }

    @Override
    public String toString() {
        return super.toString()
                + "/" + serviceCode;
    }

    public String toSubsystemIdentifierString() {
        return super.toString();
    }
}
