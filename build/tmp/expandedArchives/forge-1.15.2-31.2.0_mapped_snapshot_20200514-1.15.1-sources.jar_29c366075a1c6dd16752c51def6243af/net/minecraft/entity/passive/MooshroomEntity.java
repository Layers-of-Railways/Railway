package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity implements net.minecraftforge.common.IShearable {
   private static final DataParameter<String> MOOSHROOM_TYPE = EntityDataManager.createKey(MooshroomEntity.class, DataSerializers.STRING);
   private Effect hasStewEffect;
   private int effectDuration;
   /** Stores the UUID of the most recent lightning bolt to strike */
   private UUID lightningUUID;

   public MooshroomEntity(EntityType<? extends MooshroomEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
      return worldIn.getBlockState(pos.down()).getBlock() == Blocks.MYCELIUM ? 10.0F : worldIn.getBrightness(pos) - 0.5F;
   }

   public static boolean func_223318_c(EntityType<MooshroomEntity> p_223318_0_, IWorld p_223318_1_, SpawnReason p_223318_2_, BlockPos p_223318_3_, Random p_223318_4_) {
      return p_223318_1_.getBlockState(p_223318_3_.down()).getBlock() == Blocks.MYCELIUM && p_223318_1_.getLightSubtracted(p_223318_3_, 0) > 8;
   }

   /**
    * Called when a lightning bolt hits the entity.
    */
   public void onStruckByLightning(LightningBoltEntity lightningBolt) {
      UUID uuid = lightningBolt.getUniqueID();
      if (!uuid.equals(this.lightningUUID)) {
         this.setMooshroomType(this.getMooshroomType() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
         this.lightningUUID = uuid;
         this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(MOOSHROOM_TYPE, MooshroomEntity.Type.RED.name);
   }

   public boolean processInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.BOWL && !this.isChild() && !player.abilities.isCreativeMode) {
         itemstack.shrink(1);
         boolean flag = false;
         ItemStack itemstack1;
         if (this.hasStewEffect != null) {
            flag = true;
            itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.addEffect(itemstack1, this.hasStewEffect, this.effectDuration);
            this.hasStewEffect = null;
            this.effectDuration = 0;
         } else {
            itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
         }

         if (itemstack.isEmpty()) {
            player.setHeldItem(hand, itemstack1);
         } else if (!player.inventory.addItemStackToInventory(itemstack1)) {
            player.dropItem(itemstack1, false);
         }

         SoundEvent soundevent;
         if (flag) {
            soundevent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
         } else {
            soundevent = SoundEvents.ENTITY_MOOSHROOM_MILK;
         }

         this.playSound(soundevent, 1.0F, 1.0F);
         return true;
      } else if (false && itemstack.getItem() == Items.SHEARS && !this.isChild()) { //Forge: Moved to onSheared
         this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosYHeight(0.5D), this.getPosZ(), 0.0D, 0.0D, 0.0D);
         if (!this.world.isRemote) {
            this.remove();
            CowEntity cowentity = EntityType.COW.create(this.world);
            cowentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
            cowentity.setHealth(this.getHealth());
            cowentity.renderYawOffset = this.renderYawOffset;
            if (this.hasCustomName()) {
               cowentity.setCustomName(this.getCustomName());
               cowentity.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isNoDespawnRequired()) {
               cowentity.enablePersistence();
            }

            cowentity.setInvulnerable(this.isInvulnerable());
            this.world.addEntity(cowentity);

            for(int k = 0; k < 5; ++k) {
               this.world.addEntity(new ItemEntity(this.world, this.getPosX(), this.getPosYHeight(1.0D), this.getPosZ(), new ItemStack(this.getMooshroomType().renderState.getBlock())));
            }

            itemstack.damageItem(1, player, (p_213442_1_) -> {
               p_213442_1_.sendBreakAnimation(hand);
            });
            this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
         }

         return true;
      } else {
         if (this.getMooshroomType() == MooshroomEntity.Type.BROWN && itemstack.getItem().isIn(ItemTags.SMALL_FLOWERS)) {
            if (this.hasStewEffect != null) {
               for(int i = 0; i < 2; ++i) {
                  this.world.addParticle(ParticleTypes.SMOKE, this.getPosX() + (double)(this.rand.nextFloat() / 2.0F), this.getPosYHeight(0.5D), this.getPosZ() + (double)(this.rand.nextFloat() / 2.0F), 0.0D, (double)(this.rand.nextFloat() / 5.0F), 0.0D);
               }
            } else {
               Pair<Effect, Integer> pair = this.getStewEffect(itemstack);
               if (!player.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               for(int j = 0; j < 4; ++j) {
                  this.world.addParticle(ParticleTypes.EFFECT, this.getPosX() + (double)(this.rand.nextFloat() / 2.0F), this.getPosYHeight(0.5D), this.getPosZ() + (double)(this.rand.nextFloat() / 2.0F), 0.0D, (double)(this.rand.nextFloat() / 5.0F), 0.0D);
               }

               this.hasStewEffect = pair.getLeft();
               this.effectDuration = pair.getRight();
               this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
            }
         }

         return super.processInteract(player, hand);
      }
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putString("Type", this.getMooshroomType().name);
      if (this.hasStewEffect != null) {
         compound.putByte("EffectId", (byte)Effect.getId(this.hasStewEffect));
         compound.putInt("EffectDuration", this.effectDuration);
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setMooshroomType(MooshroomEntity.Type.getTypeByName(compound.getString("Type")));
      if (compound.contains("EffectId", 1)) {
         this.hasStewEffect = Effect.get(compound.getByte("EffectId"));
      }

      if (compound.contains("EffectDuration", 3)) {
         this.effectDuration = compound.getInt("EffectDuration");
      }

   }

   private Pair<Effect, Integer> getStewEffect(ItemStack p_213443_1_) {
      FlowerBlock flowerblock = (FlowerBlock)((BlockItem)p_213443_1_.getItem()).getBlock();
      return Pair.of(flowerblock.getStewEffect(), flowerblock.getStewEffectDuration());
   }

   private void setMooshroomType(MooshroomEntity.Type typeIn) {
      this.dataManager.set(MOOSHROOM_TYPE, typeIn.name);
   }

   public MooshroomEntity.Type getMooshroomType() {
      return MooshroomEntity.Type.getTypeByName(this.dataManager.get(MOOSHROOM_TYPE));
   }

   public MooshroomEntity createChild(AgeableEntity ageable) {
      MooshroomEntity mooshroomentity = EntityType.MOOSHROOM.create(this.world);
      mooshroomentity.setMooshroomType(this.func_213445_a((MooshroomEntity)ageable));
      return mooshroomentity;
   }

   private MooshroomEntity.Type func_213445_a(MooshroomEntity p_213445_1_) {
      MooshroomEntity.Type mooshroomentity$type = this.getMooshroomType();
      MooshroomEntity.Type mooshroomentity$type1 = p_213445_1_.getMooshroomType();
      MooshroomEntity.Type mooshroomentity$type2;
      if (mooshroomentity$type == mooshroomentity$type1 && this.rand.nextInt(1024) == 0) {
         mooshroomentity$type2 = mooshroomentity$type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
      } else {
         mooshroomentity$type2 = this.rand.nextBoolean() ? mooshroomentity$type : mooshroomentity$type1;
      }

      return mooshroomentity$type2;
   }

   @Override
   public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, net.minecraft.util.math.BlockPos pos) {
      return !this.isChild();
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, net.minecraft.util.math.BlockPos pos, int fortune) {
      java.util.List<ItemStack> ret = new java.util.ArrayList<>();
      this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosYHeight(0.5D), this.getPosZ(), 0.0D, 0.0D, 0.0D);
      if (!this.world.isRemote) {
         this.remove();
         CowEntity cowentity = EntityType.COW.create(this.world);
         cowentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
         cowentity.setHealth(this.getHealth());
         cowentity.renderYawOffset = this.renderYawOffset;
         if (this.hasCustomName()) {
             cowentity.setCustomName(this.getCustomName());
             cowentity.setCustomNameVisible(this.isCustomNameVisible());
         }
         this.world.addEntity(cowentity);
         for(int i = 0; i < 5; ++i) {
            ret.add(new ItemStack(this.getMooshroomType().renderState.getBlock()));
         }
         this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
      }
      return ret;
   }

   public static enum Type {
      RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
      BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

      private final String name;
      private final BlockState renderState;

      private Type(String nameIn, BlockState renderStateIn) {
         this.name = nameIn;
         this.renderState = renderStateIn;
      }

      /**
       * A block state that is rendered on the back of the mooshroom.
       */
      @OnlyIn(Dist.CLIENT)
      public BlockState getRenderState() {
         return this.renderState;
      }

      private static MooshroomEntity.Type getTypeByName(String nameIn) {
         for(MooshroomEntity.Type mooshroomentity$type : values()) {
            if (mooshroomentity$type.name.equals(nameIn)) {
               return mooshroomentity$type;
            }
         }

         return RED;
      }
   }
}