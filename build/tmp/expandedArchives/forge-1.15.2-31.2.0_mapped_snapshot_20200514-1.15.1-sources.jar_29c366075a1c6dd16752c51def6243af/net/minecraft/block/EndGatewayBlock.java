package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndGatewayBlock extends ContainerBlock {
   protected EndGatewayBlock(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new EndGatewayTileEntity();
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof EndGatewayTileEntity) {
         int i = ((EndGatewayTileEntity)tileentity).getParticleAmount();

         for(int j = 0; j < i; ++j) {
            double d0 = (double)pos.getX() + (double)rand.nextFloat();
            double d1 = (double)pos.getY() + (double)rand.nextFloat();
            double d2 = (double)pos.getZ() + (double)rand.nextFloat();
            double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int k = rand.nextInt(2) * 2 - 1;
            if (rand.nextBoolean()) {
               d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
               d5 = (double)(rand.nextFloat() * 2.0F * (float)k);
            } else {
               d0 = (double)pos.getX() + 0.5D + 0.25D * (double)k;
               d3 = (double)(rand.nextFloat() * 2.0F * (float)k);
            }

            worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
         }

      }
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }

   public boolean isReplaceable(BlockState p_225541_1_, Fluid p_225541_2_) {
      return false;
   }
}