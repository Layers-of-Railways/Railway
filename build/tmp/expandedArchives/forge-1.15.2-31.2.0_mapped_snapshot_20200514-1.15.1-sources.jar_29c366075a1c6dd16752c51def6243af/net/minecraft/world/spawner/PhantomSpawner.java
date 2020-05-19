package net.minecraft.world.spawner;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class PhantomSpawner {
   private int ticksUntilSpawn;

   public int tick(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      if (!spawnHostileMobs) {
         return 0;
      } else if (!worldIn.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
         return 0;
      } else {
         Random random = worldIn.rand;
         --this.ticksUntilSpawn;
         if (this.ticksUntilSpawn > 0) {
            return 0;
         } else {
            this.ticksUntilSpawn += (60 + random.nextInt(60)) * 20;
            if (worldIn.getSkylightSubtracted() < 5 && worldIn.dimension.hasSkyLight()) {
               return 0;
            } else {
               int i = 0;

               for(PlayerEntity playerentity : worldIn.getPlayers()) {
                  if (!playerentity.isSpectator()) {
                     BlockPos blockpos = new BlockPos(playerentity);
                     if (!worldIn.dimension.hasSkyLight() || blockpos.getY() >= worldIn.getSeaLevel() && worldIn.canSeeSky(blockpos)) {
                        DifficultyInstance difficultyinstance = worldIn.getDifficultyForLocation(blockpos);
                        if (difficultyinstance.isHarderThan(random.nextFloat() * 3.0F)) {
                           ServerStatisticsManager serverstatisticsmanager = ((ServerPlayerEntity)playerentity).getStats();
                           int j = MathHelper.clamp(serverstatisticsmanager.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                           int k = 24000;
                           if (random.nextInt(j) >= 72000) {
                              BlockPos blockpos1 = blockpos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                              BlockState blockstate = worldIn.getBlockState(blockpos1);
                              IFluidState ifluidstate = worldIn.getFluidState(blockpos1);
                              if (WorldEntitySpawner.isSpawnableSpace(worldIn, blockpos1, blockstate, ifluidstate)) {
                                 ILivingEntityData ilivingentitydata = null;
                                 int l = 1 + random.nextInt(difficultyinstance.getDifficulty().getId() + 1);

                                 for(int i1 = 0; i1 < l; ++i1) {
                                    PhantomEntity phantomentity = EntityType.PHANTOM.create(worldIn);
                                    phantomentity.moveToBlockPosAndAngles(blockpos1, 0.0F, 0.0F);
                                    ilivingentitydata = phantomentity.onInitialSpawn(worldIn, difficultyinstance, SpawnReason.NATURAL, ilivingentitydata, (CompoundNBT)null);
                                    worldIn.addEntity(phantomentity);
                                 }

                                 i += l;
                              }
                           }
                        }
                     }
                  }
               }

               return i;
            }
         }
      }
   }
}