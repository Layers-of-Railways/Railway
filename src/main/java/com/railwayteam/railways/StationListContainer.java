package com.railwayteam.railways;

import com.railwayteam.railways.items.StationEditorItem;
import com.railwayteam.railways.items.StationLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;

public class StationListContainer extends Container {
  public PlayerEntity player;
  protected String target;
  protected PlayerInventory playerInventory;
  protected ArrayList<StationLocation> stationList;

  public StationListContainer(ContainerType<?> type, int id, PlayerInventory inv, PacketBuffer extraData) {
    this(type, id, inv);
    target = extraData.readString();
    if (target.equals("minecart")) {
      Railways.LOGGER.debug("sent to minecart");
    }
    int len = extraData.readInt();
    for (int index=0; index<len; index++) {
      stationList.add( new StationLocation(extraData.readString()) );
    }
  }

  public StationListContainer(int id, PlayerInventory inv, PacketBuffer extraData) {
    this (Containers.SCHEDULE.type, id, inv, extraData);
  }

  public StationListContainer (int id, PlayerInventory inv, ArrayList<StationLocation> stations) {
    this (Containers.SCHEDULE.type, id, inv);
    stationList = stations;
  }

  public StationListContainer(ContainerType<?> type, int id, PlayerInventory inv) {
    super (type, id);
    player = inv.player;
    playerInventory = inv;
    stationList = new ArrayList<StationLocation>();
  }

  public void updateStationList (ArrayList<String> stations) {
    stationList.clear();
    stations.forEach(entry->stationList.add(new StationLocation(entry)));
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
}