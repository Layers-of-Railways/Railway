package net.minecraft.entity.monster;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ZombieVillagerEntity extends ZombieEntity implements IVillagerDataHolder {
   private static final DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(ZombieVillagerEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<VillagerData> VILLAGER_DATA = EntityDataManager.createKey(ZombieVillagerEntity.class, DataSerializers.VILLAGER_DATA);
   private int conversionTime;
   private UUID converstionStarter;
   private INBT gossips;
   private CompoundNBT offers;
   private int xp;

   public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> p_i50186_1_, World p_i50186_2_) {
      super(p_i50186_1_, p_i50186_2_);
      this.setVillagerData(this.getVillagerData().withProfession(Registry.VILLAGER_PROFESSION.getRandom(this.rand)));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CONVERTING, false);
      this.dataManager.register(VILLAGER_DATA, new VillagerData(IVillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.put("VillagerData", this.getVillagerData().serialize(NBTDynamicOps.INSTANCE));
      if (this.offers != null) {
         compound.put("Offers", this.offers);
      }

      if (this.gossips != null) {
         compound.put("Gossips", this.gossips);
      }

      compound.putInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
      if (this.converstionStarter != null) {
         compound.putUniqueId("ConversionPlayer", this.converstionStarter);
      }

      compound.putInt("Xp", this.xp);
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
         this.offers = compound.getCompound("Offers");
      }

      if (compound.contains("Gossips", 10)) {
         this.gossips = compound.getList("Gossips", 10);
      }

      if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
         this.startConverting(compound.hasUniqueId("ConversionPlayer") ? compound.getUniqueId("ConversionPlayer") : null, compound.getInt("ConversionTime"));
      }

      if (compound.contains("Xp", 3)) {
         this.xp = compound.getInt("Xp");
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (!this.world.isRemote && this.isAlive() && this.isConverting()) {
         int i = this.getConversionProgress();
         this.conversionTime -= i;
         if (this.conversionTime <= 0) {
            this.cureZombie((ServerWorld)this.world);
         }
      }

      super.tick();
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.GOLDEN_APPLE && this.isPotionActive(Effects.WEAKNESS)) {
         if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.world.isRemote) {
            this.startConverting(player.getUniqueID(), this.rand.nextInt(2401) + 3600);
            player.swing(hand, true);
         }

         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean canDespawn(double distanceToClosestPlayer) {
      return !this.isConverting();
   }

   /**
    * Returns whether this zombie is in the process of converting to a villager
    */
   public boolean isConverting() {
      return this.getDataManager().get(CONVERTING);
   }

   /**
    * Starts conversion of this zombie villager to a villager
    */
   private void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn) {
      this.converstionStarter = conversionStarterIn;
      this.conversionTime = conversionTimeIn;
      this.getDataManager().set(CONVERTING, true);
      this.removePotionEffect(Effects.WEAKNESS);
      this.addPotionEffect(new EffectInstance(Effects.STRENGTH, conversionTimeIn, Math.min(this.world.getDifficulty().getId() - 1, 0)));
      this.world.setEntityState(this, (byte)16);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 16) {
         if (!this.isSilent()) {
            this.world.playSound(this.getPosX(), this.getPosYEye(), this.getPosZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleStatusUpdate(id);
      }
   }

   private void cureZombie(ServerWorld p_213791_1_) {
      VillagerEntity villagerentity = EntityType.VILLAGER.create(p_213791_1_);

      for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
         ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
         if (!itemstack.isEmpty()) {
            if (EnchantmentHelper.hasBindingCurse(itemstack)) {
               villagerentity.replaceItemInInventory(equipmentslottype.getIndex() + 300, itemstack);
            } else {
               double d0 = (double)this.getDropChance(equipmentslottype);
               if (d0 > 1.0D) {
                  this.entityDropItem(itemstack);
               }
            }
         }
      }

      villagerentity.copyLocationAndAnglesFrom(this);
      villagerentity.setVillagerData(this.getVillagerData());
      if (this.gossips != null) {
         villagerentity.func_223716_a(this.gossips);
      }

      if (this.offers != null) {
         villagerentity.setOffers(new MerchantOffers(this.offers));
      }

      villagerentity.setXp(this.xp);
      villagerentity.onInitialSpawn(p_213791_1_, p_213791_1_.getDifficultyForLocation(new BlockPos(villagerentity)), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
      if (this.isChild()) {
         villagerentity.setGrowingAge(-24000);
      }

      this.remove();
      villagerentity.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         villagerentity.setCustomName(this.getCustomName());
         villagerentity.setCustomNameVisible(this.isCustomNameVisible());
      }

      if (this.isNoDespawnRequired()) {
         villagerentity.enablePersistence();
      }

      villagerentity.setInvulnerable(this.isInvulnerable());
      p_213791_1_.addEntity(villagerentity);
      if (this.converstionStarter != null) {
         PlayerEntity playerentity = p_213791_1_.getPlayerByUuid(this.converstionStarter);
         if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)playerentity, this, villagerentity);
            p_213791_1_.updateReputation(IReputationType.ZOMBIE_VILLAGER_CURED, playerentity, villagerentity);
         }
      }

      villagerentity.addPotionEffect(new EffectInstance(Effects.NAUSEA, 200, 0));
      p_213791_1_.playEvent((PlayerEntity)null, 1027, new BlockPos(this), 0);
   }

   private int getConversionProgress() {
      int i = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int j = 0;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = (int)this.getPosX() - 4; k < (int)this.getPosX() + 4 && j < 14; ++k) {
            for(int l = (int)this.getPosY() - 4; l < (int)this.getPosY() + 4 && j < 14; ++l) {
               for(int i1 = (int)this.getPosZ() - 4; i1 < (int)this.getPosZ() + 4 && j < 14; ++i1) {
                  Block block = this.world.getBlockState(blockpos$mutable.setPos(k, l, i1)).getBlock();
                  if (block == Blocks.IRON_BARS || block instanceof BedBlock) {
                     if (this.rand.nextFloat() < 0.3F) {
                        ++i;
                     }

                     ++j;
                  }
               }
            }
         }
      }

      return i;
   }

   /**
    * Gets the pitch of living sounds in living entities.
    */
   protected float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.0F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   public void setOffers(CompoundNBT p_213790_1_) {
      this.offers = p_213790_1_;
   }

   public void setGossips(INBT p_223727_1_) {
      this.gossips = p_223727_1_;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      this.setVillagerData(this.getVillagerData().withType(IVillagerType.byBiome(worldIn.getBiome(new BlockPos(this)))));
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public void setVillagerData(VillagerData p_213792_1_) {
      VillagerData villagerdata = this.getVillagerData();
      if (villagerdata.getProfession() != p_213792_1_.getProfession()) {
         this.offers = null;
      }

      this.dataManager.set(VILLAGER_DATA, p_213792_1_);
   }

   public VillagerData getVillagerData() {
      return this.dataManager.get(VILLAGER_DATA);
   }

   public void setEXP(int p_213789_1_) {
      this.xp = p_213789_1_;
   }
}