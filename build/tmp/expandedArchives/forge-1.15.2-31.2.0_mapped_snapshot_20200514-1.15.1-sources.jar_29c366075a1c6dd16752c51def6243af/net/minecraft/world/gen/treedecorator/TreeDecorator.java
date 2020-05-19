package net.minecraft.world.gen.treedecorator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;

public abstract class TreeDecorator implements IDynamicSerializable {
   protected final TreeDecoratorType<?> field_227422_a_;

   protected TreeDecorator(TreeDecoratorType<?> p_i225871_1_) {
      this.field_227422_a_ = p_i225871_1_;
   }

   public abstract void func_225576_a_(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_);

   protected void func_227424_a_(IWorldWriter p_227424_1_, BlockPos p_227424_2_, BooleanProperty p_227424_3_, Set<BlockPos> p_227424_4_, MutableBoundingBox p_227424_5_) {
      this.func_227423_a_(p_227424_1_, p_227424_2_, Blocks.VINE.getDefaultState().with(p_227424_3_, Boolean.valueOf(true)), p_227424_4_, p_227424_5_);
   }

   protected void func_227423_a_(IWorldWriter p_227423_1_, BlockPos p_227423_2_, BlockState p_227423_3_, Set<BlockPos> p_227423_4_, MutableBoundingBox p_227423_5_) {
      p_227423_1_.setBlockState(p_227423_2_, p_227423_3_, 19);
      p_227423_4_.add(p_227423_2_);
      p_227423_5_.expandTo(new MutableBoundingBox(p_227423_2_, p_227423_2_));
   }
}