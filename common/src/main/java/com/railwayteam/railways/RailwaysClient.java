package com.railwayteam.railways;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.RailwayMapPlugin;
import com.railwayteam.railways.content.conductor.ConductorCapModel;
import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.railwayteam.railways.registry.CRBlockPartials.*;

public class RailwaysClient {

  public static void init() {
    registerModelLayer(ConductorEntityModel.LAYER_LOCATION, ConductorEntityModel::createBodyLayer);
    registerModelLayer(ConductorCapModel.LAYER_LOCATION, ConductorCapModel::createBodyLayer);

    registerBuiltinPack("legacy_semaphore", "Steam 'n Rails Legacy Semaphores");
    registerBuiltinPack("green_signals", "Steam 'n Rails Green Signals");

    registerClientCommands(CRCommandsClient::register);

    CRPackets.PACKETS.registerS2CListener();

    CRPonderIndex.register();
    CRBlockPartials.init();
    CustomTrackOverlayRendering.register(CREdgePointTypes.COUPLER, CRBlockPartials.COUPLER_BOTH);
    CustomTrackOverlayRendering.register(CREdgePointTypes.SWITCH, CRBlockPartials.SWITCH_RIGHT_TURN);

    Mods.JOURNEYMAP.executeIfInstalled(() -> RailwayMapPlugin::load);
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

    registerCustomCap("IThundxr", "ithundxr", true);
    registerCustomSkin("IThundxr", "ithundxr");

    registerCustomCap("Crown", "crown", true);
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
