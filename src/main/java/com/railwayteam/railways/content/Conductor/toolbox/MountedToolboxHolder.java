package com.railwayteam.railways.content.Conductor.toolbox;

import com.railwayteam.railways.content.Conductor.ConductorEntity;
import com.railwayteam.railways.util.packet.MountedToolboxSyncPacket;
import com.railwayteam.railways.util.packet.PacketSender;
import com.simibubi.create.AllBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

//Sort of simulates a ToolboxTileEntity, but carried by a conductor
public class MountedToolboxHolder implements MenuProvider, Nameable {
  protected ConductorEntity parent;
  UUID uniqueId;
  MountedToolboxInventory inventory;
  LazyOptional<IItemHandler> inventoryProvider;
  DyeColor color;
  Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers;

  private boolean initialized = false;
  protected int lazyTickCounter;
  protected int lazyTickRate;

  private Component customName;

  public MountedToolboxHolder(ConductorEntity parent, DyeColor dyeColor) {
    this.parent = parent;
    connectedPlayers = new HashMap<>();
    inventory = new MountedToolboxInventory(this);
    inventoryProvider = LazyOptional.of(() -> inventory);
    color = dyeColor;
    setLazyTickRate(10);
  }

  public void readFromItem(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    readInventory(tag.getCompound("Inventory"));
    if (tag.contains("UniqueId"))
      setUniqueId(tag.getUUID("UniqueId"));
    if (stack.hasCustomHoverName())
      setCustomName(stack.getHoverName());
  }

  public DyeColor getColor() {
    return color;
  }

  public void initialize() {
    //TODO toolbox handler
    this.lazyTick();
  }

  public void setRemoved() {
    //TODO toolbox handler
  }

  public void setLazyTickRate(int slowTickRate) {
    this.lazyTickRate = slowTickRate;
    this.lazyTickCounter = slowTickRate;
  }

  public void tick() {
    if (!initialized && getLevel() != null) {
      initialize();
      initialized = true;
    }

    if (lazyTickCounter-- <= 0) {
      lazyTickCounter = lazyTickRate;
      lazyTick();
    }

    if (getLevel().isClientSide) {
      tickAudio();
    } else {
      tickPlayers();
    }
  }

  private void tickAudio() {
    //TODO
  }

  private void tickPlayers() {
    //TODO
  }

  public void sendData() {
    if (this.parent.level.isClientSide)
      return;
    CompoundTag nbt = new CompoundTag();
    this.write(nbt, true);
    PacketSender.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.parent), new MountedToolboxSyncPacket(this.parent, nbt));
  }

  public Level getLevel() {
    return parent.getLevel();
  }

  public void setChanged() {}

  public void lazyTick() {
    //TODO
  }

  public static MountedToolboxHolder read(ConductorEntity parent, CompoundTag compound) {
    MountedToolboxHolder holder = new MountedToolboxHolder(parent, DyeColor.BROWN);
    holder.read(compound, false);
    return holder;
  }

  public void read(CompoundTag compound, boolean clientPacket) {
    if (compound.contains("Color", CompoundTag.TAG_INT))
      color = DyeColor.byId(compound.getInt("Color"));
    inventory.deserializeNBT(compound.getCompound("Inventory"));
    if (compound.contains("UniqueId", 11))
      this.uniqueId = compound.getUUID("UniqueId");
    if (compound.contains("CustomName", 8))
      this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
    /*if (clientPacket)
      openCount = compound.getInt("OpenCount");*/
  }

  public void write(CompoundTag compound, boolean clientPacket) {
    compound.putInt("Color", color.getId());
    if (uniqueId == null)
      uniqueId = UUID.randomUUID();

    compound.put("Inventory", inventory.serializeNBT());
    compound.putUUID("UniqueId", uniqueId);

    if (customName != null)
      compound.putString("CustomName", Component.Serializer.toJson(customName));
    /*if (clientPacket)
      compound.putInt("OpenCount", openCount);*/
  }

  public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
    return MountedToolboxContainer.create(id, inv, this.parent);
  }

  public void readInventory(CompoundTag compoundTag) {
    inventory.deserializeNBT(compoundTag);
  }

  public void setUniqueId(UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public boolean isFullyInitialized() {
    // returns true when uniqueId has been initialized
    return uniqueId != null;
  }

  public void setCustomName(Component customName) {
    this.customName = customName;
  }

  @Override
  public @NotNull Component getDisplayName() {
    return customName != null ? customName
        : AllBlocks.TOOLBOXES.get(getColor())
        .get()
        .getName();
  }

  @Override
  public Component getCustomName() {
    return customName;
  }

  @Override
  public boolean hasCustomName() {
    return customName != null;
  }

  @Override
  public @NotNull Component getName() {
    return customName;
  }

  public ItemStack getDisplayStack() {
    return new ItemStack(AllBlocks.TOOLBOXES.get(getColor()).get()).setHoverName(getDisplayName());
  }

  public void sendToContainer(FriendlyByteBuf friendlyByteBuf) {
    friendlyByteBuf.writeInt(this.parent.getId());
    CompoundTag compound = new CompoundTag();
    this.write(compound, true);
    friendlyByteBuf.writeNbt(compound);
  }

  public ItemStack getCloneItemStack() {
    ItemStack stack = getDisplayStack();
    CompoundTag tag = stack.getOrCreateTag();
    CompoundTag inv = inventory.serializeNBT();
    tag.put("Inventory", inv);

    tag.putUUID("UniqueId", getUniqueId());

    return stack;
  }

  public LazyOptional<IItemHandler> getInventoryProvider() {
    return inventoryProvider;
  }

  public void invalidateCaps() {
    inventoryProvider.invalidate();
  }

  public void reviveCaps() {
    inventoryProvider = LazyOptional.of(() -> inventory);
  }
}
