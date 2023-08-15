package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CConductors extends ConfigBase {

    public final ConfigBool whistleRequiresOwning = b(false, "mustOwnBoundTrain", Comments.whistleRequiresOwning);
    public final ConfigInt maxVentLength = i(64, 1, Integer.MAX_VALUE, "maxConductorVentLength", Comments.maxVentLength);

    @Override
    public String getName() {
        return "conductors";
    }

    private static class Comments {
        static String whistleRequiresOwning = "Conductor whistle is limited to the owner of a train";
        static String maxVentLength = "Maximum length of conductor vents";
    }
}
