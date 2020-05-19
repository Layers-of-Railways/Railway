package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
   public WritableBookItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (blockstate.getBlock() == Blocks.LECTERN) {
         return LecternBlock.tryPlaceBook(world, blockpos, blockstate, context.getItem()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      playerIn.openBook(itemstack, handIn);
      playerIn.addStat(Stats.ITEM_USED.get(this));
      return ActionResult.resultSuccess(itemstack);
   }

   /**
    * this method returns true if the book's NBT Tag List "pages" is valid
    */
   public static boolean isNBTValid(@Nullable CompoundNBT nbt) {
      if (nbt == null) {
         return false;
      } else if (!nbt.contains("pages", 9)) {
         return false;
      } else {
         ListNBT listnbt = nbt.getList("pages", 8);

         for(int i = 0; i < listnbt.size(); ++i) {
            String s = listnbt.getString(i);
            if (s.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}