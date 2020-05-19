package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements INameable, ICommandSource, net.minecraftforge.common.extensions.IForgeEntity {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger();
   private static final List<ItemStack> EMPTY_EQUIPMENT = Collections.emptyList();
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double renderDistanceWeight = 1.0D;
   @Deprecated // Forge: Use the getter to allow overriding in mods
   private final EntityType<?> type;
   private int entityId = NEXT_ENTITY_ID.incrementAndGet();
   public boolean preventEntitySpawning;
   private final List<Entity> passengers = Lists.newArrayList();
   protected int rideCooldown;
   @Nullable
   private Entity ridingEntity;
   public boolean forceSpawn;
   public World world;
   public double prevPosX;
   public double prevPosY;
   public double prevPosZ;
   private double posX;
   private double posY;
   private double posZ;
   private Vec3d motion = Vec3d.ZERO;
   public float rotationYaw;
   public float rotationPitch;
   public float prevRotationYaw;
   public float prevRotationPitch;
   private AxisAlignedBB boundingBox = ZERO_AABB;
   public boolean onGround;
   public boolean collidedHorizontally;
   public boolean collidedVertically;
   public boolean collided;
   public boolean velocityChanged;
   protected Vec3d motionMultiplier = Vec3d.ZERO;
   @Deprecated //Forge: Use isAlive, remove(boolean) and revive() instead of directly accessing this field. To allow the entity to react to and better control this information.
   public boolean removed;
   public float prevDistanceWalkedModified;
   public float distanceWalkedModified;
   public float distanceWalkedOnStepModified;
   public float fallDistance;
   private float nextStepDistance = 1.0F;
   private float nextFlap = 1.0F;
   public double lastTickPosX;
   public double lastTickPosY;
   public double lastTickPosZ;
   public float stepHeight;
   public boolean noClip;
   public float entityCollisionReduction;
   protected final Random rand = new Random();
   public int ticksExisted;
   private int fire = -this.getFireImmuneTicks();
   protected boolean inWater;
   protected double submergedHeight;
   protected boolean eyesInWater;
   protected boolean inLava;
   public int hurtResistantTime;
   protected boolean firstUpdate = true;
   protected final EntityDataManager dataManager;
   protected static final DataParameter<Byte> FLAGS = EntityDataManager.createKey(Entity.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> AIR = EntityDataManager.createKey(Entity.class, DataSerializers.VARINT);
   private static final DataParameter<Optional<ITextComponent>> CUSTOM_NAME = EntityDataManager.createKey(Entity.class, DataSerializers.OPTIONAL_TEXT_COMPONENT);
   private static final DataParameter<Boolean> CUSTOM_NAME_VISIBLE = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> SILENT = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> NO_GRAVITY = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
   protected static final DataParameter<Pose> POSE = EntityDataManager.createKey(Entity.class, DataSerializers.POSE);
   public boolean addedToChunk;
   public int chunkCoordX;
   public int chunkCoordY;
   public int chunkCoordZ;
   public long serverPosX;
   public long serverPosY;
   public long serverPosZ;
   public boolean ignoreFrustumCheck;
   public boolean isAirBorne;
   public int timeUntilPortal;
   protected boolean inPortal;
   protected int portalCounter;
   public DimensionType dimension;
   /** The position of the last portal the entity was in */
   protected BlockPos lastPortalPos;
   /** A horizontal vector related to the position of the last portal the entity was in */
   protected Vec3d lastPortalVec;
   /** A direction related to the position of the last portal the entity was in */
   protected Direction teleportDirection;
   private boolean invulnerable;
   protected UUID entityUniqueID = MathHelper.getRandomUUID(this.rand);
   protected String cachedUniqueIdString = this.entityUniqueID.toString();
   protected boolean glowing;
   private final Set<String> tags = Sets.newHashSet();
   private boolean isPositionDirty;
   private final double[] pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
   private long pistonDeltasGameTime;
   private EntitySize size;
   private float eyeHeight;

   public Entity(EntityType<?> entityTypeIn, World worldIn) {
      super(Entity.class);
      this.type = entityTypeIn;
      this.world = worldIn;
      this.size = entityTypeIn.getSize();
      this.setPosition(0.0D, 0.0D, 0.0D);
      if (worldIn != null) {
         this.dimension = worldIn.dimension.getType();
      }

      this.dataManager = new EntityDataManager(this);
      this.dataManager.register(FLAGS, (byte)0);
      this.dataManager.register(AIR, this.getMaxAir());
      this.dataManager.register(CUSTOM_NAME_VISIBLE, false);
      this.dataManager.register(CUSTOM_NAME, Optional.empty());
      this.dataManager.register(SILENT, false);
      this.dataManager.register(NO_GRAVITY, false);
      this.dataManager.register(POSE, Pose.STANDING);
      this.registerData();
      this.eyeHeight = getEyeHeightForge(Pose.STANDING, this.size);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EntityConstructing(this));
      this.gatherCapabilities();
   }

   @OnlyIn(Dist.CLIENT)
   public int getTeamColor() {
      Team team = this.getTeam();
      return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
   }

   /**
    * Returns true if the player is in spectator mode.
    */
   public boolean isSpectator() {
      return false;
   }

   public final void detach() {
      if (this.isBeingRidden()) {
         this.removePassengers();
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   public void setPacketCoordinates(double p_213312_1_, double p_213312_3_, double p_213312_5_) {
      this.serverPosX = SEntityPacket.func_218743_a(p_213312_1_);
      this.serverPosY = SEntityPacket.func_218743_a(p_213312_3_);
      this.serverPosZ = SEntityPacket.func_218743_a(p_213312_5_);
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void setEntityId(int id) {
      this.entityId = id;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public boolean addTag(String tag) {
      return this.tags.size() >= 1024 ? false : this.tags.add(tag);
   }

   public boolean removeTag(String tag) {
      return this.tags.remove(tag);
   }

   /**
    * Called by the /kill command.
    */
   public void onKillCommand() {
      this.remove();
   }

   protected abstract void registerData();

   public EntityDataManager getDataManager() {
      return this.dataManager;
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ instanceof Entity) {
         return ((Entity)p_equals_1_).entityId == this.entityId;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.entityId;
   }

   /**
    * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
    * (only actually used on players though its also on Entity)
    */
   @OnlyIn(Dist.CLIENT)
   protected void preparePlayerToSpawn() {
      if (this.world != null) {
         for(double d0 = this.getPosY(); d0 > 0.0D && d0 < this.world.getDimension().getHeight(); ++d0) {
            this.setPosition(this.getPosX(), d0, this.getPosZ());
            if (this.world.hasNoCollisions(this)) {
               break;
            }
         }

         this.setMotion(Vec3d.ZERO);
         this.rotationPitch = 0.0F;
      }
   }

   /**
    * Queues the entity for removal from the world on the next tick.
    */
   public void remove() {
      this.remove(false);
   }

   public void remove(boolean keepData) {
      this.removed = true;
      if (!keepData)
         this.invalidateCaps();
   }

   protected void setPose(Pose poseIn) {
      this.dataManager.set(POSE, poseIn);
   }

   public Pose getPose() {
      return this.dataManager.get(POSE);
   }

   /**
    * Sets the rotation of the entity.
    */
   protected void setRotation(float yaw, float pitch) {
      this.rotationYaw = yaw % 360.0F;
      this.rotationPitch = pitch % 360.0F;
   }

   /**
    * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
    */
   public void setPosition(double x, double y, double z) {
      this.setRawPosition(x, y, z);
      if (this.isAddedToWorld() && !this.world.isRemote && world instanceof ServerWorld) ((ServerWorld)this.world).chunkCheck(this); // Forge - Process chunk registration after moving.
      float f = this.size.width / 2.0F;
      float f1 = this.size.height;
      this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
   }

   /**
    * Recomputes this entity's bounding box so that it is positioned at this entity's X/Y/Z.
    */
   protected void recenterBoundingBox() {
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   @OnlyIn(Dist.CLIENT)
   public void rotateTowards(double yaw, double pitch) {
      double d0 = pitch * 0.15D;
      double d1 = yaw * 0.15D;
      this.rotationPitch = (float)((double)this.rotationPitch + d0);
      this.rotationYaw = (float)((double)this.rotationYaw + d1);
      this.rotationPitch = MathHelper.clamp(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = (float)((double)this.prevRotationPitch + d0);
      this.prevRotationYaw = (float)((double)this.prevRotationYaw + d1);
      this.prevRotationPitch = MathHelper.clamp(this.prevRotationPitch, -90.0F, 90.0F);
      if (this.ridingEntity != null) {
         this.ridingEntity.applyOrientationToEntity(this);
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (!this.world.isRemote) {
         this.setFlag(6, this.isGlowing());
      }

      this.baseTick();
   }

   /**
    * Gets called every tick from main Entity class
    */
   public void baseTick() {
      this.world.getProfiler().startSection("entityBaseTick");
      if (this.isPassenger() && this.getRidingEntity().removed) {
         this.stopRiding();
      }

      if (this.rideCooldown > 0) {
         --this.rideCooldown;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      this.updatePortal();
      this.spawnRunningParticles();
      this.updateAquatics();
      if (this.world.isRemote) {
         this.extinguish();
      } else if (this.fire > 0) {
         if (this.isImmuneToFire()) {
            this.fire -= 4;
            if (this.fire < 0) {
               this.extinguish();
            }
         } else {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
            }

            --this.fire;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.getPosY() < -64.0D) {
         this.outOfWorld();
      }

      if (!this.world.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.world.getProfiler().endSection();
   }

   /**
    * Decrements the counter for the remaining time until the entity may use a portal again.
    */
   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0) {
         --this.timeUntilPortal;
      }

   }

   /**
    * Return the amount of time this entity should stay in a portal before being transported.
    */
   public int getMaxInPortalTime() {
      return 1;
   }

   /**
    * Called whenever the entity is walking inside of lava.
    */
   protected void setOnFireFromLava() {
      if (!this.isImmuneToFire()) {
         this.setFire(15);
         this.attackEntityFrom(DamageSource.LAVA, 4.0F);
      }
   }

   /**
    * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
    */
   public void setFire(int seconds) {
      int i = seconds * 20;
      if (this instanceof LivingEntity) {
         i = ProtectionEnchantment.getFireTimeForEntity((LivingEntity)this, i);
      }

      if (this.fire < i) {
         this.fire = i;
      }

   }

   public void setFireTimer(int p_223308_1_) {
      this.fire = p_223308_1_;
   }

   public int getFireTimer() {
      return this.fire;
   }

   /**
    * Removes fire from entity.
    */
   public void extinguish() {
      this.fire = 0;
   }

   /**
    * sets the dead flag. Used when you fall off the bottom of the world.
    */
   protected void outOfWorld() {
      this.remove();
   }

   /**
    * Checks if the offset position from the entity's current position is inside of a liquid.
    */
   public boolean isOffsetPositionInLiquid(double x, double y, double z) {
      return this.isLiquidPresentInAABB(this.getBoundingBox().offset(x, y, z));
   }

   /**
    * Determines if a liquid is present within the specified AxisAlignedBB.
    */
   private boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
      return this.world.hasNoCollisions(this, bb) && !this.world.containsAnyLiquid(bb);
   }

   public void move(MoverType typeIn, Vec3d pos) {
      if (this.noClip) {
         this.setBoundingBox(this.getBoundingBox().offset(pos));
         this.resetPositionToBB();
      } else {
         if (typeIn == MoverType.PISTON) {
            pos = this.handlePistonMovement(pos);
            if (pos.equals(Vec3d.ZERO)) {
               return;
            }
         }

         this.world.getProfiler().startSection("move");
         if (this.motionMultiplier.lengthSquared() > 1.0E-7D) {
            pos = pos.mul(this.motionMultiplier);
            this.motionMultiplier = Vec3d.ZERO;
            this.setMotion(Vec3d.ZERO);
         }

         pos = this.maybeBackOffFromEdge(pos, typeIn);
         Vec3d vec3d = this.getAllowedMovement(pos);
         if (vec3d.lengthSquared() > 1.0E-7D) {
            this.setBoundingBox(this.getBoundingBox().offset(vec3d));
            this.resetPositionToBB();
         }

         this.world.getProfiler().endSection();
         this.world.getProfiler().startSection("rest");
         this.collidedHorizontally = !MathHelper.epsilonEquals(pos.x, vec3d.x) || !MathHelper.epsilonEquals(pos.z, vec3d.z);
         this.collidedVertically = pos.y != vec3d.y;
         this.onGround = this.collidedVertically && pos.y < 0.0D;
         this.collided = this.collidedHorizontally || this.collidedVertically;
         BlockPos blockpos = this.getOnPosition();
         BlockState blockstate = this.world.getBlockState(blockpos);
         this.updateFallState(vec3d.y, this.onGround, blockstate, blockpos);
         Vec3d vec3d1 = this.getMotion();
         if (pos.x != vec3d.x) {
            this.setMotion(0.0D, vec3d1.y, vec3d1.z);
         }

         if (pos.z != vec3d.z) {
            this.setMotion(vec3d1.x, vec3d1.y, 0.0D);
         }

         Block block = blockstate.getBlock();
         if (pos.y != vec3d.y) {
            block.onLanded(this.world, this);
         }

         if (this.onGround && !this.isSteppingCarefully()) {
            block.onEntityWalk(this.world, blockpos, this);
         }

         if (this.canTriggerWalking() && !this.isPassenger()) {
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;
            if (block != Blocks.LADDER && block != Blocks.SCAFFOLDING) {
               d1 = 0.0D;
            }

            this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt(horizontalMag(vec3d)) * 0.6D);
            this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6D);
            if (this.distanceWalkedOnStepModified > this.nextStepDistance && !blockstate.isAir(this.world, blockpos)) {
               this.nextStepDistance = this.determineNextStepDistance();
               if (this.isInWater()) {
                  Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                  float f = entity == this ? 0.35F : 0.4F;
                  Vec3d vec3d2 = entity.getMotion();
                  float f1 = MathHelper.sqrt(vec3d2.x * vec3d2.x * (double)0.2F + vec3d2.y * vec3d2.y + vec3d2.z * vec3d2.z * (double)0.2F) * f;
                  if (f1 > 1.0F) {
                     f1 = 1.0F;
                  }

                  this.playSwimSound(f1);
               } else {
                  this.playStepSound(blockpos, blockstate);
               }
            } else if (this.distanceWalkedOnStepModified > this.nextFlap && this.makeFlySound() && blockstate.isAir(this.world, blockpos)) {
               this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
            }
         }

         try {
            this.inLava = false;
            this.doBlockCollisions();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
            this.fillCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
         }

         this.setMotion(this.getMotion().mul((double)this.getSpeedFactor(), 1.0D, (double)this.getSpeedFactor()));
         boolean flag = this.isInWaterRainOrBubbleColumn();
         if (this.world.isFlammableWithin(this.getBoundingBox().shrink(0.001D))) {
            if (!flag) {
               ++this.fire;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }

            this.dealFireDamage(1);
         } else if (this.fire <= 0) {
            this.fire = -this.getFireImmuneTicks();
         }

         if (flag && this.isBurning()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.getFireImmuneTicks();
         }

         this.world.getProfiler().endSection();
      }
   }

   protected BlockPos getOnPosition() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY - (double)0.2F);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (this.world.isAirBlock(blockpos)) {
         BlockPos blockpos1 = blockpos.down();
         BlockState blockstate = this.world.getBlockState(blockpos1);
         if (blockstate.collisionExtendsVertically(this.world, blockpos1, this)) {
            return blockpos1;
         }
      }

      return blockpos;
   }

   protected float getJumpFactor() {
      float f = this.world.getBlockState(new BlockPos(this)).getBlock().getJumpFactor();
      float f1 = this.world.getBlockState(this.getPositionUnderneath()).getBlock().getJumpFactor();
      return (double)f == 1.0D ? f1 : f;
   }

   protected float getSpeedFactor() {
      Block block = this.world.getBlockState(new BlockPos(this)).getBlock();
      float f = block.getSpeedFactor();
      if (block != Blocks.WATER && block != Blocks.BUBBLE_COLUMN) {
         return (double)f == 1.0D ? this.world.getBlockState(this.getPositionUnderneath()).getBlock().getSpeedFactor() : f;
      } else {
         return f;
      }
   }

   protected BlockPos getPositionUnderneath() {
      return new BlockPos(this.posX, this.getBoundingBox().minY - 0.5000001D, this.posZ);
   }

   protected Vec3d maybeBackOffFromEdge(Vec3d p_225514_1_, MoverType p_225514_2_) {
      return p_225514_1_;
   }

   protected Vec3d handlePistonMovement(Vec3d pos) {
      if (pos.lengthSquared() <= 1.0E-7D) {
         return pos;
      } else {
         long i = this.world.getGameTime();
         if (i != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0D);
            this.pistonDeltasGameTime = i;
         }

         if (pos.x != 0.0D) {
            double d2 = this.calculatePistonDeltas(Direction.Axis.X, pos.x);
            return Math.abs(d2) <= (double)1.0E-5F ? Vec3d.ZERO : new Vec3d(d2, 0.0D, 0.0D);
         } else if (pos.y != 0.0D) {
            double d1 = this.calculatePistonDeltas(Direction.Axis.Y, pos.y);
            return Math.abs(d1) <= (double)1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0D, d1, 0.0D);
         } else if (pos.z != 0.0D) {
            double d0 = this.calculatePistonDeltas(Direction.Axis.Z, pos.z);
            return Math.abs(d0) <= (double)1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0D, 0.0D, d0);
         } else {
            return Vec3d.ZERO;
         }
      }
   }

   private double calculatePistonDeltas(Direction.Axis axis, double distance) {
      int i = axis.ordinal();
      double d0 = MathHelper.clamp(distance + this.pistonDeltas[i], -0.51D, 0.51D);
      distance = d0 - this.pistonDeltas[i];
      this.pistonDeltas[i] = d0;
      return distance;
   }

   /**
    * Given a motion vector, return an updated vector that takes into account restrictions such as collisions (from all
    * directions) and step-up from stepHeight
    */
   private Vec3d getAllowedMovement(Vec3d vec) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
      VoxelShape voxelshape = this.world.getWorldBorder().getShape();
      Stream<VoxelShape> stream = VoxelShapes.compare(voxelshape, VoxelShapes.create(axisalignedbb.shrink(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
      Stream<VoxelShape> stream1 = this.world.getEmptyCollisionShapes(this, axisalignedbb.expand(vec), ImmutableSet.of());
      ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(stream1, stream));
      Vec3d vec3d = vec.lengthSquared() == 0.0D ? vec : collideBoundingBoxHeuristically(this, vec, axisalignedbb, this.world, iselectioncontext, reuseablestream);
      boolean flag = vec.x != vec3d.x;
      boolean flag1 = vec.y != vec3d.y;
      boolean flag2 = vec.z != vec3d.z;
      boolean flag3 = this.onGround || flag1 && vec.y < 0.0D;
      if (this.stepHeight > 0.0F && flag3 && (flag || flag2)) {
         Vec3d vec3d1 = collideBoundingBoxHeuristically(this, new Vec3d(vec.x, (double)this.stepHeight, vec.z), axisalignedbb, this.world, iselectioncontext, reuseablestream);
         Vec3d vec3d2 = collideBoundingBoxHeuristically(this, new Vec3d(0.0D, (double)this.stepHeight, 0.0D), axisalignedbb.expand(vec.x, 0.0D, vec.z), this.world, iselectioncontext, reuseablestream);
         if (vec3d2.y < (double)this.stepHeight) {
            Vec3d vec3d3 = collideBoundingBoxHeuristically(this, new Vec3d(vec.x, 0.0D, vec.z), axisalignedbb.offset(vec3d2), this.world, iselectioncontext, reuseablestream).add(vec3d2);
            if (horizontalMag(vec3d3) > horizontalMag(vec3d1)) {
               vec3d1 = vec3d3;
            }
         }

         if (horizontalMag(vec3d1) > horizontalMag(vec3d)) {
            return vec3d1.add(collideBoundingBoxHeuristically(this, new Vec3d(0.0D, -vec3d1.y + vec.y, 0.0D), axisalignedbb.offset(vec3d1), this.world, iselectioncontext, reuseablestream));
         }
      }

      return vec3d;
   }

   public static double horizontalMag(Vec3d vec) {
      return vec.x * vec.x + vec.z * vec.z;
   }

   public static Vec3d collideBoundingBoxHeuristically(@Nullable Entity p_223307_0_, Vec3d p_223307_1_, AxisAlignedBB p_223307_2_, World p_223307_3_, ISelectionContext p_223307_4_, ReuseableStream<VoxelShape> p_223307_5_) {
      boolean flag = p_223307_1_.x == 0.0D;
      boolean flag1 = p_223307_1_.y == 0.0D;
      boolean flag2 = p_223307_1_.z == 0.0D;
      if ((!flag || !flag1) && (!flag || !flag2) && (!flag1 || !flag2)) {
         ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(p_223307_5_.createStream(), p_223307_3_.getCollisionShapes(p_223307_0_, p_223307_2_.expand(p_223307_1_))));
         return collideBoundingBox(p_223307_1_, p_223307_2_, reuseablestream);
      } else {
         return getAllowedMovement(p_223307_1_, p_223307_2_, p_223307_3_, p_223307_4_, p_223307_5_);
      }
   }

   public static Vec3d collideBoundingBox(Vec3d p_223310_0_, AxisAlignedBB p_223310_1_, ReuseableStream<VoxelShape> p_223310_2_) {
      double d0 = p_223310_0_.x;
      double d1 = p_223310_0_.y;
      double d2 = p_223310_0_.z;
      if (d1 != 0.0D) {
         d1 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, p_223310_1_, p_223310_2_.createStream(), d1);
         if (d1 != 0.0D) {
            p_223310_1_ = p_223310_1_.offset(0.0D, d1, 0.0D);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, p_223310_1_, p_223310_2_.createStream(), d2);
         if (d2 != 0.0D) {
            p_223310_1_ = p_223310_1_.offset(0.0D, 0.0D, d2);
         }
      }

      if (d0 != 0.0D) {
         d0 = VoxelShapes.getAllowedOffset(Direction.Axis.X, p_223310_1_, p_223310_2_.createStream(), d0);
         if (!flag && d0 != 0.0D) {
            p_223310_1_ = p_223310_1_.offset(d0, 0.0D, 0.0D);
         }
      }

      if (!flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, p_223310_1_, p_223310_2_.createStream(), d2);
      }

      return new Vec3d(d0, d1, d2);
   }

   public static Vec3d getAllowedMovement(Vec3d vec, AxisAlignedBB collisionBox, IWorldReader worldIn, ISelectionContext selectionContext, ReuseableStream<VoxelShape> potentialHits) {
      double d0 = vec.x;
      double d1 = vec.y;
      double d2 = vec.z;
      if (d1 != 0.0D) {
         d1 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, collisionBox, worldIn, d1, selectionContext, potentialHits.createStream());
         if (d1 != 0.0D) {
            collisionBox = collisionBox.offset(0.0D, d1, 0.0D);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, collisionBox, worldIn, d2, selectionContext, potentialHits.createStream());
         if (d2 != 0.0D) {
            collisionBox = collisionBox.offset(0.0D, 0.0D, d2);
         }
      }

      if (d0 != 0.0D) {
         d0 = VoxelShapes.getAllowedOffset(Direction.Axis.X, collisionBox, worldIn, d0, selectionContext, potentialHits.createStream());
         if (!flag && d0 != 0.0D) {
            collisionBox = collisionBox.offset(d0, 0.0D, 0.0D);
         }
      }

      if (!flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, collisionBox, worldIn, d2, selectionContext, potentialHits.createStream());
      }

      return new Vec3d(d0, d1, d2);
   }

   protected float determineNextStepDistance() {
      return (float)((int)this.distanceWalkedOnStepModified + 1);
   }

   /**
    * Resets the entity's position to the center (planar) and bottom (vertical) points of its bounding box.
    */
   public void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.setRawPosition((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
      if (this.isAddedToWorld() && !this.world.isRemote && world instanceof ServerWorld) ((ServerWorld)this.world).chunkCheck(this); // Forge - Process chunk registration after moving.
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_GENERIC_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected SoundEvent getHighspeedSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected void doBlockCollisions() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();

      try (
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
         BlockPos.PooledMutable blockpos$pooledmutable1 = BlockPos.PooledMutable.retain(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
         BlockPos.PooledMutable blockpos$pooledmutable2 = BlockPos.PooledMutable.retain();
      ) {
         if (this.world.isAreaLoaded(blockpos$pooledmutable, blockpos$pooledmutable1)) {
            for(int i = blockpos$pooledmutable.getX(); i <= blockpos$pooledmutable1.getX(); ++i) {
               for(int j = blockpos$pooledmutable.getY(); j <= blockpos$pooledmutable1.getY(); ++j) {
                  for(int k = blockpos$pooledmutable.getZ(); k <= blockpos$pooledmutable1.getZ(); ++k) {
                     blockpos$pooledmutable2.setPos(i, j, k);
                     BlockState blockstate = this.world.getBlockState(blockpos$pooledmutable2);

                     try {
                        blockstate.onEntityCollision(this.world, blockpos$pooledmutable2, this);
                        this.onInsideBlock(blockstate);
                     } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                        CrashReportCategory.addBlockInfo(crashreportcategory, blockpos$pooledmutable2, blockstate);
                        throw new ReportedException(crashreport);
                     }
                  }
               }
            }
         }
      }

   }

   protected void onInsideBlock(BlockState p_191955_1_) {
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      if (!blockIn.getMaterial().isLiquid()) {
         BlockState blockstate = this.world.getBlockState(pos.up());
         SoundType soundtype = blockstate.getBlock() == Blocks.SNOW ? blockstate.getSoundType(world, pos, this) : blockIn.getSoundType(world, pos, this);
         this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
      }
   }

   protected void playSwimSound(float volume) {
      this.playSound(this.getSwimSound(), volume, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
   }

   protected float playFlySound(float volume) {
      return 0.0F;
   }

   protected boolean makeFlySound() {
      return false;
   }

   public void playSound(SoundEvent soundIn, float volume, float pitch) {
      if (!this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch);
      }

   }

   /**
    * @return True if this entity will not play sounds
    */
   public boolean isSilent() {
      return this.dataManager.get(SILENT);
   }

   /**
    * When set to true the entity will not play sounds.
    */
   public void setSilent(boolean isSilent) {
      this.dataManager.set(SILENT, isSilent);
   }

   public boolean hasNoGravity() {
      return this.dataManager.get(NO_GRAVITY);
   }

   public void setNoGravity(boolean noGravity) {
      this.dataManager.set(NO_GRAVITY, noGravity);
   }

   protected boolean canTriggerWalking() {
      return true;
   }

   protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 0.0F) {
            state.getBlock().onFallenUpon(this.world, pos, this, this.fallDistance);
         }

         this.fallDistance = 0.0F;
      } else if (y < 0.0D) {
         this.fallDistance = (float)((double)this.fallDistance - y);
      }

   }

   /**
    * Returns the <b>solid</b> collision bounding box for this entity. Used to make (e.g.) boats solid. Return null if
    * this entity is not solid.
    *  
    * For general purposes, use {@link #width} and {@link #height}.
    *  
    * @see getEntityBoundingBox
    */
   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   /**
    * Will deal the specified amount of fire damage to the entity if the entity isn't immune to fire damage.
    */
   protected void dealFireDamage(int amount) {
      if (!this.isImmuneToFire()) {
         this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
      }

   }

   public final boolean isImmuneToFire() {
      return this.getType().isImmuneToFire();
   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      if (this.isBeingRidden()) {
         for(Entity entity : this.getPassengers()) {
            entity.onLivingFall(distance, damageMultiplier);
         }
      }

      return false;
   }

   /**
    * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
    * true)
    */
   public boolean isInWater() {
      return this.inWater;
   }

   private boolean isInRain() {
      boolean flag;
      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain(this)) {
         flag = this.world.isRainingAt(blockpos$pooledmutable) || this.world.isRainingAt(blockpos$pooledmutable.setPos(this.getPosX(), this.getPosY() + (double)this.size.height, this.getPosZ()));
      }

      return flag;
   }

   private boolean isInBubbleColumn() {
      return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
   }

   /**
    * Checks if this entity is either in water or on an open air block in rain (used in wolves).
    */
   public boolean isWet() {
      return this.isInWater() || this.isInRain();
   }

   public boolean isInWaterRainOrBubbleColumn() {
      return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
   }

   public boolean isInWaterOrBubbleColumn() {
      return this.isInWater() || this.isInBubbleColumn();
   }

   public boolean canSwim() {
      return this.eyesInWater && this.isInWater();
   }

   private void updateAquatics() {
      this.handleWaterMovement();
      this.updateEyesInWater();
      this.updateSwimming();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(this.isSprinting() && this.canSwim() && !this.isPassenger());
      }

   }

   /**
    * Returns if this entity is in water and will end up adding the waters velocity to the entity
    */
   public boolean handleWaterMovement() {
      if (this.getRidingEntity() instanceof BoatEntity) {
         this.inWater = false;
      } else if (this.handleFluidAcceleration(FluidTags.WATER)) {
         if (!this.inWater && !this.firstUpdate) {
            this.doWaterSplashEffect();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.extinguish();
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   private void updateEyesInWater() {
      this.eyesInWater = this.areEyesInFluid(FluidTags.WATER, true);
   }

   /**
    * Plays the {@link #getSplashSound() splash sound}, and the {@link ParticleType#WATER_BUBBLE} and {@link
    * ParticleType#WATER_SPLASH} particles.
    */
   protected void doWaterSplashEffect() {
      Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
      float f = entity == this ? 0.2F : 0.9F;
      Vec3d vec3d = entity.getMotion();
      float f1 = MathHelper.sqrt(vec3d.x * vec3d.x * (double)0.2F + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double)0.2F) * f;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      if ((double)f1 < 0.25D) {
         this.playSound(this.getSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getHighspeedSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      }

      float f2 = (float)MathHelper.floor(this.getPosY());

      for(int i = 0; (float)i < 1.0F + this.size.width * 20.0F; ++i) {
         float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + (double)f3, (double)(f2 + 1.0F), this.getPosZ() + (double)f4, vec3d.x, vec3d.y - (double)(this.rand.nextFloat() * 0.2F), vec3d.z);
      }

      for(int j = 0; (float)j < 1.0F + this.size.width * 20.0F; ++j) {
         float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         float f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         this.world.addParticle(ParticleTypes.SPLASH, this.getPosX() + (double)f5, (double)(f2 + 1.0F), this.getPosZ() + (double)f6, vec3d.x, vec3d.y, vec3d.z);
      }

   }

   /**
    * Attempts to create sprinting particles if the entity is sprinting and not in water.
    */
   public void spawnRunningParticles() {
      if (this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }

   }

   protected void createRunningParticles() {
      int i = MathHelper.floor(this.getPosX());
      int j = MathHelper.floor(this.getPosY() - (double)0.2F);
      int k = MathHelper.floor(this.getPosZ());
      BlockPos blockpos = new BlockPos(i, j, k);
      BlockState blockstate = this.world.getBlockState(blockpos);
      if (!blockstate.addRunningEffects(world, blockpos, this))
      if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
         Vec3d vec3d = this.getMotion();
         this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.getPosX() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.size.width, this.getPosY() + 0.1D, this.getPosZ() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.size.width, vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
      }

   }

   public boolean areEyesInFluid(Tag<Fluid> tagIn) {
      return this.areEyesInFluid(tagIn, false);
   }

   public boolean areEyesInFluid(Tag<Fluid> p_213290_1_, boolean checkChunkLoaded) {
      if (this.getRidingEntity() instanceof BoatEntity) {
         return false;
      } else {
         double d0 = this.getPosYEye();
         BlockPos blockpos = new BlockPos(this.getPosX(), d0, this.getPosZ());
         if (checkChunkLoaded && !this.world.chunkExists(blockpos.getX() >> 4, blockpos.getZ() >> 4)) {
            return false;
         } else {
            IFluidState ifluidstate = this.world.getFluidState(blockpos);
            return ifluidstate.isEntityInside(world, blockpos, this, d0, p_213290_1_, true);
         }
      }
   }

   public void setInLava() {
      this.inLava = true;
   }

   public boolean isInLava() {
      return this.inLava;
   }

   public void moveRelative(float p_213309_1_, Vec3d relative) {
      Vec3d vec3d = getAbsoluteMotion(relative, p_213309_1_, this.rotationYaw);
      this.setMotion(this.getMotion().add(vec3d));
   }

   private static Vec3d getAbsoluteMotion(Vec3d relative, float p_213299_1_, float facing) {
      double d0 = relative.lengthSquared();
      if (d0 < 1.0E-7D) {
         return Vec3d.ZERO;
      } else {
         Vec3d vec3d = (d0 > 1.0D ? relative.normalize() : relative).scale((double)p_213299_1_);
         float f = MathHelper.sin(facing * ((float)Math.PI / 180F));
         float f1 = MathHelper.cos(facing * ((float)Math.PI / 180F));
         return new Vec3d(vec3d.x * (double)f1 - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)f1 + vec3d.x * (double)f);
      }
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.getPosX(), 0.0D, this.getPosZ());
      if (this.world.isBlockLoaded(blockpos$mutable)) {
         blockpos$mutable.setY(MathHelper.floor(this.getPosYEye()));
         return this.world.getBrightness(blockpos$mutable);
      } else {
         return 0.0F;
      }
   }

   /**
    * Sets the reference to the World object.
    */
   public void setWorld(World worldIn) {
      this.world = worldIn;
   }

   /**
    * Sets position and rotation, clamping and wrapping params to valid values. Used by network code.
    */
   public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      double d0 = MathHelper.clamp(x, -3.0E7D, 3.0E7D);
      double d1 = MathHelper.clamp(z, -3.0E7D, 3.0E7D);
      this.prevPosX = d0;
      this.prevPosY = y;
      this.prevPosZ = d1;
      this.setPosition(d0, y, d1);
      this.rotationYaw = yaw % 360.0F;
      this.rotationPitch = MathHelper.clamp(pitch, -90.0F, 90.0F) % 360.0F;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
      this.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, rotationYawIn, rotationPitchIn);
   }

   /**
    * Sets the location and Yaw/Pitch of an entity in the world
    */
   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.forceSetPosition(x, y, z);
      this.rotationYaw = yaw;
      this.rotationPitch = pitch;
      this.recenterBoundingBox();
   }

   /**
    * Like {@link #setRawPosition}, but also sets {@link #prevPosX}/Y/Z and {@link #lastTickPosX}/Y/Z. {@link
    * #setLocationAndAngles} does the same thing, except it also updates the bounding box.
    */
   public void forceSetPosition(double x, double y, double z) {
      this.setRawPosition(x, y, z);
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.lastTickPosX = x;
      this.lastTickPosY = y;
      this.lastTickPosZ = z;
   }

   /**
    * Returns the distance to the entity.
    */
   public float getDistance(Entity entityIn) {
      float f = (float)(this.getPosX() - entityIn.getPosX());
      float f1 = (float)(this.getPosY() - entityIn.getPosY());
      float f2 = (float)(this.getPosZ() - entityIn.getPosZ());
      return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   /**
    * Gets the squared distance to the position.
    */
   public double getDistanceSq(double x, double y, double z) {
      double d0 = this.getPosX() - x;
      double d1 = this.getPosY() - y;
      double d2 = this.getPosZ() - z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   /**
    * Returns the squared distance to the entity.
    */
   public double getDistanceSq(Entity entityIn) {
      return this.getDistanceSq(entityIn.getPositionVec());
   }

   public double getDistanceSq(Vec3d p_195048_1_) {
      double d0 = this.getPosX() - p_195048_1_.x;
      double d1 = this.getPosY() - p_195048_1_.y;
      double d2 = this.getPosZ() - p_195048_1_.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   /**
    * Called by a player entity when they collide with an entity
    */
   public void onCollideWithPlayer(PlayerEntity entityIn) {
   }

   /**
    * Applies a velocity to the entities, to push them away from eachother.
    */
   public void applyEntityCollision(Entity entityIn) {
      if (!this.isRidingSameEntity(entityIn)) {
         if (!entityIn.noClip && !this.noClip) {
            double d0 = entityIn.getPosX() - this.getPosX();
            double d1 = entityIn.getPosZ() - this.getPosZ();
            double d2 = MathHelper.absMax(d0, d1);
            if (d2 >= (double)0.01F) {
               d2 = (double)MathHelper.sqrt(d2);
               d0 = d0 / d2;
               d1 = d1 / d2;
               double d3 = 1.0D / d2;
               if (d3 > 1.0D) {
                  d3 = 1.0D;
               }

               d0 = d0 * d3;
               d1 = d1 * d3;
               d0 = d0 * (double)0.05F;
               d1 = d1 * (double)0.05F;
               d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
               d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
               if (!this.isBeingRidden()) {
                  this.addVelocity(-d0, 0.0D, -d1);
               }

               if (!entityIn.isBeingRidden()) {
                  entityIn.addVelocity(d0, 0.0D, d1);
               }
            }

         }
      }
   }

   /**
    * Adds to the current velocity of the entity, and sets {@link #isAirBorne} to true.
    */
   public void addVelocity(double x, double y, double z) {
      this.setMotion(this.getMotion().add(x, y, z));
      this.isAirBorne = true;
   }

   /**
    * Marks this entity's velocity as changed, so that it can be re-synced with the client later
    */
   protected void markVelocityChanged() {
      this.velocityChanged = true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.markVelocityChanged();
         return false;
      }
   }

   /**
    * interpolated look vector
    */
   public final Vec3d getLook(float partialTicks) {
      return this.getVectorForRotation(this.getPitch(partialTicks), this.getYaw(partialTicks));
   }

   /**
    * Gets the current pitch of the entity.
    */
   public float getPitch(float partialTicks) {
      return partialTicks == 1.0F ? this.rotationPitch : MathHelper.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);
   }

   /**
    * Gets the current yaw of the entity
    */
   public float getYaw(float partialTicks) {
      return partialTicks == 1.0F ? this.rotationYaw : MathHelper.lerp(partialTicks, this.prevRotationYaw, this.rotationYaw);
   }

   /**
    * Creates a Vec3 using the pitch and yaw of the entities rotation.
    */
   protected final Vec3d getVectorForRotation(float pitch, float yaw) {
      float f = pitch * ((float)Math.PI / 180F);
      float f1 = -yaw * ((float)Math.PI / 180F);
      float f2 = MathHelper.cos(f1);
      float f3 = MathHelper.sin(f1);
      float f4 = MathHelper.cos(f);
      float f5 = MathHelper.sin(f);
      return new Vec3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
   }

   public final Vec3d getUpVector(float partialTicks) {
      return this.calculateUpVector(this.getPitch(partialTicks), this.getYaw(partialTicks));
   }

   protected final Vec3d calculateUpVector(float p_213320_1_, float p_213320_2_) {
      return this.getVectorForRotation(p_213320_1_ - 90.0F, p_213320_2_);
   }

   public final Vec3d getEyePosition(float partialTicks) {
      if (partialTicks == 1.0F) {
         return new Vec3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
      } else {
         double d0 = MathHelper.lerp((double)partialTicks, this.prevPosX, this.getPosX());
         double d1 = MathHelper.lerp((double)partialTicks, this.prevPosY, this.getPosY()) + (double)this.getEyeHeight();
         double d2 = MathHelper.lerp((double)partialTicks, this.prevPosZ, this.getPosZ());
         return new Vec3d(d0, d1, d2);
      }
   }

   public RayTraceResult pick(double p_213324_1_, float p_213324_3_, boolean p_213324_4_) {
      Vec3d vec3d = this.getEyePosition(p_213324_3_);
      Vec3d vec3d1 = this.getLook(p_213324_3_);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * p_213324_1_, vec3d1.y * p_213324_1_, vec3d1.z * p_213324_1_);
      return this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, p_213324_4_ ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return false;
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return false;
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)p_191956_1_, this, p_191956_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      double d0 = this.getPosX() - x;
      double d1 = this.getPosY() - y;
      double d2 = this.getPosZ() - z;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      return this.isInRangeToRenderDist(d3);
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * renderDistanceWeight;
      return distance < d0 * d0;
   }

   /**
    * Writes this entity to NBT, unless it has been removed. Also writes this entity's passengers, and the entity type
    * ID (so the produced NBT is sufficient to recreate the entity).
    *  
    * Generally, {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} should be used instead of this method.
    *  
    * @return True if the entity was written (and the passed compound should be saved); false if the entity was not
    * written.
    */
   public boolean writeUnlessRemoved(CompoundNBT compound) {
      String s = this.getEntityString();
      if (!this.removed && s != null) {
         compound.putString("id", s);
         this.writeWithoutTypeId(compound);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Writes this entity to NBT, unless it has been removed or it is a passenger. Also writes this entity's passengers,
    * and the entity type ID (so the produced NBT is sufficient to recreate the entity).
    * To always write the entity, use {@link #writeWithoutTypeId}.
    *  
    * @return True if the entity was written (and the passed compound should be saved); false if the entity was not
    * written.
    */
   public boolean writeUnlessPassenger(CompoundNBT compound) {
      return this.isPassenger() ? false : this.writeUnlessRemoved(compound);
   }

   /**
    * Writes this entity, including passengers, to NBT, regardless as to whether or not it is removed or a passenger.
    * Does <b>not</b> include the entity's type ID, so the NBT is insufficient to recreate the entity using {@link
    * AnvilChunkLoader#readWorldEntity}. Use {@link #writeUnlessPassenger} for that purpose.
    */
   public CompoundNBT writeWithoutTypeId(CompoundNBT compound) {
      try {
         compound.put("Pos", this.newDoubleNBTList(this.getPosX(), this.getPosY(), this.getPosZ()));
         Vec3d vec3d = this.getMotion();
         compound.put("Motion", this.newDoubleNBTList(vec3d.x, vec3d.y, vec3d.z));
         compound.put("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         compound.putFloat("FallDistance", this.fallDistance);
         compound.putShort("Fire", (short)this.fire);
         compound.putShort("Air", (short)this.getAir());
         compound.putBoolean("OnGround", this.onGround);
         compound.putInt("Dimension", this.dimension.getId());
         compound.putBoolean("Invulnerable", this.invulnerable);
         compound.putInt("PortalCooldown", this.timeUntilPortal);
         compound.putUniqueId("UUID", this.getUniqueID());
         ITextComponent itextcomponent = this.getCustomName();
         if (itextcomponent != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
         }

         if (this.isCustomNameVisible()) {
            compound.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            compound.putBoolean("Silent", this.isSilent());
         }

         if (this.hasNoGravity()) {
            compound.putBoolean("NoGravity", this.hasNoGravity());
         }

         if (this.glowing) {
            compound.putBoolean("Glowing", this.glowing);
         }
         compound.putBoolean("CanUpdate", canUpdate);

         if (!this.tags.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for(String s : this.tags) {
               listnbt.add(StringNBT.valueOf(s));
            }

            compound.put("Tags", listnbt);
         }

         CompoundNBT caps = serializeCaps();
         if (caps != null) compound.put("ForgeCaps", caps);
         if (persistentData != null) compound.put("ForgeData", persistentData);

         this.writeAdditional(compound);
         if (this.isBeingRidden()) {
            ListNBT listnbt1 = new ListNBT();

            for(Entity entity : this.getPassengers()) {
               CompoundNBT compoundnbt = new CompoundNBT();
               if (entity.writeUnlessRemoved(compoundnbt)) {
                  listnbt1.add(compoundnbt);
               }
            }

            if (!listnbt1.isEmpty()) {
               compound.put("Passengers", listnbt1);
            }
         }

         return compound;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being saved");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   /**
    * Reads the entity from NBT (calls an abstract helper method to read specialized data)
    */
   public void read(CompoundNBT compound) {
      try {
         ListNBT listnbt = compound.getList("Pos", 6);
         ListNBT listnbt2 = compound.getList("Motion", 6);
         ListNBT listnbt3 = compound.getList("Rotation", 5);
         double d0 = listnbt2.getDouble(0);
         double d1 = listnbt2.getDouble(1);
         double d2 = listnbt2.getDouble(2);
         this.setMotion(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
         this.forceSetPosition(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
         this.rotationYaw = listnbt3.getFloat(0);
         this.rotationPitch = listnbt3.getFloat(1);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
         this.setRotationYawHead(this.rotationYaw);
         this.setRenderYawOffset(this.rotationYaw);
         this.fallDistance = compound.getFloat("FallDistance");
         this.fire = compound.getShort("Fire");
         this.setAir(compound.getShort("Air"));
         this.onGround = compound.getBoolean("OnGround");
         if (compound.contains("Dimension")) {
            this.dimension = DimensionType.getById(compound.getInt("Dimension"));
         }

         this.invulnerable = compound.getBoolean("Invulnerable");
         this.timeUntilPortal = compound.getInt("PortalCooldown");
         if (compound.hasUniqueId("UUID")) {
            this.entityUniqueID = compound.getUniqueId("UUID");
            this.cachedUniqueIdString = this.entityUniqueID.toString();
         }

         if (Double.isFinite(this.getPosX()) && Double.isFinite(this.getPosY()) && Double.isFinite(this.getPosZ())) {
            if (Double.isFinite((double)this.rotationYaw) && Double.isFinite((double)this.rotationPitch)) {
               this.recenterBoundingBox();
               this.setRotation(this.rotationYaw, this.rotationPitch);
               if (compound.contains("CustomName", 8)) {
                  this.setCustomName(ITextComponent.Serializer.fromJson(compound.getString("CustomName")));
               }

               this.setCustomNameVisible(compound.getBoolean("CustomNameVisible"));
               this.setSilent(compound.getBoolean("Silent"));
               this.setNoGravity(compound.getBoolean("NoGravity"));
               this.setGlowing(compound.getBoolean("Glowing"));
               if (compound.contains("ForgeData", 10)) persistentData = compound.getCompound("ForgeData");
               if (compound.contains("CanUpdate", 99)) this.canUpdate(compound.getBoolean("CanUpdate"));
               if (compound.contains("ForgeCaps", 10)) deserializeCaps(compound.getCompound("ForgeCaps"));
               if (compound.contains("Tags", 9)) {
                  this.tags.clear();
                  ListNBT listnbt1 = compound.getList("Tags", 8);
                  int i = Math.min(listnbt1.size(), 1024);

                  for(int j = 0; j < i; ++j) {
                     this.tags.add(listnbt1.getString(j));
                  }
               }

               this.readAdditional(compound);
               if (this.shouldSetPosAfterLoading()) {
                  this.recenterBoundingBox();
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being loaded");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   protected boolean shouldSetPosAfterLoading() {
      return true;
   }

   /**
    * Returns the string that identifies this Entity's class
    */
   @Nullable
   public final String getEntityString() {
      EntityType<?> entitytype = this.getType();
      ResourceLocation resourcelocation = EntityType.getKey(entitytype);
      return entitytype.isSerializable() && resourcelocation != null ? resourcelocation.toString() : null;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected abstract void readAdditional(CompoundNBT compound);

   protected abstract void writeAdditional(CompoundNBT compound);

   /**
    * creates a NBT list from the array of doubles passed to this function
    */
   protected ListNBT newDoubleNBTList(double... numbers) {
      ListNBT listnbt = new ListNBT();

      for(double d0 : numbers) {
         listnbt.add(DoubleNBT.valueOf(d0));
      }

      return listnbt;
   }

   /**
    * Returns a new NBTTagList filled with the specified floats
    */
   protected ListNBT newFloatNBTList(float... numbers) {
      ListNBT listnbt = new ListNBT();

      for(float f : numbers) {
         listnbt.add(FloatNBT.valueOf(f));
      }

      return listnbt;
   }

   @Nullable
   public ItemEntity entityDropItem(IItemProvider itemIn) {
      return this.entityDropItem(itemIn, 0);
   }

   @Nullable
   public ItemEntity entityDropItem(IItemProvider itemIn, int offset) {
      return this.entityDropItem(new ItemStack(itemIn), (float)offset);
   }

   @Nullable
   public ItemEntity entityDropItem(ItemStack stack) {
      return this.entityDropItem(stack, 0.0F);
   }

   /**
    * Drops an item at the position of the entity.
    */
   @Nullable
   public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
      if (stack.isEmpty()) {
         return null;
      } else if (this.world.isRemote) {
         return null;
      } else {
         ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY() + (double)offsetY, this.getPosZ(), stack);
         itementity.setDefaultPickupDelay();
         if (captureDrops() != null) captureDrops().add(itementity);
         else
         this.world.addEntity(itementity);
         return itementity;
      }
   }

   /**
    * Returns true if the entity has not been {@link #removed}.
    */
   public boolean isAlive() {
      return !this.removed;
   }

   /**
    * Checks if this entity is inside of an opaque block
    */
   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      } else {
         try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain()) {
            for(int i = 0; i < 8; ++i) {
               int j = MathHelper.floor(this.getPosY() + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.eyeHeight);
               int k = MathHelper.floor(this.getPosX() + (double)(((float)((i >> 1) % 2) - 0.5F) * this.size.width * 0.8F));
               int l = MathHelper.floor(this.getPosZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * this.size.width * 0.8F));
               if (blockpos$pooledmutable.getX() != k || blockpos$pooledmutable.getY() != j || blockpos$pooledmutable.getZ() != l) {
                  blockpos$pooledmutable.setPos(k, j, l);
                  if (this.world.getBlockState(blockpos$pooledmutable).isSuffocating(this.world, blockpos$pooledmutable)) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }

            return false;
         }
      }
   }

   public boolean processInitialInteract(PlayerEntity player, Hand hand) {
      return false;
   }

   /**
    * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
    * pushable on contact, like boats or minecarts.
    */
   @Nullable
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return null;
   }

   /**
    * Handles updating while riding another entity
    */
   public void updateRidden() {
      this.setMotion(Vec3d.ZERO);
      if (canUpdate())
      this.tick();
      if (this.isPassenger()) {
         this.getRidingEntity().updatePassenger(this);
      }
   }

   public void updatePassenger(Entity passenger) {
      this.positionRider(passenger, Entity::setPosition);
   }

   public void positionRider(Entity p_226266_1_, Entity.IMoveCallback p_226266_2_) {
      if (this.isPassenger(p_226266_1_)) {
         p_226266_2_.accept(p_226266_1_, this.getPosX(), this.getPosY() + this.getMountedYOffset() + p_226266_1_.getYOffset(), this.getPosZ());
      }
   }

   /**
    * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
    */
   @OnlyIn(Dist.CLIENT)
   public void applyOrientationToEntity(Entity entityToUpdate) {
   }

   /**
    * Returns the Y Offset of this entity.
    */
   public double getYOffset() {
      return 0.0D;
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return (double)this.size.height * 0.75D;
   }

   public boolean startRiding(Entity entityIn) {
      return this.startRiding(entityIn, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLiving() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity entityIn, boolean force) {
      for(Entity entity = entityIn; entity.ridingEntity != null; entity = entity.ridingEntity) {
         if (entity.ridingEntity == this) {
            return false;
         }
      }

      if (!net.minecraftforge.event.ForgeEventFactory.canMountEntity(this, entityIn, true)) return false;
      if (force || this.canBeRidden(entityIn) && entityIn.canFitPassenger(this)) {
         if (this.isPassenger()) {
            this.stopRiding();
         }

         this.ridingEntity = entityIn;
         this.ridingEntity.addPassenger(this);
         return true;
      } else {
         return false;
      }
   }

   protected boolean canBeRidden(Entity entityIn) {
      return this.rideCooldown <= 0;
   }

   protected boolean isPoseClear(Pose p_213298_1_) {
      return this.world.hasNoCollisions(this, this.getBoundingBox(p_213298_1_));
   }

   /**
    * Dismounts all entities riding this entity from this entity.
    */
   public void removePassengers() {
      for(int i = this.passengers.size() - 1; i >= 0; --i) {
         this.passengers.get(i).stopRiding();
      }

   }

   /**
    * Dismounts this entity from the entity it is riding.
    */
   public void stopRiding() {
      if (this.ridingEntity != null) {
         Entity entity = this.ridingEntity;
         if (!net.minecraftforge.event.ForgeEventFactory.canMountEntity(this, entity, false)) return;
         this.ridingEntity = null;
         entity.removePassenger(this);
      }

   }

   protected void addPassenger(Entity passenger) {
      if (passenger.getRidingEntity() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (!this.world.isRemote && passenger instanceof PlayerEntity && !(this.getControllingPassenger() instanceof PlayerEntity)) {
            this.passengers.add(0, passenger);
         } else {
            this.passengers.add(passenger);
         }

      }
   }

   protected void removePassenger(Entity passenger) {
      if (passenger.getRidingEntity() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         this.passengers.remove(passenger);
         passenger.rideCooldown = 60;
      }
   }

   protected boolean canFitPassenger(Entity passenger) {
      return this.getPassengers().size() < 1;
   }

   /**
    * Sets a target for the client to interpolate towards over the next few ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
   }

   @OnlyIn(Dist.CLIENT)
   public void setHeadRotation(float yaw, int pitch) {
      this.setRotationYawHead(yaw);
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   /**
    * returns a (normalized) vector of where this entity is looking
    */
   public Vec3d getLookVec() {
      return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
   }

   /**
    * returns the Entity's pitch and yaw as a Vec2f
    */
   public Vec2f getPitchYaw() {
      return new Vec2f(this.rotationPitch, this.rotationYaw);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getForward() {
      return Vec3d.fromPitchYaw(this.getPitchYaw());
   }

   /**
    * Marks the entity as being inside a portal, activating teleportation logic in onEntityUpdate() in the following
    * tick(s).
    */
   public void setPortal(BlockPos pos) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.world.isRemote && !pos.equals(this.lastPortalPos)) {
            this.lastPortalPos = new BlockPos(pos);
            NetherPortalBlock netherportalblock = (NetherPortalBlock)Blocks.NETHER_PORTAL;
            BlockPattern.PatternHelper blockpattern$patternhelper = NetherPortalBlock.createPatternHelper(this.world, this.lastPortalPos);
            double d0 = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? (double)blockpattern$patternhelper.getFrontTopLeft().getZ() : (double)blockpattern$patternhelper.getFrontTopLeft().getX();
            double d1 = Math.abs(MathHelper.pct((blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? this.getPosZ() : this.getPosX()) - (double)(blockpattern$patternhelper.getForwards().rotateY().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double)blockpattern$patternhelper.getWidth()));
            double d2 = MathHelper.pct(this.getPosY() - 1.0D, (double)blockpattern$patternhelper.getFrontTopLeft().getY(), (double)(blockpattern$patternhelper.getFrontTopLeft().getY() - blockpattern$patternhelper.getHeight()));
            this.lastPortalVec = new Vec3d(d1, d2, 0.0D);
            this.teleportDirection = blockpattern$patternhelper.getForwards();
         }

         this.inPortal = true;
      }
   }

   protected void updatePortal() {
      if (this.world instanceof ServerWorld) {
         int i = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (this.world.getServer().getAllowNether() && !this.isPassenger() && this.portalCounter++ >= i) {
               this.world.getProfiler().startSection("portal");
               this.portalCounter = i;
               this.timeUntilPortal = this.getPortalCooldown();
               this.changeDimension(this.world.dimension.getType() == DimensionType.THE_NETHER ? DimensionType.OVERWORLD : DimensionType.THE_NETHER);
               this.world.getProfiler().endSection();
            }

            this.inPortal = false;
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         this.decrementTimeUntilPortal();
      }
   }

   /**
    * Return the amount of cooldown before this entity can use a portal again.
    */
   public int getPortalCooldown() {
      return 300;
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.setMotion(x, y, z);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      switch(id) {
      case 53:
         HoneyBlock.func_226931_a_(this);
      default:
      }
   }

   /**
    * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
    */
   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return EMPTY_EQUIPMENT;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return EMPTY_EQUIPMENT;
   }

   public Iterable<ItemStack> getEquipmentAndArmor() {
      return Iterables.concat(this.getHeldEquipment(), this.getArmorInventoryList());
   }

   public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
   }

   /**
    * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
    */
   public boolean isBurning() {
      boolean flag = this.world != null && this.world.isRemote;
      return !this.isImmuneToFire() && (this.fire > 0 || flag && this.getFlag(0));
   }

   public boolean isPassenger() {
      return this.getRidingEntity() != null;
   }

   /**
    * If at least 1 entity is riding this one
    */
   public boolean isBeingRidden() {
      return !this.getPassengers().isEmpty();
   }

   @Deprecated //Forge: Use rider sensitive version
   public boolean canBeRiddenInWater() {
      return true;
   }

   public void setSneaking(boolean keyDownIn) {
      this.setFlag(1, keyDownIn);
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public boolean isSteppingCarefully() {
      return this.isSneaking();
   }

   public boolean isSuppressingBounce() {
      return this.isSneaking();
   }

   public boolean isDiscrete() {
      return this.isSneaking();
   }

   public boolean isDescending() {
      return this.isSneaking();
   }

   public boolean isCrouching() {
      return this.getPose() == Pose.CROUCHING;
   }

   /**
    * Get if the Entity is sprinting.
    */
   public boolean isSprinting() {
      return this.getFlag(3);
   }

   /**
    * Set sprinting switch for Entity.
    */
   public void setSprinting(boolean sprinting) {
      this.setFlag(3, sprinting);
   }

   public boolean isSwimming() {
      return this.getFlag(4);
   }

   public boolean isActualySwimming() {
      return this.getPose() == Pose.SWIMMING;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVisuallySwimming() {
      return this.isActualySwimming() && !this.isInWater();
   }

   public void setSwimming(boolean p_204711_1_) {
      this.setFlag(4, p_204711_1_);
   }

   public boolean isGlowing() {
      return this.glowing || this.world.isRemote && this.getFlag(6);
   }

   public void setGlowing(boolean glowingIn) {
      this.glowing = glowingIn;
      if (!this.world.isRemote) {
         this.setFlag(6, this.glowing);
      }

   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   /**
    * Only used by renderer in EntityLivingBase subclasses.
    * Determines if an entity is visible or not to a specific player, if the entity is normally invisible.
    * For EntityLivingBase subclasses, returning false when invisible will render the entity semi-transparent.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInvisibleToPlayer(PlayerEntity player) {
      if (player.isSpectator()) {
         return false;
      } else {
         Team team = this.getTeam();
         return team != null && player != null && player.getTeam() == team && team.getSeeFriendlyInvisiblesEnabled() ? false : this.isInvisible();
      }
   }

   @Nullable
   public Team getTeam() {
      return this.world.getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   /**
    * Returns whether this Entity is on the same team as the given Entity.
    */
   public boolean isOnSameTeam(Entity entityIn) {
      return this.isOnScoreboardTeam(entityIn.getTeam());
   }

   /**
    * Returns whether this Entity is on the given scoreboard team.
    */
   public boolean isOnScoreboardTeam(Team teamIn) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(teamIn) : false;
   }

   public void setInvisible(boolean invisible) {
      this.setFlag(5, invisible);
   }

   /**
    * Returns true if the flag is active for the entity. Known flags: 0: burning; 1: sneaking; 2: unused; 3: sprinting;
    * 4: swimming; 5: invisible; 6: glowing; 7: elytra flying
    */
   protected boolean getFlag(int flag) {
      return (this.dataManager.get(FLAGS) & 1 << flag) != 0;
   }

   /**
    * Enable or disable a entity flag, see getEntityFlag to read the know flags.
    */
   protected void setFlag(int flag, boolean set) {
      byte b0 = this.dataManager.get(FLAGS);
      if (set) {
         this.dataManager.set(FLAGS, (byte)(b0 | 1 << flag));
      } else {
         this.dataManager.set(FLAGS, (byte)(b0 & ~(1 << flag)));
      }

   }

   public int getMaxAir() {
      return 300;
   }

   public int getAir() {
      return this.dataManager.get(AIR);
   }

   public void setAir(int air) {
      this.dataManager.set(AIR, air);
   }

   /**
    * Called when a lightning bolt hits the entity.
    */
   public void onStruckByLightning(LightningBoltEntity lightningBolt) {
      ++this.fire;
      if (this.fire == 0) {
         this.setFire(8);
      }

      this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 5.0F);
   }

   public void onEnterBubbleColumnWithAirAbove(boolean downwards) {
      Vec3d vec3d = this.getMotion();
      double d0;
      if (downwards) {
         d0 = Math.max(-0.9D, vec3d.y - 0.03D);
      } else {
         d0 = Math.min(1.8D, vec3d.y + 0.1D);
      }

      this.setMotion(vec3d.x, d0, vec3d.z);
   }

   public void onEnterBubbleColumn(boolean downwards) {
      Vec3d vec3d = this.getMotion();
      double d0;
      if (downwards) {
         d0 = Math.max(-0.3D, vec3d.y - 0.03D);
      } else {
         d0 = Math.min(0.7D, vec3d.y + 0.06D);
      }

      this.setMotion(vec3d.x, d0, vec3d.z);
      this.fallDistance = 0.0F;
   }

   /**
    * This method gets called when the entity kills another one.
    */
   public void onKillEntity(LivingEntity entityLivingIn) {
   }

   protected void pushOutOfBlocks(double x, double y, double z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      Vec3d vec3d = new Vec3d(x - (double)blockpos.getX(), y - (double)blockpos.getY(), z - (double)blockpos.getZ());
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      Direction direction = Direction.UP;
      double d0 = Double.MAX_VALUE;

      for(Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
         blockpos$mutable.setPos(blockpos).move(direction1);
         if (!this.world.getBlockState(blockpos$mutable).isCollisionShapeOpaque(this.world, blockpos$mutable)) {
            double d1 = vec3d.getCoordinate(direction1.getAxis());
            double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
            if (d2 < d0) {
               d0 = d2;
               direction = direction1;
            }
         }
      }

      float f = this.rand.nextFloat() * 0.2F + 0.1F;
      float f1 = (float)direction.getAxisDirection().getOffset();
      Vec3d vec3d1 = this.getMotion().scale(0.75D);
      if (direction.getAxis() == Direction.Axis.X) {
         this.setMotion((double)(f1 * f), vec3d1.y, vec3d1.z);
      } else if (direction.getAxis() == Direction.Axis.Y) {
         this.setMotion(vec3d1.x, (double)(f1 * f), vec3d1.z);
      } else if (direction.getAxis() == Direction.Axis.Z) {
         this.setMotion(vec3d1.x, vec3d1.y, (double)(f1 * f));
      }

   }

   public void setMotionMultiplier(BlockState state, Vec3d motionMultiplierIn) {
      this.fallDistance = 0.0F;
      this.motionMultiplier = motionMultiplierIn;
   }

   private static void removeClickEvents(ITextComponent p_207712_0_) {
      p_207712_0_.applyTextStyle((p_213318_0_) -> {
         p_213318_0_.setClickEvent((ClickEvent)null);
      }).getSiblings().forEach(Entity::removeClickEvents);
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         ITextComponent itextcomponent1 = itextcomponent.deepCopy();
         removeClickEvents(itextcomponent1);
         return itextcomponent1;
      } else {
         return this.getProfessionName();
      }
   }

   protected ITextComponent getProfessionName() {
      return this.getType().getName(); // Forge: Use getter to allow overriding by mods
   }

   /**
    * Returns true if Entity argument is equal to this Entity
    */
   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   /**
    * Sets the head's yaw rotation of the entity.
    */
   public void setRotationYawHead(float rotation) {
   }

   /**
    * Set the render yaw offset
    */
   public void setRenderYawOffset(float offset) {
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return true;
   }

   /**
    * Called when a player attacks an entity. If this returns true the attack will not happen.
    */
   public boolean hitByEntity(Entity entityIn) {
      return false;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getUnformattedComponentText(), this.entityId, this.world == null ? "~NULL~" : this.world.getWorldInfo().getWorldName(), this.getPosX(), this.getPosY(), this.getPosZ());
   }

   /**
    * Returns whether this Entity is invulnerable to the given DamageSource.
    */
   public boolean isInvulnerableTo(DamageSource source) {
      return this.invulnerable && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer();
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   /**
    * Sets whether this Entity is invulnerable.
    */
   public void setInvulnerable(boolean isInvulnerable) {
      this.invulnerable = isInvulnerable;
   }

   /**
    * Sets this entity's location and angles to the location and angles of the passed in entity.
    */
   public void copyLocationAndAnglesFrom(Entity entityIn) {
      this.setLocationAndAngles(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationYaw, entityIn.rotationPitch);
   }

   /**
    * Prepares this entity in new dimension by copying NBT data from entity in old dimension
    */
   public void copyDataFromOld(Entity entityIn) {
      CompoundNBT compoundnbt = entityIn.writeWithoutTypeId(new CompoundNBT());
      compoundnbt.remove("Dimension");
      this.read(compoundnbt);
      this.timeUntilPortal = entityIn.timeUntilPortal;
      this.lastPortalPos = entityIn.lastPortalPos;
      this.lastPortalVec = entityIn.lastPortalVec;
      this.teleportDirection = entityIn.teleportDirection;
   }

   @Nullable
   public Entity changeDimension(DimensionType destination) {
      return this.changeDimension(destination, getServer().getWorld(destination).getDefaultTeleporter());
   }
   @Nullable
   public Entity changeDimension(DimensionType destination, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, destination)) return null;
      if (!this.world.isRemote && !this.removed) {
         this.world.getProfiler().startSection("changeDimension");
         MinecraftServer minecraftserver = this.getServer();
         DimensionType dimensiontype = this.dimension;
         ServerWorld serverworld = minecraftserver.getWorld(dimensiontype);
         ServerWorld serverworld1 = minecraftserver.getWorld(destination);
         this.dimension = destination;
         this.detach();
         this.world.getProfiler().startSection("reposition");
         Entity transportedEntity = teleporter.placeEntity(this, serverworld, serverworld1, this.rotationYaw, spawnPortal -> { //Forge: Start vanilla logic
         Vec3d vec3d = this.getMotion();
         float f = 0.0F;
         BlockPos blockpos;
         if (dimensiontype == DimensionType.THE_END && destination == DimensionType.OVERWORLD) {
            blockpos = serverworld1.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverworld1.getSpawnPoint());
         } else if (destination == DimensionType.THE_END) {
            blockpos = serverworld1.getSpawnCoordinate();
         } else {
            double movementFactor = serverworld.getDimension().getMovementFactor() / serverworld1.getDimension().getMovementFactor();
            double d0 = this.getPosX() * movementFactor;
            double d1 = this.getPosZ() * movementFactor;

            double d3 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minX() + 16.0D);
            double d4 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minZ() + 16.0D);
            double d5 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxX() - 16.0D);
            double d6 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxZ() - 16.0D);
            d0 = MathHelper.clamp(d0, d3, d5);
            d1 = MathHelper.clamp(d1, d4, d6);
            Vec3d vec3d1 = this.getLastPortalVec();
            blockpos = new BlockPos(d0, this.getPosY(), d1);
            if (spawnPortal) {
            BlockPattern.PortalInfo blockpattern$portalinfo = serverworld1.getDefaultTeleporter().placeInExistingPortal(blockpos, vec3d, this.getTeleportDirection(), vec3d1.x, vec3d1.y, this instanceof PlayerEntity);
            if (blockpattern$portalinfo == null) {
               return null;
            }

            blockpos = new BlockPos(blockpattern$portalinfo.pos);
            vec3d = blockpattern$portalinfo.motion;
            f = (float)blockpattern$portalinfo.rotation;
            }
         }

         this.world.getProfiler().endStartSection("reloading");
         Entity entity = this.getType().create(serverworld1);
         if (entity != null) {
            entity.copyDataFromOld(this);
            entity.moveToBlockPosAndAngles(blockpos, entity.rotationYaw + f, entity.rotationPitch);
            entity.setMotion(vec3d);
            serverworld1.addFromAnotherDimension(entity);
         }
            return entity;
         });//Forge: End vanilla logic

         this.remove(false);
         this.world.getProfiler().endSection();
         serverworld.resetUpdateEntityTick();
         serverworld1.resetUpdateEntityTick();
         this.world.getProfiler().endSection();
         return transportedEntity;
      } else {
         return null;
      }
   }

   /**
    * Returns false if this Entity is a boss, true otherwise.
    */
   public boolean isNonBoss() {
      return true;
   }

   /**
    * Explosion resistance of a block relative to this entity
    */
   public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, IFluidState p_180428_5_, float p_180428_6_) {
      return p_180428_6_;
   }

   public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, float p_174816_5_) {
      return true;
   }

   /**
    * The maximum height from where the entity is alowed to jump (used in pathfinder)
    */
   public int getMaxFallHeight() {
      return 3;
   }

   public Vec3d getLastPortalVec() {
      if (this.lastPortalVec == null) return Vec3d.ZERO;
      return this.lastPortalVec;
   }

   public Direction getTeleportDirection() {
      if (this.teleportDirection == null) return Direction.NORTH;
      return this.teleportDirection;
   }

   /**
    * Return whether this entity should NOT trigger a pressure plate or a tripwire.
    */
   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public void fillCrashReport(CrashReportCategory category) {
      category.addDetail("Entity Type", () -> {
         return EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      category.addDetail("Entity ID", this.entityId);
      category.addDetail("Entity Name", () -> {
         return this.getName().getString();
      });
      category.addDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getPosX(), this.getPosY(), this.getPosZ()));
      category.addDetail("Entity's Block location", CrashReportCategory.getCoordinateInfo(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosY()), MathHelper.floor(this.getPosZ())));
      Vec3d vec3d = this.getMotion();
      category.addDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
      category.addDetail("Entity's Passengers", () -> {
         return this.getPassengers().toString();
      });
      category.addDetail("Entity's Vehicle", () -> {
         return this.getRidingEntity().toString();
      });
   }

   /**
    * Return whether this entity should be rendered as on fire.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean canRenderOnFire() {
      return this.isBurning() && !this.isSpectator();
   }

   public void setUniqueId(UUID uniqueIdIn) {
      this.entityUniqueID = uniqueIdIn;
      this.cachedUniqueIdString = this.entityUniqueID.toString();
   }

   /**
    * Returns the UUID of this entity.
    */
   public UUID getUniqueID() {
      return this.entityUniqueID;
   }

   public String getCachedUniqueIdString() {
      return this.cachedUniqueIdString;
   }

   /**
    * Returns a String to use as this entity's name in the scoreboard/entity selector systems
    */
   public String getScoreboardName() {
      return this.cachedUniqueIdString;
   }

   public boolean isPushedByWater() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public static double getRenderDistanceWeight() {
      return renderDistanceWeight;
   }

   @OnlyIn(Dist.CLIENT)
   public static void setRenderDistanceWeight(double renderDistWeight) {
      renderDistanceWeight = renderDistWeight;
   }

   public ITextComponent getDisplayName() {
      return ScorePlayerTeam.formatMemberName(this.getTeam(), this.getName()).applyTextStyle((p_211516_1_) -> {
         p_211516_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
      });
   }

   public void setCustomName(@Nullable ITextComponent name) {
      this.dataManager.set(CUSTOM_NAME, Optional.ofNullable(name));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.dataManager.get(CUSTOM_NAME).orElse((ITextComponent)null);
   }

   public boolean hasCustomName() {
      return this.dataManager.get(CUSTOM_NAME).isPresent();
   }

   public void setCustomNameVisible(boolean alwaysRenderNameTag) {
      this.dataManager.set(CUSTOM_NAME_VISIBLE, alwaysRenderNameTag);
   }

   public boolean isCustomNameVisible() {
      return this.dataManager.get(CUSTOM_NAME_VISIBLE);
   }

   /**
    * Teleports the entity, forcing the destination to stay loaded for a short time
    */
   public final void teleportKeepLoaded(double p_223102_1_, double p_223102_3_, double p_223102_5_) {
      if (this.world instanceof ServerWorld) {
         ChunkPos chunkpos = new ChunkPos(new BlockPos(p_223102_1_, p_223102_3_, p_223102_5_));
         ((ServerWorld)this.world).getChunkProvider().registerTicket(TicketType.POST_TELEPORT, chunkpos, 0, this.getEntityId());
         this.world.getChunk(chunkpos.x, chunkpos.z);
         this.setPositionAndUpdate(p_223102_1_, p_223102_3_, p_223102_5_);
      }
   }

   /**
    * Sets the position of the entity and updates the 'last' variables
    */
   public void setPositionAndUpdate(double x, double y, double z) {
      if (this.world instanceof ServerWorld) {
         ServerWorld serverworld = (ServerWorld)this.world;
         this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
         this.getSelfAndPassengers().forEach((p_226267_1_) -> {
            serverworld.chunkCheck(p_226267_1_);
            p_226267_1_.isPositionDirty = true;
            p_226267_1_.repositionDirectPassengers(Entity::moveForced);
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getAlwaysRenderNameTagForRender() {
      return this.isCustomNameVisible();
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (POSE.equals(key)) {
         this.recalculateSize();
      }

   }

   public void recalculateSize() {
      EntitySize entitysize = this.size;
      Pose pose = this.getPose();
      EntitySize entitysize1 = this.getSize(pose);
      this.size = entitysize1;
      this.eyeHeight = getEyeHeightForge(pose, entitysize1);
      if (entitysize1.width < entitysize.width) {
         double d0 = (double)entitysize1.width / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(this.getPosX() - d0, this.getPosY(), this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + (double)entitysize1.height, this.getPosZ() + d0));
      } else {
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         this.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width, axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
         if (entitysize1.width > entitysize.width && !this.firstUpdate && !this.world.isRemote) {
            float f = entitysize.width - entitysize1.width;
            this.move(MoverType.SELF, new Vec3d((double)f, 0.0D, (double)f));
         }

      }
   }

   /**
    * Gets the horizontal facing direction of this Entity.
    */
   public Direction getHorizontalFacing() {
      return Direction.fromAngle((double)this.rotationYaw);
   }

   /**
    * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
    */
   public Direction getAdjustedHorizontalFacing() {
      return this.getHorizontalFacing();
   }

   protected HoverEvent getHoverEvent() {
      CompoundNBT compoundnbt = new CompoundNBT();
      ResourceLocation resourcelocation = EntityType.getKey(this.getType());
      compoundnbt.putString("id", this.getCachedUniqueIdString());
      if (resourcelocation != null) {
         compoundnbt.putString("type", resourcelocation.toString());
      }

      compoundnbt.putString("name", ITextComponent.Serializer.toJson(this.getName()));
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new StringTextComponent(compoundnbt.toString()));
   }

   public boolean isSpectatedByPlayer(ServerPlayerEntity player) {
      return true;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   /**
    * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by
    * a minecart, such as a command block).
    */
   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getBoundingBox();
   }

   protected AxisAlignedBB getBoundingBox(Pose p_213321_1_) {
      EntitySize entitysize = this.getSize(p_213321_1_);
      float f = entitysize.width / 2.0F;
      Vec3d vec3d = new Vec3d(this.getPosX() - (double)f, this.getPosY(), this.getPosZ() - (double)f);
      Vec3d vec3d1 = new Vec3d(this.getPosX() + (double)f, this.getPosY() + (double)entitysize.height, this.getPosZ() + (double)f);
      return new AxisAlignedBB(vec3d, vec3d1);
   }

   public void setBoundingBox(AxisAlignedBB bb) {
      this.boundingBox = bb;
   }

   protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return sizeIn.height * 0.85F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getEyeHeight(Pose p_213307_1_) {
      return this.getEyeHeight(p_213307_1_, this.getSize(p_213307_1_));
   }

   public final float getEyeHeight() {
      return this.eyeHeight;
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      return false;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
   }

   /**
    * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
    * the coordinates 0, 0, 0
    */
   public BlockPos getPosition() {
      return new BlockPos(this);
   }

   /**
    * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return 0.0D,
    * 0.0D, 0.0D
    */
   public Vec3d getPositionVector() {
      return this.getPositionVec();
   }

   /**
    * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return the
    * overworld
    */
   public World getEntityWorld() {
      return this.world;
   }

   /**
    * Get the Minecraft server instance
    */
   @Nullable
   public MinecraftServer getServer() {
      return this.world.getServer();
   }

   /**
    * Applies the given player interaction to this Entity.
    */
   public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
      return ActionResultType.PASS;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   protected void applyEnchantments(LivingEntity entityLivingBaseIn, Entity entityIn) {
      if (entityIn instanceof LivingEntity) {
         EnchantmentHelper.applyThornEnchantments((LivingEntity)entityIn, entityLivingBaseIn);
      }

      EnchantmentHelper.applyArthropodEnchantments(entityLivingBaseIn, entityIn);
   }

   /**
    * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in order
    * to view its associated boss bar.
    */
   public void addTrackingPlayer(ServerPlayerEntity player) {
   }

   /**
    * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
    * more information on tracking.
    */
   public void removeTrackingPlayer(ServerPlayerEntity player) {
   }

   /**
    * Transforms the entity's current yaw with the given Rotation and returns it. This does not have a side-effect.
    */
   public float getRotatedYaw(Rotation transformRotation) {
      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(transformRotation) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 270.0F;
      case CLOCKWISE_90:
         return f + 90.0F;
      default:
         return f;
      }
   }

   /**
    * Transforms the entity's current yaw with the given Mirror and returns it. This does not have a side-effect.
    */
   public float getMirroredYaw(Mirror transformMirror) {
      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(transformMirror) {
      case LEFT_RIGHT:
         return -f;
      case FRONT_BACK:
         return 180.0F - f;
      default:
         return f;
      }
   }

   /**
    * Checks if players can use this entity to access operator (permission level 2) commands either directly or
    * indirectly, such as give or setblock. A similar method exists for entities at {@link
    * net.minecraft.tileentity.TileEntity#onlyOpsCanSetNbt()}.<p>For example, {@link
    * net.minecraft.entity.item.EntityMinecartCommandBlock#ignoreItemEntityData() command block minecarts} and {@link
    * net.minecraft.entity.item.EntityMinecartMobSpawner#ignoreItemEntityData() mob spawner minecarts} (spawning command
    * block minecarts or drops) are considered accessible.</p>@return true if this entity offers ways for unauthorized
    * players to use restricted commands
    */
   public boolean ignoreItemEntityData() {
      return false;
   }

   public boolean setPositionNonDirty() {
      boolean flag = this.isPositionDirty;
      this.isPositionDirty = false;
      return flag;
   }

   /**
    * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
    * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
    */
   @Nullable
   public Entity getControllingPassenger() {
      return null;
   }

   public List<Entity> getPassengers() {
      return (List<Entity>)(this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
   }

   public boolean isPassenger(Entity entityIn) {
      for(Entity entity : this.getPassengers()) {
         if (entity.equals(entityIn)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPassenger(Class<? extends Entity> p_205708_1_) {
      for(Entity entity : this.getPassengers()) {
         if (p_205708_1_.isAssignableFrom(entity.getClass())) {
            return true;
         }
      }

      return false;
   }

   /**
    * Recursively collects the passengers of this entity. This differs from getPassengers() in that passengers of
    * passengers are recursively collected.
    */
   public Collection<Entity> getRecursivePassengers() {
      Set<Entity> set = Sets.newHashSet();

      for(Entity entity : this.getPassengers()) {
         set.add(entity);
         entity.getRecursivePassengers(false, set);
      }

      return set;
   }

   public Stream<Entity> getSelfAndPassengers() {
      return Stream.concat(Stream.of(this), this.passengers.stream().flatMap(Entity::getSelfAndPassengers));
   }

   public boolean isOnePlayerRiding() {
      Set<Entity> set = Sets.newHashSet();
      this.getRecursivePassengers(true, set);
      return set.size() == 1;
   }

   private void getRecursivePassengers(boolean playersOnly, Set<Entity> p_200604_2_) {
      for(Entity entity : this.getPassengers()) {
         if (!playersOnly || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
            p_200604_2_.add(entity);
         }

         entity.getRecursivePassengers(playersOnly, p_200604_2_);
      }

   }

   public Entity getLowestRidingEntity() {
      Entity entity;
      for(entity = this; entity.isPassenger(); entity = entity.getRidingEntity()) {
         ;
      }

      return entity;
   }

   public boolean isRidingSameEntity(Entity entityIn) {
      return this.getLowestRidingEntity() == entityIn.getLowestRidingEntity();
   }

   public boolean isRidingOrBeingRiddenBy(Entity entityIn) {
      for(Entity entity : this.getPassengers()) {
         if (entity.equals(entityIn)) {
            return true;
         }

         if (entity.isRidingOrBeingRiddenBy(entityIn)) {
            return true;
         }
      }

      return false;
   }

   public void repositionDirectPassengers(Entity.IMoveCallback p_226265_1_) {
      for(Entity entity : this.passengers) {
         this.positionRider(entity, p_226265_1_);
      }

   }

   public boolean canPassengerSteer() {
      Entity entity = this.getControllingPassenger();
      if (entity instanceof PlayerEntity) {
         return ((PlayerEntity)entity).isUser();
      } else {
         return !this.world.isRemote;
      }
   }

   /**
    * Get entity this is riding
    */
   @Nullable
   public Entity getRidingEntity() {
      return this.ridingEntity;
   }

   public PushReaction getPushReaction() {
      return PushReaction.NORMAL;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   protected int getFireImmuneTicks() {
      return 1;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, this.getPositionVec(), this.getPitchYaw(), this.world instanceof ServerWorld ? (ServerWorld)this.world : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissionLevel(int p_211513_1_) {
      return this.getPermissionLevel() >= p_211513_1_;
   }

   public boolean shouldReceiveFeedback() {
      return this.world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      Vec3d vec3d = p_200602_1_.apply(this);
      double d0 = p_200602_2_.x - vec3d.x;
      double d1 = p_200602_2_.y - vec3d.y;
      double d2 = p_200602_2_.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      this.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      this.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      this.setRotationYawHead(this.rotationYaw);
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
   }

   public boolean handleFluidAcceleration(Tag<Fluid> p_210500_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.minY);
      int l = MathHelper.ceil(axisalignedbb.maxY);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      if (!this.world.isAreaLoaded(i, k, i1, j, l, j1)) {
         return false;
      } else {
         double d0 = 0.0D;
         boolean flag = this.isPushedByWater();
         boolean flag1 = false;
         Vec3d vec3d = Vec3d.ZERO;
         int k1 = 0;

         try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain()) {
            for(int l1 = i; l1 < j; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     blockpos$pooledmutable.setPos(l1, i2, j2);
                     IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutable);
                     if (ifluidstate.isTagged(p_210500_1_)) {
                        double d1 = (double)((float)i2 + ifluidstate.getActualHeight(this.world, blockpos$pooledmutable));
                        if (d1 >= axisalignedbb.minY) {
                           flag1 = true;
                           d0 = Math.max(d1 - axisalignedbb.minY, d0);
                           if (flag) {
                              Vec3d vec3d1 = ifluidstate.getFlow(this.world, blockpos$pooledmutable);
                              if (d0 < 0.4D) {
                                 vec3d1 = vec3d1.scale(d0);
                              }

                              vec3d = vec3d.add(vec3d1);
                              ++k1;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (vec3d.length() > 0.0D) {
            if (k1 > 0) {
               vec3d = vec3d.scale(1.0D / (double)k1);
            }

            if (!(this instanceof PlayerEntity)) {
               vec3d = vec3d.normalize();
            }

            this.setMotion(this.getMotion().add(vec3d.scale(0.014D)));
         }

         this.submergedHeight = d0;
         return flag1;
      }
   }

   public double getSubmergedHeight() {
      return this.submergedHeight;
   }

   public final float getWidth() {
      return this.size.width;
   }

   public final float getHeight() {
      return this.size.height;
   }

   public abstract IPacket<?> createSpawnPacket();

   public EntitySize getSize(Pose poseIn) {
      return this.type.getSize();
   }

   public Vec3d getPositionVec() {
      return new Vec3d(this.posX, this.posY, this.posZ);
   }

   public Vec3d getMotion() {
      return this.motion;
   }

   public void setMotion(Vec3d motionIn) {
      this.motion = motionIn;
   }

   public void setMotion(double x, double y, double z) {
      this.setMotion(new Vec3d(x, y, z));
   }

   public final double getPosX() {
      return this.posX;
   }

   public double getPosXWidth(double p_226275_1_) {
      return this.posX + (double)this.getWidth() * p_226275_1_;
   }

   public double getPosXRandom(double p_226282_1_) {
      return this.getPosXWidth((2.0D * this.rand.nextDouble() - 1.0D) * p_226282_1_);
   }

   public final double getPosY() {
      return this.posY;
   }

   public double getPosYHeight(double p_226283_1_) {
      return this.posY + (double)this.getHeight() * p_226283_1_;
   }

   public double getPosYRandom() {
      return this.getPosYHeight(this.rand.nextDouble());
   }

   public double getPosYEye() {
      return this.posY + (double)this.eyeHeight;
   }

   public final double getPosZ() {
      return this.posZ;
   }

   public double getPosZWidth(double p_226285_1_) {
      return this.posZ + (double)this.getWidth() * p_226285_1_;
   }

   public double getPosZRandom(double p_226287_1_) {
      return this.getPosZWidth((2.0D * this.rand.nextDouble() - 1.0D) * p_226287_1_);
   }

   /**
    * Directly updates the {@link #posX}, {@link posY}, and {@link posZ} fields, without performing any collision
    * checks, updating the bounding box position, or sending any packets. In general, this is not what you want and
    * {@link #setPosition} is better, as that handles the bounding box.
    */
   public void setRawPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      if (this.isAddedToWorld() && !this.world.isRemote && !this.removed) this.world.getChunk((int) Math.floor(this.posX) >> 4, (int) Math.floor(this.posZ) >> 4); // Forge - ensure target chunk is loaded.
   }

   /**
    * Makes the entity despawn if requirements are reached
    */
   public void checkDespawn() {
   }

   public void moveForced(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.setLocationAndAngles(p_225653_1_, p_225653_3_, p_225653_5_, this.rotationYaw, this.rotationPitch);
   }

   @FunctionalInterface
   public interface IMoveCallback {
      void accept(Entity p_accept_1_, double p_accept_2_, double p_accept_4_, double p_accept_6_);
   }

   /* ================================== Forge Start =====================================*/

   private boolean canUpdate = true;
   @Override
   public void canUpdate(boolean value) {
      this.canUpdate = value;
   }
   @Override
   public boolean canUpdate() {
      return this.canUpdate;
   }
   private Collection<ItemEntity> captureDrops = null;
   @Override
   public Collection<ItemEntity> captureDrops() {
      return captureDrops;
   }
   @Override
   public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
      Collection<ItemEntity> ret = captureDrops;
      this.captureDrops = value;
      return ret;
   }
   private CompoundNBT persistentData;
   @Override
   public CompoundNBT getPersistentData() {
      if (persistentData == null)
         persistentData = new CompoundNBT();
      return persistentData;
   }
   @Override
   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return world.rand.nextFloat() < fallDistance - 0.5F
              && this instanceof LivingEntity
              && (this instanceof PlayerEntity || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this))
              && this.getWidth() * this.getWidth() * this.getHeight() > 0.512F;
   }

   /**
    * Internal use for keeping track of entities that are tracked by a world, to
    * allow guarantees that entity position changes will force a chunk load, avoiding
    * potential issues with entity desyncing and bad chunk data.
    */
   private boolean isAddedToWorld;

   @Override
   public final boolean isAddedToWorld() { return this.isAddedToWorld; }

   @Override
   public void onAddedToWorld() { this.isAddedToWorld = true; }

   @Override
   public void onRemovedFromWorld() { this.isAddedToWorld = false; }

   @Override
   public void revive() {
      this.removed = false;
      this.reviveCaps();
   }

   private float getEyeHeightForge(Pose pose, EntitySize size) {
      net.minecraftforge.event.entity.EntityEvent.EyeHeight evt = new net.minecraftforge.event.entity.EntityEvent.EyeHeight(this, pose, size, this.getEyeHeight(pose, size));
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(evt);
      return evt.getNewHeight();
   }
}