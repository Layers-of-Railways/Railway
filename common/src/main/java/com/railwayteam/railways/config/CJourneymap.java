package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CJourneymap extends ConfigBase {

    public final ConfigInt farTrainSyncTicks = i(200, 10, 600, "farTrainSyncTicks", Comments.inTicks, Comments.farTrainSyncTicks);
    public final ConfigInt nearTrainSyncTicks = i(5, 1, 600, "nearTrainSyncTicks", Comments.inTicks, Comments.nearTrainSyncTicks);

    @Override
    public String getName() {
        return "journeymap";
    }

    private static class Comments {
        static String inTicks = "[in Ticks]";

        static String farTrainSyncTicks = "Outside-of-render-distance train sync time";
        static String nearTrainSyncTicks = "In-render-distance train sync time";
    }
}
