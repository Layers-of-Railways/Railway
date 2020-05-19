package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public abstract class ProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   /**
    * Dispense the specified stack, play the dispense sound and spawn particles.
    */
   public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      World world = source.getWorld();
      IPosition iposition = DispenserBlock.getDispensePosition(source);
      Direction direction = source.getBlockState().get(DispenserBlock.FACING);
      IProjectile iprojectile = this.getProjectileEntity(world, iposition, stack);
      iprojectile.shoot((double)direction.getXOffset(), (double)((float)direction.getYOffset() + 0.1F), (double)direction.getZOffset(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
      world.addEntity((Entity)iprojectile);
      stack.shrink(1);
      return stack;
   }

   /**
    * Play the dispense sound from the specified block.
    */
   protected void playDispenseSound(IBlockSource source) {
      source.getWorld().playEvent(1002, source.getBlockPos(), 0);
   }

   /**
    * Return the projectile entity spawned by this dispense behavior.
    */
   protected abstract IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn);

   protected float getProjectileInaccuracy() {
      return 6.0F;
   }

   protected float getProjectileVelocity() {
      return 1.1F;
   }
}