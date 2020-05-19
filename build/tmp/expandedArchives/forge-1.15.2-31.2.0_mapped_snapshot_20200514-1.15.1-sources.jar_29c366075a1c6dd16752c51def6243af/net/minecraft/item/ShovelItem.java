package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShovelItem extends ToolItem {
   private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
   /** Map used to lookup shovel right click interactions */
   protected static final Map<Block, BlockState> SHOVEL_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState()));

   public ShovelItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Item.Properties builder) {
      super(attackDamageIn, attackSpeedIn, tier, EFFECTIVE_ON, builder.addToolType(net.minecraftforge.common.ToolType.SHOVEL, tier.getHarvestLevel()));
   }

   /**
    * Check whether this Item can harvest the given Block
    */
   public boolean canHarvestBlock(BlockState blockIn) {
      Block block = blockIn.getBlock();
      return block == Blocks.SNOW || block == Blocks.SNOW_BLOCK;
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (context.getFace() == Direction.DOWN) {
         return ActionResultType.PASS;
      } else {
         PlayerEntity playerentity = context.getPlayer();
         BlockState blockstate1 = SHOVEL_LOOKUP.get(blockstate.getBlock());
         BlockState blockstate2 = null;
         if (blockstate1 != null && world.isAirBlock(blockpos.up())) {
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            blockstate2 = blockstate1;
         } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.get(CampfireBlock.LIT)) {
            world.playEvent((PlayerEntity)null, 1009, blockpos, 0);
            blockstate2 = blockstate.with(CampfireBlock.LIT, Boolean.valueOf(false));
         }

         if (blockstate2 != null) {
            if (!world.isRemote) {
               world.setBlockState(blockpos, blockstate2, 11);
               if (playerentity != null) {
                  context.getItem().damageItem(1, playerentity, (p_220041_1_) -> {
                     p_220041_1_.sendBreakAnimation(context.getHand());
                  });
               }
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }
}