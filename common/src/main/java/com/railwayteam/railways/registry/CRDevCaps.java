package com.railwayteam.railways.registry;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class CRDevCaps {
    public static void register() {
        registerCustomCap("Slimeist", "slimeist");
        registerCustomCap("bosbesballon", "bosbesballon");
        registerCustomCap("SpottyTheTurtle", "turtle");

        registerCustomCap("RileyHighline", "rileyhighline", true);
        registerCustomSkin("RileyHighline", "rileyhighline");

        registerCustomCap("TiesToetToet", "tiestoettoet", true);

        registerCustomCap("LemmaEOF", "headphones", true);

        registerCustomCap("To0pa", "stonks_hat", true);
        registerCustomCap("Furti_Two", "stonks_hat_blue", true);
        registerCustomCap("Aypierre", "stonks_hat_red", true);

        registerCustomCap("NeonCityDrifter", "neoncitydrifter");

        registerCustomCap("demondj2002", "demon");

        registerCustomCap("littlechasiu", "littlechasiu", true);
        registerCustomSkin("littlechasiu", "littlechasiu");

        registerCustomConductorNameBasedSkin("mattentosh", "mattentosh");

        registerCustomCap("IThundxr", "crown", true);
        registerCustomSkin("IThundxr", "ithundxr");

        registerCustomConductorOnlyCap("IThundxr", "ithundxr", true);
    }
}
