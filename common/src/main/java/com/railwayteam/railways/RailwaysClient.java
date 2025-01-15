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

package com.railwayteam.railways;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.RailwayMapPlugin;
import com.railwayteam.railways.content.buffer.BufferModelUtils;
import com.railwayteam.railways.content.conductor.ConductorCapModel;
import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.railwayteam.railways.ponder.CRPonderPlugin;
import com.railwayteam.railways.registry.*;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.railwayteam.railways.util.DevCapeUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RailwaysClient {
  public static void init() {
    registerModelLayer(ConductorEntityModel.LAYER_LOCATION, ConductorEntityModel::createBodyLayer);
    registerModelLayer(ConductorCapModel.LAYER_LOCATION, ConductorCapModel::createBodyLayer);

    registerBuiltinPack("legacy_semaphore", "Steam 'n' Rails Legacy Semaphores");
    registerBuiltinPack("green_signals", "Steam 'n' Rails Green Signals");
    registerBuiltinPack("legacy_palettes", "Steam 'n' Rails Legacy Palettes Textures");

    registerClientCommands(CRCommandsClient::register);

    CRPackets.PACKETS.registerS2CListener();

    // Register Ponders
    PonderIndex.addPlugin(new CRPonderPlugin());

    CRKeys.register();
    CRBlockPartials.init();

    CustomTrackOverlayRendering.register(CREdgePointTypes.COUPLER, CRBlockPartials.COUPLER_BOTH);
    CustomTrackOverlayRendering.register(CREdgePointTypes.SWITCH, CRBlockPartials.SWITCH_RIGHT_TURN);

    Mods.JOURNEYMAP.executeIfInstalled(() -> RailwayMapPlugin::load);

    CRDevCaps.register();
    BufferModelUtils.register();

    DevCapeUtils.INSTANCE.init();
  }

  @ExpectPlatform
  public static void registerClientCommands(Consumer<CommandDispatcher<SharedSuggestionProvider>> consumer) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void registerModelLayer(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void registerBuiltinPack(String id, String name) {
    throw new AssertionError();
  }
}
