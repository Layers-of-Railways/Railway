package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CactusBlock extends Block implements net.minecraftforge.common.IPlantable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   protected static final VoxelShape field_196400_b = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape field_196401_c = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   protected CactusBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (!worldIn.isAreaLoaded(pos, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      } else {
         BlockPos blockpos = pos.up();
         if (worldIn.isAirBlock(blockpos)) {
            int i;
            for(i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
               ;
            }

            if (i < 3) {
               int j = state.get(AGE);
               if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true)) {
               if (j == 15) {
                  worldIn.setBlockState(blockpos, this.getDefaultState());
                  BlockState blockstate = state.with(AGE, Integer.valueOf(0));
                  worldIn.setBlockState(pos, blockstate, 4);
                  blockstate.neighborChanged(worldIn, blockpos, this, pos, false);
               } else {
                  worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 4);
               }
               net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
               }
            }
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return field_196400_b;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return field_196401_c;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
         Material material = blockstate.getMaterial();
         if (material.isSolid() || worldIn.getFluidState(pos.offset(direction)).isTagged(FluidTags.LAVA)) {
            return false;
         }
      }

      BlockState soil = worldIn.getBlockState(pos.down());
      return soil.canSustainPlant(worldIn, pos.down(), Direction.UP, this) && !worldIn.getBlockState(pos.up()).getMaterial().isLiquid();
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   @Override
   public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
      return net.minecraftforge.common.PlantType.Desert;
   }

   @Override
   public BlockState getPlant(IBlockReader world, BlockPos pos) {
      return getDefaultState();
   }
}