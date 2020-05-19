package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndPortalBlock extends ContainerBlock {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected EndPortalBlock(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new EndPortalTileEntity();
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote && !entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && VoxelShapes.compare(VoxelShapes.create(entityIn.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))), state.getShape(worldIn, pos), IBooleanFunction.AND)) {
         entityIn.changeDimension(worldIn.dimension.getType() == DimensionType.THE_END ? DimensionType.OVERWORLD : DimensionType.THE_END);
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)pos.getX() + (double)rand.nextFloat();
      double d1 = (double)pos.getY() + 0.8D;
      double d2 = (double)pos.getZ() + (double)rand.nextFloat();
      double d3 = 0.0D;
      double d4 = 0.0D;
      double d5 = 0.0D;
      worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }

   public boolean isReplaceable(BlockState p_225541_1_, Fluid p_225541_2_) {
      return false;
   }
}