package com.railwayteam.railways.content.Conductor;

import com.jozufozu.flywheel.repack.joml.Vector3i;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CREntities;
import com.simibubi.create.AllTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ConductorEntity extends AbstractGolem {
  public static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BYTE);
  public static final EntityDataAccessor<BlockPos> BLOCK = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BLOCK_POS);

  // keep this small for performance (plus conductors are smol)
  private static final Vector3i REACH = new Vector3i(3, 2, 3);

  private ConductorFakePlayer fakePlayer = null;

  public ConductorEntity (EntityType<? extends AbstractGolem> type, Level level) {
    super(type, level);
    this.maxUpStep = 0.5f;
  }

  @Override
  protected void defineSynchedData () {
    super.defineSynchedData();
    this.entityData.define(COLOR, idFrom(defaultColor()));
    this.entityData.define(BLOCK, this.blockPosition());
  }

  @Override
  protected void registerGoals () {
    super.registerGoals();
    goalSelector.addGoal(2, new ConductorLookedAtGoal(this));
    goalSelector.addGoal(1, new ConductorPonderBlockGoal(this));
    goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 8f));
  }

  public static AttributeSupplier.Builder createAttributes () {
    return Mob.createMobAttributes()
      .add(Attributes.MAX_HEALTH, 100.0D)
      .add(Attributes.MOVEMENT_SPEED, 0.25D)
      .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
  }

  @Override
  protected float getStandingEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pDimensions) {
    return pDimensions.height * 0.76f;
  }

  protected boolean validForHolding(ItemStack stack) {
    //Must be in main hand, otherwise it can't be retrieved
    //Add items as conductor gets functionality relating to them
    return getEquipmentSlotForItem(stack) == EquipmentSlot.MAINHAND && stack.is(AllTags.AllItemTags.TOOLBOXES.tag);
  }

  @Override
  protected @NotNull InteractionResult mobInteract (Player player, @NotNull InteractionHand hand) {
    if (player.getItemInHand(hand).getItem() instanceof DyeItem di) {
      setColor (di.getDyeColor());
      if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
      return InteractionResult.SUCCESS;
    } else if (this.getMainHandItem().isEmpty() && validForHolding(player.getItemInHand(hand))) {
      if (this.equipItemIfPossible(player.getItemInHand(hand))) {
        player.setItemInHand(hand, ItemStack.EMPTY);
        return InteractionResult.SUCCESS;
      }
    } else if (this.getMainHandItem().is(AllTags.AllItemTags.TOOLBOXES.tag)) {
      if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
        player.setItemInHand(hand, this.getMainHandItem());
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }
      return InteractionResult.SUCCESS;
    }
    return super.mobInteract(player, hand);
  }

  @Override
  public void tick() {
    super.tick();
    if (fakePlayer == null && !level.isClientSide) fakePlayer = new ConductorFakePlayer((ServerLevel)level);
  }

  public static ConductorEntity spawn (Level level, BlockPos pos, ItemStack stack) {
    if (!(stack.getItem() instanceof ConductorCapItem cap)) return null;
    ConductorEntity result = new ConductorEntity(CREntities.CONDUCTOR.get(), level);
    result.setPos(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
    result.setColor(cap.color);
    result.equipItemIfPossible(stack);
    level.addFreshEntity(result);
    return result;
  }

  public boolean isInMinecart () {
    return this.getVehicle() instanceof AbstractMinecart;
  }

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

  static class ConductorLookedAtGoal extends Goal {
    private final ConductorEntity conductor;

    @Nullable
    private LivingEntity target;

    public ConductorLookedAtGoal (ConductorEntity conductor) {
      this.conductor = conductor;
    }

    @Override
    public boolean canUse () {
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
      if (this.conductor.canUseBlock(state)) {
      //  Railways.LOGGER.info("I'm activating a block for you!");

        ClipContext context = new ClipContext(this.conductor.getEyePosition(), new Vec3(pos.getX(), pos.getY(), pos.getZ()),
          ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fake);
        BlockHitResult hitResult = level.clip(context);
        Event.Result useBlock    = Event.Result.DEFAULT;
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

  static class ConductorPonderBlockGoal extends Goal {
    private final ConductorEntity conductor;
    private BlockPos target;

    public ConductorPonderBlockGoal (ConductorEntity conductor) {
      this.conductor  = conductor;
      this.target     = conductor.entityData.get(BLOCK);
    }

    @Override
    public boolean canUse () {
      this.target = conductor.entityData.get(BLOCK);
      if (this.conductor.canUseBlock(this.conductor.level.getBlockState(this.target))) return true;
      // else search
      for (int y= -REACH.y; y< REACH.y; y++) {
        for (int x= -REACH.x; x< REACH.x; x++) {
          for (int z= -REACH.z; z< REACH.z; z++) {
            BlockPos at = this.conductor.blockPosition().offset(x, y, z);
            BlockState state = this.conductor.level.getBlockState(at);
            if (this.conductor.canUseBlock(state)) {
              this.target = at;
              conductor.entityData.set(BLOCK, this.target);
              return true;
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
  public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
    super.addAdditionalSaveData(pCompound);
    pCompound.put("target", NbtUtils.writeBlockPos(getEntityData().get(BLOCK)));
  }

  @Override
  public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
    super.readAdditionalSaveData(pCompound);
    if (pCompound.contains("target", Tag.TAG_COMPOUND)) {
      getEntityData().set(BLOCK, NbtUtils.readBlockPos(pCompound.getCompound("target")));
    }
  }
}
