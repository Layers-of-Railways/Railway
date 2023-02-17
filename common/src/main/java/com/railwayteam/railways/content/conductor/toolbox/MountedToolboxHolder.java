package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin.AccessorBlockEntity;
import com.railwayteam.railways.mixin.AccessorToolboxTileEntity;
import com.railwayteam.railways.util.packet.PacketSender;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.curiosities.toolbox.ToolboxTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MountedToolboxHolder extends ToolboxTileEntity {
  protected final ConductorEntity parent;

  public MountedToolboxHolder(ConductorEntity parent, DyeColor dyeColor) {
    super(AllTileEntities.TOOLBOX.get(), parent.blockPosition(), AllBlocks.TOOLBOXES.get(dyeColor).getDefaultState());
    this.parent = parent;
    setLevel(parent.level);
    setLazyTickRate(10);
  }

  public void readFromItem(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    if (tag == null)
      return;
    readInventory(tag.getCompound("Inventory"));
    if (tag.contains("UniqueId"))
      setUniqueId(tag.getUUID("UniqueId"));
    if (stack.hasCustomHoverName())
      setCustomName(stack.getHoverName());
  }

  public ConductorEntity getParent() {
    return parent;
  }

  public void tick() {
    // keep saved block pos updated for updateOpenCount and tickAudio
    ((AccessorBlockEntity) this).setWorldPosition(parent.blockPosition());
    super.tick();
  }

  @Override
  protected void read(CompoundTag compound, boolean clientPacket) {
    super.read(compound, clientPacket);
    if (compound.contains("Color", CompoundTag.TAG_INT)) {
      DyeColor color = DyeColor.byId(compound.getInt("Color"));
      // change the color by setting the stored state and updating the color provider
      BlockState state = AllBlocks.TOOLBOXES.get(color).get().defaultBlockState();
      setBlockState(state);
    }
  }

  @Override
  public void write(CompoundTag compound, boolean clientPacket) {
    super.write(compound, clientPacket);
    compound.putInt("Color", getColor().getId());
  }

  // TODO: does this conflict with the accessor?
  public List<Player> getConnectedPlayers() {
    Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers = ((AccessorToolboxTileEntity) this).getConnectedPlayers();
    Set<Player> players = new HashSet<>();
    for (Map.Entry<Integer, WeakHashMap<Player, Integer>> entry : connectedPlayers.entrySet()) {
       players.addAll(entry.getValue().keySet());
    }
    return players.stream().toList();
  }

  public void sendData() {
    if (level == null || level.isClientSide)
      return;
    CompoundTag nbt = new CompoundTag();
    this.write(nbt, true);
    PacketSender.syncMountedToolboxNBT(this.parent, nbt);
  }

  public void setChanged() {
    // override and do nothing, this isn't in-world
  }

  public static MountedToolboxHolder read(ConductorEntity parent, CompoundTag compound) {
    MountedToolboxHolder holder = new MountedToolboxHolder(parent, DyeColor.BROWN);
    holder.read(compound, false);
    return holder;
  }

  public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
    return MountedToolboxContainer.create(id, inv, this.parent);
  }

  public ItemStack getDisplayStack() {
    ItemStack stack = new ItemStack(AllBlocks.TOOLBOXES.get(getColor()).get());
    if (hasCustomName())
      stack.setHoverName(getCustomName());
    return stack;
  }

  public ItemStack getCloneItemStack() {
    ItemStack stack = getDisplayStack();
    CompoundTag tag = stack.getOrCreateTag();

    CompoundTag data = new CompoundTag();
    write(data, false);
    CompoundTag inv = data.getCompound("Inventory");
    tag.put("Inventory", inv);

    tag.putUUID("UniqueId", getUniqueId());

    return stack;
  }
}
