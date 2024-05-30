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

package com.railwayteam.railways;

import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.registry.*;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class ModSetup {

  @ExpectPlatform
  public static void useBaseTab() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void useTracksTab() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void usePalettesTab() {
    throw new AssertionError();
  }

  public static void register() {
    useBaseTab();
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRCreativeModeTabs.register();
    CRItems.register();
    CRSpriteShifts.register();
    CRBlockEntities.register();
    CRBlocks.register();
    CRPalettes.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
    CRDataFixers.register();
    CRExtraRegistration.register();
    CasingCollisionUtils.register();
    CRInteractionBehaviours.register();
    CRMovementBehaviours.register();
    CRPortalTracks.register();

    // Compat
    useTracksTab();
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
    NaturesSpiritTrackCompat.register();
    DreamsAndDesiresTrackCompat.register();
    QuarkTrackCompat.register();
    TFCTrackCompat.register();
  }
}
