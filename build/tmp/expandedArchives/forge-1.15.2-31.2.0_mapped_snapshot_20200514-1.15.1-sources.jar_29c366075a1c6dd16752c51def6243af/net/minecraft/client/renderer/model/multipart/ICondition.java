package net.minecraft.client.renderer.model.multipart;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface ICondition {
   ICondition TRUE = (p_223290_0_) -> {
      return (p_223289_0_) -> {
         return true;
      };
   };
   ICondition FALSE = (p_223288_0_) -> {
      return (p_223287_0_) -> {
         return false;
      };
   };

   Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_getPredicate_1_);
}