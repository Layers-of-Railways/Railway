package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.math.MutableBoundingBox;

public abstract class MarginedStructureStart extends StructureStart {
   public MarginedStructureStart(Structure<?> p_i225874_1_, int p_i225874_2_, int p_i225874_3_, MutableBoundingBox p_i225874_4_, int p_i225874_5_, long p_i225874_6_) {
      super(p_i225874_1_, p_i225874_2_, p_i225874_3_, p_i225874_4_, p_i225874_5_, p_i225874_6_);
   }

   protected void recalculateStructureSize() {
      super.recalculateStructureSize();
      int i = 12;
      this.bounds.minX -= 12;
      this.bounds.minY -= 12;
      this.bounds.minZ -= 12;
      this.bounds.maxX += 12;
      this.bounds.maxY += 12;
      this.bounds.maxZ += 12;
   }
}