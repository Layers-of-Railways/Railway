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
        static String realisticTrains = "Make trains require fuel to run (With either fuel tanks or coal through chest/barrel)";
        static String realisticFuelTanks = "Make fuel tanks only accept proper liquid fuels (So water and stuff cant go into them)";
    }
}
