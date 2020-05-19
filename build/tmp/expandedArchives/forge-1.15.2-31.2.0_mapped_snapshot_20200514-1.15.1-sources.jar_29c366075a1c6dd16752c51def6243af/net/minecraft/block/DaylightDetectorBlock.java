package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.DaylightDetectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends ContainerBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

   public DaylightDetectorBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWER, Integer.valueOf(0)).with(INVERTED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public boolean isTransparent(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return blockState.get(POWER);
   }

   public static void updatePower(BlockState p_196319_0_, World p_196319_1_, BlockPos p_196319_2_) {
      if (p_196319_1_.dimension.hasSkyLight()) {
         int i = p_196319_1_.getLightFor(LightType.SKY, p_196319_2_) - p_196319_1_.getSkylightSubtracted();
         float f = p_196319_1_.getCelestialAngleRadians(1.0F);
         boolean flag = p_196319_0_.get(INVERTED);
         if (flag) {
            i = 15 - i;
         } else if (i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float)i * MathHelper.cos(f));
         }

         i = MathHelper.clamp(i, 0, 15);
         if (p_196319_0_.get(POWER) != i) {
            p_196319_1_.setBlockState(p_196319_2_, p_196319_0_.with(POWER, Integer.valueOf(i)), 3);
         }

      }
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (player.isAllowEdit()) {
         if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
         } else {
            BlockState blockstate = state.cycle(INVERTED);
            worldIn.setBlockState(pos, blockstate, 4);
            updatePower(blockstate, worldIn, pos);
            return ActionResultType.SUCCESS;
         }
      } else {
         return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
      }
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(BlockState state) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new DaylightDetectorTileEntity();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(POWER, INVERTED);
   }
}