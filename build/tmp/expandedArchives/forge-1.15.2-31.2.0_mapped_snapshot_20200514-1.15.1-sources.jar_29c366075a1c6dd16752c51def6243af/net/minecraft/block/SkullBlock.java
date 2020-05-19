package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SkullBlock extends AbstractSkullBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_0_15;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

   protected SkullBlock(SkullBlock.ISkullType type, Block.Properties properties) {
      super(type, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(ROTATION, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.empty();
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(ROTATION, Integer.valueOf(MathHelper.floor((double)(context.getPlacementYaw() * 16.0F / 360.0F) + 0.5D) & 15));
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(ROTATION, Integer.valueOf(rot.rotate(state.get(ROTATION), 16)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.with(ROTATION, Integer.valueOf(mirrorIn.mirrorRotation(state.get(ROTATION), 16)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(ROTATION);
   }

   public interface ISkullType {
   }

   public static enum Types implements SkullBlock.ISkullType {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;
   }
}