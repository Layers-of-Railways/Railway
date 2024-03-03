package com.railwayteam.railways.content.conductor;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.content.conductor.vent.VentBlock;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTags;
import com.railwayteam.railways.util.EntityUtils;
import com.railwayteam.railways.util.ItemUtils;
import com.railwayteam.railways.util.Utils;
import com.railwayteam.railways.util.packet.CameraMovePacket;
import com.railwayteam.railways.util.packet.SetCameraViewPacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.WorldAttached;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

import static com.railwayteam.railways.content.conductor.ConductorPossessionController.*;

// note: item handler capability is implemented on forge in CommonEventsForge, and fabric does not have entity APIs
public class ConductorEntity extends AbstractGolem {
  @SuppressWarnings("ConstantValue")
  public static boolean isPlayerDisguised(Player player) {
    if (player.getInventory() == null) return false;
    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
    if (headStack.isEmpty())
      return false;
    if (!CRTags.AllItemTags.CONDUCTOR_CAPS.matches(headStack))
      return false;
    String hoverName = headStack.getHoverName().getString();
    return hoverName.startsWith("[sus]") || hoverName.equals("sus");
  }

  public static final GameProfile FAKE_PLAYER_PROFILE = new GameProfile(
          UUID.fromString("B0FADEE5-4411-3475-ADD0-C4EA7E30D050"),
          "[Conductor]"
  );

  public static boolean canSpyInteract(BlockState blockState) {
    Block block = blockState.getBlock();
    return blockState.is(BlockTags.BUTTONS) || blockState.is(BlockTags.TRAPDOORS) || block instanceof LeverBlock
            || block instanceof VentBlock || CRTags.AllBlockTags.CONDUCTOR_SPY_USABLE.matches(blockState);
  }

  public ItemStack getSecondaryHeadStack() {
    return ItemStack.EMPTY;
  }

  public class FrequencyListener implements IRedstoneLinkable {
    public final String key;
    public final @Nullable Couple<Frequency> frequency;
    public int receivedStrength = 0;

    public FrequencyListener(String key) {
      this.key = key;
      this.frequency = ConductorEntity.this.frequencies.entries().get(this.key).orElse(null);
      if (frequency != null)
        Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(ConductorEntity.this.level, this);
    }

    @Override
    public int getTransmittedStrength() {
      return 0;
    }

    @Override
    public void setReceivedStrength(int power) {
      receivedStrength = power;
    }

    @Override
    public boolean isListening() {
      return true;
    }

    @Override
    public boolean isAlive() {
      return ConductorEntity.this.isAlive() && frequency != null;
    }

    @Override
    public Couple<Frequency> getNetworkKey() {
      return frequency;
    }

    @Override
    public BlockPos getLocation() {
      return ConductorEntity.this.blockPosition();
    }

    public boolean isPowered() {
      return receivedStrength > 0;
    }
  }

  private final FrequencyHolder frequencies = new FrequencyHolder();

  public class FrequencyHolder implements Iterable<Optional<Couple<@NotNull Frequency>>> {
    public FrequencyHolder() {
    }

    public FrequencyHolder(@Nullable Couple<@NotNull Frequency> forward, @Nullable Couple<@NotNull Frequency> backward,
                           @Nullable Couple<@NotNull Frequency> left, @Nullable Couple<@NotNull Frequency> right,
                           @Nullable Couple<@NotNull Frequency> jump, @Nullable Couple<@NotNull Frequency> sneak) {
      this.setForward(forward);
      this.setBackward(backward);
      this.setLeft(left);
      this.setRight(right);
      this.setJump(jump);
      this.setSneak(sneak);
    }

    @NotNull
    @Override
    public Iterator<Optional<Couple<@NotNull Frequency>>> iterator() {
      return List.of(
              Optional.ofNullable(getForward()),
              Optional.ofNullable(getBackward()),
              Optional.ofNullable(getLeft()),
              Optional.ofNullable(getRight()),
              Optional.ofNullable(getJump()),
              Optional.ofNullable(getSneak())
      ).iterator();
    }

    public Map<String, Optional<Couple<@NotNull Frequency>>> entries() {
      return Map.of(
              "forward", Optional.ofNullable(getForward()),
              "backward", Optional.ofNullable(getBackward()),
              "left", Optional.ofNullable(getLeft()),
              "right", Optional.ofNullable(getRight()),
              "jump", Optional.ofNullable(getJump()),
              "sneak", Optional.ofNullable(getSneak())
      );
    }

    public Map<String, Couple<@NotNull ItemStack>> stacks() {
      Map<String, Couple<@NotNull ItemStack>> stacks = new HashMap<>();
      for (Map.Entry<String, Optional<Couple<@NotNull Frequency>>> entry : entries().entrySet()) {
        if (entry.getValue().isEmpty()) {
          stacks.put(entry.getKey(), Couple.create(ItemStack.EMPTY, ItemStack.EMPTY));
        } else {
          Couple<@NotNull Frequency> freqs = entry.getValue().get();
          stacks.put(entry.getKey(), Couple.create(freqs.getFirst().getStack(), freqs.getSecond().getStack()));
        }
      }
      return ImmutableMap.copyOf(stacks);
    }

    public Map<String, Consumer<Optional<Couple<@NotNull Frequency>>>> setters() {
      return Map.of(
              "forward", (freq) -> setForward(freq.orElse(null)),
              "backward", (freq) -> setBackward(freq.orElse(null)),
              "left", (freq) -> setLeft(freq.orElse(null)),
              "right", (freq) -> setRight(freq.orElse(null)),
              "jump", (freq) -> setJump(freq.orElse(null)),
              "sneak", (freq) -> setSneak(freq.orElse(null))
      );
    }

    public List<Consumer<Optional<Couple<@NotNull Frequency>>>> settersInOrder() {
      return List.of(
              (freq) -> setForward(freq.orElse(null)),
              (freq) -> setBackward(freq.orElse(null)),
              (freq) -> setLeft(freq.orElse(null)),
              (freq) -> setRight(freq.orElse(null)),
              (freq) -> setJump(freq.orElse(null)),
              (freq) -> setSneak(freq.orElse(null))
      );
    }

    public CompoundTag write() {
      CompoundTag tag = new CompoundTag();
      for (var freq : this.entries().entrySet()) {
        if (freq.getValue().isPresent()) {
          CompoundTag subTag = new CompoundTag();
          subTag.put("first", freq.getValue().get().getFirst().getStack().save(new CompoundTag()));
          subTag.put("second", freq.getValue().get().getSecond().getStack().save(new CompoundTag()));
          tag.put(freq.getKey(), subTag);
        }
      }
      return tag;
    }

    public FrequencyHolder read(CompoundTag tag) {
      for (var freq : this.setters().entrySet()) {
        if (tag.contains(freq.getKey(), Tag.TAG_COMPOUND)) {
          ItemStack first = ItemStack.of(tag.getCompound(freq.getKey()).getCompound("first"));
          ItemStack second = ItemStack.of(tag.getCompound(freq.getKey()).getCompound("second"));
          freq.getValue().accept(Optional.of(Couple.create(Frequency.of(first), Frequency.of(second))));
        } else {
          freq.getValue().accept(Optional.empty());
        }
      }
      return this;
    }

    private static Couple<ItemStack> freqToStacks(@Nullable Couple<Frequency> freqs) {
      if (freqs == null)
        return Couple.create(ItemStack.EMPTY, ItemStack.EMPTY);
      return freqs.map(Frequency::getStack);
    }

    private static Couple<Frequency> stacksToFreq(Couple<ItemStack> stacks) {
      return stacks.map(Frequency::of);
    }

    private void set(String name, @Nullable Couple<Frequency> value) {
      Couple<ItemStack> stacks = freqToStacks(value);

      Couple<EntityDataAccessor<ItemStack>> freqAccessors = FREQUENCY_DATA.get(name);
      for (boolean first : Iterate.trueAndFalse)
        ConductorEntity.this.entityData.set(freqAccessors.get(first), stacks.get(first));
    }

    private Couple<Frequency> get(String name) {
      Couple<EntityDataAccessor<ItemStack>> freqAccessors = FREQUENCY_DATA.get(name);

      return stacksToFreq(freqAccessors.map(ConductorEntity.this.entityData::get));
    }

    @Nullable
    public Couple<Frequency> getForward() {
      return get("forward");
    }

    public void setForward(@Nullable Couple<Frequency> forward) {
      set("forward", forward);
    }

    @Nullable
    public Couple<Frequency> getBackward() {
      return get("backward");
    }

    public void setBackward(@Nullable Couple<Frequency> backward) {
      set("backward", backward);
    }

    @Nullable
    public Couple<Frequency> getLeft() {
      return get("left");
    }

    public void setLeft(@Nullable Couple<Frequency> left) {
      set("left", left);
    }

    @Nullable
    public Couple<Frequency> getRight() {
      return get("right");
    }

    public void setRight(@Nullable Couple<Frequency> right) {
      set("right", right);
    }

    @Nullable
    public Couple<Frequency> getJump() {
      return get("jump");
    }

    public void setJump(@Nullable Couple<Frequency> jump) {
      set("jump", jump);
    }

    @Nullable
    public Couple<Frequency> getSneak() {
      return get("sneak");
    }

    public void setSneak(@Nullable Couple<Frequency> sneak) {
      set("sneak", sneak);
    }
  }

  public static final WorldAttached<Set<ConductorEntity>> WITH_TOOLBOXES = new WorldAttached<>(w -> new HashSet<>());

  public static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BYTE);
  public static final EntityDataAccessor<BlockPos> BLOCK = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BLOCK_POS);
  public static final EntityDataAccessor<Integer> JOB = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.INT);
  public static final EntityDataAccessor<Boolean> HOLDING_SCHEDULES = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);

  public static final Map<String, Couple<EntityDataAccessor<ItemStack>>> FREQUENCY_DATA = new HashMap<>();

  static {
    for (String name : List.of("forward", "backward", "left", "right", "jump", "sneak")) {
      FREQUENCY_DATA.put(name, Couple.create(
              SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.ITEM_STACK),
              SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.ITEM_STACK)
      ));
    }
  }

  // keep this small for performance (plus conductors are smol)
  private static final Vec3i REACH = new Vec3i(3, 2, 3);

  private ServerPlayer fakePlayer = null;
  MountedToolbox toolbox = null;
  private List<ItemStack> heldSchedules;

  // possession is based on SecurityCraft (MIT license)
  /* Possession variables */
  // keep track of position for packets
  public double firstGoodX;
  public double firstGoodY;
  public double firstGoodZ;
  public double lastGoodX;
  public double lastGoodY;
  public double lastGoodZ;
  public int receivedMovePacketCount;
  public int knownMovePacketCount;

  // client variables for packets
  public double xLast;
  public double yLast;
  public double zLast;
  public double xRotLast;
  public double yRotLast;

  private void resetPosition() {
    firstGoodX = lastGoodX = getX();
    firstGoodY = lastGoodY = getY();
    firstGoodZ = lastGoodZ = getZ();
    knownMovePacketCount = receivedMovePacketCount;
  }

  public void doCheckFallDamage(double y, boolean onGround) {
    if (this.touchingUnloadedChunk()) {
      return;
    }
    BlockPos blockPos = this.getOnPos();
    super.checkFallDamage(y, onGround, this.level.getBlockState(blockPos), blockPos);
  }

  // make public
  @Override
  public void jumpFromGround() {
    super.jumpFromGround();
  }

  public void teleportToForce(double x, double y, double z) {
    this.absMoveTo(x, y, z, this.getYRot(), this.getXRot());
    this.getSelfAndPassengers().forEach(entity -> {
      for (Entity entity2 : entity.getPassengers()) {
        entity.positionRider(entity2);//, Entity::moveTo);
      }
    });

    if (level instanceof ServerLevel serverLevel) {
      serverLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(this.blockPosition()), 5, this.getId());
    }

    firstGoodX = x;
    firstGoodY = y;
    firstGoodZ = z;
    lastGoodX = x;
    lastGoodY = y;
    lastGoodZ = z;
    ServerPlayer player;
    if (isPossessed() && (player = currentlyViewing.get()) != null) {
      CRPackets.PACKETS.sendTo(player, new CameraMovePacket(this, new ServerboundMovePlayerPacket.PosRot(x, y, z, this.getYRot(), this.getXRot(), this.isOnGround())));
    }
  }

  public int ventCooldown = 0;

  public static final List<Player> RECENTLY_DISMOUNTED_PLAYERS = new ArrayList<>();
  @NotNull
  private WeakReference<ServerPlayer> currentlyViewing = new WeakReference<>(null);
  private int initialChunkLoadingDistance = 0;
  private boolean hasSentChunks = false;

  public void setChunkLoadingDistance(int chunkLoadingDistance) {
    initialChunkLoadingDistance = chunkLoadingDistance;
  }

  public boolean hasSentChunks() {
    return hasSentChunks;
  }

  public void setHasSentChunks(boolean hasSentChunks) {
    this.hasSentChunks = hasSentChunks;
  }

  public SectionPos oldSectionPos = null;

  public static boolean hasRecentlyDismounted(Player player) {
    return RECENTLY_DISMOUNTED_PLAYERS.remove(player);
  }

  public boolean startViewing(ServerPlayer player) {
    ServerPlayer current = currentlyViewing.get();
    if (current != null && current.getCamera() == this && current.isAlive() && current != player) {
      return false;
    }
    ServerLevel serverLevel = player.getLevel();
    if (serverLevel != level) {
      return false;
    }
    currentlyViewing = new WeakReference<>(player);
    oldSectionPos = null;
    SectionPos chunkPos = SectionPos.of(blockPosition());
    int viewDistance = player.server.getPlayerList().getViewDistance();

    if (player.getCamera() instanceof ConductorEntity conductor)
      conductor.stopViewing(player);

    setChunkLoadingDistance(viewDistance);

    /*for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
      for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
        ForgeChunkManager.forceChunk(serverLevel, SecurityCraft.MODID, dummyEntity, x, z, true, false);
      }
    }*/ // put chunkloading tickets in #tick

    //can't use ServerPlayer#setCamera here because it also teleports the player
    player.camera = this;
    CRPackets.PACKETS.sendTo(player, new SetCameraViewPacket(this));
    resetPosition();
    // update ConductorPossessionController.setRenderPosition in #tick
    return true;
  }

  public void stopViewing(ServerPlayer player) {
    if (!level.isClientSide) {
      currentlyViewing.clear();
      player.camera = player;
      CRPackets.PACKETS.sendTo(player, new SetCameraViewPacket(player));
      RECENTLY_DISMOUNTED_PLAYERS.add(player);
    }
  }

  @SuppressWarnings("DuplicatedCode")
  public void onSpyInteract(BlockPos pos) {
    BlockState state;
    if (this.canReach(pos) && canSpyInteract((state = this.level.getBlockState(pos))) && fakePlayer != null) {
      ClipContext context = new ClipContext(this.getEyePosition(), new Vec3(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5),
              ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fakePlayer);
      BlockHitResult hitResult = level.clip(context);
      //Railways.LOGGER.info("pos: "+pos+", Hpos: "+hitResult.getBlockPos());
      if (!pos.equals(hitResult.getBlockPos()))
        return;
      boolean canUse = state.getShape(level, pos).isEmpty() || EntityUtils.handleUseEvent(fakePlayer, InteractionHand.MAIN_HAND, hitResult);
      if (canUse) {
        if (state.getBlock() instanceof VentBlock ventBlock) {
          ventBlock.teleportConductor(level, pos, this, hitResult.getDirection().getOpposite());
        } else {
          state.use(level, fakePlayer, InteractionHand.MAIN_HAND, hitResult);
        }
      }
    }
  }

  // specific movement-related stuff

  public float oBob;
  public float bob;

  @Override
  @Environment(EnvType.CLIENT)
  public float getViewXRot(float partialTicks) {
    if (ClientHandler.isPossessed(this))
      return this.getXRot();
    return super.getViewXRot(partialTicks);
  }

  @Environment(EnvType.CLIENT)
  public PlayerModel<?> visualBaseModel;
  @Environment(EnvType.CLIENT)
  public Player visualBaseEntity;

  @Override
  public boolean isCrouching() {
    return level.isClientSide ? (visualBaseEntity != null ? visualBaseEntity.isCrouching() : super.isCrouching()) : super.isCrouching();
  }

  // make public
  @Override
  @Environment(EnvType.CLIENT)
  public void setSharedFlag(int flag, boolean set) {
    super.setSharedFlag(flag, set);
  }

  public boolean isPossessed() {
    return level.isClientSide ? ClientHandler.isPossessed(this) : currentlyViewing.get() != null;
  }

  public boolean isPossessedAndClient() {
    return level.isClientSide && isPossessed();
  }

  // only used by MouseHandler
  public void turnView(double yRot, double xRot) {
    float f = (float)xRot * 0.15f;
    float g = (float)yRot * 0.15f;
    rotateAnyway = true;
    this.setXRot(this.getXRot() + f);
    this.setYRot(this.getYRot() + g);
    rotateAnyway = true;
    this.setXRot(Mth.clamp(this.getXRot(), -90.0f, 90.0f));
    this.xRotO += f;
    this.yRotO += g;
    this.xRotO = Mth.clamp(this.xRotO, -90.0f, 90.0f);
    if (this.getVehicle() != null) {
      this.getVehicle().onPassengerTurned(this);
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public float getViewYRot(float partialTick) {
    return this.isPassenger() || !isPossessed() ? super.getViewYRot(partialTick) : this.getYRot();
  }

  private boolean rotateAnyway = false;
  private boolean consumeRotateAnyway() {
    if (rotateAnyway) {
      rotateAnyway = false;
      return true;
    }
    return false;
  }

  @Override
  public void setXRot(float xRot) {
    if (isPossessedAndClient() && !consumeRotateAnyway())
      return;
    super.setXRot(xRot);
  }

  @Override
  protected boolean isHorizontalCollisionMinor(@NotNull Vec3 deltaMovement) {
    if (!isPossessedAndClient())
      return super.isHorizontalCollisionMinor(deltaMovement);
    float f = this.getYRot() * ((float)Math.PI / 180);
    double d0 = Mth.sin(f);
    double d1 = Mth.cos(f);
    double d2 = (double)this.xxa * d1 - (double)this.zza * d0;
    double d3 = (double)this.zza * d1 + (double)this.xxa * d0;
    double d4 = Mth.square(d2) + Mth.square(d3);
    double d5 = Mth.square(deltaMovement.x) + Mth.square(deltaMovement.z);
    if (!(d4 < (double)1.0E-5f) && !(d5 < (double)1.0E-5f)) {
      double d6 = d2 * deltaMovement.x + d3 * deltaMovement.z;
      double d7 = Math.acos(d6 / Math.sqrt(d4 * d5));
      return d7 < 0.13962633907794952;
    }
    return false;
  }

  public void updatePossessionInputs() {
    if (isPossessedAndClient()) {
      _updatePossessionInputs();
    }
  }

  private static float calculateImpulse(boolean input, boolean otherInput) {
    if (input == otherInput) {
      return 0.0f;
    }
    return input ? 1.0f : -1.0f;
  }

  @Environment(EnvType.CLIENT)
  private void _updatePossessionInputs() {
    this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    this.zza = calculateImpulse(wasUpPressed(), wasDownPressed());
    this.xxa = calculateImpulse(wasLeftPressed(), wasRightPressed());
    if (!wasSprintPressed()) {
      zza *= 0.3F;
      xxa *= 0.3F;
    }
    this.jumping = wasJumpPressed();
    this.flyingSpeed = 0.2f;
    if (wasSprintPressed()) {
      this.flyingSpeed += 0.006f;
    }
  }

  @Override
  public void push(@NotNull Entity entity) {
    if (ConductorPossessionController.getPossessingConductor(entity) == this)
      return;
    super.push(entity);
  }

  @Override
  public boolean isPushable() {
    return super.isPushable() && !isPossessed();
  }

  @Override
  public boolean isControlledByLocalInstance() {
    return super.isControlledByLocalInstance() || isPossessedAndClient();
  }

  private boolean suffocatesAt(BlockPos pos) {
    AABB aabb = this.getBoundingBox();
    AABB aabb1 = new AABB(pos.getX(), aabb.minY, pos.getZ(), (double)pos.getX() + 1.0, aabb.maxY, (double)pos.getZ() + 1.0).deflate(1.0E-7);
    return this.level.collidesWithSuffocatingBlock(this, aabb1);
  }

  private void moveTowardsClosestSpace(double x, double z) {
    BlockPos blockpos = new BlockPos(x, this.getY(), z);
    if (this.suffocatesAt(blockpos)) {
      Direction[] adirection;
      double d0 = x - (double)blockpos.getX();
      double d1 = z - (double)blockpos.getZ();
      Direction direction = null;
      double d2 = Double.MAX_VALUE;
      for (Direction direction1 : adirection = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
        double d4;
        double d3 = direction1.getAxis().choose(d0, 0.0, d1);
        double d = d4 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d3 : d3;
        if (!(d4 < d2) || this.suffocatesAt(blockpos.relative(direction1))) continue;
        d2 = d4;
        direction = direction1;
      }
      if (direction != null) {
        Vec3 vec3 = this.getDeltaMovement();
        if (direction.getAxis() == Direction.Axis.X) {
          this.setDeltaMovement(0.1 * (double)direction.getStepX(), vec3.y, vec3.z);
        } else {
          this.setDeltaMovement(vec3.x, vec3.y, 0.1 * (double)direction.getStepZ());
        }
      }
    }
  }

  @Override
  public void aiStep() {
    if (isPossessedAndClient()) {
      this.oBob = this.bob;
      this.yHeadRot = this.getYRot();
      if (!this.noPhysics) {
        this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
        this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
        this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
        this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
      }
    }
    super.aiStep();
    if (isPossessedAndClient()) {
      float f = !this.onGround || this.isDeadOrDying() || this.isSwimming() ? 0.0f : Math.min(0.1f, (float)this.getDeltaMovement().horizontalDistance());
      this.bob += (f - this.bob) * 0.4f;
    }
  }

  /* End possession variables */

  protected FrequencyListener forwardListener;
  protected FrequencyListener backwardListener;
  protected FrequencyListener leftListener;
  protected FrequencyListener rightListener;
  protected FrequencyListener jumpListener;
  protected FrequencyListener sneakListener;

  protected void updateFrequencyListeners() {
    forwardListener = new FrequencyListener("forward");
    backwardListener = new FrequencyListener("backward");
    leftListener = new FrequencyListener("left");
    rightListener = new FrequencyListener("right");
    jumpListener = new FrequencyListener("jump");
    sneakListener = new FrequencyListener("sneak");
  }

  public int getForwardSignalStrength() {
    if (forwardListener == null) return 0;
    return forwardListener.receivedStrength;
  }

  private List<ItemStack> getHeldSchedules() {
    if (heldSchedules == null) {
      heldSchedules = new ArrayList<>();
    }
    return heldSchedules;
  }

  public ConductorEntity(EntityType<? extends AbstractGolem> type, Level level) {
    super(type, level);
    this.maxUpStep = 0.5f;
  }

  public boolean isHoldingSchedules() {
    return !getHeldSchedules().isEmpty();
  }

  public boolean isHoldingSchedulesClient() {
    return this.entityData.get(HOLDING_SCHEDULES);
  }

  public void addSchedule(ItemStack stack) {
    if (stack.isEmpty()) return;
    getHeldSchedules().add(stack.copy());
    this.entityData.set(HOLDING_SCHEDULES, true);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(COLOR, idFrom(defaultColor()));
    this.entityData.define(BLOCK, this.blockPosition());
    this.entityData.define(JOB, Job.DEFAULT.ordinal());
    this.entityData.define(HOLDING_SCHEDULES, this.isHoldingSchedules());
    for (Map.Entry<String, Couple<EntityDataAccessor<ItemStack>>> entry : FREQUENCY_DATA.entrySet()) {
      for (boolean first : Iterate.trueAndFalse) {
        this.entityData.define(entry.getValue().get(first), ItemStack.EMPTY);
      }
    }
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    //NOTE: priority 0 is the highest priority, priority infinity lowest
    goalSelector.addGoal(2, new ConductorLookedAtGoal(this));
    goalSelector.addGoal(1, new ConductorPonderBlockGoal(this));
    goalSelector.addGoal(1, new FollowToolboxPlayerGoal(this, 1.25d));
    goalSelector.addGoal(1, new RemoteControlGoal(this, 1.25d));
    goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8f) {
        @Override
        public boolean canUse () {
            return super.canUse() && !isPossessed();
        }

      @Override
      public boolean canContinueToUse() {
        return super.canContinueToUse() && !isPossessed();
      }
    });
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
      .add(Attributes.MAX_HEALTH, 20.0D)
      .add(Attributes.MOVEMENT_SPEED, 0.25D)
      .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
      .add(Attributes.ARMOR, 8.0D)
      .add(Attributes.ARMOR_TOUGHNESS, 8.0D);
  }

  @Override
  public void remove(@NotNull RemovalReason reason) {
    super.remove(reason);
    WITH_TOOLBOXES.get(level).remove(this);
  }

  @Override
  public void onClientRemoval() {
    WITH_TOOLBOXES.get(level).remove(this);
  }

  @Override
  public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
    if (getRootVehicle() instanceof CarriageContraptionEntity) // no damage when riding
      return false;
    if (pSource.getEntity() instanceof LivingEntity living && living.getMainHandItem().is(AllItems.WRENCH.get()))
      pAmount = 10;
    return super.hurt(pSource, pAmount);
  }

  @Override
  protected int decreaseAirSupply(int pAir) {
    return pAir;
  }

  @Override
  protected float getStandingEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pDimensions) {
    return pDimensions.height * 0.76f;
  }

  public boolean canReach(Vec3i pos) {
    return pos.distToCenterSqr(position()) <= REACH.distSqr(Vec3i.ZERO);
  }

  protected boolean isToolbox(ItemStack stack) {
    return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ToolboxBlock;
  }

  public boolean isCarryingToolbox() {
    return toolbox != null;
  }

  public ItemStack getToolboxDisplayStack() {
    if (isCarryingToolbox()) {
      return toolbox.getDisplayStack();
    }
    return ItemStack.EMPTY;
  }

  protected void setToolbox(@Nullable MountedToolbox toolbox) {
    this.toolbox = toolbox;
    if (toolbox != null) {
      WITH_TOOLBOXES.get(level).add(this);
    } else {
      WITH_TOOLBOXES.get(level).remove(this);
    }
  }

  @Nullable
  public MountedToolbox getToolbox() {
    return toolbox;
  }

  @NotNull
  public MountedToolbox getOrCreateToolboxHolder() {
    if (!isCarryingToolbox()) {
      setToolbox(new MountedToolbox(this, DyeColor.BROWN));
    }
    return toolbox;
  }

  public void equipToolbox(ItemStack stack) {
    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ToolboxBlock toolbox) {
      setToolbox(new MountedToolbox(this, toolbox.getColor()));
      this.toolbox.readFromItem(stack);
      this.toolbox.sendData();
      setJob(Job.TOOLBOX_CARRIER);
    }
  }

  public void setJob(Job job) {
    getEntityData().set(JOB, job.ordinal());
  }

  public Job getJob() {
    return Job.values()[getEntityData().get(JOB)];
  }

  @Override
  public void startSeenByPlayer(@NotNull ServerPlayer pServerPlayer) {
    super.startSeenByPlayer(pServerPlayer);
    if (toolbox != null)
      toolbox.sendData();
  }

  public ItemStack unequipToolbox() {
    setJob(Job.DEFAULT);
    if (level.isClientSide || toolbox == null) {
      if (toolbox != null)
        toolbox.setRemoved();
      setToolbox(null);
      return ItemStack.EMPTY;
    }
    toolbox.unequipTracked();
    ItemStack itemStack = toolbox.getCloneItemStack();
    toolbox.setRemoved();

    setToolbox(null);
    return itemStack;
  }

  protected void openToolbox(Player player) {
    if (player instanceof ServerPlayer serverPlayer)
      MountedToolbox.openMenu(serverPlayer, toolbox);
  }

  @Override
  protected @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
    if (player.getItemInHand(hand).getItem() instanceof DyeItem di) {
      setColor (di.getDyeColor());
      if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (player.getItemInHand(hand).getItem().equals(AllBlocks.ANDESITE_CASING.asStack().getItem())) {
      if(this.getHealth()<this.getMaxHealth()){
        this.setHealth(this.getMaxHealth());
        if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
        return InteractionResult.SUCCESS;}
    } else if (!this.isCarryingToolbox() && isToolbox(player.getItemInHand(hand)) && getJob() == Job.DEFAULT) {
      this.equipToolbox(player.getItemInHand(hand));
      player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (this.isCarryingToolbox()) {
      if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
        player.setItemInHand(hand, this.unequipToolbox());
      } else if (!level.isClientSide) {
        openToolbox(player);
      }
      return InteractionResult.SUCCESS;
    } else if (player.getItemInHand(hand).isEmpty() && !getHeldSchedules().isEmpty()) { //retrieve held schedules
      for (ItemStack item : heldSchedules) {
        if (!player.addItem(item)) {
          player.drop(item, false);
        }
      }
      this.entityData.set(HOLDING_SCHEDULES, false);
      getHeldSchedules().clear();
    } else if (getJob() == Job.DEFAULT && AllBlocks.REDSTONE_LINK.isIn(player.getItemInHand(hand))) {
      setJob(Job.REMOTE_CONTROL);
      player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (getJob() == Job.REMOTE_CONTROL) {
      if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
        player.setItemInHand(hand, AllBlocks.REDSTONE_LINK.asStack());
        setJob(Job.DEFAULT);
      } else if (player.getItemInHand(hand).getItem() instanceof LinkedControllerItem) {
        // copy frequencies from linked controller to conductor
        int i = 0;
        for (var freq : frequencies.settersInOrder()) {
          freq.accept(Optional.of(LinkedControllerItem.toFrequency(player.getItemInHand(hand), i)));
          i++;
        }
        updateFrequencyListeners();
      }
    } else if (getJob() == Job.DEFAULT && AllItems.GOGGLES.isIn(player.getItemInHand(hand))) {
      setJob(Job.SPY);
      player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (getJob() == Job.SPY) {
      if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
        player.setItemInHand(hand, AllItems.GOGGLES.asStack());
        setJob(Job.DEFAULT);
      }
    }
    /*else if (player.getItemInHand(hand).getItem() == Items.PHANTOM_MEMBRANE) {
      if (level instanceof ServerLevel serverLevel) {
        List<ServerPlayer> serverPlayers = serverLevel.getServer().getPlayerList().getPlayers(); // tmp for testing
        if (serverPlayers.size() > 0)
          startViewing(serverPlayers.get(0));
      }
    }*/
    return super.mobInteract(player, hand);
  }

  @Nullable
  @Override
  protected SoundEvent getHurtSound(DamageSource damageSource) {
    return SoundEvents.NETHERITE_BLOCK_BREAK;
  }

  @Nullable
  @Override
  protected SoundEvent getDeathSound() {
    return AllSoundEvents.CRUSHING_1.getMainEvent();
  }

  @Override
  public void tick() {
    this.resetPosition();
    SectionPos sectionPos = SectionPos.of(this);
    if (!sectionPos.equals(oldSectionPos)) {
        setHasSentChunks(false);
    }
    if (level.isClientSide) {
      ConductorPossessionController.tryUpdatePossession(this);
      updatePossessionInputs();
    }
    super.tick();
    if (ventCooldown > 0)
      ventCooldown--;
    if (level instanceof ServerLevel serverLevel) {
      if (fakePlayer == null) {
        fakePlayer = EntityUtils.createConductorFakePlayer(serverLevel, this);
      }
      if ((Object) currentlyViewing.get() instanceof ServerPlayer player) {
        SectionPos chunkPos = SectionPos.of(blockPosition());
        int viewDistance = player.server.getPlayerList().getViewDistance();
        for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
          for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
            serverLevel.getChunkSource().addRegionTicket(TicketType.FORCED, new ChunkPos(x, z), 3, new ChunkPos(x, z));
          }
        }
      }
    }

    if (toolbox != null) toolbox.tick();
  }

  public static ConductorEntity spawn(Level level, BlockPos pos, ItemStack stack) {
    if (!(stack.getItem() instanceof ConductorCapItem cap)) return null;
    ConductorEntity result = new ConductorEntity(CREntities.CONDUCTOR.get(), level);
    result.setPos(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
    result.setColor(cap.color);
    result.equipItemIfPossible(stack);
    level.addFreshEntity(result);
    return result;
  }

  public boolean isInMinecart() {
    return this.getVehicle() instanceof AbstractMinecart;
  }

  @SuppressWarnings("SameReturnValue")
  public static DyeColor defaultColor() {
    return DyeColor.BLUE;
  }

  public void setColor(DyeColor color) {
    getEntityData().set(COLOR, idFrom(color));
  }

  public DyeColor getColor() {
    return colorFrom(this.entityData.get(COLOR));
  }

  public boolean isCorrectEngineerCap(ItemStack hat) {
    if (hat.isEmpty()) return true;
    return (hat.getItem() instanceof ConductorCapItem cap) && (cap.color == getColor());
  }

  boolean isLookingAtMe(Player player) {
    if (player.isSpectator())
      return false;
    boolean looking = false;
    ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
    if (isCorrectEngineerCap(helmet) || !ItemUtils.blocksEndermanView(helmet, player, null)) {
      Vec3 playerView = player.getViewVector(1f).normalize();
      Vec3 headLine   = this.getEyePosition().subtract(player.getEyePosition()).normalize();
      double angle    = playerView.dot(headLine); // a . b / |a||b| = cos theta
      looking = (angle > 1d - 0.017d) && player.hasLineOfSight(this); // apply small angle approximation
    }
    return looking;
  }

  public static DyeColor colorFrom(byte b) {
    if (b >= 16) return null;
    return DyeColor.byId(b);
  }

  public static byte idFrom(DyeColor color) {
    int c = color.getId();
    if (c >= 16) return 16;
    return (byte)c;
  }

  public boolean canUseBlock (BlockState state) {
    return state.is(BlockTags.BUTTONS) || state.is(Blocks.LEVER) || state.getBlock() instanceof TrackSwitchBlock;
  }

  @Override
  protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
    super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
    Job job = getJob();
    ItemStack holdingStack = this.unequipToolbox();
    if (!holdingStack.isEmpty()) {
      this.spawnAtLocation(holdingStack);
    }
    if (isHoldingSchedules()) {
      for (ItemStack scheduleStack : getHeldSchedules())
        this.spawnAtLocation(scheduleStack);
    }
    if (job == Job.REMOTE_CONTROL)
      this.spawnAtLocation(AllBlocks.REDSTONE_LINK.asStack());
    else if (job == Job.SPY)
      this.spawnAtLocation(AllItems.GOGGLES.asStack());
  }

  static class JobBasedGoal extends Goal {

    private final Job job;
    protected final ConductorEntity conductor;

    public JobBasedGoal(ConductorEntity conductor, Job job) {
      this.conductor = conductor;
      this.job = job;
    }

    @Override
    public boolean canUse() {
      return conductor.getJob() == job && !conductor.isPossessed();
    }

    @Override
    public boolean canContinueToUse() {
      return this.canUse();
    }
  }

  static class FollowToolboxPlayerGoal extends JobBasedGoal {

    protected final double speedModifier;
    @Nullable
    protected Player target;
    protected int timeToRecalcPath;
    public FollowToolboxPlayerGoal(ConductorEntity conductor, double speedModifier) {
      super(conductor, Job.TOOLBOX_CARRIER);
      this.speedModifier = speedModifier;
    }

    @Override
    public void tick() {
      super.tick();
      if (--this.timeToRecalcPath <= 0) {
        this.timeToRecalcPath = this.adjustedTickDelay(10);
        if (this.conductor.distanceToSqr(target) > 4*4) {
          this.conductor.getNavigation().moveTo(target, this.speedModifier);
        } else {
          this.conductor.getNavigation().stop();
        }
      }
    }

    @Override
    public boolean canUse() {
      return super.canUse() && conductor.isCarryingToolbox() && !conductor.getToolbox().getConnectedPlayers().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
      return super.canContinueToUse() && conductor.isCarryingToolbox() && conductor.getToolbox().getConnectedPlayers().contains(target) && target.isAlive() && !target.isSpectator();
    }

    @Override
    public void start() {
      super.start();
      List<Player> players = conductor.getToolbox().getConnectedPlayers();
      target = players.get(conductor.random.nextInt(players.size()));
    }

    @Override
    public void stop() {
      super.stop();
      target = null;
    }
  }

  static class RemoteControlGoal extends JobBasedGoal {

    protected final double speedModifier;
    protected Vec3 targetDirection = Vec3.ZERO;
    protected double targetStrength = 15.0;
    public RemoteControlGoal(ConductorEntity conductor, double speedModifier) {
      super(conductor, Job.REMOTE_CONTROL);
      this.speedModifier = speedModifier;
    }

    protected double getGroundY(Vec3 vec) {
      BlockPos blockPos = new BlockPos(vec);
      return this.conductor.level.getBlockState(blockPos.below()).isAir() ?
              vec.y :
              WalkNodeEvaluator.getFloorLevel(this.conductor.level, blockPos);
    }

    private int honkPacketCooldown = 0;
    private boolean usedToHonk;

    @Override
    public void tick() {
      super.tick();
      Pair<Vec3, Double> pair = calculateTarget();
      targetDirection = pair.getFirst();
      targetStrength = pair.getSecond();
      Vec3 target = this.conductor.position().add(targetDirection.scale(2));
      if (conductor.getRootVehicle() instanceof CarriageContraptionEntity cce && cce.getControllingPlayer().isEmpty()
              && cce.getContraption() instanceof CarriageContraption cc && conductor.fakePlayer != null) {
        BlockPos seat = cc.getSeatOf(conductor.uuid);
        if (seat == null)
          return;
        Couple<Boolean> controlsPresent = cc.conductorSeats.get(seat);
        if (controlsPresent == null)
          return;
        ScheduleRuntime runtime;
        if (cce.getCarriage() != null && (runtime = cce.getCarriage().train.runtime).getSchedule() != null && !(runtime.completed || runtime.paused))
          return;
        BlockPos controlsPos = null;
        BlockPos reverseControlsPos = null;
        if (controlsPresent.getFirst()) {
          controlsPos = seat.relative(cc.getAssemblyDirection().getOpposite());
        }
        if (controlsPresent.getSecond()) {
          if (controlsPos == null)
            controlsPos = seat.relative(cc.getAssemblyDirection());
          else
            reverseControlsPos = seat.relative(cc.getAssemblyDirection());
        }
        if (controlsPos == null){
          return;
        }
        Set<Integer> controls = getControls();
        if (reverseControlsPos != null && controls.contains(1) && !controls.contains(0)) { // go backwards quickly
          controls.remove(1);
          controls.add(0);
          /*boolean left = controls.remove(2);
          boolean right = controls.remove(3);
          if (left) controls.add(3);
          if (right) controls.add(2);*/
          controlsPos = reverseControlsPos;
        }
        boolean isSprintKeyPressed = controls.remove(5);
        cce.control(controlsPos, controls, conductor.fakePlayer);

        Train train = cce.getCarriage().train;
        if (isSprintKeyPressed && honkPacketCooldown-- <= 0) {
          train.determineHonk(conductor.level);
          if (train.lowHonk != null) {
            Utils.sendHonkPacket(train, true);
            honkPacketCooldown = 5;
            usedToHonk = true;
          }
        }

        if (!isSprintKeyPressed && usedToHonk) {
          Utils.sendHonkPacket(train, false);
          honkPacketCooldown = 0;
          usedToHonk = false;
        }
      } else if (targetDirection.lengthSqr() > 0.01) {
        this.conductor.getMoveControl().setWantedPosition(target.x, getGroundY(target), target.z,
                this.speedModifier * targetStrength / 15);
      }
      /*else
        this.conductor.getMoveControl().setWantedPosition(this.conductor.position().x, this.conductor.position().y, this.conductor.position().z, 0.0);*/
    }

    private Set<Integer> getControls() {
      Set<Integer> controls = new HashSet<>();
      if (conductor.forwardListener != null && conductor.forwardListener.isPowered())
        controls.add(0);
      if (conductor.backwardListener != null && conductor.backwardListener.isPowered())
        controls.add(1);
      if (conductor.leftListener != null && conductor.leftListener.isPowered())
        controls.add(2);
      if (conductor.rightListener != null && conductor.rightListener.isPowered())
        controls.add(3);
      if (conductor.jumpListener != null && conductor.jumpListener.isPowered())
        controls.add(4);
      if (conductor.sneakListener != null && conductor.sneakListener.isPowered())
        controls.add(5);
      return controls;
    }

    private Pair<Vec3, Double> calculateTarget() {
      double x = 0;
      double y = 0;
      double z = 0;

      if (conductor.forwardListener != null && conductor.forwardListener.isPowered())
        z -= conductor.forwardListener.receivedStrength; // north
      if (conductor.backwardListener != null && conductor.backwardListener.isPowered())
        z += conductor.backwardListener.receivedStrength; // south

      if (conductor.leftListener != null && conductor.leftListener.isPowered())
        x -= conductor.leftListener.receivedStrength; // west
      if (conductor.rightListener != null && conductor.rightListener.isPowered())
        x += conductor.rightListener.receivedStrength; // east

      if (conductor.jumpListener != null && conductor.jumpListener.isPowered())
        y += conductor.jumpListener.receivedStrength; // up
      if (conductor.sneakListener != null && conductor.sneakListener.isPowered())
        y -= conductor.sneakListener.receivedStrength; // down

      double avgStrength = 0;
      int count = 0;
      if (x != 0) {
        avgStrength += Math.abs(x);
        count++;
      }
      if (y != 0) {
        avgStrength += Math.abs(y);
        count++;
      }
      if (z != 0) {
        avgStrength += Math.abs(z);
        count++;
      }

      return Pair.of(new Vec3(x, Math.max(0, y), z).scale(1 / 15.0), count == 0 ? 0 : avgStrength / count);
    }

    @Override
    public void start() {
      super.start();
      Pair<Vec3, Double> pair = calculateTarget();
      targetDirection = pair.getFirst();
      targetStrength = pair.getSecond();
    }

    @Override
    public void stop() {
      super.stop();
      targetDirection = Vec3.ZERO;
      targetStrength = 15.0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
      return true;
    }
  }

  static class ConductorLookedAtGoal extends JobBasedGoal {

    @Nullable
    private LivingEntity target;

    public ConductorLookedAtGoal(ConductorEntity conductor) {
      super(conductor, Job.REDSTONE_OPERATOR);
    }

    @Override
    public boolean canUse() {
      if (!super.canUse())
        return false;
      for (Player player : this.conductor.level.players()) {
        if (player.hasLineOfSight(this.conductor)) {
          return ((conductor.distanceToSqr(player)) < 256) && conductor.isLookingAtMe(player);
        }
      }
      return false;
    }

    public void start() {
    //  Railways.LOGGER.info("Player looked at me!");
      Level level      = this.conductor.level;
      BlockPos pos     = this.conductor.getEntityData().get(BLOCK);
      BlockState state = level.getBlockState(pos);
      Block block      = state.getBlock();
      ServerPlayer fake = this.conductor.fakePlayer;

      // -- activate a button or lever --
      if (this.conductor.canReach(pos) && this.conductor.canUseBlock(state) && fake != null) {
      //  Railways.LOGGER.info("I'm activating a block for you!");

        ClipContext context = new ClipContext(this.conductor.getEyePosition(), new Vec3(pos.getX(), pos.getY(), pos.getZ()),
          ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fake);
        BlockHitResult hitResult = level.clip(context);
        //Railways.LOGGER.info("pos: "+pos+", Hpos: "+hitResult.getBlockPos());
        if (!pos.equals(hitResult.getBlockPos()))
          return;
        boolean canUse = state.getShape(level, pos).isEmpty() || EntityUtils.handleUseEvent(fake, InteractionHand.MAIN_HAND, hitResult);
        if (canUse) {
          state.use(level, fake, InteractionHand.MAIN_HAND, hitResult);
        }
      }
    }

    public void tick() {
      if (this.target != null) this.conductor.lookControl.setLookAt(this.target);
    }
  }

  static class ConductorPonderBlockGoal extends JobBasedGoal {
    private BlockPos target;

    public ConductorPonderBlockGoal(ConductorEntity conductor) {
      super(conductor, Job.REDSTONE_OPERATOR);
      this.target     = conductor.entityData.get(BLOCK);
    }

    @Override
    public boolean canUse() {
      if (!super.canUse())
        return false;
      this.target = conductor.entityData.get(BLOCK);
      if (this.conductor.canReach(target) && this.conductor.canUseBlock(this.conductor.level.getBlockState(this.target))) return true;
      // else search
      for (int y= -REACH.getY(); y< REACH.getY(); y++) {
        for (int x= -REACH.getX(); x< REACH.getX(); x++) {
          for (int z= -REACH.getZ(); z< REACH.getZ(); z++) {
            BlockPos at = this.conductor.blockPosition().offset(x, y, z);
            BlockState state = this.conductor.level.getBlockState(at);
            if (this.conductor.canUseBlock(state)) {
              ClipContext context = new ClipContext(this.conductor.getEyePosition(), new Vec3(at.getX(), at.getY(), at.getZ()),
                  ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
              BlockHitResult hitResult = this.conductor.level.clip(context);
              if (hitResult.getBlockPos().equals(at)) {
                this.target = at;
                conductor.entityData.set(BLOCK, this.target);
                return true;
              }
            }
          }
        }
      }
      return false;
    }

    public void tick() {
      this.conductor.lookControl.setLookAt(target.getX(), target.getY(), target.getZ());
    }
  }

  @Override
  public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    nbt.put("target", NbtUtils.writeBlockPos(getEntityData().get(BLOCK)));
    nbt.putByte("color", getEntityData().get(COLOR));
    if (toolbox != null) {
      CompoundTag toolboxTag = new CompoundTag();
      toolbox.write(toolboxTag, false);
      nbt.put("toolboxHolder", toolboxTag);
    }
    if (!getHeldSchedules().isEmpty()) {
      ListTag schedulesTag = new ListTag();
      boolean hasItem = false;
      for (ItemStack heldSchedule : heldSchedules) {
        if (!heldSchedule.isEmpty()) {
          schedulesTag.add(heldSchedule.save(new CompoundTag()));
          hasItem = true;
        }
      }
      if (hasItem)
        nbt.put("heldSchedules", schedulesTag);
    }
    nbt.putString("job", getJob().name());
    nbt.put("frequencies", frequencies.write());
  }

  @Override
  public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    if (nbt.contains("color", Tag.TAG_BYTE)) {
      getEntityData().set(COLOR, nbt.getByte("color"));
    }
    if (nbt.contains("target", Tag.TAG_COMPOUND)) {
      getEntityData().set(BLOCK, NbtUtils.readBlockPos(nbt.getCompound("target")));
    }
    if (nbt.contains("toolboxHolder", Tag.TAG_COMPOUND)) {
      setToolbox(MountedToolbox.read(this, nbt.getCompound("toolboxHolder")));
    } else {
      setToolbox(null);
    }
    if (nbt.contains("job", Tag.TAG_STRING)) {
      setJob(Job.valueOf(nbt.getString("job")));
    } else {
      setJob(Job.DEFAULT);
    }
    getHeldSchedules().clear();
    if (nbt.contains("heldSchedules", Tag.TAG_LIST)) {
      ListTag schedulesTag = nbt.getList("heldSchedules", Tag.TAG_COMPOUND);
      for (int i = 0; i < schedulesTag.size(); i++) {
        ItemStack stack = ItemStack.of(schedulesTag.getCompound(i));
        if (!stack.isEmpty())
          getHeldSchedules().add(stack);
      }
    }
    if (!level.isClientSide) {
      getEntityData().set(HOLDING_SCHEDULES, isHoldingSchedules());
      frequencies.read(nbt.getCompound("frequencies"));
      updateFrequencyListeners();
    }
  }

  public enum Job {
    REDSTONE_OPERATOR,
    TOOLBOX_CARRIER,
    REMOTE_CONTROL,
    SPY
    ;
    public static final Job DEFAULT = REDSTONE_OPERATOR;
  }
}
