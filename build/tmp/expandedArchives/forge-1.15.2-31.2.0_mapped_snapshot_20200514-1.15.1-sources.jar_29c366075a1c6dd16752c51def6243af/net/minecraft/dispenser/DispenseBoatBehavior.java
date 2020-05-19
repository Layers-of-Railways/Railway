package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseBoatBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior dispenseItemBehaviour = new DefaultDispenseItemBehavior();
   private final BoatEntity.Type type;

   public DispenseBoatBehavior(BoatEntity.Type typeIn) {
      this.type = typeIn;
   }

   /**
    * Dispense the specified stack, play the dispense sound and spawn particles.
    */
   public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      Direction direction = source.getBlockState().get(DispenserBlock.FACING);
      World world = source.getWorld();
      double d0 = source.getX() + (double)((float)direction.getXOffset() * 1.125F);
      double d1 = source.getY() + (double)((float)direction.getYOffset() * 1.125F);
      double d2 = source.getZ() + (double)((float)direction.getZOffset() * 1.125F);
      BlockPos blockpos = source.getBlockPos().offset(direction);
      double d3;
      if (world.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
         d3 = 1.0D;
      } else {
         if (!world.getBlockState(blockpos).isAir() || !world.getFluidState(blockpos.down()).isTagged(FluidTags.WATER)) {
            return this.dispenseItemBehaviour.dispense(source, stack);
         }

         d3 = 0.0D;
      }

      BoatEntity boatentity = new BoatEntity(world, d0, d1 + d3, d2);
      boatentity.setBoatType(this.type);
      boatentity.rotationYaw = direction.getHorizontalAngle();
      world.addEntity(boatentity);
      stack.shrink(1);
      return stack;
   }

   /**
    * Play the dispense sound from the specified block.
    */
   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playEvent(1000, source.getBlockPos(), 0);
   }
}