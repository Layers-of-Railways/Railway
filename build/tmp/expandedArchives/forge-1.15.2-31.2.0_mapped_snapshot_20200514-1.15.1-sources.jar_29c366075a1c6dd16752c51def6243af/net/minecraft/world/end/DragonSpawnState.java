package net.minecraft.world.end;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

public enum DragonSpawnState {
   START {
      public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
         BlockPos blockpos = new BlockPos(0, 128, 0);

         for(EnderCrystalEntity endercrystalentity : crystals) {
            endercrystalentity.setBeamTarget(blockpos);
         }

         manager.setRespawnState(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
         if (ticks < 100) {
            if (ticks == 0 || ticks == 50 || ticks == 51 || ticks == 52 || ticks >= 95) {
               worldIn.playEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            manager.setRespawnState(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
         int i = 40;
         boolean flag = ticks % 40 == 0;
         boolean flag1 = ticks % 40 == 39;
         if (flag || flag1) {
            List<EndSpikeFeature.EndSpike> list = EndSpikeFeature.generateSpikes(worldIn);
            int j = ticks / 40;
            if (j < list.size()) {
               EndSpikeFeature.EndSpike endspikefeature$endspike = list.get(j);
               if (flag) {
                  for(EnderCrystalEntity endercrystalentity : crystals) {
                     endercrystalentity.setBeamTarget(new BlockPos(endspikefeature$endspike.getCenterX(), endspikefeature$endspike.getHeight() + 1, endspikefeature$endspike.getCenterZ()));
                  }
               } else {
                  int k = 10;

                  for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(endspikefeature$endspike.getCenterX() - 10, endspikefeature$endspike.getHeight() - 10, endspikefeature$endspike.getCenterZ() - 10), new BlockPos(endspikefeature$endspike.getCenterX() + 10, endspikefeature$endspike.getHeight() + 10, endspikefeature$endspike.getCenterZ() + 10))) {
                     worldIn.removeBlock(blockpos, false);
                  }

                  worldIn.createExplosion((Entity)null, (double)((float)endspikefeature$endspike.getCenterX() + 0.5F), (double)endspikefeature$endspike.getHeight(), (double)((float)endspikefeature$endspike.getCenterZ() + 0.5F), 5.0F, Explosion.Mode.DESTROY);
                  EndSpikeFeatureConfig endspikefeatureconfig = new EndSpikeFeatureConfig(true, ImmutableList.of(endspikefeature$endspike), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE.withConfiguration(endspikefeatureconfig).place(worldIn, worldIn.getChunkProvider().getChunkGenerator(), new Random(), new BlockPos(endspikefeature$endspike.getCenterX(), 45, endspikefeature$endspike.getCenterZ()));
               }
            } else if (flag) {
               manager.setRespawnState(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
         if (ticks >= 100) {
            manager.setRespawnState(END);
            manager.resetSpikeCrystals();

            for(EnderCrystalEntity endercrystalentity : crystals) {
               endercrystalentity.setBeamTarget((BlockPos)null);
               worldIn.createExplosion(endercrystalentity, endercrystalentity.getPosX(), endercrystalentity.getPosY(), endercrystalentity.getPosZ(), 6.0F, Explosion.Mode.NONE);
               endercrystalentity.remove();
            }
         } else if (ticks >= 80) {
            worldIn.playEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if (ticks == 0) {
            for(EnderCrystalEntity endercrystalentity1 : crystals) {
               endercrystalentity1.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (ticks < 5) {
            worldIn.playEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
      }
   };

   private DragonSpawnState() {
   }

   public abstract void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos);
}