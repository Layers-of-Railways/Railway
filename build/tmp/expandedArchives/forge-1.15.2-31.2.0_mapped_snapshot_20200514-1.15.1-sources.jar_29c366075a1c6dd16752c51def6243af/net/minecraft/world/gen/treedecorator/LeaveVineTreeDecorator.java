package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class LeaveVineTreeDecorator extends TreeDecorator {
   public LeaveVineTreeDecorator() {
      super(TreeDecoratorType.LEAVE_VINE);
   }

   public <T> LeaveVineTreeDecorator(Dynamic<T> p_i225870_1_) {
      this();
   }

   public void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      p_225576_4_.forEach((p_227421_5_) -> {
         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos = p_227421_5_.west();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos)) {
               this.func_227420_a_(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos1 = p_227421_5_.east();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos1)) {
               this.func_227420_a_(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos2 = p_227421_5_.north();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos2)) {
               this.func_227420_a_(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos3 = p_227421_5_.south();
            if (AbstractTreeFeature.isAir(p_225576_1_, blockpos3)) {
               this.func_227420_a_(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
            }
         }

      });
   }

   private void func_227420_a_(IWorldGenerationReader p_227420_1_, BlockPos p_227420_2_, BooleanProperty p_227420_3_, Set<BlockPos> p_227420_4_, MutableBoundingBox p_227420_5_) {
      this.func_227424_a_(p_227420_1_, p_227420_2_, p_227420_3_, p_227420_4_, p_227420_5_);
      int i = 4;

      for(BlockPos blockpos = p_227420_2_.down(); AbstractTreeFeature.isAir(p_227420_1_, blockpos) && i > 0; --i) {
         this.func_227424_a_(p_227420_1_, blockpos, p_227420_3_, p_227420_4_, p_227420_5_);
         blockpos = blockpos.down();
      }

   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.TREE_DECORATOR_TYPE.getKey(this.field_227422_a_).toString()))))).getValue();
   }
}