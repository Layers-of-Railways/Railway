package com.railwayteam.railways;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class StationListContainer extends Container {
  public PlayerEntity player;
  protected PlayerInventory playerInventory;

  public StationListContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer extraData) {
    this(type, id, inv);
  }

  public StationListContainer(ContainerType<?> type, int id, PlayerInventory inv) {
    super (type, id);
    player = inv.player;
    playerInventory = inv;
  }

  public StationListContainer(int id, PlayerInventory inv, PacketBuffer extraData) {
    this (Containers.SCHEDULE.type, id, inv, extraData);
  }

  @Override
  public boolean canMergeSlot (ItemStack stack, Slot slotIn) {
    return false;
  }

  @Override
  public boolean canDragIntoSlot (Slot slotIn) {
    return false;
  }

  @Override
  public boolean canInteractWith (PlayerEntity playerIn) {
    return true;
  }

  @Override
  public ItemStack slotClick (int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack transferStackInSlot (PlayerEntity playerIn, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public void onContainerClosed (PlayerEntity player) {
    super.onContainerClosed(player);
  //  player.sendMessage(new StringTextComponent("closed menu"));
  }
}