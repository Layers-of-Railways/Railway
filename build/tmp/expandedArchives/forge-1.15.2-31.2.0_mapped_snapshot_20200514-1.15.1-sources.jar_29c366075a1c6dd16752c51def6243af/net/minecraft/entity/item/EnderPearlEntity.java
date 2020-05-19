package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderPearlEntity extends ProjectileItemEntity {
   private LivingEntity perlThrower;

   public EnderPearlEntity(EntityType<? extends EnderPearlEntity> p_i50153_1_, World p_i50153_2_) {
      super(p_i50153_1_, p_i50153_2_);
   }

   public EnderPearlEntity(World worldIn, LivingEntity throwerIn) {
      super(EntityType.ENDER_PEARL, throwerIn, worldIn);
      this.perlThrower = throwerIn;
   }

   @OnlyIn(Dist.CLIENT)
   public EnderPearlEntity(World worldIn, double x, double y, double z) {
      super(EntityType.ENDER_PEARL, x, y, z, worldIn);
   }

   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   /**
    * Called when this EntityThrowable hits a block or entity.
    */
   protected void onImpact(RayTraceResult result) {
      LivingEntity livingentity = this.getThrower();
      if (result.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)result).getEntity();
         if (entity == this.perlThrower) {
            return;
         }

         entity.attackEntityFrom(DamageSource.causeThrownDamage(this, livingentity), 0.0F);
      }

      if (result.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)result).getPos();
         TileEntity tileentity = this.world.getTileEntity(blockpos);
         if (tileentity instanceof EndGatewayTileEntity) {
            EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
            if (livingentity != null) {
               if (livingentity instanceof ServerPlayerEntity) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayerEntity)livingentity, this.world.getBlockState(blockpos));
               }

               endgatewaytileentity.teleportEntity(livingentity);
               this.remove();
               return;
            }

            endgatewaytileentity.teleportEntity(this);
            return;
         }
      }

      for(int i = 0; i < 32; ++i) {
         this.world.addParticle(ParticleTypes.PORTAL, this.getPosX(), this.getPosY() + this.rand.nextDouble() * 2.0D, this.getPosZ(), this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
      }

      if (!this.world.isRemote) {
         if (livingentity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
            if (serverplayerentity.connection.getNetworkManager().isChannelOpen() && serverplayerentity.world == this.world && !serverplayerentity.isSleeping()) {
               net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(serverplayerentity, this.getPosX(), this.getPosY(), this.getPosZ(), 5.0F);
               if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower patch size
               if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                  EndermiteEntity endermiteentity = EntityType.ENDERMITE.create(this.world);
                  endermiteentity.setSpawnedByPlayer(true);
                  endermiteentity.setLocationAndAngles(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), livingentity.rotationYaw, livingentity.rotationPitch);
                  this.world.addEntity(endermiteentity);
               }

               if (livingentity.isPassenger()) {
                  livingentity.stopRiding();
               }

               livingentity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               livingentity.fallDistance = 0.0F;
               livingentity.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
               } //Forge: End
            }
         } else if (livingentity != null) {
            livingentity.setPositionAndUpdate(this.getPosX(), this.getPosY(), this.getPosZ());
            livingentity.fallDistance = 0.0F;
         }

         this.remove();
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      LivingEntity livingentity = this.getThrower();
      if (livingentity != null && livingentity instanceof PlayerEntity && !livingentity.isAlive()) {
         this.remove();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType destination, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (this.owner.dimension != destination) {
         this.owner = null;
      }

      return super.changeDimension(destination, teleporter);
   }
}