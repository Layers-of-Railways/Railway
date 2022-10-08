package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CRContainerTypes;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.gui.container.ContainerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static com.simibubi.create.content.curiosities.toolbox.ToolboxInventory.STACKS_PER_COMPARTMENT;

public class MountedToolboxContainer extends ContainerBase<ConductorEntity> {
  public MountedToolboxContainer(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
    super(type, id, inv, extraData);
  }

  public MountedToolboxContainer(MenuType<?> type, int id, Inventory inv, ConductorEntity entity) {
    super(type, id, inv, entity);
    if (entity.isCarryingToolbox())
      entity.getToolboxHolder().startOpen(player);
  }

  public static MountedToolboxContainer create(int id, Inventory inv, ConductorEntity entity) {
    return new MountedToolboxContainer(CRContainerTypes.MOUNTED_TOOLBOX.get(), id, inv, entity);
  }

  @Override
  protected ConductorEntity createOnClient(FriendlyByteBuf extraData) {
    int entity_id = extraData.readInt();
    ClientLevel world = Minecraft.getInstance().level;
    Entity entity = world.getEntity(entity_id);
    if (entity instanceof ConductorEntity conductorEntity) {
      conductorEntity.getOrCreateToolboxHolder().read(extraData.readNbt(), true);
      return conductorEntity;
    }
    return null;
  }

  @Override
  public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
    Slot clickedSlot = getSlot(index);
    if (!clickedSlot.hasItem())
      return ItemStack.EMPTY;

    ItemStack stack = clickedSlot.getItem();
    int size = contentHolder.getToolboxHolder().inventory.getSlots();
    boolean success = false;
    if (index < size) {
      success = !moveItemStackTo(stack, size, slots.size(), false);
      contentHolder.getToolboxHolder().inventory.onContentsChanged(index);
    } else
      success = !moveItemStackTo(stack, 0, size - 1, false);

    return success ? ItemStack.EMPTY : stack;
  }

  @Override
  public void clicked(int index, int flags, ClickType type, Player player) {
    int size = contentHolder.getToolboxHolder().inventory.getSlots();

    if (index >= 0 && index < size) {
      ItemStack itemInClickedSlot = getSlot(index).getItem();
      ItemStack carried = getCarried();

      if (type == ClickType.PICKUP && !carried.isEmpty() && !itemInClickedSlot.isEmpty()
          && ToolboxInventory.canItemsShareCompartment(itemInClickedSlot, carried)) {
        int subIndex = index % STACKS_PER_COMPARTMENT;
        if (subIndex != STACKS_PER_COMPARTMENT - 1) {
          clicked(index - subIndex + STACKS_PER_COMPARTMENT - 1, flags, type, player);
          return;
        }
      }

      if (type == ClickType.PICKUP && carried.isEmpty() && itemInClickedSlot.isEmpty())
        if (!player.level.isClientSide) {
          contentHolder.getToolboxHolder().inventory.filters.set(index / STACKS_PER_COMPARTMENT, ItemStack.EMPTY);
          contentHolder.getToolboxHolder().sendData();
        }

    }
    super.clicked(index, flags, type, player);
  }

  @Override
  public boolean canDragTo(Slot slot) {
    return slot.index > contentHolder.getToolboxHolder().inventory.getSlots() && super.canDragTo(slot);
  }

  public ItemStack getFilter(int compartment) {
    return contentHolder.getToolboxHolder().inventory.filters.get(compartment);
  }

  public int totalCountInCompartment(int compartment) {
    int count = 0;
    int baseSlot = compartment * STACKS_PER_COMPARTMENT;
    for (int i = 0; i < STACKS_PER_COMPARTMENT; i++)
      count += getSlot(baseSlot + i).getItem()
          .getCount();
    return count;
  }

  public boolean renderPass;

  @Override
  protected void addSlots() {
    MountedToolboxInventory inventory = contentHolder.getToolboxHolder().inventory;

    int x = 79;
    int y = 37;

    int[] xOffsets = { x, x + 33, x + 66, x + 66 + 6, x + 66, x + 33, x, x - 6 };
    int[] yOffsets = { y, y - 6, y, y + 33, y + 66, y + 66 + 6, y + 66, y + 33 };

    for (int compartment = 0; compartment < 8; compartment++) {
      int baseIndex = compartment * STACKS_PER_COMPARTMENT;

      // Representative Slots
      addSlot(new MountedToolboxSlot(this, inventory, baseIndex, xOffsets[compartment], yOffsets[compartment]));

      // Hidden Slots
      for (int i = 1; i < STACKS_PER_COMPARTMENT; i++)
        addSlot(new SlotItemHandler(inventory, baseIndex + i, -10000, -10000));
    }

    addPlayerSlots(8, 165);
  }

  @Override
  protected void saveData(ConductorEntity contentHolder) {

  }

  @Override
  protected void initAndReadInventory(ConductorEntity contentHolder) {

  }

  @Override
  public void removed(Player playerIn) {
    super.removed(playerIn);
    if (!playerIn.level.isClientSide) {
      if (contentHolder.isCarryingToolbox())
        contentHolder.getToolboxHolder().stopOpen(playerIn);
    }
  }
}
