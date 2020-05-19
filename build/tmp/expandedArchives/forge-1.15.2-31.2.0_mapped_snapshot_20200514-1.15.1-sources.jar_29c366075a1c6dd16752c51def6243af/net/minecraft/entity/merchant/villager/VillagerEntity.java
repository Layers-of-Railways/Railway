package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTasks;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.GossipManager;
import net.minecraft.village.GossipType;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VillagerEntity extends AbstractVillagerEntity implements IReputationTracking, IVillagerDataHolder {
   private static final DataParameter<VillagerData> VILLAGER_DATA = EntityDataManager.createKey(VillagerEntity.class, DataSerializers.VILLAGER_DATA);
   /** Mapping between valid food items and their respective efficiency values. */
   public static final Map<Item, Integer> FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
   /** Defaults items a villager regardless of its profession can pick up. */
   private static final Set<Item> ALLOWED_INVENTORY_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);
   private int timeUntilReset;
   private boolean leveledUp;
   @Nullable
   private PlayerEntity previousCustomer;
   private byte foodLevel;
   private final GossipManager gossip = new GossipManager();
   private long field_213783_bN;
   private long lastGossipDecay;
   private int xp;
   private long lastRestock;
   private int field_223725_bO;
   private long field_223726_bP;
   private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.field_225462_q, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.field_226332_A_, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME);
   private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_LAST_SEEN);
   public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> field_213774_bB = ImmutableMap.of(MemoryModuleType.HOME, (p_213769_0_, p_213769_1_) -> {
      return p_213769_1_ == PointOfInterestType.HOME;
   }, MemoryModuleType.JOB_SITE, (p_213771_0_, p_213771_1_) -> {
      return p_213771_0_.getVillagerData().getProfession().getPointOfInterest() == p_213771_1_;
   }, MemoryModuleType.MEETING_POINT, (p_213772_0_, p_213772_1_) -> {
      return p_213772_1_ == PointOfInterestType.MEETING;
   });

   public VillagerEntity(EntityType<? extends VillagerEntity> type, World worldIn) {
      this(type, worldIn, IVillagerType.PLAINS);
   }

   public VillagerEntity(EntityType<? extends VillagerEntity> type, World worldIn, IVillagerType villagerType) {
      super(type, worldIn);
      ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
      this.getNavigator().setCanSwim(true);
      this.setCanPickUpLoot(true);
      this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.NONE));
      this.brain = this.createBrain(new Dynamic<>(NBTDynamicOps.INSTANCE, new CompoundNBT()));
   }

   public Brain<VillagerEntity> getBrain() {
      return (Brain<VillagerEntity>) super.getBrain();
   }

   protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
      Brain<VillagerEntity> brain = new Brain<>(MEMORY_TYPES, SENSOR_TYPES, dynamicIn);
      this.initBrain(brain);
      return brain;
   }

   public void resetBrain(ServerWorld serverWorldIn) {
      Brain<VillagerEntity> brain = this.getBrain();
      brain.stopAllTasks(serverWorldIn, this);
      this.brain = brain.copy();
      this.initBrain(this.getBrain());
   }

   private void initBrain(Brain<VillagerEntity> villagerBrain) {
      VillagerProfession villagerprofession = this.getVillagerData().getProfession();
      float f = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
      if (this.isChild()) {
         villagerBrain.setSchedule(Schedule.VILLAGER_BABY);
         villagerBrain.registerActivity(Activity.PLAY, VillagerTasks.play(f));
      } else {
         villagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
         villagerBrain.registerActivity(Activity.WORK, VillagerTasks.work(villagerprofession, f), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
      }

      villagerBrain.registerActivity(Activity.CORE, VillagerTasks.core(villagerprofession, f));
      villagerBrain.registerActivity(Activity.MEET, VillagerTasks.meet(villagerprofession, f), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT)));
      villagerBrain.registerActivity(Activity.REST, VillagerTasks.rest(villagerprofession, f));
      villagerBrain.registerActivity(Activity.IDLE, VillagerTasks.idle(villagerprofession, f));
      villagerBrain.registerActivity(Activity.PANIC, VillagerTasks.panic(villagerprofession, f));
      villagerBrain.registerActivity(Activity.PRE_RAID, VillagerTasks.preRaid(villagerprofession, f));
      villagerBrain.registerActivity(Activity.RAID, VillagerTasks.raid(villagerprofession, f));
      villagerBrain.registerActivity(Activity.HIDE, VillagerTasks.hide(villagerprofession, f));
      villagerBrain.setDefaultActivities(ImmutableSet.of(Activity.CORE));
      villagerBrain.setFallbackActivity(Activity.IDLE);
      villagerBrain.switchTo(Activity.IDLE);
      villagerBrain.updateActivity(this.world.getDayTime(), this.world.getGameTime());
   }

   /**
    * This is called when Entity's growing age timer reaches 0 (negative values are considered as a child, positive as
    * an adult)
    */
   protected void onGrowingAdult() {
      super.onGrowingAdult();
      if (this.world instanceof ServerWorld) {
         this.resetBrain((ServerWorld)this.world);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
   }

   protected void updateAITasks() {
      this.world.getProfiler().startSection("brain");
      this.getBrain().tick((ServerWorld)this.world, this);
      this.world.getProfiler().endSection();
      if (!this.hasCustomer() && this.timeUntilReset > 0) {
         --this.timeUntilReset;
         if (this.timeUntilReset <= 0) {
            if (this.leveledUp) {
               this.levelUp();
               this.leveledUp = false;
            }

            this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, 0));
         }
      }

      if (this.previousCustomer != null && this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).updateReputation(IReputationType.TRADE, this.previousCustomer, this);
         this.world.setEntityState(this, (byte)14);
         this.previousCustomer = null;
      }

      if (!this.isAIDisabled() && this.rand.nextInt(100) == 0) {
         Raid raid = ((ServerWorld)this.world).findRaid(new BlockPos(this));
         if (raid != null && raid.isActive() && !raid.isOver()) {
            this.world.setEntityState(this, (byte)42);
         }
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.hasCustomer()) {
         this.resetCustomer();
      }

      super.updateAITasks();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.getShakeHeadTicks() > 0) {
         this.setShakeHeadTicks(this.getShakeHeadTicks() - 1);
      }

      this.tickGossip();
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      boolean flag = itemstack.getItem() == Items.NAME_TAG;
      if (flag) {
         itemstack.interactWithEntity(player, this, hand);
         return true;
      } else if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer() && !this.isSleeping() && !player.isSecondaryUseActive()) {
         if (this.isChild()) {
            this.shakeHead();
            return super.processInteract(player, hand);
         } else {
            boolean flag1 = this.getOffers().isEmpty();
            if (hand == Hand.MAIN_HAND) {
               if (flag1 && !this.world.isRemote) {
                  this.shakeHead();
               }

               player.addStat(Stats.TALKED_TO_VILLAGER);
            }

            if (flag1) {
               return super.processInteract(player, hand);
            } else {
               if (!this.world.isRemote && !this.offers.isEmpty()) {
                  this.displayMerchantGui(player);
               }

               return true;
            }
         }
      } else {
         return super.processInteract(player, hand);
      }
   }

   private void shakeHead() {
      this.setShakeHeadTicks(40);
      if (!this.world.isRemote()) {
         this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   private void displayMerchantGui(PlayerEntity player) {
      this.recalculateSpecialPricesFor(player);
      this.setCustomer(player);
      this.openMerchantContainer(player, this.getDisplayName(), this.getVillagerData().getLevel());
   }

   public void setCustomer(@Nullable PlayerEntity player) {
      boolean flag = this.getCustomer() != null && player == null;
      super.setCustomer(player);
      if (flag) {
         this.resetCustomer();
      }

   }

   protected void resetCustomer() {
      super.resetCustomer();
      this.resetAllSpecialPrices();
   }

   private void resetAllSpecialPrices() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.resetSpecialPrice();
      }

   }

   public boolean func_223340_ej() {
      return true;
   }

   public void func_213766_ei() {
      this.calculateDemandOfOffers();

      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.resetUses();
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
         this.dropCraftedBread();
      }

      this.lastRestock = this.world.getGameTime();
      ++this.field_223725_bO;
   }

   private boolean hasUsedOffer() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         if (merchantoffer.hasBeenUsed()) {
            return true;
         }
      }

      return false;
   }

   private boolean func_223720_ew() {
      return this.field_223725_bO == 0 || this.field_223725_bO < 2 && this.world.getGameTime() > this.lastRestock + 2400L;
   }

   public boolean func_223721_ek() {
      long i = this.lastRestock + 12000L;
      long j = this.world.getGameTime();
      boolean flag = j > i;
      long k = this.world.getDayTime();
      if (this.field_223726_bP > 0L) {
         long l = this.field_223726_bP / 24000L;
         long i1 = k / 24000L;
         flag |= i1 > l;
      }

      this.field_223726_bP = k;
      if (flag) {
         this.lastRestock = j;
         this.func_223718_eH();
      }

      return this.func_223720_ew() && this.hasUsedOffer();
   }

   private void func_223719_ex() {
      int i = 2 - this.field_223725_bO;
      if (i > 0) {
         for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.resetUses();
         }
      }

      for(int j = 0; j < i; ++j) {
         this.calculateDemandOfOffers();
      }

   }

   private void calculateDemandOfOffers() {
      for(MerchantOffer merchantoffer : this.getOffers()) {
         merchantoffer.calculateDemand();
      }

   }

   private void recalculateSpecialPricesFor(PlayerEntity playerIn) {
      int i = this.getPlayerReputation(playerIn);
      if (i != 0) {
         for(MerchantOffer merchantoffer : this.getOffers()) {
            merchantoffer.increaseSpecialPrice(-MathHelper.floor((float)i * merchantoffer.getPriceMultiplier()));
         }
      }

      if (playerIn.isPotionActive(Effects.HERO_OF_THE_VILLAGE)) {
         EffectInstance effectinstance = playerIn.getActivePotionEffect(Effects.HERO_OF_THE_VILLAGE);
         int k = effectinstance.getAmplifier();

         for(MerchantOffer merchantoffer1 : this.getOffers()) {
            double d0 = 0.3D + 0.0625D * (double)k;
            int j = (int)Math.floor(d0 * (double)merchantoffer1.getBuyingStackFirst().getCount());
            merchantoffer1.increaseSpecialPrice(-Math.max(j, 1));
         }
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VILLAGER_DATA, new VillagerData(IVillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.put("VillagerData", this.getVillagerData().serialize(NBTDynamicOps.INSTANCE));
      compound.putByte("FoodLevel", this.foodLevel);
      compound.put("Gossips", this.gossip.serialize(NBTDynamicOps.INSTANCE).getValue());
      compound.putInt("Xp", this.xp);
      compound.putLong("LastRestock", this.lastRestock);
      compound.putLong("LastGossipDecay", this.lastGossipDecay);
      compound.putInt("RestocksToday", this.field_223725_bO);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("VillagerData", 10)) {
         this.setVillagerData(new VillagerData(new Dynamic<>(NBTDynamicOps.INSTANCE, compound.get("VillagerData"))));
      }

      if (compound.contains("Offers", 10)) {
         this.offers = new MerchantOffers(compound.getCompound("Offers"));
      }

      if (compound.contains("FoodLevel", 1)) {
         this.foodLevel = compound.getByte("FoodLevel");
      }

      ListNBT listnbt = compound.getList("Gossips", 10);
      this.gossip.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, listnbt));
      if (compound.contains("Xp", 3)) {
         this.xp = compound.getInt("Xp");
      }

      this.lastRestock = compound.getLong("LastRestock");
      this.lastGossipDecay = compound.getLong("LastGossipDecay");
      this.setCanPickUpLoot(true);
      if (this.world instanceof ServerWorld) {
         this.resetBrain((ServerWorld)this.world);
      }

      this.field_223725_bO = compound.getInt("RestocksToday");
   }

   public boolean canDespawn(double distanceToClosestPlayer) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return null;
      } else {
         return this.hasCustomer() ? SoundEvents.ENTITY_VILLAGER_TRADE : SoundEvents.ENTITY_VILLAGER_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VILLAGER_DEATH;
   }

   public void playWorkstationSound() {
      SoundEvent soundevent = this.getVillagerData().getProfession().getSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public void setVillagerData(VillagerData p_213753_1_) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != p_213753_1_.getProfession()) {
         this.offers = null;
      }

      this.dataManager.set(VILLAGER_DATA, p_213753_1_);
   }

   public VillagerData getVillagerData() {
      return this.dataManager.get(VILLAGER_DATA);
   }

   protected void onVillagerTrade(MerchantOffer offer) {
      int i = 3 + this.rand.nextInt(4);
      this.xp += offer.getGivenExp();
      this.previousCustomer = this.getCustomer();
      if (this.canLevelUp()) {
         this.timeUntilReset = 40;
         this.leveledUp = true;
         i += 5;
      }

      if (offer.getDoesRewardExp()) {
         this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY() + 0.5D, this.getPosZ(), i));
      }

   }

   /**
    * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
    * change our actual active target (for example if we are currently busy attacking someone else)
    */
   public void setRevengeTarget(@Nullable LivingEntity livingBase) {
      if (livingBase != null && this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).updateReputation(IReputationType.VILLAGER_HURT, livingBase, this);
         if (this.isAlive() && livingBase instanceof PlayerEntity) {
            this.world.setEntityState(this, (byte)13);
         }
      }

      super.setRevengeTarget(livingBase);
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      LOGGER.info("Villager {} died, message: '{}'", this, cause.getDeathMessage(this).getString());
      Entity entity = cause.getTrueSource();
      if (entity != null) {
         this.func_223361_a(entity);
      }

      this.func_213742_a(MemoryModuleType.HOME);
      this.func_213742_a(MemoryModuleType.JOB_SITE);
      this.func_213742_a(MemoryModuleType.MEETING_POINT);
      super.onDeath(cause);
   }

   private void func_223361_a(Entity p_223361_1_) {
      if (this.world instanceof ServerWorld) {
         Optional<List<LivingEntity>> optional = this.brain.getMemory(MemoryModuleType.VISIBLE_MOBS);
         if (optional.isPresent()) {
            ServerWorld serverworld = (ServerWorld)this.world;
            optional.get().stream().filter((p_223349_0_) -> {
               return p_223349_0_ instanceof IReputationTracking;
            }).forEach((p_223342_2_) -> {
               serverworld.updateReputation(IReputationType.VILLAGER_KILLED, p_223361_1_, (IReputationTracking)p_223342_2_);
            });
         }
      }
   }

   public void func_213742_a(MemoryModuleType<GlobalPos> p_213742_1_) {
      if (this.world instanceof ServerWorld) {
         MinecraftServer minecraftserver = ((ServerWorld)this.world).getServer();
         this.brain.getMemory(p_213742_1_).ifPresent((p_213752_3_) -> {
            ServerWorld serverworld = minecraftserver.getWorld(p_213752_3_.getDimension());
            PointOfInterestManager pointofinterestmanager = serverworld.getPointOfInterestManager();
            Optional<PointOfInterestType> optional = pointofinterestmanager.getType(p_213752_3_.getPos());
            BiPredicate<VillagerEntity, PointOfInterestType> bipredicate = field_213774_bB.get(p_213742_1_);
            if (optional.isPresent() && bipredicate.test(this, optional.get())) {
               pointofinterestmanager.release(p_213752_3_.getPos());
               DebugPacketSender.func_218801_c(serverworld, p_213752_3_.getPos());
            }

         });
      }
   }

   public boolean canBreed() {
      return this.foodLevel + this.getFoodValueFromInventory() >= 12 && this.getGrowingAge() == 0;
   }

   private boolean func_223344_ex() {
      return this.foodLevel < 12;
   }

   private void func_213765_en() {
      if (this.func_223344_ex() && this.getFoodValueFromInventory() != 0) {
         for(int i = 0; i < this.getVillagerInventory().getSizeInventory(); ++i) {
            ItemStack itemstack = this.getVillagerInventory().getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               Integer integer = FOOD_VALUES.get(itemstack.getItem());
               if (integer != null) {
                  int j = itemstack.getCount();

                  for(int k = j; k > 0; --k) {
                     this.foodLevel = (byte)(this.foodLevel + integer);
                     this.getVillagerInventory().decrStackSize(i, 1);
                     if (!this.func_223344_ex()) {
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   public int getPlayerReputation(PlayerEntity player) {
      return this.gossip.getReputation(player.getUniqueID(), (p_223103_0_) -> {
         return true;
      });
   }

   private void decrFoodLevel(int qty) {
      this.foodLevel = (byte)(this.foodLevel - qty);
   }

   public void func_223346_ep() {
      this.func_213765_en();
      this.decrFoodLevel(12);
   }

   public void setOffers(MerchantOffers offersIn) {
      this.offers = offersIn;
   }

   private boolean canLevelUp() {
      int i = this.getVillagerData().getLevel();
      return VillagerData.func_221128_d(i) && this.xp >= VillagerData.func_221127_c(i);
   }

   private void levelUp() {
      this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
      this.populateTradeData();
   }

   protected ITextComponent getProfessionName() {
      net.minecraft.util.ResourceLocation profName = this.getVillagerData().getProfession().getRegistryName();
      return new TranslationTextComponent(this.getType().getTranslationKey() + '.' + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + '.' : "") + profName.getPath());
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 12) {
         this.spawnParticles(ParticleTypes.HEART);
      } else if (id == 13) {
         this.spawnParticles(ParticleTypes.ANGRY_VILLAGER);
      } else if (id == 14) {
         this.spawnParticles(ParticleTypes.HAPPY_VILLAGER);
      } else if (id == 42) {
         this.spawnParticles(ParticleTypes.SPLASH);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      if (reason == SpawnReason.BREEDING) {
         this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
      }

      if (reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
         this.setVillagerData(this.getVillagerData().withType(IVillagerType.byBiome(worldIn.getBiome(new BlockPos(this)))));
      }

      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public VillagerEntity createChild(AgeableEntity ageable) {
      double d0 = this.rand.nextDouble();
      IVillagerType ivillagertype;
      if (d0 < 0.5D) {
         ivillagertype = IVillagerType.byBiome(this.world.getBiome(new BlockPos(this)));
      } else if (d0 < 0.75D) {
         ivillagertype = this.getVillagerData().getType();
      } else {
         ivillagertype = ((VillagerEntity)ageable).getVillagerData().getType();
      }

      VillagerEntity villagerentity = new VillagerEntity(EntityType.VILLAGER, this.world, ivillagertype);
      villagerentity.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(villagerentity)), SpawnReason.BREEDING, (ILivingEntityData)null, (CompoundNBT)null);
      return villagerentity;
   }

   /**
    * Called when a lightning bolt hits the entity.
    */
   public void onStruckByLightning(LightningBoltEntity lightningBolt) {
      WitchEntity witchentity = EntityType.WITCH.create(this.world);
      witchentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
      witchentity.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(witchentity)), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
      witchentity.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         witchentity.setCustomName(this.getCustomName());
         witchentity.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.world.addEntity(witchentity);
      this.remove();
   }

   /**
    * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
    * better.
    */
   protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.getItem();
      Item item = itemstack.getItem();
      if (this.func_223717_b(item)) {
         Inventory inventory = this.getVillagerInventory();
         boolean flag = false;

         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack itemstack1 = inventory.getStackInSlot(i);
            if (itemstack1.isEmpty() || itemstack1.getItem() == item && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return;
         }

         int j = inventory.count(item);
         if (j == 256) {
            return;
         }

         if (j > 256) {
            inventory.func_223374_a(item, j - 256);
            return;
         }

         this.onItemPickup(itemEntity, itemstack.getCount());
         ItemStack itemstack2 = inventory.addItem(itemstack);
         if (itemstack2.isEmpty()) {
            itemEntity.remove();
         } else {
            itemstack.setCount(itemstack2.getCount());
         }
      }

   }

   public boolean func_223717_b(Item p_223717_1_) {
      return ALLOWED_INVENTORY_ITEMS.contains(p_223717_1_) || this.getVillagerData().getProfession().getSpecificItems().contains(p_223717_1_);
   }

   /**
    * Used by {@link net.minecraft.entity.ai.EntityAIVillagerInteract EntityAIVillagerInteract} to check if the villager
    * can give some items from an inventory to another villager.
    */
   public boolean canAbondonItems() {
      return this.getFoodValueFromInventory() >= 24;
   }

   public boolean wantsMoreFood() {
      return this.getFoodValueFromInventory() < 12;
   }

   /**
    * @return calculated food value from item stacks in this villager's inventory
    */
   private int getFoodValueFromInventory() {
      Inventory inventory = this.getVillagerInventory();
      return FOOD_VALUES.entrySet().stream().mapToInt((p_226553_1_) -> {
         return inventory.count(p_226553_1_.getKey()) * p_226553_1_.getValue();
      }).sum();
   }

   private void dropCraftedBread() {
      Inventory inventory = this.getVillagerInventory();
      int i = inventory.count(Items.WHEAT);
      int j = i / 3;
      if (j != 0) {
         int k = j * 3;
         inventory.func_223374_a(Items.WHEAT, k);
         ItemStack itemstack = inventory.addItem(new ItemStack(Items.BREAD, j));
         if (!itemstack.isEmpty()) {
            this.entityDropItem(itemstack, 0.5F);
         }

      }
   }

   /**
    * Returns true if villager has seeds, potatoes or carrots in inventory
    */
   public boolean isFarmItemInInventory() {
      Inventory inventory = this.getVillagerInventory();
      return inventory.hasAny(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
   }

   protected void populateTradeData() {
      VillagerData villagerdata = this.getVillagerData();
      Int2ObjectMap<VillagerTrades.ITrade[]> int2objectmap = VillagerTrades.VILLAGER_DEFAULT_TRADES.get(villagerdata.getProfession());
      if (int2objectmap != null && !int2objectmap.isEmpty()) {
         VillagerTrades.ITrade[] avillagertrades$itrade = int2objectmap.get(villagerdata.getLevel());
         if (avillagertrades$itrade != null) {
            MerchantOffers merchantoffers = this.getOffers();
            this.addTrades(merchantoffers, avillagertrades$itrade, 2);
         }
      }
   }

   public void func_213746_a(VillagerEntity villager, long gameTime) {
      if ((gameTime < this.field_213783_bN || gameTime >= this.field_213783_bN + 1200L) && (gameTime < villager.field_213783_bN || gameTime >= villager.field_213783_bN + 1200L)) {
         this.gossip.transferFrom(villager.gossip, this.rand, 10);
         this.field_213783_bN = gameTime;
         villager.field_213783_bN = gameTime;
         this.spawnGolems(gameTime, 5);
      }
   }

   private void tickGossip() {
      long i = this.world.getGameTime();
      if (this.lastGossipDecay == 0L) {
         this.lastGossipDecay = i;
      } else if (i >= this.lastGossipDecay + 24000L) {
         this.gossip.tick();
         this.lastGossipDecay = i;
      }
   }

   public void spawnGolems(long gameTime, int requiredPeers) {
      if (this.canSpawnGolems(gameTime)) {
         AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(10.0D, 10.0D, 10.0D);
         List<VillagerEntity> list = this.world.getEntitiesWithinAABB(VillagerEntity.class, axisalignedbb);
         List<VillagerEntity> list1 = list.stream().filter((p_226554_2_) -> {
            return p_226554_2_.canSpawnGolems(gameTime);
         }).limit(5L).collect(Collectors.toList());
         if (list1.size() >= requiredPeers) {
            IronGolemEntity irongolementity = this.trySpawnGolem();
            if (irongolementity != null) {
               list.forEach((p_226552_2_) -> {
                  p_226552_2_.updateGolemLastSeenMemory(gameTime);
               });
            }
         }
      }
   }

   private void updateGolemLastSeenMemory(long gameTime) {
      this.brain.setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, gameTime);
   }

   private boolean hasSeenGolemRecently(long gameTime) {
      Optional<Long> optional = this.brain.getMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);
      if (!optional.isPresent()) {
         return false;
      } else {
         Long olong = optional.get();
         return gameTime - olong <= 600L;
      }
   }

   public boolean canSpawnGolems(long gameTime) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != VillagerProfession.NONE && villagerdata.getProfession() != VillagerProfession.NITWIT) {
         if (!this.hasSleptAndWorkedRecently(this.world.getGameTime())) {
            return false;
         } else {
            return !this.hasSeenGolemRecently(gameTime);
         }
      } else {
         return false;
      }
   }

   @Nullable
   private IronGolemEntity trySpawnGolem() {
      BlockPos blockpos = new BlockPos(this);

      for(int i = 0; i < 10; ++i) {
         double d0 = (double)(this.world.rand.nextInt(16) - 8);
         double d1 = (double)(this.world.rand.nextInt(16) - 8);
         double d2 = 6.0D;

         for(int j = 0; j >= -12; --j) {
            BlockPos blockpos1 = blockpos.add(d0, d2 + (double)j, d1);
            if ((this.world.getBlockState(blockpos1).isAir() || this.world.getBlockState(blockpos1).getMaterial().isLiquid()) && this.world.getBlockState(blockpos1.down()).getMaterial().isOpaque()) {
               d2 += (double)j;
               break;
            }
         }

         BlockPos blockpos2 = blockpos.add(d0, d2, d1);
         IronGolemEntity irongolementity = EntityType.IRON_GOLEM.create(this.world, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos2, SpawnReason.MOB_SUMMONED, false, false);
         if (irongolementity != null) {
            if (irongolementity.canSpawn(this.world, SpawnReason.MOB_SUMMONED) && irongolementity.isNotColliding(this.world)) {
               this.world.addEntity(irongolementity);
               return irongolementity;
            }

            irongolementity.remove();
         }
      }

      return null;
   }

   public void updateReputation(IReputationType type, Entity target) {
      if (type == IReputationType.ZOMBIE_VILLAGER_CURED) {
         this.gossip.add(target.getUniqueID(), GossipType.MAJOR_POSITIVE, 20);
         this.gossip.add(target.getUniqueID(), GossipType.MINOR_POSITIVE, 25);
      } else if (type == IReputationType.TRADE) {
         this.gossip.add(target.getUniqueID(), GossipType.TRADING, 2);
      } else if (type == IReputationType.VILLAGER_HURT) {
         this.gossip.add(target.getUniqueID(), GossipType.MINOR_NEGATIVE, 25);
      } else if (type == IReputationType.VILLAGER_KILLED) {
         this.gossip.add(target.getUniqueID(), GossipType.MAJOR_NEGATIVE, 25);
      }

   }

   public int getXp() {
      return this.xp;
   }

   public void setXp(int xpIn) {
      this.xp = xpIn;
   }

   private void func_223718_eH() {
      this.func_223719_ex();
      this.field_223725_bO = 0;
   }

   public GossipManager getGossip() {
      return this.gossip;
   }

   public void func_223716_a(INBT p_223716_1_) {
      this.gossip.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, p_223716_1_));
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPacketSender.sendLivingEntity(this);
   }

   public void startSleeping(BlockPos pos) {
      super.startSleeping(pos);
      this.brain.setMemory(MemoryModuleType.LAST_SLEPT, LongSerializable.of(this.world.getGameTime()));
   }

   public void wakeUp() {
      super.wakeUp();
      this.brain.setMemory(MemoryModuleType.field_226332_A_, LongSerializable.of(this.world.getGameTime()));
   }

   private boolean hasSleptAndWorkedRecently(long gameTime) {
      Optional<LongSerializable> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
      Optional<LongSerializable> optional1 = this.brain.getMemory(MemoryModuleType.LAST_WORKED_AT_POI);
      if (optional.isPresent() && optional1.isPresent()) {
         return gameTime - optional.get().get() < 24000L && gameTime - optional1.get().get() < 36000L;
      } else {
         return false;
      }
   }
}