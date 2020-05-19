package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class FletchingTableBlock extends CraftingTableBlock {
   protected FletchingTableBlock(Block.Properties properties) {
      super(properties);
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      return ActionResultType.PASS;
   }
}