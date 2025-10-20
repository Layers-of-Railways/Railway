/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

        registerCustomCap("rabbitminers", "rabbitminers", true);

        registerCustomCap("TropheusJay", "tropheusjay", true);
        registerCustomSkin("TropheusJay", "tropheusjay");

        registerCustomCap("cshcrafter", "cshcrafter", true);

        registerCustomCap("pride", "pride");
    }
}
