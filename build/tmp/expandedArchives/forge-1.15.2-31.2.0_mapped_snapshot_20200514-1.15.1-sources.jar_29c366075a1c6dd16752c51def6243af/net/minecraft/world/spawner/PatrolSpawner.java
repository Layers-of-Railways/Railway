package net.minecraft.world.spawner;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class PatrolSpawner {
   private int field_222698_b;

   public int tick(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      if (!spawnHostileMobs) {
         return 0;
      } else if (!worldIn.getGameRules().getBoolean(GameRules.field_230127_D_)) {
         return 0;
      } else {
         Random random = worldIn.rand;
         --this.field_222698_b;
         if (this.field_222698_b > 0) {
            return 0;
         } else {
            this.field_222698_b += 12000 + random.nextInt(1200);
            long i = worldIn.getDayTime() / 24000L;
            if (i >= 5L && worldIn.isDaytime()) {
               if (random.nextInt(5) != 0) {
                  return 0;
               } else {
                  int j = worldIn.getPlayers().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     PlayerEntity playerentity = worldIn.getPlayers().get(random.nextInt(j));
                     if (playerentity.isSpectator()) {
                        return 0;
                     } else if (worldIn.isVillage(playerentity.getPosition())) {
                        return 0;
                     } else {
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos.Mutable blockpos$mutable = (new BlockPos.Mutable(playerentity)).move(k, 0, l);
                        if (!worldIn.isAreaLoaded(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome biome = worldIn.getBiome(blockpos$mutable);
                           Biome.Category biome$category = biome.getCategory();
                           if (biome$category == Biome.Category.MUSHROOM) {
                              return 0;
                           } else {
                              int i1 = 0;
                              int j1 = (int)Math.ceil((double)worldIn.getDifficultyForLocation(blockpos$mutable).getAdditionalDifficulty()) + 1;

                              for(int k1 = 0; k1 < j1; ++k1) {
                                 ++i1;
                                 blockpos$mutable.setY(worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                 if (k1 == 0) {
                                    if (!this.spawnPatroller(worldIn, blockpos$mutable, random, true)) {
                                       break;
                                    }
                                 } else {
                                    this.spawnPatroller(worldIn, blockpos$mutable, random, false);
                                 }

                                 blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                 blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                              }

                              return i1;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private boolean spawnPatroller(World worldIn, BlockPos p_222695_2_, Random random, boolean p_222695_4_) {
      BlockState blockstate = worldIn.getBlockState(p_222695_2_);
      if (!WorldEntitySpawner.isSpawnableSpace(worldIn, p_222695_2_, blockstate, blockstate.getFluidState())) {
         return false;
      } else if (!PatrollerEntity.func_223330_b(EntityType.PILLAGER, worldIn, SpawnReason.PATROL, p_222695_2_, random)) {
         return false;
      } else {
         PatrollerEntity patrollerentity = EntityType.PILLAGER.create(worldIn);
         if (patrollerentity != null) {
            if (p_222695_4_) {
               patrollerentity.setLeader(true);
               patrollerentity.resetPatrolTarget();
            }

            patrollerentity.setPosition((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
            patrollerentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(p_222695_2_), SpawnReason.PATROL, (ILivingEntityData)null, (CompoundNBT)null);
            worldIn.addEntity(patrollerentity);
            return true;
         } else {
            return false;
         }
      }
   }
}