package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WallTorchBlock extends TorchBlock {
   public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;
   private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), Direction.WEST, Block.makeCuboidShape(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.makeCuboidShape(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

   protected WallTorchBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH));
   }

   /**
    * Returns the unlocalized name of the block with "tile." appended to the front.
    */
   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return func_220289_j(state);
   }

   public static VoxelShape func_220289_j(BlockState p_220289_0_) {
      return SHAPES.get(p_220289_0_.get(HORIZONTAL_FACING));
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      Direction direction = state.get(HORIZONTAL_FACING);
      BlockPos blockpos = pos.offset(direction.getOpposite());
      BlockState blockstate = worldIn.getBlockState(blockpos);
      return blockstate.isSolidSide(worldIn, blockpos, direction);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = this.getDefaultState();
      IWorldReader iworldreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      Direction[] adirection = context.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.with(HORIZONTAL_FACING, direction1);
            if (blockstate.isValidPosition(iworldreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing.getOpposite() == stateIn.get(HORIZONTAL_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      Direction direction = stateIn.get(HORIZONTAL_FACING);
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.7D;
      double d2 = (double)pos.getZ() + 0.5D;
      double d3 = 0.22D;
      double d4 = 0.27D;
      Direction direction1 = direction.getOpposite();
      worldIn.addParticle(ParticleTypes.SMOKE, d0 + 0.27D * (double)direction1.getXOffset(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getZOffset(), 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(ParticleTypes.FLAME, d0 + 0.27D * (double)direction1.getXOffset(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getZOffset(), 0.0D, 0.0D, 0.0D);
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING);
   }
}