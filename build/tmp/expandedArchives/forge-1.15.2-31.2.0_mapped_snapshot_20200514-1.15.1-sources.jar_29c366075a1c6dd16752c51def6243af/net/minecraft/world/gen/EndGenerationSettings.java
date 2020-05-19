package net.minecraft.world.gen;

import net.minecraft.util.math.BlockPos;

public class EndGenerationSettings extends GenerationSettings {
   private BlockPos spawnPos;

   public EndGenerationSettings setSpawnPos(BlockPos pos) {
      this.spawnPos = pos;
      return this;
   }

   public BlockPos getSpawnPos() {
      return this.spawnPos;
   }
}