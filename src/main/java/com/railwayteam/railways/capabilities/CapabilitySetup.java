package com.railwayteam.railways.capabilities;

import com.railwayteam.railways.Railways;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CapabilitySetup {
  @CapabilityInject(StationListCapability.class)
  public static Capability<StationListCapability> CAPABILITY_STATION_LIST = null;

  public static final ResourceLocation STATION_LIST_KEY = new ResourceLocation(Railways.MODID + ":stationlist");

  private static void register () {
    CapabilityManager.INSTANCE.register(
      StationListCapability.class,
      new StationListCapability.StationListNBTStorage(),
      StationListCapability::createADefaultInstance
    );
  }

  @SubscribeEvent
  public static void onCommonSetupEvent (FMLCommonSetupEvent event) {
    register();
  }
}
