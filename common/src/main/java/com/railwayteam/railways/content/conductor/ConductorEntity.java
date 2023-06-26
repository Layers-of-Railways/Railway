package com.railwayteam.railways.content.conductor;

import com.mojang.authlib.GameProfile;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.util.EntityUtils;
import com.railwayteam.railways.util.ItemUtils;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

// note: item handler capability is implemented on forge in CommonEventsForge, and fabric does not have entity APIs
public class ConductorEntity extends AbstractGolem {
  public static final GameProfile FAKE_PLAYER_PROFILE = new GameProfile(
          UUID.fromString("B0FADEE5-4411-3475-ADD0-C4EA7E30D050"),
          "[Conductor]"
  );

  public class FrequencyListener implements IRedstoneLinkable {
    public final String key;
    public final @Nullable Couple<Frequency> frequency;
    public int receivedStrength = 0;

    public FrequencyListener(String key) {
      this.key = key;
      this.frequency = ConductorEntity.this.getFrequencies().entries().get(this.key).orElse(null);
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

  public static class FrequencyHolder implements Iterable<Optional<Couple<@NotNull Frequency>>> {
    public @Nullable Couple<@NotNull Frequency> forward;
    public @Nullable Couple<@NotNull Frequency> backward;
    public @Nullable Couple<@NotNull Frequency> left;
    public @Nullable Couple<@NotNull Frequency> right;
    public @Nullable Couple<@NotNull Frequency> jump;
    public @Nullable Couple<@NotNull Frequency> sneak;

    public FrequencyHolder() {
    }

    public FrequencyHolder(@Nullable Couple<@NotNull Frequency> forward, @Nullable Couple<@NotNull Frequency> backward,
                           @Nullable Couple<@NotNull Frequency> left, @Nullable Couple<@NotNull Frequency> right,
                           @Nullable Couple<@NotNull Frequency> jump, @Nullable Couple<@NotNull Frequency> sneak) {
      this.forward = forward;
      this.backward = backward;
      this.left = left;
      this.right = right;
      this.jump = jump;
      this.sneak = sneak;
    }

    public FrequencyHolder copy() {
      return new FrequencyHolder(
              forward == null ? null : forward.copy(),
              backward == null ? null : backward.copy(),
              left == null ? null : left.copy(),
              right == null ? null : right.copy(),
              jump == null ? null : jump.copy(),
              sneak == null ? null : sneak.copy()
      );
    }

    @NotNull
    @Override
    public Iterator<Optional<Couple<@NotNull Frequency>>> iterator() {
      return List.of(
              Optional.ofNullable(forward),
              Optional.ofNullable(backward),
              Optional.ofNullable(left),
              Optional.ofNullable(right),
              Optional.ofNullable(jump),
              Optional.ofNullable(sneak)
      ).iterator();
    }

    public Map<String, Optional<Couple<@NotNull Frequency>>> entries() {
      return Map.of(
              "forward", Optional.ofNullable(forward),
              "backward", Optional.ofNullable(backward),
              "left", Optional.ofNullable(left),
              "right", Optional.ofNullable(right),
              "jump", Optional.ofNullable(jump),
              "sneak", Optional.ofNullable(sneak)
      );
    }

    public Map<String, Consumer<Optional<Couple<@NotNull Frequency>>>> setters() {
      return Map.of(
              "forward", (freq) -> forward = freq.orElse(null),
              "backward", (freq) -> backward = freq.orElse(null),
              "left", (freq) -> left = freq.orElse(null),
              "right", (freq) -> right = freq.orElse(null),
              "jump", (freq) -> jump = freq.orElse(null),
              "sneak", (freq) -> sneak = freq.orElse(null)
      );
    }

    public List<Consumer<Optional<Couple<@NotNull Frequency>>>> settersInOrder() {
      return List.of(
              (freq) -> forward = freq.orElse(null),
              (freq) -> backward = freq.orElse(null),
              (freq) -> left = freq.orElse(null),
              (freq) -> right = freq.orElse(null),
              (freq) -> jump = freq.orElse(null),
              (freq) -> sneak = freq.orElse(null)
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
  }

  public static final WorldAttached<Set<ConductorEntity>> WITH_TOOLBOXES = new WorldAttached<>(w -> new HashSet<>());

  // FIXME: cannot have custom serializers! This will explode!
  private static final EntityDataSerializer<Job> JOB_SERIALIZER = new EntityDataSerializer<>() {
    public void write(FriendlyByteBuf buf, @NotNull Job job) {
      buf.writeEnum(job);
    }

    public @NotNull Job read(FriendlyByteBuf buf) {
      return buf.readEnum(Job.class);
    }

    public @NotNull Job copy(@NotNull Job job) {
      return job;
    }
  };

  private static final EntityDataSerializer<FrequencyHolder> FREQUENCY_SERIALIZER = new EntityDataSerializer<>() {
    @Override
    public void write(@NotNull FriendlyByteBuf buffer, @NotNull FrequencyHolder value) {
      for (Optional<Couple<Frequency>> freq : value) {
        buffer.writeBoolean(freq.isPresent());
        freq.ifPresent((f) -> {
          buffer.writeItem(f.getFirst().getStack());
          buffer.writeItem(f.getSecond().getStack());
        });
      }
    }

    @Override
    public @NotNull FrequencyHolder read(@NotNull FriendlyByteBuf buffer) {
      return new FrequencyHolder(
              buffer.readBoolean() ? Couple.create(Frequency.of(buffer.readItem()), Frequency.of(buffer.readItem())) : null,
              buffer.readBoolean() ? Couple.create(Frequency.of(buffer.readItem()), Frequency.of(buffer.readItem())) : null,
              buffer.readBoolean() ? Couple.create(Frequency.of(buffer.readItem()), Frequency.of(buffer.readItem())) : null,
              buffer.readBoolean() ? Couple.create(Frequency.of(buffer.readItem()), Frequency.of(buffer.readItem())) : null,
              buffer.readBoolean() ? Couple.create(Frequency.of(buffer.readItem()), Frequency.of(buffer.readItem())) : null,
              buffer.readBoolean() ? Couple.create(Frequency.of(buffer.readItem()), Frequency.of(buffer.readItem())) : null
      );
    }

    @Override
    public @NotNull FrequencyHolder copy(@NotNull FrequencyHolder value) {
      return value.copy();
    }
  };

  static {
    EntityDataSerializers.registerSerializer(JOB_SERIALIZER);
    EntityDataSerializers.registerSerializer(FREQUENCY_SERIALIZER);
  }

  public static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BYTE);
  public static final EntityDataAccessor<BlockPos> BLOCK = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BLOCK_POS);
  public static final EntityDataAccessor<Job> JOB = SynchedEntityData.defineId(ConductorEntity.class, JOB_SERIALIZER);
  public static final EntityDataAccessor<Boolean> HOLDING_SCHEDULES = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
  public static final EntityDataAccessor<FrequencyHolder> FREQUENCIES = SynchedEntityData.defineId(ConductorEntity.class, FREQUENCY_SERIALIZER);

  // keep this small for performance (plus conductors are smol)
  private static final Vec3i REACH = new Vec3i(3, 2, 3);

  private ServerPlayer fakePlayer = null;
  MountedToolbox toolbox = null;
  private List<ItemStack> heldSchedules;

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
  protected void defineSynchedData () {
    super.defineSynchedData();
    this.entityData.define(COLOR, idFrom(defaultColor()));
    this.entityData.define(BLOCK, this.blockPosition());
    this.entityData.define(JOB, Job.DEFAULT);
    this.entityData.define(HOLDING_SCHEDULES, this.isHoldingSchedules());
    this.entityData.define(FREQUENCIES, new FrequencyHolder());
  }

  public FrequencyHolder getFrequencies() {
    return this.entityData.get(FREQUENCIES);
  }

  public void setFrequencies(FrequencyHolder holder) {
      this.entityData.set(FREQUENCIES, holder);
  }

  public void updateFrequencies(Consumer<FrequencyHolder> consumer) {
    FrequencyHolder holder = getFrequencies();
    consumer.accept(holder);
    setFrequencies(holder);
  }

  @Override
  protected void registerGoals () {
    super.registerGoals();
    //NOTE: priority 0 is the highest priority, priority infinity lowest
    goalSelector.addGoal(2, new ConductorLookedAtGoal(this));
    goalSelector.addGoal(1, new ConductorPonderBlockGoal(this));
    goalSelector.addGoal(1, new FollowToolboxPlayerGoal(this, 1.25d));
    goalSelector.addGoal(1, new RemoteControlGoal(this, 1.25d));
    goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8f));
  }

  public static AttributeSupplier.Builder createAttributes () {
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
    getEntityData().set(JOB, job);
  }

  public Job getJob() {
    return getEntityData().get(JOB);
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
  protected @NotNull InteractionResult mobInteract (Player player, @NotNull InteractionHand hand) {
    if (player.getItemInHand(hand).getItem() instanceof DyeItem di) {
      setColor (di.getDyeColor());
      if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (player.getItemInHand(hand).getItem().equals(AllBlocks.ANDESITE_CASING.asStack().getItem())) {
      if(this.getHealth()!=this.getMaxHealth()){
        this.setHealth(this.getHealth()+1);
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
        for (var freq : getFrequencies().settersInOrder()) {
          freq.accept(Optional.of(LinkedControllerItem.toFrequency(player.getItemInHand(hand), i)));
          i++;
        }
        updateFrequencyListeners();
      }
    }
    return super.mobInteract(player, hand);
  }

  @Override
  public void tick() {
    super.tick();
    if (fakePlayer == null && level instanceof ServerLevel serverLevel)
      fakePlayer = EntityUtils.createConductorFakePlayer(serverLevel);
    if (toolbox != null) toolbox.tick();
  }

  public static ConductorEntity spawn (Level level, BlockPos pos, ItemStack stack) {
    if (!(stack.getItem() instanceof ConductorCapItem cap)) return null;
    ConductorEntity result = new ConductorEntity(CREntities.CONDUCTOR.get(), level);
    result.setPos(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
    result.setColor(cap.color);
    result.equipItemIfPossible(stack);
    level.addFreshEntity(result);
    return result;
  }

  public boolean isInMinecart () {
    return this.getVehicle() instanceof AbstractMinecart;
  }

  @SuppressWarnings("SameReturnValue")
  public static DyeColor defaultColor () { return DyeColor.BLUE; }

  public void setColor (DyeColor color) { getEntityData().set(COLOR, idFrom(color)); }

  public DyeColor getColor() {
    return colorFrom(this.entityData.get(COLOR));
  }

  public boolean isCorrectEngineerCap (ItemStack hat) {
    if (hat.isEmpty()) return true;
    return (hat.getItem() instanceof ConductorCapItem cap) && (cap.color == getColor());
  }

  boolean isLookingAtMe (Player player) {
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

  public static DyeColor colorFrom (byte b) {
    if (b >= 16) return null;
    return DyeColor.byId(b);
  }

  public static byte idFrom (DyeColor color) {
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
    ItemStack holdingStack = this.unequipToolbox();
    if (!holdingStack.isEmpty()) {
      this.spawnAtLocation(holdingStack);
    }
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
      return conductor.getJob() == job;
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
        BlockPos controlsPos = null; // fixme this seems to go backwards sometimes
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
    public boolean canUse () {
      if (!super.canUse())
        return false;
      for (Player player : this.conductor.level.players()) {
        if (player.hasLineOfSight(this.conductor)) {
          return ((conductor.distanceToSqr(player)) < 256) && conductor.isLookingAtMe(player);
        }
      }
      return false;
    }

    public void start () {
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

    public void tick () {
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
    public boolean canUse () {
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

    public void start () {
      //Railways.LOGGER.info("thinking about a block...");
    }

    public void tick () {
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
    if (getHeldSchedules().size() != 0) {
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
    nbt.put("frequencies", getFrequencies().write());
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
      updateFrequencies((freqHolder) -> freqHolder.read(nbt.getCompound("frequencies")));
      updateFrequencyListeners();
    }
  }

  public enum Job {
    REDSTONE_OPERATOR,
    TOOLBOX_CARRIER,
    REMOTE_CONTROL
    ;
    public static final Job DEFAULT = REDSTONE_OPERATOR;
  }
}
