/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.config;

import com.railwayteam.railways.content.smokestack.SmokeType;
import com.railwayteam.railways.content.smokestack.particles.legacy.SmokeParticle.SmokeQuality;
import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CClient extends ConfigBase {

    public final ConfigGroup client = group(0, "client", Comments.client);


    // no group
    public final ConfigBool showExtendedCouplerDebug = b(false, "showExtendedCouplerDebug", Comments.showExtendedCouplerDebug);
    public final ConfigBool skipClientDerailing = b(false, "skipClientDerailing", Comments.skipClientDerailing);
    public final ConfigBool useConductorSpyShader = b(true, "useConductorSpyShader", Comments.useConductorSpyShader);
    public final ConfigFloat trackOverlayOffset = f(0.0f, -256.0f, 256.0f, "trackOverlayOffset", Comments.trackOverlayOffset);
    public final ConfigBool useDevCape = b(true, "useDevCape", Comments.useDevCape, Comments.useDevCape2);
    public final ConfigBool renderNormalCap = b(true, "renderNormalCap", Comments.renderNormalCap);
    public final ConfigBool animatedFlywheels = b(true, "animatedFlywheels", Comments.animatedFlywheels);

    // smoke
    public final ConfigGroup smoke = group(1, "smoke", Comments.smoke);
    public final ConfigEnum<SmokeType> smokeType = e(SmokeType.CARTOON, "smokeType", Comments.smokeType);

    public final ConfigGroup oldSmoke = group(2, "old", Comments.oldSmoke);
    public final ConfigInt smokeLifetime = i(500, 20, 1000, "smokeLifetime", Comments.inTicks, Comments.smokeLifetime);
    public final ConfigFloat smokePercentage = f(0.75f, 0.0f, 10.0f, "smokePercentage", Comments.smokePercentage);
    public final ConfigEnum<SmokeQuality> smokeQuality = e(SmokeQuality.HIGH, "smokeQuality", Comments.smokeQuality);
    public final ConfigBool thickerSmoke = b(true, "thickerSmoke", Comments.thickerSmoke);

    public final ConfigGroup cartoonSmoke = group(2, "cartoon", Comments.cartoonSmoke);
    public final ConfigBool spawnFasterPuffs = b(true, "spawnFasterPuffs", Comments.spawnFasterPuffs);
    public final ConfigBool spawnSteam = b(false, "spawnSteam", Comments.spawnSteam);

    // journeymap
    public final ConfigGroup journeymap = group(1, "journeymap", Comments.journeymap);
    public final ConfigInt journeymapUpdateTicks = i(1, 1, 600, "updateRate", Comments.inTicks, Comments.journeymapUpdateTicks);
    public final ConfigInt journeymapRemoveObsoleteTicks = i(200, 10, 1200, "removeObsoleteRate", Comments.inTicks, Comments.journeymapRemoveObsoleteTicks);


    @Override
    public String getName() {
        return "client";
    }

    private static class Comments {
        static String inTicks = "[in Ticks]";

        static String client = "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";

        static String showExtendedCouplerDebug = "Show extended debug info in coupler goggle overlay";
        static String skipClientDerailing = "Skip clientside train derailing. This prevents stuttering when a train places tracks, but trains will not appear derailed when they crash";
        static String useConductorSpyShader = "Use a scanline shader when spying through a conductor";
        static String trackOverlayOffset = "Vertical offset for track overlays";
        static String useDevCape = "Whether to actually apply the dev cape (ignored for non-devs)";
        static String useDevCape2 = "This setting may require a relog to take effect";
        static String renderNormalCap = "Should the normal create conductor cap be rendered on top of the conductors existing hat?";
        static String animatedFlywheels = "Should flywheels and blocks extending the FlywheelBlock class be animated when apart of trains?";

        static String smoke = "Smoke Settings";
        static String oldSmoke = "Old-style Smoke Settings";
        static String smokeLifetime = "Lifetime of smoke particles emitted by contraptions";
        static String smokePercentage = "Smoke emission rate on contraptions";
        static String smokeQuality = "Smoke texture quality";
        static String thickerSmoke = "Thicker smoke (renders 2 extra layers per particle)";
        static String smokeType = "Smoke particle style";
        static String cartoonSmoke = "Cartoon-style Smoke Settings";
        static String spawnFasterPuffs = "Spawn faster-rising small puffs of smoke on an interval";
        static String spawnSteam = "Spawn steam on an interval";

        static String journeymap = "Journeymap Settings";
        static String journeymapUpdateTicks = "Journeymap train overlay update time";
        static String journeymapRemoveObsoleteTicks = "Journeymap train overlay old marker removal check time";
    }
}
