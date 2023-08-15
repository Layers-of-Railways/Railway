package com.railwayteam.railways.config;

import com.railwayteam.railways.content.smokestack.SmokeParticle.SmokeQuality;
import com.simibubi.create.foundation.config.ConfigBase;

public class CClient extends ConfigBase {

    public final ConfigGroup client = group(0, "client", Comments.client);

    // no group
    public final ConfigBool showExtendedCouplerDebug = b(false, "showExtendedCouplerDebug", Comments.showExtendedCouplerDebug);
    public final ConfigBool skipClientDerailing = b(false, "skipClientDerailing", Comments.skipClientDerailing);
    public final ConfigBool useConductorSpyShader = b(true, "useConductorSpyShader", Comments.useConductorSpyShader);
    public final ConfigFloat trackOverlayOffset = f(0.0f, -256.0f, 256.0f, "trackOverlayOffset", Comments.trackOverlayOffset);

    // smoke
    public final ConfigGroup smoke = group(1, "smoke", Comments.smoke);
    public final ConfigInt smokeLifetime = i(610, 20, 1000, "smokeLifetime", Comments.inTicks, Comments.smokeLifetime);
    public final ConfigFloat smokePercentage = f(1.0f, 0.0f, 10.0f, "smokePercentage", Comments.smokePercentage);
    public final ConfigEnum<SmokeQuality> smokeQuality = e(SmokeQuality.ULTRA, "smokeQuality", Comments.smokeQuality);

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

        static String smoke = "Smoke Settings";
        static String smokeLifetime = "Lifetime of smoke particles emitted by contraptions";
        static String smokePercentage = "Smoke emission rate on contraptions";
        static String smokeQuality = "Smoke texture quality";

        static String journeymap = "Journeymap Settings";
        static String journeymapUpdateTicks = "Journeymap train overlay update time";
        static String journeymapRemoveObsoleteTicks = "Journeymap train overlay old marker removal check time";
    }
}
