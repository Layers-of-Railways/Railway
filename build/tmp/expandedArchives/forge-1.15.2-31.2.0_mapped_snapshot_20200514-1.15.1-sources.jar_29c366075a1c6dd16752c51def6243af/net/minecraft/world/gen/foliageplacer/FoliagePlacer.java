package net.minecraft.world.gen.foliageplacer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class FoliagePlacer implements IDynamicSerializable {
   protected final int field_227381_a_;
   protected final int field_227382_b_;
   protected final FoliagePlacerType<?> field_227383_c_;

   public FoliagePlacer(int p_i225848_1_, int p_i225848_2_, FoliagePlacerType<?> p_i225848_3_) {
      this.field_227381_a_ = p_i225848_1_;
      this.field_227382_b_ = p_i225848_2_;
      this.field_227383_c_ = p_i225848_3_;
   }

   public abstract void func_225571_a_(IWorldGenerationReader p_225571_1_, Random p_225571_2_, TreeFeatureConfig p_225571_3_, int p_225571_4_, int p_225571_5_, int p_225571_6_, BlockPos p_225571_7_, Set<BlockPos> p_225571_8_);

   public abstract int func_225573_a_(Random p_225573_1_, int p_225573_2_, int p_225573_3_, TreeFeatureConfig p_225573_4_);

   protected abstract boolean func_225572_a_(Random p_225572_1_, int p_225572_2_, int p_225572_3_, int p_225572_4_, int p_225572_5_, int p_225572_6_);

   public abstract int func_225570_a_(int p_225570_1_, int p_225570_2_, int p_225570_3_, int p_225570_4_);

   protected void func_227384_a_(IWorldGenerationReader p_227384_1_, Random p_227384_2_, TreeFeatureConfig p_227384_3_, int p_227384_4_, BlockPos p_227384_5_, int p_227384_6_, int p_227384_7_, Set<BlockPos> p_227384_8_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = -p_227384_7_; i <= p_227384_7_; ++i) {
         for(int j = -p_227384_7_; j <= p_227384_7_; ++j) {
            if (!this.func_225572_a_(p_227384_2_, p_227384_4_, i, p_227384_6_, j, p_227384_7_)) {
               blockpos$mutable.setPos(i + p_227384_5_.getX(), p_227384_6_ + p_227384_5_.getY(), j + p_227384_5_.getZ());
               this.func_227385_a_(p_227384_1_, p_227384_2_, blockpos$mutable, p_227384_3_, p_227384_8_);
            }
         }
      }

   }

   protected void func_227385_a_(IWorldGenerationReader p_227385_1_, Random p_227385_2_, BlockPos p_227385_3_, TreeFeatureConfig p_227385_4_, Set<BlockPos> p_227385_5_) {
      if (AbstractTreeFeature.isAirOrLeaves(p_227385_1_, p_227385_3_) || AbstractTreeFeature.isTallPlants(p_227385_1_, p_227385_3_) || AbstractTreeFeature.isWater(p_227385_1_, p_227385_3_)) {
         p_227385_1_.setBlockState(p_227385_3_, p_227385_4_.leavesProvider.getBlockState(p_227385_2_, p_227385_3_), 19);
         p_227385_5_.add(p_227385_3_.toImmutable());
      }

   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.FOLIAGE_PLACER_TYPE.getKey(this.field_227383_c_).toString())).put(p_218175_1_.createString("radius"), p_218175_1_.createInt(this.field_227381_a_)).put(p_218175_1_.createString("radius_random"), p_218175_1_.createInt(this.field_227382_b_));
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(builder.build()))).getValue();
   }
}