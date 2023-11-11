package com.railwayteam.railways.config;

import com.railwayteam.railways.content.smokestack.SmokeType;
import com.railwayteam.railways.content.smokestack.particles.legacy.SmokeParticle.SmokeQuality;
import com.simibubi.create.foundation.config.ConfigBase;

@SuppressWarnings("unused")
public class CClient extends ConfigBase {

    public final ConfigGroup client = group(0, "client", Comments.client);

    // no group
    public final ConfigBool disableOptifineWarning = b(false, "disableOptifineNag", Comments.disableOptifineWarning);
    public final ConfigBool showExtendedCouplerDebug = b(false, "showExtendedCouplerDebug", Comments.showExtendedCouplerDebug);
    public final ConfigBool skipClientDerailing = b(false, "skipClientDerailing", Comments.skipClientDerailing);
    public final ConfigBool useConductorSpyShader = b(true, "useConductorSpyShader", Comments.useConductorSpyShader);
    public final ConfigFloat trackOverlayOffset = f(0.0f, -256.0f, 256.0f, "trackOverlayOffset", Comments.trackOverlayOffset);
    public final ConfigBool useDevCape = b(true, "useDevCape", Comments.useDevCape, Comments.useDevCape2);

    // smoke
    public final ConfigGroup smoke = group(1, "smoke", Comments.smoke);
    public final ConfigEnum<SmokeType> smokeType = e(SmokeType.OLD, "smokeType", Comments.smokeType);
    public final ConfigGroup oldSmoke = group(2, "old", Comments.oldSmoke);
    public final ConfigInt smokeLifetime = i(500, 20, 1000, "smokeLifetime", Comments.inTicks, Comments.smokeLifetime);
    public final ConfigFloat smokePercentage = f(0.75f, 0.0f, 10.0f, "smokePercentage", Comments.smokePercentage);
    public final ConfigEnum<SmokeQuality> smokeQuality = e(SmokeQuality.HIGH, "smokeQuality", Comments.smokeQuality);
    public final ConfigBool thickerSmoke = b(true, "thickerSmoke", Comments.thickerSmoke);

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

        static String disableOptifineWarning = "Disable the optifine warning screen [DANGER]: Using optifine With Steam 'n' Rails may cause issues and you will not get any support for optifine related issues.";
        static String showExtendedCouplerDebug = "Show extended debug info in coupler goggle overlay";
        static String skipClientDerailing = "Skip clientside train derailing. This prevents stuttering when a train places tracks, but trains will not appear derailed when they crash";
        static String useConductorSpyShader = "Use a scanline shader when spying through a conductor";
        static String trackOverlayOffset = "Vertical offset for track overlays";
        static String useDevCape = "Whether to actually apply the dev cape (ignored for non-devs)";
        static String useDevCape2 = "This setting may require a relog to take effect";

        static String smoke = "Smoke Settings";
        static String oldSmoke = "Old-style Smoke Settings";
        static String smokeLifetime = "Lifetime of smoke particles emitted by contraptions";
        static String smokePercentage = "Smoke emission rate on contraptions";
        static String smokeQuality = "Smoke texture quality";
        static String thickerSmoke = "Thicker smoke (renders 2 extra layers per particle)";
        static String smokeType = "Smoke particle style";

        static String journeymap = "Journeymap Settings";
        static String journeymapUpdateTicks = "Journeymap train overlay update time";
        static String journeymapRemoveObsoleteTicks = "Journeymap train overlay old marker removal check time";
    }
}
