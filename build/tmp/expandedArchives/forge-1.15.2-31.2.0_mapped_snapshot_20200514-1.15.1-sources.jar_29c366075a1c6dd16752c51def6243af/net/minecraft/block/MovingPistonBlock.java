package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class MovingPistonBlock extends ContainerBlock {
   public static final DirectionProperty FACING = PistonHeadBlock.FACING;
   public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

   public MovingPistonBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return null;
   }

   public static TileEntity createTilePiston(BlockState p_196343_0_, Direction p_196343_1_, boolean p_196343_2_, boolean p_196343_3_) {
      return new PistonTileEntity(p_196343_0_, p_196343_1_, p_196343_2_, p_196343_3_);
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof PistonTileEntity) {
            ((PistonTileEntity)tileentity).clearPistonTileEntity();
         }

      }
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
      BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
      BlockState blockstate = worldIn.getBlockState(blockpos);
      if (blockstate.getBlock() instanceof PistonBlock && blockstate.get(PistonBlock.EXTENDED)) {
         worldIn.removeBlock(blockpos, false);
      }

   }

   public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return false;
   }

   public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return false;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         worldIn.removeBlock(pos, false);
         return ActionResultType.CONSUME;
      } else {
         return ActionResultType.PASS;
      }
   }

   public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
      PistonTileEntity pistontileentity = this.getTileEntity(builder.getWorld(), builder.assertPresent(LootParameters.POSITION));
      return pistontileentity == null ? Collections.emptyList() : pistontileentity.getPistonState().getDrops(builder);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      PistonTileEntity pistontileentity = this.getTileEntity(worldIn, pos);
      return pistontileentity != null ? pistontileentity.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
   }

   @Nullable
   private PistonTileEntity getTileEntity(IBlockReader p_220170_1_, BlockPos p_220170_2_) {
      TileEntity tileentity = p_220170_1_.getTileEntity(p_220170_2_);
      return tileentity instanceof PistonTileEntity ? (PistonTileEntity)tileentity : null;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
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

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, TYPE);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}