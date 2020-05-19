package net.minecraft.world.gen.foliageplacer;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class SpruceFoliagePlacer extends FoliagePlacer {
   public SpruceFoliagePlacer(int p_i225852_1_, int p_i225852_2_) {
      super(p_i225852_1_, p_i225852_2_, FoliagePlacerType.SPRUCE);
   }

   public <T> SpruceFoliagePlacer(Dynamic<T> p_i225853_1_) {
      this(p_i225853_1_.get("radius").asInt(0), p_i225853_1_.get("radius_random").asInt(0));
   }

   public void func_225571_a_(IWorldGenerationReader p_225571_1_, Random p_225571_2_, TreeFeatureConfig p_225571_3_, int p_225571_4_, int p_225571_5_, int p_225571_6_, BlockPos p_225571_7_, Set<BlockPos> p_225571_8_) {
      int i = p_225571_2_.nextInt(2);
      int j = 1;
      int k = 0;

      for(int l = p_225571_4_; l >= p_225571_5_; --l) {
         this.func_227384_a_(p_225571_1_, p_225571_2_, p_225571_3_, p_225571_4_, p_225571_7_, l, i, p_225571_8_);
         if (i >= j) {
            i = k;
            k = 1;
            j = Math.min(j + 1, p_225571_6_);
         } else {
            ++i;
         }
      }

   }

   public int func_225573_a_(Random p_225573_1_, int p_225573_2_, int p_225573_3_, TreeFeatureConfig p_225573_4_) {
      return this.field_227381_a_ + p_225573_1_.nextInt(this.field_227382_b_ + 1);
   }

   protected boolean func_225572_a_(Random p_225572_1_, int p_225572_2_, int p_225572_3_, int p_225572_4_, int p_225572_5_, int p_225572_6_) {
      return Math.abs(p_225572_3_) == p_225572_6_ && Math.abs(p_225572_5_) == p_225572_6_ && p_225572_6_ > 0;
   }

   public int func_225570_a_(int p_225570_1_, int p_225570_2_, int p_225570_3_, int p_225570_4_) {
      return p_225570_4_ <= 1 ? 0 : 2;
   }
}