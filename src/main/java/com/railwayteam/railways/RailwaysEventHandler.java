package com.railwayteam.railways;

import com.railwayteam.railways.capabilities.CartScheduleCapability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RailwaysEventHandler {
  @SubscribeEvent
  public void attachCapabilitiesEntity (final AttachCapabilitiesEvent<Entity> ace) {
    if (ace.getObject() instanceof MinecartEntity) {
      ace.addCapability(new ResourceLocation(Railways.MODID, "schedule"), new CartScheduleCapability() );
    }
  }
}
