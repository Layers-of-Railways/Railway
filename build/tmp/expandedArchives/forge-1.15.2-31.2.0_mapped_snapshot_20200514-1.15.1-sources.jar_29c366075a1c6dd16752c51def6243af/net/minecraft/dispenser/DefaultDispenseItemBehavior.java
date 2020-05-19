package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class DefaultDispenseItemBehavior implements IDispenseItemBehavior {
   public final ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
      ItemStack itemstack = this.dispenseStack(p_dispense_1_, p_dispense_2_);
      this.playDispenseSound(p_dispense_1_);
      this.spawnDispenseParticles(p_dispense_1_, p_dispense_1_.getBlockState().get(DispenserBlock.FACING));
      return itemstack;
   }

   /**
    * Dispense the specified stack, play the dispense sound and spawn particles.
    */
   protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      Direction direction = source.getBlockState().get(DispenserBlock.FACING);
      IPosition iposition = DispenserBlock.getDispensePosition(source);
      ItemStack itemstack = stack.split(1);
      doDispense(source.getWorld(), itemstack, 6, direction, iposition);
      return stack;
   }

   public static void doDispense(World worldIn, ItemStack stack, int speed, Direction facing, IPosition position) {
      double d0 = position.getX();
      double d1 = position.getY();
      double d2 = position.getZ();
      if (facing.getAxis() == Direction.Axis.Y) {
         d1 = d1 - 0.125D;
      } else {
         d1 = d1 - 0.15625D;
      }

      ItemEntity itementity = new ItemEntity(worldIn, d0, d1, d2, stack);
      double d3 = worldIn.rand.nextDouble() * 0.1D + 0.2D;
      itementity.setMotion(worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed + (double)facing.getXOffset() * d3, worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed + (double)0.2F, worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed + (double)facing.getZOffset() * d3);
      worldIn.addEntity(itementity);
   }

   /**
    * Play the dispense sound from the specified block.
    */
   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playEvent(1000, source.getBlockPos(), 0);
   }

   /**
    * Order clients to display dispense particles from the specified block and facing.
    */
   protected void spawnDispenseParticles(IBlockSource source, Direction facingIn) {
      source.getWorld().playEvent(2000, source.getBlockPos(), facingIn.getIndex());
   }
}