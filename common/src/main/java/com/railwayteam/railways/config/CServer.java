/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.config;

import net.createmod.catnip.config.ConfigBase;

@SuppressWarnings("unused")
public class CServer extends ConfigBase {

    public final ConfigGroup misc = group(0, "misc", Comments.misc);

    public final ConfigBool strictCoupler = b(false, "strictCoupler", Comments.strictCoupler);
    public final ConfigBool flipDistantSwitches = b(true, "flipDistantSwitches", Comments.flipDistantSwitches);
    public final ConfigInt switchPlacementRange = i(64, 16, 128, "switchPlacementRange", Comments.switchPlacementRange);
    public final ConfigBool explosiveTrackDamage = b(false, "creeperTrackDamage", Comments.explosiveTrackDamage);
    public final ConfigFloat handcarHungerMultiplier = f(.01f, 0, 1, "handcarHungerMultiplier", Comments.handcarHungerMultiplier);

    public final CSemaphores semaphores = nested(0, CSemaphores::new, Comments.semaphores);
    public final CConductors conductors = nested(0, CConductors::new, Comments.conductors);
    public final CRealism realism = nested(0, CRealism::new, Comments.realism);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String misc = "Miscellaneous settings";

        static String strictCoupler = "Coupler will require points to be on the same or adjacent track edge, this will prevent the coupler from working if there is any form of junction in between the two points.";
        static String flipDistantSwitches = "Allow controlling Brass Switches remotely when approaching them on a train";
        static String switchPlacementRange = "Max distance between targeted track and placed switch stand";
        static String explosiveTrackDamage = "Allow creepers and ghast fireballs to damage tracks";
        static String handcarHungerMultiplier = "Multiplier used for calculating exhaustion from speed when a handcar is used.";

        static String semaphores = "Semaphore settings";
        static String conductors = "Conductor settings";
        static String realism = "Realism Settings";
    }
}
