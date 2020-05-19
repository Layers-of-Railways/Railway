package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class TrunkVineTreeDecorator extends TreeDecorator {
   public TrunkVineTreeDecorator() {
      super(TreeDecoratorType.TRUNK_VINE);
   }

   public <T> TrunkVineTreeDecorator(Dynamic<T> p_i225873_1_) {
      this();
   }

   public void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      p_225576_3_.forEach((p_227433_5_) -> {
         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos = p_227433_5_.west();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos)) {
               this.func_227424_a_(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos1 = p_227433_5_.east();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos1)) {
               this.func_227424_a_(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos2 = p_227433_5_.north();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos2)) {
               this.func_227424_a_(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos3 = p_227433_5_.south();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos3)) {
               this.func_227424_a_(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
            }
         }

      });
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.TREE_DECORATOR_TYPE.getKey(this.field_227422_a_).toString()))))).getValue();
   }
}