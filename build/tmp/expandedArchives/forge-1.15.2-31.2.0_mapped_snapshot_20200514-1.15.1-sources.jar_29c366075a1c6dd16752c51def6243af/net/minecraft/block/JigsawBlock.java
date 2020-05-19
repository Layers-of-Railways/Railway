package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;

public class JigsawBlock extends DirectionalBlock implements ITileEntityProvider {
   protected JigsawBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   // FORGE: Fix jigsaw blocks not mirroring properly
   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   @Override
   @Deprecated
   public BlockState mirror(BlockState state, net.minecraft.util.Mirror mirrorIn) {
      return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getFace());
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new JigsawTileEntity();
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof JigsawTileEntity && player.canUseCommandBlock()) {
         player.openJigsaw((JigsawTileEntity)tileentity);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public static boolean func_220171_a(Template.BlockInfo p_220171_0_, Template.BlockInfo p_220171_1_) {
      return p_220171_0_.state.get(FACING) == p_220171_1_.state.get(FACING).getOpposite() && p_220171_0_.nbt.getString("attachement_type").equals(p_220171_1_.nbt.getString("attachement_type"));
   }
}