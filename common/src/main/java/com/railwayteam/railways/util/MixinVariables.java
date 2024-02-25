package com.railwayteam.railways.util;

import org.jetbrains.annotations.ApiStatus;

/**
 * Holds helper Variables for Mixin's
 */
public class MixinVariables {
    @ApiStatus.Internal
    public static boolean trackEdgeTemporarilyFlipped = false;

    @ApiStatus.Internal
    public static boolean trackEdgeCarriageTravelling = false;

    @ApiStatus.Internal
    public static boolean temporarilySkipSwitches = false;

    @ApiStatus.Internal
    public static int signalPropagatorCallDepth = 0;

    @ApiStatus.Internal
    public static int navigationCallDepth = 0;

    @ApiStatus.Internal
    public static boolean largeGhastFireballExplosion = false;
}
