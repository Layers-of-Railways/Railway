package com.railwayteam.railways.content.conductor.toolbox;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin_interfaces.IMountedToolboxHandler;
import com.railwayteam.railways.util.packet.PacketSender;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.curiosities.toolbox.ToolboxHandler;
import com.simibubi.create.content.curiosities.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//Sort of simulates a ToolboxTileEntity, but carried by a conductor
public class MountedToolboxHolder implements MenuProvider, Nameable {

  public final LerpedFloat lid = LerpedFloat.linear()
      .startWithValue(0);

  public final LerpedFloat drawers = LerpedFloat.linear()
      .startWithValue(0);

  protected final ConductorEntity parent;
  UUID uniqueId;
  final MountedToolboxInventory inventory;
  LazyOptional<IItemHandler> inventoryProvider;
  DyeColor color;
  final Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers;
  protected int openCount;

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

  public ConductorEntity getParent() {
    return parent;
  }

  public void initialize() {
    IMountedToolboxHandler.onLoad(parent);
    this.lazyTick();
  }

  public void setRemoved() {
    IMountedToolboxHandler.onUnload(parent);
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

    lid.chase(openCount > 0 ? 1 : 0, 0.2f, LerpedFloat.Chaser.LINEAR);
    drawers.chase(openCount > 0 ? 1 : 0, 0.2f, LerpedFloat.Chaser.EXP);
    lid.tickChaser();
    drawers.tickChaser();
  }

  public List<Player> getConnectedPlayers() {
    Set<Player> players = new HashSet<>();
    for (Map.Entry<Integer, WeakHashMap<Player, Integer>> entry : connectedPlayers.entrySet()) {
       players.addAll(entry.getValue().keySet());
    }
    return players.stream().toList();
  }

  private void tickPlayers() {
    boolean update = false;

    for (Iterator<Map.Entry<Integer, WeakHashMap<Player, Integer>>> toolboxSlots = connectedPlayers.entrySet()
        .iterator(); toolboxSlots.hasNext();) {

      Map.Entry<Integer, WeakHashMap<Player, Integer>> toolboxSlotEntry = toolboxSlots.next();
      WeakHashMap<Player, Integer> set = toolboxSlotEntry.getValue();
      int slot = toolboxSlotEntry.getKey();

      ItemStack referenceItem = inventory.filters.get(slot);
      boolean clear = referenceItem.isEmpty();

      for (Iterator<Map.Entry<Player, Integer>> playerEntries = set.entrySet()
          .iterator(); playerEntries.hasNext();) {
        Map.Entry<Player, Integer> playerEntry = playerEntries.next();

        Player player = playerEntry.getKey();
        int hotbarSlot = playerEntry.getValue();

        if (!clear && !IMountedToolboxHandler.withinRange(player, parent))
          continue;

        Inventory playerInv = player.getInventory();
        ItemStack playerStack = playerInv.getItem(hotbarSlot);

        if (clear || !playerStack.isEmpty()
            && !MountedToolboxInventory.canItemsShareCompartment(playerStack, referenceItem)) {
          player.getPersistentData()
              .getCompound("CreateToolboxData")
              .remove(String.valueOf(hotbarSlot));
          playerEntries.remove();
          if (player instanceof ServerPlayer)
            ToolboxHandler.syncData(player);
          continue;
        }

        int count = playerStack.getCount();
        int targetAmount = (referenceItem.getMaxStackSize() + 1) / 2;

        if (count < targetAmount) {
          int amountToReplenish = targetAmount - count;

          if (isOpenInContainer(player)) {
            ItemStack extracted = inventory.takeFromCompartment(amountToReplenish, slot, true);
            if (!extracted.isEmpty()) {
              ToolboxHandler.unequip(player, hotbarSlot, false);
              ToolboxHandler.syncData(player);
              continue;
            }
          }

          ItemStack extracted = inventory.takeFromCompartment(amountToReplenish, slot, false);
          if (!extracted.isEmpty()) {
            update = true;
            ItemStack template = playerStack.isEmpty() ? extracted : playerStack;
            playerInv.setItem(hotbarSlot,
                ItemHandlerHelper.copyStackWithSize(template, count + extracted.getCount()));
          }
        }

        if (count > targetAmount) {
          int amountToDeposit = count - targetAmount;
          ItemStack toDistribute = ItemHandlerHelper.copyStackWithSize(playerStack, amountToDeposit);

          if (isOpenInContainer(player)) {
            int deposited = amountToDeposit - inventory.distributeToCompartment(toDistribute, slot, true)
                .getCount();
            if (deposited > 0) {
              ToolboxHandler.unequip(player, hotbarSlot, true);
              ToolboxHandler.syncData(player);
              continue;
            }
          }

          int deposited = amountToDeposit - inventory.distributeToCompartment(toDistribute, slot, false)
              .getCount();
          if (deposited > 0) {
            update = true;
            playerInv.setItem(hotbarSlot,
                ItemHandlerHelper.copyStackWithSize(playerStack, count - deposited));
          }
        }
      }

      if (clear)
        toolboxSlots.remove();
    }

    if (update)

      sendData();

  }

  private boolean isOpenInContainer(Player player) {
    return player.containerMenu instanceof MountedToolboxContainer
        && ((MountedToolboxContainer) player.containerMenu).contentHolder == parent;
  }

  public void unequipTracked() {
    if (parent.level.isClientSide)
      return;

    Set<ServerPlayer> affected = new HashSet<>();

    for (Map.Entry<Integer, WeakHashMap<Player, Integer>> toolboxSlotEntry : connectedPlayers.entrySet()) {

      WeakHashMap<Player, Integer> set = toolboxSlotEntry.getValue();

      for (Map.Entry<Player, Integer> playerEntry : set.entrySet()) {
        Player player = playerEntry.getKey();
        int hotbarSlot = playerEntry.getValue();

        ToolboxHandler.unequip(player, hotbarSlot, false);
        if (player instanceof ServerPlayer)
          affected.add((ServerPlayer) player);
      }
    }

    for (ServerPlayer player : affected)
      ToolboxHandler.syncData(player);
    connectedPlayers.clear();
  }

  public void unequip(int slot, Player player, int hotbarSlot, boolean keepItems) {
    if (!connectedPlayers.containsKey(slot))
      return;
    connectedPlayers.get(slot)
        .remove(player);
    if (keepItems)
      return;

    Inventory playerInv = player.getInventory();
    ItemStack playerStack = playerInv.getItem(hotbarSlot);
    ItemStack toInsert = ToolboxInventory.cleanItemNBT(playerStack.copy());
    ItemStack remainder = inventory.distributeToCompartment(toInsert, slot, false);

    if (remainder.getCount() != toInsert.getCount())
      playerInv.setItem(hotbarSlot, remainder);
  }

  private void tickAudio() {
    Vec3 vec = parent.position();
    if (lid.settled()) {
      if (openCount > 0 && lid.getChaseTarget() == 0) {
        getLevel().playLocalSound(vec.x, vec.y, vec.z, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.25F,
            getLevel().random.nextFloat() * 0.1F + 1.2F, true);
        getLevel().playLocalSound(vec.x, vec.y, vec.z, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.1F,
            getLevel().random.nextFloat() * 0.1F + 1.1F, true);
      }
      if (openCount == 0 && lid.getChaseTarget() == 1)
        getLevel().playLocalSound(vec.x, vec.y, vec.z, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.1F,
            getLevel().random.nextFloat() * 0.1F + 1.1F, true);

    } else if (openCount == 0 && lid.getChaseTarget() == 0 && lid.getValue(0) > 1 / 16f
        && lid.getValue(1) < 1 / 16f)
      getLevel().playLocalSound(vec.x, vec.y, vec.z, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.25F,
          getLevel().random.nextFloat() * 0.1F + 1.2F, true);
  }

  public void sendData() {
    if (this.parent.level.isClientSide)
      return;
    CompoundTag nbt = new CompoundTag();
    this.write(nbt, true);
    PacketSender.syncMountedToolboxNBT(this.parent, nbt);
  }

  public Level getLevel() {
    return parent.getLevel();
  }

  @SuppressWarnings("EmptyMethod")
  public void setChanged() {}

  public void lazyTick() {
    updateOpenCount();
    // keep re-advertising active TEs
    IMountedToolboxHandler.onLoad(parent);
  }

  void updateOpenCount() {
    if (getLevel().isClientSide)
      return;
    if (openCount == 0)
      return;

    int prevOpenCount = openCount;
    openCount = 0;

    for (Player playerentity : getLevel().getEntitiesOfClass(Player.class, new AABB(parent.position(), parent.position()).inflate(8)))
      if (playerentity.containerMenu instanceof MountedToolboxContainer
          && ((MountedToolboxContainer) playerentity.containerMenu).contentHolder == parent)
        openCount++;

    sendData();
  }

  public void startOpen(Player player) {
    if (player.isSpectator())
      return;
    if (openCount < 0)
      openCount = 0;
    openCount++;
    sendData();
  }

  public void stopOpen(Player player) {
    if (player.isSpectator())
      return;
    openCount--;
    sendData();
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
    if (clientPacket)
      openCount = compound.getInt("OpenCount");
  }

  public void write(CompoundTag compound, boolean clientPacket) {
    compound.putInt("Color", color.getId());
    if (uniqueId == null)
      uniqueId = UUID.randomUUID();

    compound.put("Inventory", inventory.serializeNBT());
    compound.putUUID("UniqueId", uniqueId);

    if (customName != null)
      compound.putString("CustomName", Component.Serializer.toJson(customName));
    if (clientPacket)
      compound.putInt("OpenCount", openCount);
  }

  public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
    return MountedToolboxContainer.create(id, inv, this.parent);
  }

  public void connectPlayer(int slot, Player player, int hotbarSlot) {
    if (parent.level.isClientSide)
      return;
    WeakHashMap<Player, Integer> map = connectedPlayers.computeIfAbsent(slot, WeakHashMap::new);
    Integer previous = map.get(player);
    if (previous != null) {
      if (previous == hotbarSlot)
        return;
      ToolboxHandler.unequip(player, previous, false);
    }
    map.put(player, hotbarSlot);
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
    ItemStack stack = new ItemStack(AllBlocks.TOOLBOXES.get(getColor()).get());
    if (hasCustomName())
      stack.setHoverName(getCustomName());
    return stack;
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
