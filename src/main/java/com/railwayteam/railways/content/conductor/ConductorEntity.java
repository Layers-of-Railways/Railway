package com.railwayteam.railways.content.conductor;

import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxHolder;
import com.railwayteam.railways.mixin_interfaces.IMountedToolboxHandler;
import com.railwayteam.railways.registry.CREntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.curiosities.toolbox.ToolboxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ConductorEntity extends AbstractGolem {


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

  static {
    EntityDataSerializers.registerSerializer(JOB_SERIALIZER);
  }

  public static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BYTE);
  public static final EntityDataAccessor<BlockPos> BLOCK = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BLOCK_POS);
  public static final EntityDataAccessor<Job> JOB = SynchedEntityData.defineId(ConductorEntity.class, JOB_SERIALIZER);

  // keep this small for performance (plus conductors are smol)
  private static final Vec3i REACH = new Vec3i(3, 2, 3);

  private ConductorFakePlayer fakePlayer = null;
  MountedToolboxHolder toolboxHolder = null;

  public ConductorEntity (EntityType<? extends AbstractGolem> type, Level level) {
    super(type, level);
  }

  @Override
  public float getStepHeight() {
    return 0.5f;
  }

  @Override
  protected void defineSynchedData () {
    super.defineSynchedData();
    this.entityData.define(COLOR, idFrom(defaultColor()));
    this.entityData.define(BLOCK, this.blockPosition());
    this.entityData.define(JOB, Job.DEFAULT);
  }

  @Override
  protected void registerGoals () {
    super.registerGoals();
    //NOTE: priority 0 is the highest priority, priority infinity lowest
    goalSelector.addGoal(2, new ConductorLookedAtGoal(this));
    goalSelector.addGoal(1, new ConductorPonderBlockGoal(this));
    goalSelector.addGoal(1, new FollowToolboxPlayerGoal(this, 1.25d));
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
    return toolboxHolder != null;
  }

  public ItemStack getToolboxDisplayStack() {
    if (isCarryingToolbox()) {
      return toolboxHolder.getDisplayStack();
    }
    return ItemStack.EMPTY;
  }

  @Nullable
  public MountedToolboxHolder getToolboxHolder() {
    return toolboxHolder;
  }

  @NotNull
  public MountedToolboxHolder getOrCreateToolboxHolder() {
    if (toolboxHolder == null) {
      toolboxHolder = new MountedToolboxHolder(this, DyeColor.BROWN);
    }
    return toolboxHolder;
  }

  public void equipToolbox(ItemStack stack) {
    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ToolboxBlock) {
      toolboxHolder = new MountedToolboxHolder(this, ((ToolboxBlock) blockItem.getBlock()).getColor());
      toolboxHolder.readFromItem(stack);
      toolboxHolder.sendData();
      getEntityData().set(JOB, Job.TOOLBOX_CARRIER);
    }
  }

  @Override
  public void startSeenByPlayer(@NotNull ServerPlayer pServerPlayer) {
    super.startSeenByPlayer(pServerPlayer);
    if (toolboxHolder != null)
      toolboxHolder.sendData();
  }

  public ItemStack unequipToolbox() {
    getEntityData().set(JOB, Job.DEFAULT);
    if (level.isClientSide || toolboxHolder == null) {
      if (toolboxHolder != null)
        toolboxHolder.setRemoved();
      toolboxHolder = null;
      return ItemStack.EMPTY;
    }
    toolboxHolder.unequipTracked();
    ItemStack itemStack = toolboxHolder.getCloneItemStack();
    toolboxHolder.setRemoved();

    toolboxHolder = null;
    return itemStack;
  }

  protected void openToolbox(Player player) {
    NetworkHooks.openScreen((ServerPlayer) player, this.toolboxHolder, this.toolboxHolder::sendToContainer);
  }

  @Override
  protected @NotNull InteractionResult mobInteract (Player player, @NotNull InteractionHand hand) {
    if (player.getItemInHand(hand).getItem() instanceof DyeItem di) {
      setColor (di.getDyeColor());
      if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (!this.isCarryingToolbox() && isToolbox(player.getItemInHand(hand))) {
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
    }
    return super.mobInteract(player, hand);
  }

  @Override
  public void tick() {
    super.tick();
    if (fakePlayer == null && !level.isClientSide) fakePlayer = new ConductorFakePlayer((ServerLevel)level);
    if (toolboxHolder != null) toolboxHolder.tick();
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

  public boolean isCorrectEngineerCap (ItemStack hat) {
    if (hat.isEmpty()) return true;
    return (hat.getItem() instanceof ConductorCapItem cap) && (cap.color == colorFrom(this.entityData.get(COLOR)));
  }

  boolean isLookingAtMe (Player player) {
    if (player.isSpectator())
      return false;
    boolean looking = false;
    ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
    if (isCorrectEngineerCap(helmet) || !helmet.isEnderMask(player, null)) {
      Vec3 playerView = player.getViewVector(1f).normalize();
      Vec3 headLine   = this.getEyePosition().subtract(player.getEyePosition()).normalize();
      double angle    = playerView.dot(headLine); // a . b / |a||b| = cos theta
      looking = (angle > 1d - 0.017d) && player.hasLineOfSight(this); // apply small angle approximation
    }
    return looking;
  }

  static DyeColor colorFrom (byte b) {
    if (b >= 16) return null;
    return DyeColor.byId(b);
  }

  static byte idFrom (DyeColor color) {
    int c = color.getId();
    if (c >= 16) return 16;
    return (byte)c;
  }

  public boolean canUseBlock (BlockState state) {
    return state.is(BlockTags.BUTTONS) || state.is(Blocks.LEVER);
  }

  @Override
  public void die(@NotNull DamageSource pDamageSource) {
    super.die(pDamageSource);
    if (this.level.isClientSide) {
      IMountedToolboxHandler.onUnload(this);
    }
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
      return conductor.getEntityData().get(JOB) == job;
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
      return super.canUse() && conductor.isCarryingToolbox() && !conductor.getToolboxHolder().getConnectedPlayers().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
      return super.canContinueToUse() && conductor.isCarryingToolbox() && conductor.getToolboxHolder().getConnectedPlayers().contains(target) && target.isAlive() && !target.isSpectator();
    }

    @Override
    public void start() {
      super.start();
      List<Player> players = conductor.getToolboxHolder().getConnectedPlayers();
      target = players.get(conductor.random.nextInt(players.size()));
    }

    @Override
    public void stop() {
      super.stop();
      target = null;
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
      ConductorFakePlayer fake = this.conductor.fakePlayer;

      // -- activate a button or lever --
      if (this.conductor.canReach(pos) && this.conductor.canUseBlock(state) && fake != null) {
      //  Railways.LOGGER.info("I'm activating a block for you!");

        ClipContext context = new ClipContext(this.conductor.getEyePosition(), new Vec3(pos.getX(), pos.getY(), pos.getZ()),
          ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fake);
        BlockHitResult hitResult = level.clip(context);
        //Railways.LOGGER.info("pos: "+pos+", Hpos: "+hitResult.getBlockPos());
        Event.Result useBlock    = Event.Result.DEFAULT;
        if (!pos.equals(hitResult.getBlockPos()))
          return;
        if (!state.getShape(level, pos).isEmpty()) {
          PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(fake, InteractionHand.MAIN_HAND, pos, hitResult);
          useBlock = event.getUseBlock();
        }
        if (useBlock != Event.Result.DENY) {
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
    if (toolboxHolder != null) {
      CompoundTag toolboxTag = new CompoundTag();
      toolboxHolder.write(toolboxTag, false);
      nbt.put("toolboxHolder", toolboxTag);
    }
    nbt.putString("job", getEntityData().get(JOB).name());
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
      toolboxHolder = MountedToolboxHolder.read(this, nbt.getCompound("toolboxHolder"));
    } else {
      toolboxHolder = null;
    }
    if (nbt.contains("job", Tag.TAG_STRING)) {
      getEntityData().set(JOB, Job.valueOf(nbt.getString("job")));
    } else {
      getEntityData().set(JOB, Job.DEFAULT);
    }
  }

  @Override
  public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
    if (isCarryingToolbox() && capability == ForgeCapabilities.ITEM_HANDLER) {
      return toolboxHolder.getInventoryProvider().cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    if (isCarryingToolbox()) {
      toolboxHolder.invalidateCaps();
    }
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    if (isCarryingToolbox()) {
      toolboxHolder.reviveCaps();
    }
  }

  public enum Job {
    REDSTONE_OPERATOR,
    TOOLBOX_CARRIER
    ;
    public static final Job DEFAULT = REDSTONE_OPERATOR;
  }
}
