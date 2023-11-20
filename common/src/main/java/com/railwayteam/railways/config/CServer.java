package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CServer extends ConfigBase {

    public final ConfigGroup misc = group(0, "misc", Comments.misc);

    public final ConfigBool strictCoupler = b(false, "strictCoupler", Comments.strictCoupler);
    public final ConfigBool flipDistantSwitches = b(true, "flipDistantSwitches", Comments.flipDistantSwitches);
    public final ConfigInt switchPlacementRange = i(64, 16, 128, "switchPlacementRange", Comments.switchPlacementRange);
    public final ConfigBool creeperTrackDamage = b(false, "creeperTrackDamage", Comments.creeperTrackDamage);

    public final CSemaphores semaphores = nested(0, CSemaphores::new, Comments.semaphores);
    public final CConductors conductors = nested(0, CConductors::new, Comments.conductors);
    public final CJourneymap journeymap = nested(0, CJourneymap::new, Comments.journeymap);
    public final COptimization optimization = nested(0, COptimization::new, Comments.optimization);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String misc = "Miscellaneous settings";

        static String strictCoupler = "Coupler will require points to be on the same or adjacent track edge, this will prevent the coupler from working if there is any form of junction in between the two points.";
        static String flipDistantSwitches = "Allow controlling Brass Switches remotely when approaching them on a train";
        static String switchPlacementRange = "Max distance between targeted track and placed switch stand";
        static String creeperTrackDamage = "Allow creepers to damage tracks";

        static String semaphores = "Semaphore settings";
        static String conductors = "Conductor settings";
        static String journeymap = "Journeymap compat settings";
        static String optimization = "Optimization settings (may be experimental)";
    }
}
