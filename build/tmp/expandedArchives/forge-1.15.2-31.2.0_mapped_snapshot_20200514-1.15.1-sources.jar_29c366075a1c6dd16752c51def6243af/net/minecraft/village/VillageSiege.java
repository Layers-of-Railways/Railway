package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class VillageSiege {
   private boolean hasSetupSiege;
   private VillageSiege.State siegeState = VillageSiege.State.SIEGE_DONE;
   private int siegeCount;
   private int nextSpawnTime;
   private int spawnX;
   private int spawnY;
   private int spawnZ;

   public int tick(ServerWorld p_225477_1_, boolean p_225477_2_, boolean p_225477_3_) {
      if (!p_225477_1_.isDaytime() && p_225477_2_) {
         float f = p_225477_1_.getCelestialAngle(0.0F);
         if ((double)f == 0.5D) {
            this.siegeState = p_225477_1_.rand.nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
         }

         if (this.siegeState == VillageSiege.State.SIEGE_DONE) {
            return 0;
         } else {
            if (!this.hasSetupSiege) {
               if (!this.trySetupSiege(p_225477_1_)) {
                  return 0;
               }

               this.hasSetupSiege = true;
            }

            if (this.nextSpawnTime > 0) {
               --this.nextSpawnTime;
               return 0;
            } else {
               this.nextSpawnTime = 2;
               if (this.siegeCount > 0) {
                  this.spawnZombie(p_225477_1_);
                  --this.siegeCount;
               } else {
                  this.siegeState = VillageSiege.State.SIEGE_DONE;
               }

               return 1;
            }
         }
      } else {
         this.siegeState = VillageSiege.State.SIEGE_DONE;
         this.hasSetupSiege = false;
         return 0;
      }
   }

   private boolean trySetupSiege(ServerWorld p_75529_1_) {
      for(PlayerEntity playerentity : p_75529_1_.getPlayers()) {
         if (!playerentity.isSpectator()) {
            BlockPos blockpos = playerentity.getPosition();
            if (p_75529_1_.isVillage(blockpos) && p_75529_1_.getBiome(blockpos).getCategory() != Biome.Category.MUSHROOM) {
               for(int i = 0; i < 10; ++i) {
                  float f = p_75529_1_.rand.nextFloat() * ((float)Math.PI * 2F);
                  this.spawnX = blockpos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
                  this.spawnY = blockpos.getY();
                  this.spawnZ = blockpos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);
                  Vec3d siegeLocation = this.findRandomSpawnPos(p_75529_1_, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
                  if (siegeLocation != null) {
                     if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.village.VillageSiegeEvent(this, p_75529_1_, playerentity, siegeLocation))) return false;
                     this.nextSpawnTime = 0;
                     this.siegeCount = 20;
                     break;
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   private void spawnZombie(ServerWorld p_75530_1_) {
      Vec3d vec3d = this.findRandomSpawnPos(p_75530_1_, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
      if (vec3d != null) {
         ZombieEntity zombieentity;
         try {
            zombieentity = EntityType.ZOMBIE.create(p_75530_1_); //Forge: Direct Initialization is deprecated, use EntityType.
            zombieentity.onInitialSpawn(p_75530_1_, p_75530_1_.getDifficultyForLocation(new BlockPos(zombieentity)), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
         } catch (Exception exception) {
            exception.printStackTrace();
            return;
         }

         zombieentity.setLocationAndAngles(vec3d.x, vec3d.y, vec3d.z, p_75530_1_.rand.nextFloat() * 360.0F, 0.0F);
         p_75530_1_.addEntity(zombieentity);
      }
   }

   @Nullable
   private Vec3d findRandomSpawnPos(ServerWorld p_225476_1_, BlockPos p_225476_2_) {
      for(int i = 0; i < 10; ++i) {
         int j = p_225476_2_.getX() + p_225476_1_.rand.nextInt(16) - 8;
         int k = p_225476_2_.getZ() + p_225476_1_.rand.nextInt(16) - 8;
         int l = p_225476_1_.getHeight(Heightmap.Type.WORLD_SURFACE, j, k);
         BlockPos blockpos = new BlockPos(j, l, k);
         if (p_225476_1_.isVillage(blockpos) && MonsterEntity.canMonsterSpawnInLight(EntityType.ZOMBIE, p_225476_1_, SpawnReason.EVENT, blockpos, p_225476_1_.rand)) {
            return new Vec3d((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         }
      }

      return null;
   }

   static enum State {
      SIEGE_CAN_ACTIVATE,
      SIEGE_TONIGHT,
      SIEGE_DONE;
   }
}