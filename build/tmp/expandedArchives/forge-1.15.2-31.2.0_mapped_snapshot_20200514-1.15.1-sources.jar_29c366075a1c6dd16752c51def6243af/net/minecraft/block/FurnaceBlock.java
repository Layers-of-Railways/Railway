package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FurnaceBlock extends AbstractFurnaceBlock {
   protected FurnaceBlock(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new FurnaceTileEntity();
   }

   /**
    * Interface for handling interaction with blocks that impliment AbstractFurnaceBlock. Called in onBlockActivated
    * inside AbstractFurnaceBlock.
    */
   protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof FurnaceTileEntity) {
         player.openContainer((INamedContainerProvider)tileentity);
         player.addStat(Stats.INTERACT_WITH_FURNACE);
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(LIT)) {
         double d0 = (double)pos.getX() + 0.5D;
         double d1 = (double)pos.getY();
         double d2 = (double)pos.getZ() + 0.5D;
         if (rand.nextDouble() < 0.1D) {
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction direction = stateIn.get(FACING);
         Direction.Axis direction$axis = direction.getAxis();
         double d3 = 0.52D;
         double d4 = rand.nextDouble() * 0.6D - 0.3D;
         double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
         double d6 = rand.nextDouble() * 6.0D / 16.0D;
         double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
         worldIn.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
         worldIn.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
      }
   }
}