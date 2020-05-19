package net.minecraft.world.spawner;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

public class CatSpawner {
   private int field_221125_a;

   public int tick(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      if (spawnPeacefulMobs && worldIn.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
         --this.field_221125_a;
         if (this.field_221125_a > 0) {
            return 0;
         } else {
            this.field_221125_a = 1200;
            PlayerEntity playerentity = worldIn.getRandomPlayer();
            if (playerentity == null) {
               return 0;
            } else {
               Random random = worldIn.rand;
               int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               BlockPos blockpos = (new BlockPos(playerentity)).add(i, 0, j);
               if (!worldIn.isAreaLoaded(blockpos.getX() - 10, blockpos.getY() - 10, blockpos.getZ() - 10, blockpos.getX() + 10, blockpos.getY() + 10, blockpos.getZ() + 10)) {
                  return 0;
               } else {
                  if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, worldIn, blockpos, EntityType.CAT)) {
                     if (worldIn.isCloseToVillage(blockpos, 2)) {
                        return this.func_221121_a(worldIn, blockpos);
                     }

                     if (Feature.SWAMP_HUT.isPositionInsideStructure(worldIn, blockpos)) {
                        return this.func_221123_a(worldIn, blockpos);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int func_221121_a(ServerWorld worldIn, BlockPos p_221121_2_) {
      int i = 48;
      if (worldIn.getPointOfInterestManager().getCountInRange(PointOfInterestType.HOME.getPredicate(), p_221121_2_, 48, PointOfInterestManager.Status.IS_OCCUPIED) > 4L) {
         List<CatEntity> list = worldIn.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(p_221121_2_)).grow(48.0D, 8.0D, 48.0D));
         if (list.size() < 5) {
            return this.spawnCat(p_221121_2_, worldIn);
         }
      }

      return 0;
   }

   private int func_221123_a(World worldIn, BlockPos pos) {
      int i = 16;
      List<CatEntity> list = worldIn.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(pos)).grow(16.0D, 8.0D, 16.0D));
      return list.size() < 1 ? this.spawnCat(pos, worldIn) : 0;
   }

   private int spawnCat(BlockPos pos, World worldIn) {
      CatEntity catentity = EntityType.CAT.create(worldIn);
      if (catentity == null) {
         return 0;
      } else {
         catentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(pos), SpawnReason.NATURAL, (ILivingEntityData)null, (CompoundNBT)null);
         catentity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
         worldIn.addEntity(catentity);
         return 1;
      }
   }
}