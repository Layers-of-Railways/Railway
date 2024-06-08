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
