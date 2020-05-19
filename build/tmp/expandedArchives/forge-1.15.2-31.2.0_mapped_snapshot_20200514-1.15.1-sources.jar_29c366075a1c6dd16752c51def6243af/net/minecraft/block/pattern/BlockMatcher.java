package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockMatcher implements Predicate<BlockState> {
   private final Block block;

   public BlockMatcher(Block blockType) {
      this.block = blockType;
   }

   public static BlockMatcher forBlock(Block blockType) {
      return new BlockMatcher(blockType);
   }

   public boolean test(@Nullable BlockState p_test_1_) {
      return p_test_1_ != null && p_test_1_.getBlock() == this.block;
   }
}