package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CConductors extends ConfigBase {

    public final ConfigBool whistleRequiresOwning = b(false, "mustOwnBoundTrain", Comments.whistleRequiresOwning);
    public final ConfigInt maxVentLength = i(64, 1, Integer.MAX_VALUE, "maxConductorVentLength", Comments.maxVentLength);
    public final ConfigInt whistleRebindRate = i(10, 1, 600, "whistleRebindRate", Comments.whistleRebindRate);

    @Override
    public String getName() {
        return "conductors";
    }

    private static class Comments {
        static String whistleRequiresOwning = "Conductor whistle is limited to the owner of a train";
        static String maxVentLength = "Maximum length of conductor vents";
        static String whistleRebindRate = "How often a conductor whistle updates the train of the bound conductor";
    }
}
