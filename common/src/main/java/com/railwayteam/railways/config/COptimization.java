package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class COptimization extends ConfigBase {

    public final ConfigBool disableTrainCollision = b(false, "disableTrainCollision", Comments.disableTrainCollision);
    public final ConfigBool optimizeFunnelBeltInteraction = b(false, "optimizeFunnelBeltInteraction", Comments.optimizeFunnelBeltInteraction);

    @Override
    public String getName() {
        return "optimization";
    }

    private static class Comments {
        static String disableTrainCollision = "Disable collisions between trains. May have significant performance impact if playing with many trains";
        static String optimizeFunnelBeltInteraction = "Optimizes belts placing items onto belts by skipping the calculation of an unused, but expensive, variable";
    }
}
