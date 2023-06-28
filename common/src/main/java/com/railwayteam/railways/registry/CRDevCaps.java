package com.railwayteam.railways.registry;

import com.jozufozu.flywheel.core.PartialModel;
import com.railwayteam.railways.Railways;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class CRDevCaps {
    public static final PartialModel ITHUNDXR_MODEL = new PartialModel(Railways.asResource("item/dev_caps/ithundxr"));

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

        registerCustomCap("IThundxr", "crown", true);
        registerCustomSkin("IThundxr", "ithundxr");
    }
}
