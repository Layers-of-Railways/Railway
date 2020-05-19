package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ScaffoldingItem extends BlockItem {
   public ScaffoldingItem(Block p_i50039_1_, Item.Properties p_i50039_2_) {
      super(p_i50039_1_, p_i50039_2_);
   }

   @Nullable
   public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext context) {
      BlockPos blockpos = context.getPos();
      World world = context.getWorld();
      BlockState blockstate = world.getBlockState(blockpos);
      Block block = this.getBlock();
      if (blockstate.getBlock() != block) {
         return ScaffoldingBlock.func_220117_a(world, blockpos) == 7 ? null : context;
      } else {
         Direction direction;
         if (context.func_225518_g_()) {
            direction = context.isInside() ? context.getFace().getOpposite() : context.getFace();
         } else {
            direction = context.getFace() == Direction.UP ? context.getPlacementHorizontalFacing() : Direction.UP;
         }

         int i = 0;
         BlockPos.Mutable blockpos$mutable = (new BlockPos.Mutable(blockpos)).move(direction);

         while(i < 7) {
            if (!world.isRemote && !World.isValid(blockpos$mutable)) {
               PlayerEntity playerentity = context.getPlayer();
               int j = world.getHeight();
               if (playerentity instanceof ServerPlayerEntity && blockpos$mutable.getY() >= j) {
                  SChatPacket schatpacket = new SChatPacket((new TranslationTextComponent("build.tooHigh", j)).applyTextStyle(TextFormatting.RED), ChatType.GAME_INFO);
                  ((ServerPlayerEntity)playerentity).connection.sendPacket(schatpacket);
               }
               break;
            }

            blockstate = world.getBlockState(blockpos$mutable);
            if (blockstate.getBlock() != this.getBlock()) {
               if (blockstate.isReplaceable(context)) {
                  return BlockItemUseContext.func_221536_a(context, blockpos$mutable, direction);
               }
               break;
            }

            blockpos$mutable.move(direction);
            if (direction.getAxis().isHorizontal()) {
               ++i;
            }
         }

         return null;
      }
   }

   protected boolean checkPosition() {
      return false;
   }
}