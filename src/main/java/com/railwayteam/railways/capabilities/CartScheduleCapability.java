package com.railwayteam.railways.capabilities;

import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CartScheduleCapability implements ICapabilitySerializable<ListNBT> {
  @CapabilityInject(ISchedulable.class)
  public static Capability<ISchedulable> CART_SCHEDULE;
  private LazyOptional<ISchedulable> instance = LazyOptional.of(CART_SCHEDULE::getDefaultInstance);

  @SubscribeEvent
  public static void onCommonSetup (FMLCommonSetupEvent event) {
    CapabilityManager.INSTANCE.register(ISchedulable.class, ScheduleStorage.scheduleStorage, Schedulable::new);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> cap, @Nullable Direction side) {
    return CART_SCHEDULE.orEmpty(cap, instance);
  }

  @Override
  public ListNBT serializeNBT () {
    return (ListNBT)CART_SCHEDULE.getStorage()
      .writeNBT(CART_SCHEDULE, instance.orElseThrow(()-> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
  }

  @Override
  public void deserializeNBT (ListNBT lnbt) {
    CART_SCHEDULE.getStorage().readNBT(CART_SCHEDULE, instance.orElseThrow(()-> new IllegalArgumentException("LazyOptional cannot be empty!")), null, lnbt);
  }
}
