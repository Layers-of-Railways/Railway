package net.minecraft.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class PotionEntity extends ThrowableEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(PotionEntity.class, DataSerializers.ITEMSTACK);
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Predicate<LivingEntity> WATER_SENSITIVE = PotionEntity::isWaterSensitiveEntity;

   public PotionEntity(EntityType<? extends PotionEntity> typeIn, World worldIn) {
      super(typeIn, worldIn);
   }

   public PotionEntity(World worldIn, LivingEntity livingEntityIn) {
      super(EntityType.POTION, livingEntityIn, worldIn);
   }

   public PotionEntity(World worldIn, double x, double y, double z) {
      super(EntityType.POTION, x, y, z, worldIn);
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public ItemStack getItem() {
      ItemStack itemstack = this.getDataManager().get(ITEM);
      if (itemstack.getItem() != Items.SPLASH_POTION && itemstack.getItem() != Items.LINGERING_POTION) {
         if (this.world != null) {
            LOGGER.error("ThrownPotion entity {} has no item?!", (int)this.getEntityId());
         }

         return new ItemStack(Items.SPLASH_POTION);
      } else {
         return itemstack;
      }
   }

   public void setItem(ItemStack stack) {
      this.getDataManager().set(ITEM, stack.copy());
   }

   /**
    * Gets the amount of gravity to apply to the thrown entity with each tick.
    */
   protected float getGravityVelocity() {
      return 0.05F;
   }

   /**
    * Called when this EntityThrowable hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         ItemStack itemstack = this.getItem();
         Potion potion = PotionUtils.getPotionFromItem(itemstack);
         List<EffectInstance> list = PotionUtils.getEffectsFromStack(itemstack);
         boolean flag = potion == Potions.WATER && list.isEmpty();
         if (result.getType() == RayTraceResult.Type.BLOCK && flag) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
            Direction direction = blockraytraceresult.getFace();
            BlockPos blockpos = blockraytraceresult.getPos().offset(direction);
            this.extinguishFires(blockpos, direction);
            this.extinguishFires(blockpos.offset(direction.getOpposite()), direction);

            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
               this.extinguishFires(blockpos.offset(direction1), direction1);
            }
         }

         if (flag) {
            this.applyWater();
         } else if (!list.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(itemstack, potion);
            } else {
               this.func_213888_a(list, result.getType() == RayTraceResult.Type.ENTITY ? ((EntityRayTraceResult)result).getEntity() : null);
            }
         }

         int i = potion.hasInstantEffect() ? 2007 : 2002;
         this.world.playEvent(i, new BlockPos(this), PotionUtils.getColor(itemstack));
         this.remove();
      }
   }

   private void applyWater() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
      List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, WATER_SENSITIVE);
      if (!list.isEmpty()) {
         for(LivingEntity livingentity : list) {
            double d0 = this.getDistanceSq(livingentity);
            if (d0 < 16.0D && isWaterSensitiveEntity(livingentity)) {
               livingentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(livingentity, this.getThrower()), 1.0F);
            }
         }
      }

   }

   private void func_213888_a(List<EffectInstance> p_213888_1_, @Nullable Entity p_213888_2_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
      List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(LivingEntity livingentity : list) {
            if (livingentity.canBeHitWithPotion()) {
               double d0 = this.getDistanceSq(livingentity);
               if (d0 < 16.0D) {
                  double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                  if (livingentity == p_213888_2_) {
                     d1 = 1.0D;
                  }

                  for(EffectInstance effectinstance : p_213888_1_) {
                     Effect effect = effectinstance.getPotion();
                     if (effect.isInstant()) {
                        effect.affectEntity(this, this.getThrower(), livingentity, effectinstance.getAmplifier(), d1);
                     } else {
                        int i = (int)(d1 * (double)effectinstance.getDuration() + 0.5D);
                        if (i > 20) {
                           livingentity.addPotionEffect(new EffectInstance(effect, i, effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.doesShowParticles()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(ItemStack p_190542_1_, Potion p_190542_2_) {
      AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
      areaeffectcloudentity.setOwner(this.getThrower());
      areaeffectcloudentity.setRadius(3.0F);
      areaeffectcloudentity.setRadiusOnUse(-0.5F);
      areaeffectcloudentity.setWaitTime(10);
      areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());
      areaeffectcloudentity.setPotion(p_190542_2_);

      for(EffectInstance effectinstance : PotionUtils.getFullEffectsFromItem(p_190542_1_)) {
         areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
      }

      CompoundNBT compoundnbt = p_190542_1_.getTag();
      if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
         areaeffectcloudentity.setColor(compoundnbt.getInt("CustomPotionColor"));
      }

      this.world.addEntity(areaeffectcloudentity);
   }

   private boolean isLingering() {
      return this.getItem().getItem() == Items.LINGERING_POTION;
   }

   private void extinguishFires(BlockPos pos, Direction p_184542_2_) {
      BlockState blockstate = this.world.getBlockState(pos);
      Block block = blockstate.getBlock();
      if (block == Blocks.FIRE) {
         this.world.extinguishFire((PlayerEntity)null, pos.offset(p_184542_2_), p_184542_2_.getOpposite());
      } else if (block == Blocks.CAMPFIRE && blockstate.get(CampfireBlock.LIT)) {
         this.world.playEvent((PlayerEntity)null, 1009, pos, 0);
         this.world.setBlockState(pos, blockstate.with(CampfireBlock.LIT, Boolean.valueOf(false)));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      ItemStack itemstack = ItemStack.read(compound.getCompound("Potion"));
      if (itemstack.isEmpty()) {
         this.remove();
      } else {
         this.setItem(itemstack);
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      ItemStack itemstack = this.getItem();
      if (!itemstack.isEmpty()) {
         compound.put("Potion", itemstack.write(new CompoundNBT()));
      }

   }

   private static boolean isWaterSensitiveEntity(LivingEntity p_190544_0_) {
      return p_190544_0_ instanceof EndermanEntity || p_190544_0_ instanceof BlazeEntity;
   }
}