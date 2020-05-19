package net.minecraft.world.lighting;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.NibbleArray;

public interface IWorldLightListener extends ILightListener {
   @Nullable
   NibbleArray getData(SectionPos p_215612_1_);

   int getLightFor(BlockPos worldPos);

   public static enum Dummy implements IWorldLightListener {
      INSTANCE;

      @Nullable
      public NibbleArray getData(SectionPos p_215612_1_) {
         return null;
      }

      public int getLightFor(BlockPos worldPos) {
         return 0;
      }

      public void updateSectionStatus(SectionPos pos, boolean isEmpty) {
      }
   }
}