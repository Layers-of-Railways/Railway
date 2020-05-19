package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinecartItem extends Item {
   private static final IDispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

      /**
       * Dispense the specified stack, play the dispense sound and spawn particles.
       */
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         Direction direction = source.getBlockState().get(DispenserBlock.FACING);
         World world = source.getWorld();
         double d0 = source.getX() + (double)direction.getXOffset() * 1.125D;
         double d1 = Math.floor(source.getY()) + (double)direction.getYOffset();
         double d2 = source.getZ() + (double)direction.getZOffset() * 1.125D;
         BlockPos blockpos = source.getBlockPos().offset(direction);
         BlockState blockstate = world.getBlockState(blockpos);
         RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
         double d3;
         if (blockstate.isIn(BlockTags.RAILS)) {
            if (railshape.isAscending()) {
               d3 = 0.6D;
            } else {
               d3 = 0.1D;
            }
         } else {
            if (!blockstate.isAir(world, blockpos) || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
               return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }

            BlockState blockstate1 = world.getBlockState(blockpos.down());
            RailShape railshape1 = blockstate1.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate1.getBlock()).getRailDirection(blockstate1, world, blockpos.down(), null) : RailShape.NORTH_SOUTH;
            if (direction != Direction.DOWN && railshape1.isAscending()) {
               d3 = -0.4D;
            } else {
               d3 = -0.9D;
            }
         }

         AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.create(world, d0, d1 + d3, d2, ((MinecartItem)stack.getItem()).minecartType);
         if (stack.hasDisplayName()) {
            abstractminecartentity.setCustomName(stack.getDisplayName());
         }

         world.addEntity(abstractminecartentity);
         stack.shrink(1);
         return stack;
      }

      /**
       * Play the dispense sound from the specified block.
       */
      protected void playDispenseSound(IBlockSource source) {
         source.getWorld().playEvent(1000, source.getBlockPos(), 0);
      }
   };
   private final AbstractMinecartEntity.Type minecartType;

   public MinecartItem(AbstractMinecartEntity.Type minecartTypeIn, Item.Properties builder) {
      super(builder);
      this.minecartType = minecartTypeIn;
      DispenserBlock.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (!blockstate.isIn(BlockTags.RAILS)) {
         return ActionResultType.FAIL;
      } else {
         ItemStack itemstack = context.getItem();
         if (!world.isRemote) {
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
               d0 = 0.5D;
            }

            AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.create(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.minecartType);
            if (itemstack.hasDisplayName()) {
               abstractminecartentity.setCustomName(itemstack.getDisplayName());
            }

            world.addEntity(abstractminecartentity);
         }

         itemstack.shrink(1);
         return ActionResultType.SUCCESS;
      }
   }
}