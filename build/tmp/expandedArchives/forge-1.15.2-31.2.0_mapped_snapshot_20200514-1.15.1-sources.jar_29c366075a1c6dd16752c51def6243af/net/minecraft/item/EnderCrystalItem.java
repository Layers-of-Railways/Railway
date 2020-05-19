package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;

public class EnderCrystalItem extends Item {
   public EnderCrystalItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (blockstate.getBlock() != Blocks.OBSIDIAN && blockstate.getBlock() != Blocks.BEDROCK) {
         return ActionResultType.FAIL;
      } else {
         BlockPos blockpos1 = blockpos.up();
         if (!world.isAirBlock(blockpos1)) {
            return ActionResultType.FAIL;
         } else {
            double d0 = (double)blockpos1.getX();
            double d1 = (double)blockpos1.getY();
            double d2 = (double)blockpos1.getZ();
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
            if (!list.isEmpty()) {
               return ActionResultType.FAIL;
            } else {
               if (!world.isRemote) {
                  EnderCrystalEntity endercrystalentity = new EnderCrystalEntity(world, d0 + 0.5D, d1, d2 + 0.5D);
                  endercrystalentity.setShowBottom(false);
                  world.addEntity(endercrystalentity);
                  if (world.dimension instanceof EndDimension) {
                     DragonFightManager dragonfightmanager = ((EndDimension)world.dimension).getDragonFightManager();
                     dragonfightmanager.tryRespawnDragon();
                  }
               }

               context.getItem().shrink(1);
               return ActionResultType.SUCCESS;
            }
         }
      }
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}