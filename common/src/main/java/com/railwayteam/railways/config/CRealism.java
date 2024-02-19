package com.railwayteam.railways.config;

import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CRealism extends ConfigBase {
    public final ConfigBool realisticTrains = b(false, "realisticTrains", Comments.realisticTrains);
    public final ConfigBool realisticFuelTanks = b(true, "realisticFuelTanks", Comments.realisticFuelTanks);

    @Override
    public String getName() {
            return "realism";
    }

    private static class Comments {
        static String realisticTrains = "Make trains require fuel to run (either from fuel tanks or solid fuels in chests/barrels)";
        static String realisticFuelTanks = "Make fuel tanks only accept proper liquid fuels (so water etc can't go into them)";
    }
}
