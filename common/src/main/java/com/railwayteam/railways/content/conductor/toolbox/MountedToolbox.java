package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin.AccessorBlockEntity;
import com.railwayteam.railways.mixin.AccessorToolboxBlockEntity;
import com.railwayteam.railways.util.packet.PacketSender;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MountedToolbox extends ToolboxBlockEntity {
  protected final ConductorEntity parent;

  public MountedToolbox(ConductorEntity parent, DyeColor dyeColor) {
    super(AllBlockEntityTypes.TOOLBOX.get(), parent.blockPosition(), AllBlocks.TOOLBOXES.get(dyeColor).getDefaultState());
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

  @Override
  public void tick() {
    // keep saved block pos updated for updateOpenCount and tickAudio
    ((AccessorBlockEntity) this).setWorldPosition(parent.blockPosition());
    super.tick();
  }

  @Override
  public void read(CompoundTag compound, boolean clientPacket) {
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
    Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers = ((AccessorToolboxBlockEntity) this).getConnectedPlayers();
    Set<Player> players = new HashSet<>();
    for (Map.Entry<Integer, WeakHashMap<Player, Integer>> entry : connectedPlayers.entrySet()) {
       players.addAll(entry.getValue().keySet());
    }
    return players.stream().toList();
  }

  @Override
  public void sendData() {
    if (level == null || level.isClientSide)
      return;
    CompoundTag nbt = new CompoundTag();
    this.write(nbt, true);
    PacketSender.syncMountedToolboxNBT(this.parent, nbt);
  }

  @Override
  public void setChanged() {
    // override and do nothing, this isn't in-world
  }

  public static MountedToolbox read(ConductorEntity parent, CompoundTag compound) {
    MountedToolbox holder = new MountedToolbox(parent, DyeColor.BROWN);
    holder.read(compound, false);
    return holder;
  }

  @Override
  public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
    return MountedToolboxContainer.create(id, inv, this);
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

  @Override
  public void sendToContainer(FriendlyByteBuf buffer) {
    buffer.writeVarInt(parent.getId());
    buffer.writeNbt(getUpdateTag());
  }

  @ExpectPlatform
  public static void openMenu(ServerPlayer player, MountedToolbox toolbox) {
    throw new AssertionError();
  }
}
