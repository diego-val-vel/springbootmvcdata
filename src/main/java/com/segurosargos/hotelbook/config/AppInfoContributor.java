package com.segurosargos.hotelbook.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * InfoContributor que agrega informacion personalizada de la aplicacion al endpoint /actuator/info.
 */
@Component
public class AppInfoContributor implements InfoContributor {

    private final Environment environment;

    public AppInfoContributor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void contribute(Info.Builder builder) {
        String[] activeProfiles = environment.getActiveProfiles();
        String environmentName;
        if (activeProfiles.length == 0) {
            environmentName = "default";
        } else {
            environmentName = String.join(",", activeProfiles);
        }

        Map<String, Object> appDetails = new HashMap<>();
        appDetails.put("name", "HotelBook");
        appDetails.put("version", "1.0.0");
        appDetails.put("environment", environmentName);

        builder.withDetail("app", appDetails);
    }
}
