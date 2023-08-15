package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CSemaphores extends ConfigBase {

    public final ConfigBool simplifiedPlacement = b(true, "simplifiedPlacement", Comments.simplifiedPlacement);
    public final ConfigBool flipYellowOrder = b(false, "flipYellowOrder", Comments.flipYellowOrder);

    @Override
    public String getName() {
        return "semaphores";
    }

    private static class Comments {
        static String simplifiedPlacement = "Simplified semaphore placement (no upside-down placement)";
        static String flipYellowOrder = "Whether semaphore color order is reversed when the semaphores are oriented upside-down";
    }
}
