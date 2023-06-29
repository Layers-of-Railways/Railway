package com.railwayteam.railways;

import com.mojang.brigadier.CommandDispatcher;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.RailwayMapPlugin;
import com.railwayteam.railways.content.conductor.ConductorCapModel;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.railwayteam.railways.mixin.client.AccessorEntity;
import com.railwayteam.railways.util.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.EquipmentSlot;

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

    registerCustomCap("RileyHighline", "rileyhighline");
    registerCustomSkin("RileyHighline", "rileyhighline");
    preventTiltingCap("RileyHighline");

    registerCustomCap("TiesToetToet", "tiestoettoet");
    preventTiltingCap("TiesToetToet");

    registerCustomCap("LemmaEOF", "headphones");
    preventTiltingCap("LemmaEOF");

    registerCustomCap("To0pa", "stonks_hat");
    registerCustomCap("Furti_Two", "stonks_hat_blue");
    registerCustomCap("Aypierre", "stonks_hat_red");

    preventTiltingCap("To0pa");
    preventTiltingCap("Furti_Two");
    preventTiltingCap("Aypierre");

    registerCustomCap("NeonCityDrifter", "neoncitydrifter");

    registerCustomCap("demondj2002", "demon");
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

  public static void transformVisualConductor(AbstractClientPlayer player, ConductorEntity conductor) {
    conductor.xo = player.xo;
    conductor.yo = player.yo;
    conductor.zo = player.zo;
    conductor.xOld = player.xOld;
    conductor.yOld = player.yOld;
    conductor.zOld = player.zOld;
    conductor.xRotO = player.xRotO;
    conductor.yRotO = player.yRotO;
    ((AccessorEntity) conductor).setXRot(player.getXRot());
    ((AccessorEntity) conductor).setYRot(player.getYRot());

    conductor.yHeadRot = player.yHeadRot;
    conductor.yBodyRot = player.yBodyRot;
    conductor.yBodyRotO = player.yBodyRotO;
    conductor.yHeadRotO = player.yHeadRotO;

    conductor.animationPosition = player.animationPosition;
    conductor.animationSpeed = player.animationSpeed;
    conductor.animationSpeedOld = player.animationSpeedOld;

    conductor.tickCount = player.tickCount;

    conductor.setOnGround(player.isOnGround());

    for (EquipmentSlot slot : EquipmentSlot.values())
      conductor.setItemSlot(slot, player.getItemBySlot(slot));

    conductor.setSharedFlag(7, player.isFallFlying());
  }
}
