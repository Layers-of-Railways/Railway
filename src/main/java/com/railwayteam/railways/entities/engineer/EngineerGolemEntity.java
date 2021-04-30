package com.railwayteam.railways.entities.engineer;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;

public class EngineerGolemEntity extends LivingEntity {
  public static final String name = "engineer_golem";

  public EngineerGolemEntity(EntityType<? extends LivingEntity> type, World world) {
    super(type, world);
  }

  @Override
  public Iterable<ItemStack> getArmorInventoryList() {
    return new ArrayList<ItemStack>();
  }

  @Override
  public ItemStack getItemStackFromSlot(EquipmentSlotType slotType) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setItemStackToSlot(EquipmentSlotType slotType, ItemStack stack) {
  }

  @Override
  public HandSide getPrimaryHand() {
    return null;
  }

  @Override
  public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}
